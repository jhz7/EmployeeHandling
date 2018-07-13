package controllers

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import services.AuthServiceMessage._
import clients.AuthServiceClient

/**
  * Document: SiteAuthController.scala <br>
  * Description: Exposes an API Rest for login users <br>
  * Date: 2018/07/10 <br>
  * @author Jhon Edwin Zambrano Ortiz
  * @param authClient Web service client implementation for AuthApi
  * @param localExecutionContext Default Play Thread ool
  * @param controllerComponents Components for baseController
  */
class SiteAuthController @Inject() (authClient: AuthServiceClient,
                                   implicit val localExecutionContext: ExecutionContext,
                                   val controllerComponents: ControllerComponents)
  extends BaseController with SiteControllerHelper with play.api.i18n.I18nSupport{

  // Base form by login data validation
  private val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginRequest.apply)(LoginRequest.unapply)
  )

  /**
    * Method: login <br>
    * Description: Render a HTML view with a login form <br>
    * Date: 2018/07/10 <br>
    * @return
    */
  def login: Action[AnyContent] = Action(parse.anyContent) { implicit req =>
    Ok(views.html.login(loginForm))
  }

  /**
    * Method: logout <br>
    * Description: Makes a request to AuthApi to delete the sessionId <br>
    * Date: 2018/07/12 <br>
    * @return
    */
  def logout: Action[AnyContent] = Action.async(parse.anyContent) { implicit req =>
    req.sessionCookieId match {
      case Some(sessionId) => authClient.logout(sessionId) map {
        case rsp: LogoutSuccess => redirectToLogin
        case rsp: LogoutError   => redirectToIndex
      }
      case None            => Future.successful(redirectToLogin)
    }
  }

  /**
    * Method: submitLogin <br>
    * Description: Validates an entry loginForm and sends it to AuthApi <br>
    * Date: 2018/07/10 <br>
    * @return
    */
  def submitLogin: Action[AnyContent] = Action.async(parse.anyContent) { implicit request =>
    loginForm.bindFromRequest().fold(
      hasErrors = { loginForm =>
        Future.successful(BadRequest(views.html.login(loginForm)))
      },
      success = { loginReq =>
        authClient.login(loginReq) map {
          case res: LoginSuccess => redirectToIndex.withSessionCookie(res.sessionId)
          case _ => BadRequest(views.html.login(loginForm.withError("username", "User not found or password incorrect")))
        }
      }
    )
  }

  // Helpers to redirect
  private val redirectToLogin: Result =
    Redirect(routes.SiteAuthController.login())

  private val redirectToIndex: Result =
    Redirect(routes.SiteEmployeeController.index())
}
