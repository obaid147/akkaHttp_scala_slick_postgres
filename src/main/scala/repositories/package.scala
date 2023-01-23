import core.BaseEntity
import spray.json._
import spray.json.DefaultJsonProtocol._
import java.sql.Timestamp


package object repositories {

   object models {

     case class Employee(
                          uuid: String,
                          firstName: String,
                          lastName: String,
                          address: String,
                          phoneNumber: String,
                          age: Long,
                          createdAt: Timestamp,
                          createdBy: Long,
                          isDeleted: Boolean = false,
                          updatedAt: Option[Timestamp] = None,
                          updatedBy: Option[Long] = None
                        ) extends BaseEntity
   }
}
