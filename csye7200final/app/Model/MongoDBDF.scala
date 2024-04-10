package Model
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import com.mongodb.spark._
import org.bson.Document
import org.mongodb.scala.{MongoClient, MongoCollection}
import org.mongodb.scala.bson.collection.immutable.Document

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{VectorAssembler}
import org.apache.spark.ml.regression.{RandomForestRegressor}
import org.apache.spark.ml.evaluation.RegressionEvaluator

object MongoDBDF {
  def main(args: Array[String]): Unit ={
    getDF()
  }
  def getDF(): DataFrame ={
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


    //cleaning ss dataset
    def mapCategory(category: String): Int = category match {
      case "Overweight" => 2
      case "Normal" | "Normal Weight" => 1
      case "Obese" => 0
      case _ => -1 // Handle unknown categories if any
    }

    // Create a UDF (User Defined Function) to apply the mapping function to the DataFrame
    val mapCategoryUDF = udf[Int, String](mapCategory)

    // Update the values in the same column using the UDF
    val updatedDFS = dfS.withColumn("BMI Category", mapCategoryUDF(col("BMI Category")))

    val columnsToRemove1 = Seq("Daily Steps", "Heart Rate", "Occupation", "Person ID", "Sleep Disorder")
    val dfSfinal = updatedDFS.drop(columnsToRemove1: _*)
    //change the pattern of Blood Pressure
    val newDF = dfSfinal.withColumn("Blood Pressure", split(col("Blood Pressure"), "/").getItem(0))
    // Define a function to map gender to numeric values
    def mapGender(gender: String): Int = gender match {
      case "Male" => 0
      case "Female" => 1
      case _ => -1 // Handle unknown genders if any
    }
    // Create a UDF (User Defined Function) to apply the mapping function to the DataFrame
    val mapGenderUDF = udf[Int, String](mapGender)

    // Update the values in the "Gender" column using the UDF
    val newDFCGender = newDF.withColumn("Gender", mapGenderUDF(col("Gender")))
    val df1 = newDFCGender;

    val addingPhysicalNumber = newDFCGender.agg(mean(col("Physical Activity Level"))).collect()(0)(0)

    val df2 = updatedDF.withColumn("Physical Activity Level",
      when(col("Physical Activity Level") === 0, 30)
        .otherwise(col("Physical Activity Level").cast("int") + addingPhysicalNumber))


    def appendDataFrames(df1: DataFrame, df2: DataFrame): DataFrame = {
      // Add an identifier column to each dataframe to differentiate between them after merging
      val df1WithID = df1.withColumn("source", lit("df1"))
      val df2WithID = df2.withColumn("source", lit("df2"))

      // Union the dataframes to append df2 to df1
      val mergedDF = df1WithID.unionByName(df2WithID)

      // Drop the source column
      mergedDF.drop("source")
    }
    val defaultQualityOfSleep = 0
    val defaultSleepDuration = 0.0
    val defaultStressLevel = 0

    // Add new columns to df2 with default values
    val df2WithNewColumns = df2.withColumn("Quality of Sleep", lit(defaultQualityOfSleep))
      .withColumn("Sleep Duration", lit(defaultSleepDuration))
      .withColumn("Stress Level", lit(defaultStressLevel))

    // Show the dataframe with new columns
    df2WithNewColumns.show()
    //merged dataframe
    val mergedDF = appendDataFrames(df1, df2WithNewColumns)
    println("Here is the merged DataFrame: ")
    mergedDF.show(400)

    val outputPath = "app/data"
    try {
      mergedDF.coalesce(1).write.csv(outputPath)
      println(s"DataFrame exported to CSV successfully at: $outputPath")
    } catch {
      case e: Exception => println(s"Error occurred while exporting DataFrame: ${e.getMessage}")
    }

    mergedDF

  }
}
