package rest

import Controllers.EmployeeControllerComponent
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction}

import scala.concurrent.ExecutionContext.Implicits.global

class EmployeeRest(controller: EmployeeControllerComponent) extends Directives {

  implicit val system = ActorSystem.create("Test")
  implicit val materializer = ActorMaterializer()
  implicit val f = DefaultFormats


  val routes =
  /*(path("employee" / IntNumber) | parameter("id".as[Int])) { id => // getById
      get {
        complete {
          controller.getEmployeeById(id).map { result =>
            HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
          }
        }
      }
    } ~*/ path("employee") {
    post {
      headerValueByName("apiKey") { token => // Save an employee
        authorize(validateApiKey(token)) {
          entity(as[String]) { data =>
            complete {
              controller.insertEmployeeController(data).map { result =>
                HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
              }
            }
          }
        }
      }
    } ~ get {
      complete {
        controller.getAllEmployees().map { result => // get all employees
          //println(result)
          HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
        }
      }
    }
  }/* ~ (path("employee" / IntNumber) | parameter("id".as[Int]))  { id => // delete an employee by updating IsDeleted Field
      delete {
        complete {
          controller.deleteById(id).map { result =>
            HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
          }
        }
      } ~ put {
        entity(as[String]) { data =>
          complete {
            val emp = parse(data).extract[Employee]
            controller.updateById(id, emp).map { result =>
              HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
            }
          }
        }
      }
    }
*/
    /*~ path("employee" / "employeeId" / LongNumber) { id =>
     delete {
       complete {
         ImplEmployeeRepository.deleteRecord(id).map { result =>
           HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
         }
       }
     } ~ put {
       entity(as[String]) { data =>
         complete {
           val dd = parse(data).extract[Employee]
           ImplEmployeeRepository.updateEmployee(id, dd).map { result =>
             HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
           }
         } /*recover {
             case ex => val (statusCode, message) = handleErrorMessages(ex)
               if (statusCode == StatusCodes.NoContent)
                 HttpResponse(status = statusCode)
               else
                 HttpResponse(status = statusCode, entity = HttpEntity(MediaTypes.`application/json`, message.asJson))
           }*/
       }
     }
   } ~ pathPrefix("employeeByName") {
     path(Rest) { name =>
       get {
         complete {
           ImplEmployeeRepository.getEmployeeByName(name).map { result =>
             HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
           }
         }
       }
     }
   }
  }*/

    def validateApiKey(apiKey: String): Boolean = {
      //println("----aa---")
      //val apiKeysJson = ConfigFactory.load().getString("apiKeys").trim
      // println("----aa---")

      //add other validations here
      true
    }

    case class ErrorMessageContainer(message: String, ex: Option[String] = None, code: String = "")
}