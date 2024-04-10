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
    val source = Source.fromFile("app/data/df.csv")
    val data = try {
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
    val input1 = request.body.asFormUrlEncoded.get("input1").head
    val input2 = request.body.asFormUrlEncoded.get("input2").head
    val input3 = request.body.asFormUrlEncoded.get("input3").head

    // Do something with the input values
    println(s"Input 1: $input1, Input 2: $input2, Input 3: $input3")
    Ok(s"Input 1: $input1, Input 2: $input2, Input 3: $input3")
  }
}

case class CSVRow(Age: Int, BMI_Category: Int, Blood_Pressure: Int,Gender:Double,PhysicalActivity_Level:Double,Quality_Of_Sleep:Int,Sleep_Duaration:Double,Stress_Level:Int,ID:String)
