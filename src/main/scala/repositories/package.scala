import core.BaseEntity
import org.joda.time.DateTime
import spray.json._
import spray.json.DefaultJsonProtocol._

import java.sql.Timestamp
import java.util.UUID


package object repositories {

   object models {

     case class Employee(
                          uuid: UUID,
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
