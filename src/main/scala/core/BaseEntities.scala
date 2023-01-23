package core

import slick.driver.PostgresDriver.api._

import java.util.UUID
import scala.reflect._

trait BaseEntity {
  val isDeleted: Boolean
  val uuid: String
}

/*trait WorkflowBaseEntity extends BaseEntity{
  val isApproved:Boolean
}*/

abstract class BaseTable[E: ClassTag](tag: Tag, schemaName: Option[String], tableName: String)
  extends Table[E](tag, schemaName, tableName) {
  val classOfEntity = classTag[E].runtimeClass
  val id: Rep[Long] = column[Long]("Id", O.PrimaryKey, O.AutoInc)
  val uuid: Rep[String] = column[String]("uuid")
  val isDeleted: Rep[Boolean] = column[Boolean]("IsDeleted", O.Default(false)) //soft delete
}


/*abstract class WorkflowBaseTable[E: ClassTag](tag: Tag, schemaName: Option[String], tableName: String)
  extends BaseTable[E](tag, schemaName, tableName) {

  val isApproved: Rep[Boolean] = column[Boolean]("IsApproved")

}*/
