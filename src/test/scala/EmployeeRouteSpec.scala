import akka.http.scaladsl.model.{HttpResponse, RequestEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture

case class Employee(uuid: String, firstName: String, age: Long)

class EmployeeRouteSpec extends WordSpec with Matchers with ScalatestRouteTest {
  import EmployeeRouteSpec._

  "An employee backend" should {
    "return all employees data" in {
      Get("/company/employee") ~> myRoute ~> check {
        status should be (StatusCodes.OK)

        entityAs[Seq[Employee]] shouldBe myEmployee
      }
    }

    "be able to add an employee" in {
      val newEmployee = Employee(uuid = "3", firstName = "Bob", age = 35L)
      val jsonRequest = Marshal(newEmployee).to[RequestEntity].futureValue
      Post("/company/employee", jsonRequest) ~> myRoute ~> check {
        status should be(StatusCodes.Created)
        val response = entityAs[Employee]
        response shouldBe newEmployee
      }
    }
  }
}


trait EMPJsonProtocol extends DefaultJsonProtocol {
  implicit val bookFormat: RootJsonFormat[Employee] = jsonFormat3(Employee)
}

import akka.http.scaladsl.server.Directives._

object EmployeeRouteSpec extends EMPJsonProtocol with SprayJsonSupport {
  var myEmployee = Seq(
    Employee(
      uuid = "1",
      firstName = "John",
      age = 24L
    ),
    Employee(
      uuid = "2",
      firstName = "Jane",
      age = 30L
    )
  )

  val myRoute = pathPrefix("company" / "employee") {
    (pathEndOrSingleSlash & get) {
      complete(myEmployee)
    } ~ post {
      entity(as[Employee]) { newEmployee =>
        complete {
          myEmployee = myEmployee :+ newEmployee
          StatusCodes.Created -> newEmployee
        }
      }
    }
  }
}

