package Model
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.GBTRegressor

object GBT {
  def processModel(args: Array[String]): Double = {

    val spark = SparkSession.builder()
      .master("local[1]")
      .appName("GBT_train")
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

    val df1 = assembler.transform(train_df)

    val gbt = new GBTRegressor()
      .setLabelCol("BMI")
      .setFeaturesCol("features")
      .setMaxIter(30)
    //gbt model for BMI
    val model = gbt.fit(df1)
    val predictionsBMI = model.transform(df1)
    val thresholdedPredictions = predictionsBMI.withColumn("BMI_predictions",
      when(abs(col("prediction") - 0) < 0.1, 0).when(abs(col("prediction") - 1) < 0.1, 1)
        .when(abs(col("prediction") - 2) < 0.1, 2)
        .otherwise(col("prediction")))

    thresholdedPredictions.select("BMI_predictions", "BMI").show(5)
    val correctPredictions = thresholdedPredictions.filter((col("BMI_predictions") === col("BMI")))
    val accuracy = correctPredictions.count().toDouble / thresholdedPredictions.count()
    println(s"Accuracy: $accuracy")
    //model.save("app/gbtModelForBMI")

    //gbt model for BP
    val gbt1 = new GBTRegressor()
      .setLabelCol("BP")
      .setFeaturesCol("features")
      .setMaxIter(30)
    val model1 = gbt1.fit(df1)
    val predictionsBP = model1.transform(df1)
    val inputData = List((args(0).toInt, args(1).toInt, args(2).toDouble, args(3).toInt, args(4).toDouble, args(5).toDouble))
    val inputDF = spark.createDataFrame(inputData).toDF("Age", "Gender", "Exercise", "Quality", "Duration", "Stress")
    val df2 = assembler.transform(inputDF)
    val predictions = model1.transform(df2)
    val predictedClass = predictions.select("prediction").head.getDouble(0)
    println(predictedClass)
    //model1.save("app/gbtModelForBP")
    spark.stop()
    predictedClass
  }


}
