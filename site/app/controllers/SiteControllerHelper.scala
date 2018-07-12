package controllers

import play.api.mvc._

/**
  * Document: SiteControllerHelper.scala <br>
  * Description: Trait to aggregate new methods for Request and Result objects <br>
  * Date: 2018/07/05 <br>
  * @author Jhon Edwin Zambrano Ortiz
  */
trait SiteControllerHelper extends ControllerHelpers {

  /**
    * Class: RequestCookieOps <br>
    * Description: Implicit class that aggregate sessionCookieId method
    * to Request Objects to find specific cookie as an Option[String] <br>
    * @param request HTTP
    */
  implicit class RequestCookieOps(request: Request[AnyContent]) {
    def sessionCookieId: Option[String] =
      request.cookies.get("EmployeeAuth").map(_.value)
  }

  /**
    * Class: ResultCookieOps <br>
    * Description: Implicit class that aggregate withSessionCookie method
    * to Result Objects to add specific cookie <br>
    * @param result HTTP
    */
  implicit class ResultCookieOps(result: Result) {
    def withSessionCookie(sessionId: String): Result =
      result.withCookies(Cookie("EmployeeAuth", sessionId))
  }
}