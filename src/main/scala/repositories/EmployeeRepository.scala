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

  def compareEmployees(row: PutEmployee, emp: DbEmployee): Boolean = {
      row.firstName == emp.firstName &&
      row.lastName == emp.lastName &&
      row.address == emp.address &&
      row.phoneNumber == emp.phoneNumber &&
      row.age == emp.age &&
      row.isDeleted == emp.isDeleted
  }

  def putEmployeeById(id: String, row: PutEmployee): Future[Int] = {
    val employeeRecord = getEmpById(id)

    employeeRecord.flatMap { record =>
      record.map { emp =>
        if (compareEmployees(row, emp)) {
          println("You entered the same data...")
          Future.successful(1)
        } else {
          val updatedBy = Some(20L)
          val updatedAt = Some(Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())))

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
        }
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

  def insertTwice(row: Employee): Future[DbEmployee] = {
    val userId = 10
    val uuid = UUID.randomUUID().toString
    val timeStamp = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()))
    val saveData1 = DbEmployee(uuid, row.firstName, row.lastName, row.address, row.phoneNumber, row.age, timeStamp, userId)
    val lengthLastName = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec" //todo edit it
    val saveData2 = DbEmployee(uuid, row.firstName, lengthLastName, row.address, row.phoneNumber, row.age, timeStamp, userId)
    insertTwoRows(saveData1, saveData2)
    //todo: if transaction is success,
    //1.) check what error goes to user: note that down here and show to aamir
    // 2.) let's see how we can modify that to return user defined error and error code to user.
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

  def insert(row: Employee): Future[Option[DbEmployee]] = {
    val userId = 10
    val uuid = UUID.randomUUID().toString
    val timeStamp = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()))
    val saveData = DbEmployee(uuid, row.firstName, row.lastName, row.address, row.phoneNumber, row.age, timeStamp, userId)

    //todo: we have to do this in transaction
    //todo: use UUID instead of string for uuid unique column
    insertAndFetch(saveData)
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
