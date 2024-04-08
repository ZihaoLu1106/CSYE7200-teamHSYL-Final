package controllers


import javax.inject._
import play.api.mvc._
import scala.concurrent.ExecutionContext
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   *
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("mydatabase")
  val collection: MongoCollection[data] = database.getCollection("users")
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

}
