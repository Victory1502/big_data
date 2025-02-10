import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import scala.util.Random

object SparkStreamingKafka {




  def main(args: Array[String]): Unit = {
    // Initialisation de SparkSession
    val spark = SparkSession.builder
      .appName("KafkaSparkStreaming")
      .master("local[*]") // Peut être modifié en fonction de ton cluster Spark
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN") // Réduit les logs inutiles

    // Définition du schéma des transactions
    val transactionSchema = new StructType()
      .add("idTransaction", StringType)
      .add("typeTransaction", StringType)
      .add("montant", DoubleType)
      .add("devise", StringType)
      .add("date", StringType)
      .add("lieu", StringType)
      .add("moyenPaiement", StringType)
      .add("details", MapType(StringType, StringType))
      .add("utilisateur", MapType(StringType, StringType))

    // Lecture du topic Kafka "transaction"
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092") // Modifie selon ton environnement
      .option("subscribe", "transaction")
      .option("startingOffsets", "earliest")
      .load()






    // Conversion des données Kafka (clé-valeur) en JSON lisible
    val transactionsDF = kafkaDF
      .selectExpr("CAST(value AS STRING) as json")
      .select(from_json(col("json"), transactionSchema).as("data"))
      .select("data.*")

    // Nettoyage et enrichissement des données
    val tauxDeChange = 0.92
    val cleanedDF = transactionsDF
      .withColumn("montantEUR", col("montant") * tauxDeChange) // Convertir USD en EUR
      .withColumn("devise", lit("euro"))
      .withColumn("timestamp", current_timestamp()) // Ajouter un timestamp
      .withColumn("date", to_date(col("date"), "yyyy-MM-dd'T'HH:mm:ss")) // Transformer date
      .filter(col("montant").isNotNull) // Supprimer transactions erronées (montant vide)
      .filter(col("utilisateur.adresse").isNotNull) // Supprimer adresses None

    // Configuration MinIO pour stocker les données traitées
    spark.conf.set("fs.s3a.access.key", "minio")
    spark.conf.set("fs.s3a.secret.key", "minio123")
    spark.conf.set("fs.s3a.endpoint", "http://localhost:9000") // Modifie selon ton MinIO
    spark.conf.set("fs.s3a.path.style.access", "true")

    //   // Écriture en Parquet sur MinIO
    val writeQuery = cleanedDF.writeStream
      .outputMode("append")
      .format("parquet")
      .option("path", "s3a://transaction/processed")
      .option("checkpointLocation", "/tmp/checkpoints/")
      .start()



    // Affichage en mode streaming
    val query = transactionsDF.writeStream
      .outputMode("append")
      .format("console")
      .start()

    query.awaitTermination() // Laisse tourner le streaming
    writeQuery.awaitTermination() // Attendre la fin du processus d'écriture
  }
}

