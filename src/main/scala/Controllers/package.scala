import spray.json._
import spray.json.DefaultJsonProtocol._
import java.sql.Timestamp


package object Controllers {


  object models {
    case class Employee(
                         firstName: String,
                         lastName: String,
                         address: String,
                         phoneNumber: String,
                         age: Long
                       )

    object EmployeeJsonProtocol extends DefaultJsonProtocol {
      implicit val employeeFormat = jsonFormat(
        Employee,
        "first_name",
        "last_name",
        "address",
        "phone_number",
        "age")
    }

    case class PatchEmployee(uuid: String, firstName: String, address:String, age: Long)

    object PatchEmployeeJsonProtocol extends DefaultJsonProtocol {
      implicit val patchEmployeeFormat = jsonFormat(
        PatchEmployee,
        "uuid",
        "first_name",
        "address",
        "age")
    }

    case class PutEmployee(uuid: String,
                           firstName: String,
                           lastName: String,
                           address: String,
                           phoneNumber: String,
                           age: Long)

    object EmployeePutJsonProtocol extends DefaultJsonProtocol {
      implicit object TimestampFormat extends JsonFormat[Timestamp] {
        def write(obj: Timestamp) = JsNumber(obj.getTime)

        def read(json: JsValue) = json match {
          case JsNumber(time) => new Timestamp(time.toLong)
          case _ => throw DeserializationException("Timestamp expected")
        }
      }

      implicit val employeePutRepoFormat = jsonFormat(
        PutEmployee,
        "uuid",
        "first_name",
        "last_name",
        "address",
        "phone_number",
        "age"
      )
    }

  }
}
