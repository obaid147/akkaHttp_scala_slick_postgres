import spray.json._
import spray.json.DefaultJsonProtocol._

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


  }




}
