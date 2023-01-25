package repositories

import Controllers.models.Employee
import repositories.models.{Employee => DbEmployee}
import core.BaseRepository
import slick.lifted.TableQuery
import Entities.EmployeeTable
import akka.http.scaladsl.model.StatusCodes

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}
/** PUT
 * create a method putEmployee here and pass DbEmployee argument
 * then inside putEmplyee create updated_by and updated_at fields with values and then create DB Employee case class
 */

/**
 * PATCH
  * 2. create a method here patchEmployee(create a case class PatchEMpoyee{address, age, firstName}) -- PATCH call from UI
 * 2 B.
 */

abstract class EmployeeRepository  extends BaseRepository[EmployeeTable, DbEmployee](TableQuery[EmployeeTable]){

  import Controllers.models.{PatchEmployee, PutEmployee}
  def patch(row: PatchEmployee) = {

    val emp = getEmpById(row.uuid)

    val createdByFuture = emp.map(_.map(_.createdBy)).map(_.getOrElse(1L))
    val lastNameFuture = emp.map(_.map(_.lastName)).map(_.getOrElse(""))
    val phoneNumberFuture = emp.map(_.map(_.phoneNumber)).map(_.getOrElse(""))
    val createdAtFuture = emp.map(_.map(_.createdAt)).map(_.get)
    val updatedAt = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()))
    val updatedBy = 100L
    val isDeletedFuture = emp.map(_.map(_.isDeleted)).map(_.getOrElse(false))

    val updateFuture = for {
      lastName <- lastNameFuture
      phoneNumber <- phoneNumberFuture
      createdAt <- createdAtFuture
      createdBy <- createdByFuture
      isDeleted <- isDeletedFuture
    } yield (lastName, phoneNumber, createdAt, createdBy, isDeleted)

    updateFuture.flatMap {
      case (lastName, phoneNumber, createdAt, createdBy, isDeleted) =>
        super.updateById(
          row.uuid,
          DbEmployee(
            row.uuid,
            row.firstName,
            lastName,
            row.address,
            phoneNumber,
            row.age,
            createdAt,
            createdBy,
            isDeleted,
            Some(updatedAt),
            Some(updatedBy)
          )
        )
    }

  }

  def putEmployeeById(id: String, row: PutEmployee)= {
    val employeeRecord = getEmpById(id)
    val updatedBy = Some(20L)
    val updatedAt = Some(Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())))
    //val createdBy = 10L

    employeeRecord.flatMap { record =>
      record.map { emp =>
        super.updateById(id,
          DbEmployee(
            row.uuid,
            row.firstName,
            row.lastName,
            row.address,
            row.phoneNumber,
            row.age,
            emp.createdAt,
            emp.createdBy,
            row.isDeleted,
            updatedAt,
            updatedBy))
      }.getOrElse(Future.successful(0))
    }
  }

  def insertItem(row: Employee): Future[Option[DbEmployee]] = {
    val userId = 10
    val uuid = UUID.randomUUID().toString
    val timeStamp = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()))
    val saveData = DbEmployee(uuid, row.firstName, row.lastName, row.address, row.phoneNumber, row.age, timeStamp, userId)

    //todo: we have to do this in transaction
    //todo: use UUID instead of string for uuid unique column
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

  def getEmpById(uuid: String): Future[Option[DbEmployee]] = {
    /*val superRes = super.getById(id)
    superRes.map(_.map(_.copy(id = 1)))*/
    getById(uuid)
  }

  /**
   *
   * change name everywhere to PUT instead of UPDATE
   */
  /*override def putEmployee(id: UUID, row: DbEmployee): Future[Int] = {
    super.updateById(id, row)
  }*/

  override def deleteById(id: String) = {
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
