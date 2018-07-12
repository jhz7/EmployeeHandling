package controllers

import play.api.mvc._
import play.api.libs.json._

/**
  * Document: ControllerHelpers.scala <br>
  * Description: Trait to aggregate new methods for Request objects <br>
  * Date: 2018/07/05 <br>
  * @author Jhon Edwin Zambrano Ortiz
  */
trait ControllerHelpers extends Results {

  /**
    * Class: RequestJsonOps <br>
    * Description: Implicit class that aggregate jsonAs method
    * to Request Objects to deserialize JSON objects <br>
    * Date: 2018/07/05 <br>
    * @param request HTTP
    */
  implicit class RequestJsonOps(request: Request[AnyContent]) {
    def jsonAs[A](implicit reads: Reads[A]): JsResult[A] =
      request.body.asJson match {
        case Some(json) => Json.fromJson[A](json)
        case None       => JsError(JsPath, "No JSON specified")
      }
  }
}
