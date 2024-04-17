package Model

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.RandomForestRegressor
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.linalg.Vector


object DataAnalysis {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder()
            .master("local[1]")
            .appName("GBT_train")
            .getOrCreate()

        val trainFilePath = "app/data/data.csv"

        val customSchema = StructType(Array(
            StructField("Age", IntegerType, nullable = true),
            StructField("BMI", DoubleType, nullable = true),
            StructField("BP", IntegerType, nullable = true),
            StructField("Gender", IntegerType, nullable = true),
            StructField("Exercise", DoubleType, nullable = true),
            StructField("Quality", IntegerType, nullable = true),
            StructField("Duration", DoubleType, nullable = true),
            StructField("Stress", IntegerType, nullable = true),
            StructField("Id", StringType, nullable = true)
        ))

        // read csv file
        val train_df = spark.read
            .format("csv")
            .option("header", "false")
            .schema(customSchema)
            .option("inferSchema", "true")
            .load(trainFilePath)
            .drop("Id")

        //   testFeatureCorrelation(train_df)
        //  val newDF = testFeatureInteraction(train_df)
        //  newDF.printSchema()
        // newDF.show()


        testFeatureCorrelation(train_df)

    }

    def testFeatureCorrelation(df: DataFrame): List[Double] = {
        // Calculate correlation between each feature and BMI
        val featureList = List("Age", "BP", "Gender", "Exercise", "Quality", "Duration", "Stress")
        val correlations = featureList.map { feature =>
            val correlation = df.stat.corr("BMI", feature)
            println(s"Correlation between BMI and $feature: $correlation")
            correlation
        }
        correlations
    }


}