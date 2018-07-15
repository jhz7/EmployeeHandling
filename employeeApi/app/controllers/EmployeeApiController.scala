package controllers

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import play.api.mvc._
import play.api.db.{Database, NamedDatabase}
import play.api.libs.json._

import clients.AuthServiceClient
import services.AuthServiceMessage._
import services.EmployeeServiceMessage._

/**
  * Document: EmployeeApiController.scala <br>
  * Description: Rest API to handle http requests to data base. <br>
  * Date: 2018-07-05 <br>
  * @author Jhon Edwin Zambrano Ortiz
  * @param db Data base instance
  * @param localExecutionContext Default Play Thread Pool
  * @param controllerComponents Components for baseController
  */
class EmployeeApiController @Inject()(@NamedDatabase("develop") db: Database,
                                      implicit val localExecutionContext: ExecutionContext,
                                      authClient: AuthServiceClient,
                                      val controllerComponents: ControllerComponents)
  extends BaseController with ControllerHelpers{

  // EmployeeApiService instance
  private val employeeService = services.EmployeeApiService(db)

  /**
    * Method: get <br>
    * Description: Rest service to return an employee requested on data base <br>
    * Date: 2018/07/05 <br>
    * @param document Employee legalId
    * @return Employee
    */
  def get(document: String): Action[AnyContent] = Action.async(parse.anyContent) { req =>
    withAuthenticatedUser(req) flatMap {
      case rsp: Credentials => employeeService.employee(document) map {
        employeeResponse => Ok(Json.toJson[EmployeeResponse](employeeResponse))
      }
      case _ => Future.successful(
        Ok(Json.toJson[EmployeeResponse](EmployeeError(SESSION_ID_NOT_FOUND)))
      )
    }
  }

  /**
    * Method: delete <br>
    * Description: Rest service to delete an employee requested on data base <br>
    * Date: 2018/07/05 <br>
    * @param document Employee legalId
    * @return Ok
    */
  def delete(document: String): Action[AnyContent] = Action.async(parse.anyContent) { req =>
    withAuthenticatedUser(req) flatMap {
      case rsp: Credentials => employeeService.delete(document) map {
        employeeResponse =>
          Ok(Json.toJson[EmployeeResponse](employeeResponse))
      }
      case _ => Future.successful(
        Ok(Json.toJson[EmployeeResponse](EmployeeError(SESSION_ID_NOT_FOUND)))
      )
    }
  }

  /**
    * Method: update <br>
    * Description: Rest service to update an employee on data base <br>
    * Date: 2018/07/05 <br>
    * @return Ok
    */
  def update: Action[AnyContent]= Action.async(parse.anyContent) { req =>
    withAuthenticatedUser(req) flatMap {
      case rsp: Credentials =>
        req.jsonAs[Employee] match {
          case JsSuccess(employee, _) =>
            employeeService.update(employee) map { employeeResponse =>
              Ok(Json.toJson[EmployeeResponse](employeeResponse))
          }
          case err: JsError => Future.successful(BadRequest(ErrorJson(err)))
        }
      case _ => Future.successful(
        Ok(Json.toJson[EmployeeResponse](EmployeeError(SESSION_ID_NOT_FOUND)))
      )
    }
  }

  /**
    * Method: add <br>
    * Description: Rest service to add an employee on data base <br>
    * Date: 2018/07/05 <br>
    * @return Ok
    */
  def add: Action[AnyContent]= Action.async(parse.anyContent) { req =>
    withAuthenticatedUser(req) flatMap {
      case rsp: Credentials =>
        req.jsonAs[Employee] match {
          case JsSuccess(employee, _) =>
            employeeService.add(employee) map { employeeResponse =>
              Ok(Json.toJson[EmployeeResponse](employeeResponse))
          }
          case err: JsError => Future.successful(BadRequest(ErrorJson(err)))
        }
      case _ => Future.successful(
        Ok(Json.toJson[EmployeeResponse](EmployeeError(SESSION_ID_NOT_FOUND)))
      )
    }

  }

  /**
    * Method: getAll <br>
    * Description: Rest service to return all employees requested to data base <br>
    * Date: 2018/07/05 <br>
    * @return All employees registered
    */
  def getAll: Action[AnyContent] = Action.async(parse.anyContent) { req =>
    withAuthenticatedUser(req) flatMap {
      case res: Credentials => employeeService.allEmployees map {
        employeeResponse => Ok(Json.toJson[EmployeeResponse](employeeResponse))
      }
      case _ => Future.successful(
        Ok(Json.toJson[EmployeeResponse](EmployeeError(SESSION_ID_NOT_FOUND)))
      )
    }
  }

  /**
    * Method: withAuthenticatedUser <br>
    * Description: Gets a incoming sessionId in request header and sends to
    * AuthApi to verify its existence <br>
    * Date: 2018/07/11 <br>
    * @param request HTTP
    * @return
    */
  private def withAuthenticatedUser(request: Request[AnyContent]): Future[WhoamiResponse] =
    request.headers.get("Own-Auth") match {
      case Some(sessionId) => authClient.whoami(sessionId)
      case None            => Future.successful(SessionNotFound(SESSION_ID_NOT_FOUND))
    }
}
