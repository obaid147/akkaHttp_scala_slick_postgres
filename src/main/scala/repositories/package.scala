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

     /*object EmployeeRepoJsonProtocol extends DefaultJsonProtocol {
       implicit object TimestampFormat extends JsonFormat[Timestamp] {
         def write(obj: Timestamp) = JsNumber(obj.getTime)

         def read(json: JsValue) = json match {
           case JsNumber(time) => new Timestamp(time.toLong)
           case _ => throw DeserializationException("Timestamp expected")
         }
       }
       implicit val employeeRepoFormat = jsonFormat(
         Employee,
         "uuid",
         "first_name",
         "last_name",
         "address",
         "phone_number",
         "age",
         "created_at",
         "created_by",
         "is_deleted",
         "updated_at",
         "updated_by"
       )
     }*/

   }
}
