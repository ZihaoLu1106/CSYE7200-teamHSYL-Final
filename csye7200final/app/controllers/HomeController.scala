package controllers

import Model.MongoDBDF

import javax.inject._
import play.api._
import play.api.mvc._
import scala.io.Source

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
    val formData = request.body.asFormUrlEncoded
    val input1Option = formData.flatMap(_.get("input1").flatMap(_.headOption))
    val input2Option = formData.flatMap(_.get("input2").flatMap(_.headOption))
    val input3Option = formData.flatMap(_.get("input3").flatMap(_.headOption))
    println("nmd")
    (input1Option, input2Option, input3Option) match {
      case (Some(input1), Some(input2), Some(input3)) =>
        println(s"Input 1: $input1, Input 2: $input2, Input 3: $input3")
        Ok(views.html.processInputs(input1, input2, input3))
      case _ =>
        BadRequest("Missing input values")
    }
  }
}

case class CSVRow(Age: Int, BMI_Category: Int, Blood_Pressure: Int,Gender:Double,PhysicalActivity_Level:Double,Quality_Of_Sleep:Int,Sleep_Duaration:Double,Stress_Level:Int,ID:String)
