package controllers

import clients.EmployeeServiceClient
import javax.inject.Inject
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import services.EmployeeServiceMessage._
import services.AuthServiceMessage._

/**
  * Document: SiteEmployeeController.scala <br>
  * Description: Controller that expose methods to handle http request from client application <br>
  * Date: 2018/07/05 <br>
  * @param employeeClient Web service client implementation for EmployeeApi
  * @param localExecutionContext Default Play Thread pool
  * @param controllerComponents Components for baseController
  */
class SiteEmployeeController @Inject()(employeeClient: EmployeeServiceClient,
                                       implicit val localExecutionContext: ExecutionContext,
                                       val controllerComponents: ControllerComponents)
  extends BaseController with play.api.i18n.I18nSupport with SiteControllerHelper
{

  // Base form by employee data validation
  private val employeeForm = Form(
    mapping(
      "number"       -> number,
      "code"         -> nonEmptyText,
      "name"         -> nonEmptyText,
      "registryDate" -> nonEmptyText,
      "active"       -> boolean
    )(Employee.apply)(Employee.unapply)
  )

  /**
    * Method: index <br>
    * Description: index method is used to render a view with all employees <br>
    * Date: 2018/07/05 <br>
    * @return HTTP response
    */
  def index: Action[AnyContent] = Action.async(parse.anyContent) {
    implicit req =>
      withSessionCookieId(req){ sessionId =>
        employeeClient.getAllEmployees(sessionId).map {
          case EmployeeSuccess(employees) => Ok(views.html.employees(employees))
          case EmployeeError(msg) if msg == SESSION_ID_NOT_FOUND => redirectToLogin
          case EmployeeError(msg) => NotFound(msg)
        }
      }
  }

  /**
    * Method: edit <br>
    * Description: edit method return a form with all data of employee to edit <br>
    * Date: 2018/07/05
    * @param document Employee legalId
    * @return View with employee to edit
    */
  def edit(document: String): Action[AnyContent] = Action.async(parse.anyContent) {
    implicit req =>
      withSessionCookieId(req) { sessionId =>
        employeeClient.getEmployee(sessionId, document) map {
          case EmployeeSuccess(employees) => Ok(views.html.edit(employeeForm.fill(employees.head)))
          case EmployeeError(msg) if msg == SESSION_ID_NOT_FOUND => redirectToLogin
          case EmployeeError(msg) => NotFound(msg)
        }
      }
  }

  /**
    * Method: add <br>
    * Description: add method return an empty form fill and add an employee data <br>
    * @return View with an empty form to fill employee data
    */
  def add: Action[AnyContent] = Action.async(parse.anyContent) {
    implicit req =>
      withSessionCookieId(req){ sessionId =>
        Future.successful(Ok(views.html.add(employeeForm)))
      }
  }

  /**
    * Method: submitUpdate <br>
    * Description: This method validates a request form and sends data to data base to update it <br>
    * Date: 2018/07/09 <br>
    * @return View response
    */
  def submitUpdate: Action[AnyContent] = Action.async(parse.anyContent) {
    implicit req =>
      withSessionCookieId(req) { sessionId =>
        employeeForm.bindFromRequest().fold(
          hasErrors = { badEmployeeForm: Form[Employee] =>
            Future.successful(Ok(views.html.edit(badEmployeeForm)))
          },
          success = { employee: Employee =>
            employeeClient.updateEmployee(sessionId, employee) map {
              case res: EmployeeSuccess => redirectToIndex
              case EmployeeError(msg) if msg == SESSION_ID_NOT_FOUND => redirectToLogin
              case EmployeeError(msg) => NotFound(msg)
            }
          }
        )
      }
  }

  /**
    * Method: submitAdd <br>
    * Description: This method validates a request form and sends data to data base to save it <br>
    * Date: 2018/07/09 <br>
    * @return View response
    */
  def submitAdd: Action[AnyContent] = Action.async(parse.anyContent) {
    implicit req =>
      withSessionCookieId(req){ sessionId =>
        employeeForm.bindFromRequest().fold(
          hasErrors = { badEmployeeForm =>
            Future.successful(Ok(views.html.add(badEmployeeForm)))
          },
          success = { employee =>
            employeeClient.addEmployee(sessionId, employee) map {
              case res: EmployeeSuccess => redirectToIndex
              case EmployeeError(msg) if msg == SESSION_ID_NOT_FOUND => redirectToLogin
              case EmployeeError(msg) => NotFound(msg)
            }
          }
        )
      }
  }

  /**
    * Method: delete <br>
    * Description: this method makes a request to data base to delete an employee based on
    * his document. <br>
    * Date: 2018/07/05 <br>
    * @param document Employee legalId
    * @return Index controller request
    */
  def delete(document: String): Action[AnyContent] = Action.async(parse.anyContent) {
    implicit req =>
      withSessionCookieId(req){ sessionId =>
        employeeClient.deleteEmployee(sessionId, document) map {
          _ => redirectToIndex
        }
      }
  }

  /**
    * Method: redirectToIndex <br>
    * Description: Private utility to redirect to index controller <br>
    * Date: 2018/07/05 <br>
    */
  private val redirectToIndex: Result =
    Redirect(routes.SiteEmployeeController.index())

  /**
    * Method: redirectToLogin <br>
    * Description: Private utility to redirect to login controller <br>
    * Date: 2018/07/10 <br>
    */
  private val redirectToLogin: Result =
    Redirect(routes.SiteAuthController.login())

  /**
    * Method: withSessionCookieId <br>
    * Description: Validates the existence of sessionId cookie on Request <br>
    * Date: 2018/07/10 <br>
    * @param request HTTP
    * @param func Perform sessionId and produce a Future[Result]
    * @return
    */
  private def withSessionCookieId(request: Request[AnyContent])(func: String => Future[Result]): Future[Result] =
    request.sessionCookieId match {
      case Some(sessionId) => func(sessionId)
      case None            => Future.successful(redirectToLogin)
    }
}
