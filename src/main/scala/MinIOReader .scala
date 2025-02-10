import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

object MinIOReader {

  def main(args: Array[String]): Unit = {
    // Initialisation de SparkSession
    val spark = SparkSession.builder
      .appName("MinIOReader")
      .master("local[*]") // Exécution en mode local
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN") // Réduit les logs inutiles

    // Configuration MinIO
    spark.conf.set("fs.s3a.access.key", "minio")
    spark.conf.set("fs.s3a.secret.key", "minio123")
    spark.conf.set("fs.s3a.endpoint", "http://localhost:9000") // Point d'accès MinIO
    spark.conf.set("fs.s3a.path.style.access", "true")

    // Chemin du bucket MinIO
    val minioPath = "s3a://transaction/processed"

    // Définition du schéma des transactions (identique à celui utilisé dans le streaming)
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

    // Lecture des fichiers .snappy.parquet depuis MinIO
    val minioDF = try {
      spark.read
        .schema(transactionSchema) // Appliquer le schéma
        .parquet(minioPath) // Lire les fichiers Parquet (y compris .snappy.parquet)
    } catch {
      case e: Exception =>
        println(s"Erreur lors de la lecture depuis MinIO : ${e.getMessage}")
        spark.emptyDataFrame // Retourner un DataFrame vide en cas d'erreur
    }

    // Afficher les données lues depuis MinIO
    if (minioDF.isEmpty) {
      println("Aucune donnée trouvée dans MinIO. Assurez-vous que le bucket contient des fichiers Parquet.")
    } else {
      println(s"Nombre de fichiers lus depuis MinIO : ${minioDF.inputFiles.length}")
      println("Données lues depuis MinIO :")
      minioDF.show() // Afficher les données
    }

    // Arrêt de la session Spark
    spark.stop()
  }
}