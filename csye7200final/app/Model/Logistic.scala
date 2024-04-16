package Model
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}

object Logistic extends App{
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
  val lrModel = lr.fit(df1)
  val predictions = lrModel.transform(df1)
  // Print the coefficients and intercept
  println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

  val evaluator = new org.apache.spark.ml.evaluation.BinaryClassificationEvaluator()
    .setLabelCol("BMI")
    .setRawPredictionCol("rawPrediction")
    .setMetricName("areaUnderROC")

  val accuracy = evaluator.evaluate(predictions)
  println(s"Accuracy: $accuracy")

  // Stop the SparkSession
  spark.stop()
}
