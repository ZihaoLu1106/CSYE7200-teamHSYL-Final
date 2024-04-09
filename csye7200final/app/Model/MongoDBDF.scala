package Model
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import com.mongodb.spark._
import org.bson.Document
import org.mongodb.scala.{MongoClient, MongoCollection}
import org.mongodb.scala.bson.collection.immutable.Document

object MongoDBDF {
  def main(args: Array[String]): Unit ={
    val spark = SparkSession.builder()
      .master("local")
      .appName("MongoSparkConnectorIntro")
      .config("spark.mongodb.read.connection.uri", "mongodb+srv://hsyl:12345@final.dumyeta.mongodb.net/")
      .config("spark.mongodb.write.connection.uri", "mongodb+srv://hsyl:12345@final.dumyeta.mongodb.net/")
      .getOrCreate()

    val uri="mongodb+srv://luzihao:C7kkjpjq8EHC5Qcj@cluster0.mdmm4kj.mongodb.net/"

    val dfH = spark.read
      .format("mongodb")
      .option("database", "sample")
      .option("collection", "heart")
      .load()

    val dfS = spark.read
      .format("mongodb")
      .option("database", "sample")
      .option("collection", "ss")
      .load()
    dfS.show()

    // cleaning heart Analysis dataset
    // removed unrelated columns to the project
    val columnsToRemove = Seq("cp","caa", "oldpeak", "restecg","thall","thalachh","slp","output","fbs")
    val dfHfinal = dfH.drop(columnsToRemove: _*)
    val renameMap = Map("trtbps" -> "Blood Pressure",
    "chol" -> "BMI Category", "age" -> "Age", "exng" -> "Physical Activity Level", "sex" -> "Gender")
    var renamedDF = dfHfinal
    for ((oldName, newName) <- renameMap) {
      renamedDF = renamedDF.withColumnRenamed(oldName, newName)
    }
    val updatedDF = renamedDF.withColumn("BMI Category", when(col("BMI Category") <= 242, 0)
      .when(col("BMI Category") > 242 && col("BMI Category") <= 418, 1)
      .otherwise(2))
    updatedDF.show(5)
  }
}
