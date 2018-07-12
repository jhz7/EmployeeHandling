package controllers

import javax.inject.Inject
import play.api.db.{Database, NamedDatabase}
import play.api.libs.json._
import play.api.mvc._
import services.AuthServiceMessage._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Document: AuthApiController.scala <br>
  * Description: Rest API to handle all about the application authentication <br>
  * Date: 2018/07/10 <br>
  * @param db Data Base instance
  * @param controllerComponents Components for BaseController
  * @param ec Default Play Thread Pool
  */
class AuthApiController @Inject() (@NamedDatabase("develop") db: Database,
                                   val controllerComponents: ControllerComponents,
                                   implicit val ec: ExecutionContext)
  extends BaseController with ControllerHelpers{

  // AuthApiService instance
  private val authService = services.AuthApiService(db)

  /**
    * Method: login <br>
    * Description: Performs the user login request <br>
    * Date: 2018/07/10 <br>
    * @return
    */
  def login: Action[AnyContent] = Action.async(parse.anyContent) { implicit req =>
    req.jsonAs[LoginRequest] match {
      case JsSuccess(loginRequest, _) =>
        authService.login(loginRequest) map {
          case res: LoginSuccess      => Ok(Json.toJson(res))
          case res: UserNotFound      => BadRequest(Json.toJson(res))
          case res: PasswordIncorrect => BadRequest(Json.toJson(res))
        }
      case err: JsError => Future.successful(BadRequest(ErrorJson(err)))
    }
  }

  /**
    * Method: whoami <br>
    * Description: Verifies the existence for sessionId parameter in application <br>
    * Date: 2018/07/10 <br>
    * @param sessionId to verify
    * @return
    */
  def whoami(sessionId: String): Action[AnyContent] = Action.async(parse.anyContent) { implicit req =>
    authService.whoami(sessionId) match {
      case res: Credentials     => Future.successful(Ok(Json.toJson(res)))
      case res: SessionNotFound => Future.successful(NotFound(Json.toJson(res)))
    }
  }

  /**
    * Method: logout <br>
    * Description: Performs the delete of an active sessionId <br>
    * Date: 2018/07/10 <br>
    * @param sessionId to delete
    * @return
    */
  def logout(sessionId: String) = Action { req =>
    authService.logout(sessionId)
    Ok(Json.toJson(LogoutSuccess(sessionId)))
  }
}
