package Entities

import core.{BaseEntity, BaseTable}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{Rep, TableQuery, Tag}

import scala.concurrent.Future

class EmployeeTable(_tableTag: Tag) extends BaseTable[Employee](_tableTag, Some("public"), "employee") {

  def * = (id, firstName, lastName, address, phone_number, age, isDeleted) <> (Employee.tupled, Employee.unapply)

  def ? = (Rep.Some(id), Rep.Some(firstName),Rep.Some(lastName),Rep.Some(address),Rep.Some(phone_number),Rep.Some(age),Rep.Some(isDeleted)).shaped.<>({ r =>  import r._; _1.map(_ => Employee.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

  override val id: Rep[Long] = column[Long]("employeeid", O.AutoInc, O.PrimaryKey)
  val firstName: Rep[String] = column[String]("firstname")
  val lastName = column[String]("last_name")
  val address = column[String]("address")
  val phone_number = column[String]("phonenumber")
  val age = column[Long]("age")
  override val isDeleted: Rep[Boolean] = column[Boolean]("isdeleted")
  lazy val employeeTable = new TableQuery(tag => new EmployeeTable(tag))
}

case class Employee(id: Long, firstName: String, lastName: String, address: String, phone_number: String, age: Long, isDeleted: Boolean) extends BaseEntity