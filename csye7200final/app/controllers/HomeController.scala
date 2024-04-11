package controllers

import Model.MongoDBDF
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import javax.inject._
import play.api._
import play.api.mvc._

import scala.io.Source
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.classification.GBTClassificationModel

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())

  }
  def about() = Action { implicit request: Request[AnyContent] =>
    val source = Source.fromFile("app/data/data.csv")
    val data = try {
      println("nmd2")
      source.getLines().drop(1).map { line =>
        val Array(age, bmiCategory, bloodPressure, gender, physicalActivityLevel, qualityOfSleep, sleepDuration, stressLevel, id) = line.split(",").map(_.trim)
        CSVRow(age.toInt, bmiCategory.toInt, bloodPressure.toInt, gender.toDouble, physicalActivityLevel.toDouble, qualityOfSleep.toInt, sleepDuration.toDouble, stressLevel.toInt, id)
      }.toList
    } finally {
      source.close()
    }
    Ok(views.html.about(data)) // Renders the about view
  }
  def processInputs() = Action { implicit request: Request[AnyContent] =>
    println("nmd")
    val age = request.body.asFormUrlEncoded.get("age").headOption.getOrElse("Anonymous")
    val gender = request.body.asFormUrlEncoded.get("gender").headOption.getOrElse("Anonymous")
    val exercise = request.body.asFormUrlEncoded.get("exercise").headOption.getOrElse("Anonymous")
    val quality = request.body.asFormUrlEncoded.get("qualityOfSleep").headOption.getOrElse("Anonymous")
    val duration = request.body.asFormUrlEncoded.get("sleepDuration").headOption.getOrElse("Anonymous")
    val stress = request.body.asFormUrlEncoded.get("stressLevel").headOption.getOrElse("Anonymous")

    println("nmd")




    Redirect(routes.HomeController.displayOutput(age,gender,exercise,quality,duration,stress))


  }
  def displayOutput(ageD:String,genderD:String,exerciseD:String,qualityD:String,durationD:String,stressD:String) = Action { implicit request: Request[AnyContent] =>
    println("nmd")

    val spark = SparkSession.builder()
      .appName("YourAppName")
      .config("spark.master", "local") // Set Spark master
      .getOrCreate()
    val age=ageD.toInt
    val gender=genderD.toInt
    val exercise=exerciseD.toDouble
    val quality=qualityD.toInt
    val duration=durationD.toDouble
    val stress=stressD.toInt
    println("xxx")

    val modelBP=GBTClassificationModel.load("app/gbtModelForBP")
    println("yyy")

    val inputData = Seq((age, gender, exercise, quality, duration, stress))
    val inputDF = spark.createDataFrame(inputData).toDF("age", "gender", "exercise", "quality", "duration", "stress")

    // Make predictions
    val predictions = modelBP.transform(inputDF)

    // Extract the prediction result
    val predictedClass = predictions.select("prediction").head.getDouble(0)

    println(predictedClass)

    Ok(views.html.output(age,gender,exercise,quality,duration,stress))
  }
}

case class CSVRow(Age: Int, BMI_Category: Int, Blood_Pressure: Int,Gender:Double,PhysicalActivity_Level:Double,Quality_Of_Sleep:Int,Sleep_Duaration:Double,Stress_Level:Int,ID:String)
