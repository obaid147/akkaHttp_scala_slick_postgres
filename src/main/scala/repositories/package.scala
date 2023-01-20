import core.BaseEntity
import org.joda.time.DateTime

import java.sql.Timestamp


package object repositories {

   object models {
     case class Employee(
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
