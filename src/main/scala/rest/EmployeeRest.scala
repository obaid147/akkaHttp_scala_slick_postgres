package rest

import Controllers.EmployeeControllerComponent
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction}
import Controllers.models.{PatchEmployee, PutEmployee}
import repositories.models.{Employee => DbEmployee}
import Controllers.models.Employee
import cats.data.Validated
import org.json4s.jackson.Json
import spray.json.{JsObject, JsValue, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class EmployeeRest(controller: EmployeeControllerComponent) extends Directives {

  implicit val system: ActorSystem = ActorSystem.create("Test")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val f: DefaultFormats.type = DefaultFormats

  import cats.data.Validated
  import cats.implicits._
  import spray.json._
  def validateKeys(json: JsValue): Validated[String, JsObject] =
    json match {
      case obj: JsObject =>
        val keys = Seq("first_name", "last_name", "address", "phone_number", "age")
        val missingKeys = keys.filterNot(obj.fields.contains)
        Validated.cond(missingKeys.isEmpty, obj, s"Object is missing required member(s): ${missingKeys.mkString(", ")}")
      case _ => "Not a JSON object".invalid
    }

  val routes: Route =
  path("employee" / JavaUUID) { id =>
      get {
        complete {
          controller.getEmployeeById(id.toString).map { result =>
            HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
          }
        }
      }
    } ~ path("employee") {
    post {
      headerValueByName("apiKey") { token => // Save an employee
        authorize(validateApiKey(token)) {
          entity(as[String]) { data =>
            val employeeTry = Try(data.parseJson)

            val employeeValidated = for {
              json <- employeeTry.toEither
              validated <- validateKeys(json).toEither
            } yield validated

            complete {
              employeeValidated match {
              case Right(_) =>
                controller.insertEmployeeController(data).map {
                  case Some(result) =>
                    HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
                  case None =>
                    HttpResponse(status = StatusCodes.BadRequest, entity = HttpEntity(MediaTypes.`application/json`, "Invalid Data"))
                }
              case Left(error) =>
                HttpResponse(status = StatusCodes.BadRequest, entity = HttpEntity(MediaTypes.`application/json`, error.toString))
              }
            }
          }
        }
      }
    }~ get {
      complete {
        controller.getAllEmployees().map { result => // get all employees
          HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
        }
      }
    }
  } ~ path("employee" / JavaUUID) { id => // delete an employee by updating IsDeleted Field
    delete {
      complete {
        controller.deleteById(id.toString).map {
          case result if result == 1 =>
            HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
          case _ =>
            HttpResponse(status = StatusCodes.BadRequest, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose("uuid not found"))))

        }
      }
    } ~  put {
      entity(as[String]) { data =>
        complete {
          //val emp = parse(data).extract[DbEmployee]
          controller.putEmployee(data).map {
            case result =>
            HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
            case 0 =>
              HttpResponse(status = StatusCodes.BadRequest, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(0))))
          }
        }
      }
    } ~ patch {
      entity(as[String]) { data =>
        complete {
          controller.patchEmployee(data).map {
            case result =>
              HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
            case 0 =>
              HttpResponse(status = StatusCodes.BadRequest, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(0))))
          }
        }
      }
    }
  } ~ path("transaction") {
      post {
        entity(as[String]) { data =>
          complete {
            controller.insertEmployeeTwice(data).map { res =>
              HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(res))))
            }
          }
        }
      }
  }
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