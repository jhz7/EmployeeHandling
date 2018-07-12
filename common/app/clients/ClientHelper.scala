package clients

import play.Logger
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import play.api.libs.ws.WSResponse

import scala.concurrent.Future

/**
  * Document: ClientHelper.scala <br>
  * Description: Helper for client implementations <br>
  * Date: 2018/07/10 <br>
  * @author Jhon Edwin Zambrano Ortiz
  */
trait ClientHelper {

  /**
    * Method: parseResponse <br>
    * Description: Cast WSResponse to response message object <br>
    * @param response from web service call
    * @param reads implicit JSON reader
    * @tparam A type parameter
    * @return Future[A]
    */
  def parseResponse[A](response: WSResponse)(implicit reads: Reads[A]): Future[A] = {
    //Logger.debug(s"Parsing API response using $reads: ${response.json}")
    Json.fromJson[A](response.json) match {
      case JsSuccess(value, _) => Future.successful(value)
      case error: JsError =>
        Future.failed(InvalidResponseException(response, error))
    }
  }

  /**
    * Class: InvalidResponseException
    * @param response from web service call
    * @param jsError obtain to try deserializa JSON objects
    */
  case class InvalidResponseException( response: WSResponse, jsError: JsError)
    extends Exception(s"BAD API response:\n${response.json}\n${jsError}")
}
