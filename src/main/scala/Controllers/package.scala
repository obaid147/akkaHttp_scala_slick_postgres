package object Controllers {

  object models {
    case class Employee(
                         firstName: String,
                         lastName: String,
                         address: String,
                         phoneNumber: String,
                         age: Long
                       )
  }
}



