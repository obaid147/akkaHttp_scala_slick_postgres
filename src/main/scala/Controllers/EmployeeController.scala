package Controllers

import spray.json._
import DefaultJsonProtocol._
import Controllers.models.Employee
import repositories.models.{Employee => EmployeeResult}
import akka.actor.ActorRef
import org.json4s.{DefaultFormats, JValue}
import org.json4s.jackson.JsonMethods.parse
import repositories.ImplEmployeeRepository

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object MyFormat extends DefaultJsonProtocol {
  implicit val formatData = jsonFormat5(Employee)
}
abstract class EmployeeControllerComponent {
  def insertEmployeeController(data: String): Future[EmployeeResult]

  def getAllEmployees(): Future[Seq[EmployeeResult]]
  /*
    def getEmployeeById(id: Int): Future[Option[Employee]]

    def deleteById(id: Int): Future[Int]

    def updateById(id: Int, e: Employee): Future[Int]*/
}

// google guice,
object EmployeeController extends EmployeeControllerComponent {
/*
   def updateById(id: Int, row: Employee) = {
    ImplEmployeeRepository.updateById(id, row)

  }*/

  def getAllEmployees() = {
    ImplEmployeeRepository.getAll
  }

  /*implicit val f = DefaultFormats      // JACKSON lib

  def insertEmployeeController(data: String): Future[EmployeeResult] = {
    val employeeTry: Try[Employee] = Try(parse(data).extract[Employee])
    val x: JValue = parse(data)
    import MyFormat._
    val emp = data.parseJson.convertTo[Employee]
    //ImplEmployeeRepository.insertItem(emp)
    employeeTry match {      case Success(s) =>
        ImplEmployeeRepository.insertItem(s)

      case Failure(f) =>
        println("===1======")
        println(f.getMessage)
        println("===2======")
        Future.failed(InvalidInputException(ErrorCodes.INVALID_INPUT_EXCEPTION,
        message = "some msg", exception = new Exception(f.getCause)))
    }
  }*/

  def insertEmployeeController(data: String): Future[EmployeeResult] = {  // spray-json
    import MyFormat._
    val employeeTry = Try(data.parseJson.convertTo[Employee])

    employeeTry match {
      case Success(s) =>
        ImplEmployeeRepository.insertItem(s)

      case Failure(f) =>
        Future.failed(InvalidInputException(ErrorCodes.INVALID_INPUT_EXCEPTION,
          message = "some msg", exception = new Exception(f.getCause)))
    }
  }

/*  override def getEmployeeById(id: Int): Future[Option[Employee]] = {
    ImplEmployeeRepository.getById(id)
  }

  override def deleteById(id: Int): Future[Int] = {
    ImplEmployeeRepository.deleteById(id)
  }*/


}

trait MyException extends Exception

case class InvalidInputException(errorCode: String = ErrorCodes.INVALID_INPUT_EXCEPTION, message: String = ErrorMessages.INVALID_INPUT, exception: Throwable) extends MyException

object ErrorCodes {
  val INVALID_INPUT_EXCEPTION: String = "1010"
}

object ErrorMessages {
  val INVALID_INPUT: String = "Invalid Input"
}