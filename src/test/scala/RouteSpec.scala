import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.json4s.{DefaultFormats, Extraction}
import org.json4s.jackson.JsonMethods.compact
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.server.Directives._
import repositories.models

import scala.concurrent.Future
import scala.util.Success


class RouteSpec extends AnyWordSpec with Matchers with ScalatestRouteTest{

  import RouteSpec._

  "A" should {
    "be" in {
      assert(List("a", "b") contains "a")
    }
  }

  "GET /employee" should {

    "return a 200 OK status and all employee data in JSON format" in {
      Get("/employee") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`
        val result: List[Employee] = responseAs[List[Employee]]
        Future(Vector(result)).map(x => x shouldBe allEmployees)
      }
    }

    "" in {

    }
  }


}

object RouteSpec extends EMPJsonProtocol with SprayJsonSupport {
  import Controllers.{EmployeeController => controller}
  import rest.{EmployeeRest => Rest}

  val routes: Route = new Rest(controller).routes

  val allEmployees: Future[Seq[models.Employee]] = controller.getAllEmployees()


}
