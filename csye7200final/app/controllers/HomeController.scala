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
    println("nmd")
    val input1Option = request.body.asFormUrlEncoded.get("input1").headOption.getOrElse("Anonymous")
    val input2Option = request.body.asFormUrlEncoded.get("input1").headOption.getOrElse("Anonymous")
    val input3Option = request.body.asFormUrlEncoded.get("input1").headOption.getOrElse("Anonymous")

    println(s"Input 1: $input1Option, Input 2: $input2Option, Input 3: $input3Option")
    println("nmd")

    Redirect(routes.HomeController.displayOutput(input1Option,input2Option,input3Option))


  }
  def displayOutput(input1Option:String,input2Option:String,input3Option:String) = Action { implicit request: Request[AnyContent] =>
    println("nmd")
    Ok(views.html.output(input1Option,input2Option,input3Option))
  }
}

case class CSVRow(Age: Int, BMI_Category: Int, Blood_Pressure: Int,Gender:Double,PhysicalActivity_Level:Double,Quality_Of_Sleep:Int,Sleep_Duaration:Double,Stress_Level:Int,ID:String)
