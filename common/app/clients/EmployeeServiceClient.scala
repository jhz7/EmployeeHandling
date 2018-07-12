package clients

import javax.inject.Inject
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Document: EmployeeServiceClient.scala <br>
  * Description: Common client to call EmployeeApi <br>
  * Date: 2018/07/03 <br>
  * @author Jhon Edwin Zambrano Ortiz
  */
class EmployeeServiceClient @Inject() (ws: WSClient)(implicit val ec: ExecutionContext) extends ClientHelper {
  import services.EmployeeServiceMessage._

  /**
    * Method: request <br>
    * Description: Base request to invoke rest web service for Employee API <br>
    * Date: 2018/07/10 <br>
    * @param sessionId from HTTP request
    * @param uri to append on base uri
    * @return
    */
  private def request(sessionId: String, uri: String): WSRequest =
    ws.url("http://localhost:9002/employees" + uri).
      addHttpHeaders("Own-Auth" -> sessionId).
      withFollowRedirects(true)

  /**
    * Method: getEmployee <br>
    * Description: Calls rest web service to obtain an employee<br>
    * Date: 2018/07/10 <br>
    * @param document Employee legalId
    * @return
    */
  def getEmployee (sessionId: String, document: String): Future[EmployeeResponse] = {
    val url = s"/$document"
    request(sessionId, url).
      get().
      flatMap(parseResponse[EmployeeResponse])
  }

  /**
    * Method: getAllEmployees <br>
    * Description: Calls rest web service to obtain all employees <br>
    * Date: 2018/07/10 <br>
    * @return
    */
  def getAllEmployees(sessionId: String): Future[EmployeeResponse] = {
    val url = "/all"
    request(sessionId, url).
      get().
      flatMap(parseResponse[EmployeeResponse])
  }

  /**
    * Method: addEmployee <br>
    * Description: Calls rest web service to send an employee<br>
    * Date: 2018/07/10 <br>
    * @param employee to add
    * @return
    */
  def addEmployee(sessionId: String, employee: Employee): Future[EmployeeResponse] = {
    val url = "/add"
    request(sessionId, url).
      post(Json.toJson(employee)).
      flatMap(parseResponse[EmployeeResponse])
  }

  /**
    * Method: updateEmployee <br>
    * Description: Calls rest web service to update an employee <br>
    * Date: 2018/07/10 <br>
    * @param employee to update
    * @return
    */
  def updateEmployee(sessionId: String, employee: Employee): Future[EmployeeResponse] = {
    val url = "/update"
    request(sessionId, url).
      post(Json.toJson(employee)).
      flatMap(parseResponse[EmployeeResponse])
  }

  /**
    * Method: deleteEmployee <br>
    * Description: Calls rest web service to delete an employee based on his legalId<br>
    * Date: 2018/07/10 <br>
    * @param document Employee legalId
    * @return
    */
  def deleteEmployee(sessionId: String, document: String): Future[EmployeeResponse] = {
    val url = s"/$document/delete"
    request(sessionId, url).
      delete().
      flatMap(parseResponse[EmployeeResponse])
  }
}
