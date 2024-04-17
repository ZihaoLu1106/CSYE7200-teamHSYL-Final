package Model

import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{abs, col, when}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}

object Logistic{
    def processModel(args: Array[String]): Array[Double] = {
        val spark = SparkSession.builder()
          .master("local[1]")
          .appName("Logistic_train")
          .getOrCreate()

        // Specify the path to your CSV file
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

        train_df.show()
        val assembler = new VectorAssembler()
          .setInputCols(Array("Age", "Gender", "Exercise", "Quality", "Duration", "Stress"))
          .setOutputCol("features")

        val lr = new LogisticRegression()
          .setLabelCol("BMI")
          .setFeaturesCol("features")

        val df1 = assembler.transform(train_df)
        // Fit the model
        val inputData = List((args(0).toInt, args(1).toInt, args(2).toDouble, args(3).toInt, args(4).toDouble, args(5).toDouble))
        val inputDF = spark.createDataFrame(inputData).toDF("Age", "Gender", "Exercise", "Quality", "Duration", "Stress")
        val lrModel = lr.fit(df1)
        val predictions = lrModel.transform(df1)
        // Print the coefficients and intercept

        val thresholdedPredictions = predictions.withColumn("BMI_predictions",
            when(abs(col("prediction") - 0) < 0.1, 0).when(abs(col("prediction") - 1) < 0.1, 1)
              .when(abs(col("prediction") - 2) < 0.1, 2)
              .otherwise(col("prediction")))

        thresholdedPredictions.select("BMI_predictions", "BMI").show(5)
        val correctPredictions = thresholdedPredictions.filter((col("BMI_predictions") === col("BMI")))
        val accuracy = correctPredictions.count().toDouble / thresholdedPredictions.count()
        println(s"Accuracy: $accuracy")
        val df2 = assembler.transform(inputDF)
        val predictions1 = lrModel.transform(df2)

        val lr1 = new LogisticRegression()
          .setLabelCol("BP")
          .setFeaturesCol("features")

        val lrModel1 = lr1.fit(df1)

        val predictions2 = lrModel1.transform(df2)
        val predictedClass = predictions1.select("prediction").head.getDouble(0) // get BMI
        val predictedClass1 = predictions2.select("prediction").head.getDouble(0) // get BP
        println(predictedClass)
        println(predictedClass1)
        // Stop the SparkSession
        spark.stop()
        Array(predictedClass1, predictedClass)
    }
}
