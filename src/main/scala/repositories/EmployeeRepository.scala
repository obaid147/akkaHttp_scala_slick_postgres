package repositories

import Entities._
import core.BaseRepository
import slick.lifted.TableQuery
import Entities.Employee
import Entities.EmployeeTable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

abstract class EmployeeRepository  extends BaseRepository[EmployeeTable, Employee](TableQuery[EmployeeTable]){

  def insertItem(row: Employee): Future[Employee] = {
    super.save(row)
  }

  /*def getEmployees = {
    //super.getAll
    Future.successful(Seq(Employee(1, "obaid", false)))
  }*/

  override def getById(id: Long): Future[Option[Employee]] = {
    /*val superRes = super.getById(id)
    superRes.map(_.map(_.copy(id = 1)))*/
    super.getById(id)
  }

  override def getAll: Future[Seq[Employee]] = super.getAll

  override def save(row: Employee): Future[Employee] = super.save(row)

  override def updateById(id: Long, row: Employee): Future[Int] = {
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
