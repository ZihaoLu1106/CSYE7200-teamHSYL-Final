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
        CSVRow(age.toInt, bmiCategory.toInt, bloodPressure.toInt, gender.toDouble, physicalActivityLevel.toInt, qualityOfSleep.toInt, sleepDuration.toDouble, stressLevel.toInt, id)
      }.toList
    } finally {
      source.close()
    }
    Ok(views.html.about(data)) // Renders the about view
  }
}

case class CSVRow(Age: Int, BMI_Category: Int, Blood_Pressure: Int,Genter:Double,PhysicalActivity_Level:Int,Quality_Of_Sleep:Int,Sleep_Duaration:Double,Stress_Level:Int,ID:String)
