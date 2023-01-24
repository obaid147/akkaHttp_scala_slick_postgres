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
                           age: Long,
                           isDeleted: Boolean)

    object EmployeePutJsonProtocol extends DefaultJsonProtocol {

      implicit val employeePutRepoFormat = jsonFormat(
        PutEmployee,
        "uuid",
        "first_name",
        "last_name",
        "address",
        "phone_number",
        "age",
        "is_deleted"
      )
    }

  }
}
