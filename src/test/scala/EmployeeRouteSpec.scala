import akka.http.scaladsl.model.{RequestEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
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

    "add an employee into database" in {
      val newEmployee = Employee(uuid = "4", firstName = "Bob", age = 35L)
      val jsonRequest = Marshal(newEmployee).to[RequestEntity].futureValue
      Post("/company/employee", jsonRequest) ~> myRoute ~> check {
        status should be(StatusCodes.Created)
        val response = entityAs[Employee]
        response shouldBe newEmployee
      }
    }

    "return an employee by id" in {
      Get("/company/employee/1") ~> myRoute ~> check {
        entityAs[Employee] shouldBe myEmployee.head
        responseAs[Option[Employee]] shouldBe Some(Employee("1", "John",24L))
      }
    }

    "return employees with same first name" in {
      Get("/company/employee/firstName/John") ~> myRoute ~> check {
        status should be(StatusCodes.OK)
        entityAs[Seq[Employee]] shouldBe myEmployee.filter(_.firstName == "John")
      }
    }

  }

}

trait EMPJsonProtocol extends DefaultJsonProtocol {
  implicit val bookFormat: RootJsonFormat[Employee] = jsonFormat3(Employee)
}


object EmployeeRouteSpec extends EMPJsonProtocol with SprayJsonSupport {
  var myEmployee: Seq[Employee] = Seq(
    Employee(
      uuid = "1",
      firstName = "John",
      age = 24L
    ),
    Employee(
      uuid = "2",
      firstName = "Doe",
      age = 30L
    ),
    Employee(
      uuid = "3",
      firstName = "John",
      age = 10L
    )
  )

  val myRoute: Route = pathPrefix("company" / "employee") {
    (pathEndOrSingleSlash & get) {
      complete(myEmployee)
    } ~ post {
      entity(as[Employee]) { newEmployee =>
        complete {
          myEmployee = myEmployee :+ newEmployee
          StatusCodes.Created -> newEmployee
        }
      }
    } ~ get {
      path("firstName" / Segment) { fName =>
        complete(myEmployee.filter(_.firstName == fName))
      }
    } ~ get {
      (path(IntNumber) | parameter("uuid".as[Int])) { uuid =>
        complete(myEmployee.find(_.uuid == uuid.toString))
      }
    }
  }

}
