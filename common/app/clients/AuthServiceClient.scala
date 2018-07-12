package clients

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Document: AuthServiceClient.scala <br>
  * Description: Common client to call AuthApi <br>
  * Date: 2018/07/10 <br>
  * @author Jhon Edwin Zambrano Ortiz
  */
@Singleton
class AuthServiceClient @Inject() (ws: WSClient)(implicit val ec: ExecutionContext) extends ClientHelper{
  import services.AuthServiceMessage._

  /**
    * Method: request <br>
    * Description: Base request to invoke rest web service for Authentication API <br>
    * Date: 2018/07/10 <br>
    * @param uri URI to invoke
    * @return WSRequest
    */
  private def request(uri: String): WSRequest =
    ws.url(s"http://localhost:9001$uri")

  /**
    * Method: login <br>
    * Description: Calls rest web service to send login parameters <br>
    * Date: 2018/07/190 <br>
    * @param req Request login parameters
    * @return Future[LoginResponse]
    */
  def login(req: LoginRequest): Future[LoginResponse] =
    request("/login").post(Json.toJson(req)).flatMap(parseResponse[LoginResponse])

  /**
    * Method: whoami <br>
    * Description: Calls rest web service to ask for credentials for actual session <br>
    * Date: 2018/07/10 <br>
    * @param sessionId ID of actual session
    * @return Future[WhoamiResponse]
    */
  def whoami(sessionId: String): Future[WhoamiResponse] =
    request(s"/whoami/$sessionId").get().flatMap(parseResponse[WhoamiResponse])

  /**
    * Method: logout <br>
    * Description: Calls rest web service to delete the sessionId parameter <br>
    * Date: 2018/07/12 <br>
    * @param sessionId to log out
    * @return Future[LogoutResponse]
    */
  def logout(sessionId: String): Future[LogoutResponse] =
    request(s"/logout/$sessionId").get().flatMap(parseResponse[LogoutResponse])
}
