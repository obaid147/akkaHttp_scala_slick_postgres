package Entities

import core.{BaseEntity, BaseTable}
import repositories.models.Employee
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{Rep, TableQuery, Tag}

import java.sql.Timestamp
import java.util.UUID
import scala.concurrent.Future

class EmployeeTable(_tableTag: Tag) extends BaseTable[Employee](_tableTag, Some("public"), "employee") {
  def * = (uuid, firstName, lastName, address, phone_number, age,
    createdAt, createdBy, isDeleted, updatedAt, updatedBy) <> (Employee.tupled, Employee.unapply)

  def ? = (Rep.Some(uuid), Rep.Some(firstName),Rep.Some(lastName),Rep.Some(address),
    Rep.Some(phone_number),Rep.Some(age),Rep.Some(createdAt),
    Rep.Some(createdBy),Rep.Some(isDeleted),Rep.Some(updatedAt),Rep.Some(updatedBy)).shaped.<>({ r =>  import r._; _1.map(_ =>
    Employee.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get , _9.get,
      _10.get, _11.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

  override val id: Rep[Long] = column[Long]("employeeid", O.AutoInc, O.PrimaryKey)
  override val uuid: Rep[String] = column[String]("uuid")
  val firstName: Rep[String] = column[String]("firstname")
  val lastName = column[String]("last_name")
  val address = column[String]("address")
  val phone_number = column[String]("phonenumber")
  val age = column[Long]("age")
  override val isDeleted: Rep[Boolean] = column[Boolean]("isdeleted")

  val createdAt: Rep[Timestamp] = column[Timestamp]("created_at", O.SqlType("timestamp not null default now()"))
  val createdBy: Rep[Long] = column[Long]("created_by", O.SqlType("int"))
  val updatedAt: Rep[Option[Timestamp]] = column[Option[Timestamp]]("updated_at", O.SqlType("timestamp"))
  val updatedBy: Rep[Option[Long]] = column[Option[Long]]("updated_by")

  lazy val employeeTable = new TableQuery(tag => new EmployeeTable(tag))
}


object database