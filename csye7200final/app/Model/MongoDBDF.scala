package Model
import org.apache.spark.sql.{DataFrame, SparkSession}
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

    val dataFrameHeart = spark.read
      .format("mongodb")
      .option("database", "sample")
      .option("collection", "heart")
      .load()

    dataFrameHeart.show()

    val dataFramess = spark.read
      .format("mongodb")
      .option("database", "sample")
      .option("collection", "ss")
      .load()

    dataFramess.show()

  }

}
