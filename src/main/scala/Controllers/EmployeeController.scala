package Controllers

import spray.json._
import Controllers.models.{Employee, EmployeeJsonProtocol, EmployeePutJsonProtocol, PatchEmployee, PutEmployee}
import repositories.models.{Employee => EmployeeResult}
import repositories.ImplEmployeeRepository
import Controllers.models.PatchEmployeeJsonProtocol._

import java.util.UUID
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


abstract class EmployeeControllerComponent  {
  def insertEmployeeController(data: String): Future[Option[EmployeeResult]]

  def getAllEmployees(): Future[Seq[EmployeeResult]]

  def getEmployeeById(uuid: String): Future[Option[EmployeeResult]]

  def deleteById(id: String): Future[Int]

  def putEmployee(data: String): Future[Int]

  def patchEmployee(data: String): Future[Int]

  def insertEmployeeTwice(data: String): Future[EmployeeResult]
}

object EmployeeController extends EmployeeControllerComponent {


  import EmployeeJsonProtocol._
  import EmployeePutJsonProtocol._


  def insertEmployeeTwice(data: String): Future[EmployeeResult] = {
    val employee = data.parseJson.convertTo[Employee]
    ImplEmployeeRepository.insertTwice(employee)
  }

  def getAllEmployees() = {
    ImplEmployeeRepository.getAll
  }

  def insertEmployeeController(data: String): Future[Option[EmployeeResult]] = {
    val employee = data.parseJson.convertTo[Employee]

    employee match {
      case emp =>
        ImplEmployeeRepository.insertItem(emp)
      case _ =>
        Future.successful(None)
    }
  }

  def putEmployee(data: String) = {

    /*val emp = data.parseJson.convertTo[PutEmployee] // handle validation error
    ImplEmployeeRepository.putEmployeeById(emp.uuid, emp)*/

    val putEmployeeTry = Try(data.parseJson.convertTo[PutEmployee])

    putEmployeeTry match {
      case Success(s) =>
        ImplEmployeeRepository.putEmployeeById(s.uuid, s)

      case Failure(f) =>
        println(f.getMessage)
        Future.successful(0)
    }

  }
  override def getEmployeeById(uuid: String): Future[Option[EmployeeResult]] = {
    ImplEmployeeRepository.getEmpById(uuid)
  }

  override def deleteById(id: String): Future[Int] = {
    ImplEmployeeRepository.deleteById(id)
  }

  def patchEmployee(data: String) = {
    /*val emp = data.parseJson.convertTo[PatchEmployee]
    ImplEmployeeRepository.patch(emp)*/
    val employeeTry = Try(data.parseJson.convertTo[PatchEmployee])

    employeeTry match {
      case Success(value) =>
        ImplEmployeeRepository.patch(value)
      case Failure(ex) =>
        /*println(ex.getMessage)
        Future.failed(InvalidInputException(ErrorCodes.INVALID_INPUT_EXCEPTION,
          message = "some msg", exception = new Exception(ex.getCause)))*/
        println(ex.getMessage)
        Future.successful(0)
    }
  }

}

trait MyException extends Exception

case class InvalidInputException(errorCode: String = ErrorCodes.INVALID_INPUT_EXCEPTION, message: String = ErrorMessages.INVALID_INPUT, exception: Throwable) extends MyException

object ErrorCodes {
  val INVALID_INPUT_EXCEPTION: String = "1010"
}

object ErrorMessages {
  val INVALID_INPUT: String = "Invalid Input"
}