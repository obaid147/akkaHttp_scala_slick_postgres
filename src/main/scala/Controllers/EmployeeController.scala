package Controllers

import spray.json._
import Controllers.models.{Employee, EmployeeJsonProtocol}
import repositories.models.{Employee => EmployeeResult}
import repositories.ImplEmployeeRepository

import java.util.UUID
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


abstract class EmployeeControllerComponent  {
  def insertEmployeeController(data: String): Future[Option[EmployeeResult]]

  def getAllEmployees(): Future[Seq[EmployeeResult]]

  def getEmployeeById(uuid: UUID): Future[Option[EmployeeResult]]

  def deleteById(id: Int): Future[Int]

  def updateById(id: Int, e: EmployeeResult): Future[Int]
}

object EmployeeController extends EmployeeControllerComponent {

  import EmployeeJsonProtocol._

  def getAllEmployees() = {
    ImplEmployeeRepository.getAll
  }
  def insertEmployeeController(data: String): Future[Option[EmployeeResult]] = {  // spray-json


    val employeeTry = Try(data.parseJson.convertTo[Employee])

    employeeTry match {
      case Success(s) =>
        ImplEmployeeRepository.insertItem(s)

      case Failure(f) =>
        println(f.getMessage)
        Future.failed(InvalidInputException(ErrorCodes.INVALID_INPUT_EXCEPTION,
          message = "some msg", exception = new Exception(f.getCause)))
    }
  }

     def updateById(id: Int, row: EmployeeResult) = {
      ImplEmployeeRepository.updateById(id, row)

    }
  override def getEmployeeById(uuid: UUID): Future[Option[EmployeeResult]] = {
    ImplEmployeeRepository.getEmpById(uuid)
  }
    override def deleteById(id: Int): Future[Int] = {
      ImplEmployeeRepository.deleteById(id)
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