package repositories

import Controllers.models.Employee
import repositories.models.{Employee => DbEmployee}
import core.BaseRepository
import slick.lifted.TableQuery
import Entities.EmployeeTable

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

abstract class EmployeeRepository  extends BaseRepository[EmployeeTable, DbEmployee](TableQuery[EmployeeTable]){

  def insertItem(row: Employee): Future[Option[DbEmployee]] = {
    val userId = 10
    val uuid = UUID.randomUUID()
    val timeStamp = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()))
    val saveData = DbEmployee(uuid, row.firstName, row.lastName, row.address, row.phoneNumber, row.age, timeStamp, userId)

    //todo: we have to do this in transaction
    for {
      _ <- save(saveData)
      result <- getById(uuid)
    } yield result
  }

  override def getAll: Future[Seq[DbEmployee]] = super.getAll

  /*def getEmployees = {
    //super.getAll
    Future.successful(Seq(Employee(1, "obaid", false)))
  }*/

  def getEmpById(uuid: UUID): Future[Option[DbEmployee]] = {
    /*val superRes = super.getById(id)
    superRes.map(_.map(_.copy(id = 1)))*/
    getById(uuid)
  }

  override def updateById(id: Long, row: DbEmployee): Future[Int] = {
    super.updateById(id, row)
  }

  override def deleteById(id: Long) = {
    super.deleteById(id)
  }


  /*
  def getEmployeeByName(name: String): Future[Seq[Employee]] = {
    for {
      employees <- super.getAll
      result = employees.filter(_.firstName.equalsIgnoreCase(name))
    } yield result
  }*/

}

object ImplEmployeeRepository extends EmployeeRepository
