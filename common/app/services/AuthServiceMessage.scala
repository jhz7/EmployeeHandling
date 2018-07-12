package services

import play.api.libs.json._

/**
  * Document: AuthServiceMessage.scala <br>
  * Description: Implicit JSON Serializers and Deserializers for AuthApi Messages <br>
  * Date: 2018/07/10 <br>
  * @author Jhon Edwin Zambrano Ortiz
  */
object AuthServiceMessage {
  type Username  = String
  type Password  = String
  type SessionId = String

  // Login endpoint

  final case class LoginRequest(username: Username, password: Password)

  implicit val LoginRequestFormat: OFormat[LoginRequest] = Json.format[LoginRequest]

  sealed trait LoginResponse
  final case class LoginSuccess(sessionId: SessionId) extends LoginResponse
  final case class UserNotFound(username: Username) extends LoginResponse
  final case class PasswordIncorrect(username: Username) extends LoginResponse

  val LoginSuccessFormat: OFormat[LoginSuccess]           = Json.format[LoginSuccess]
  val UserNotFoundFormat: OFormat[UserNotFound]           = Json.format[UserNotFound]
  val PasswordIncorrectFormat: OFormat[PasswordIncorrect] = Json.format[PasswordIncorrect]

  implicit object LoginResponseFormat extends OFormat[LoginResponse] {
    def reads(in: JsValue):JsResult[LoginResponse] =
      (in \ "type").as[JsString] match {
        case JsString("LoginSuccess")      => LoginSuccessFormat.reads(in)
        case JsString("UserNotFound")      => UserNotFoundFormat.reads(in)
        case JsString("PasswordIncorrect") => PasswordIncorrectFormat.reads(in)
        case other                         => JsError(JsPath \ "type", s"Invalid type: $other")
      }

    def writes(in: LoginResponse):JsObject = in match {
      case in: LoginSuccess      => LoginSuccessFormat.writes(in)      ++ Json.obj("type" -> "LoginSuccess")
      case in: UserNotFound      => UserNotFoundFormat.writes(in)      ++ Json.obj("type" -> "UserNotFound")
      case in: PasswordIncorrect => PasswordIncorrectFormat.writes(in) ++ Json.obj("type" -> "PasswordIncorrect")
    }
  }

  // Logout endpoint

  sealed trait LogoutResponse
  final case class LogoutSuccess(sessionId: SessionId) extends LogoutResponse
  final case class LogoutError(message: String) extends LogoutResponse

  val LogoutSuccessFormat: OFormat[LogoutSuccess] = Json.format[LogoutSuccess]
  val LogoutErrorFormat: OFormat[LogoutError]     = Json.format[LogoutError]

  implicit object LogoutResponseFormat extends OFormat[LogoutResponse] {
    def reads(in: JsValue):JsResult[LogoutResponse] =
      (in \ "type").as[JsString] match {
        case JsString("logoutSuccess") => LogoutSuccessFormat.reads(in)
        case JsString("logoutError")   => LogoutErrorFormat.reads(in)
        case other                     => JsError(JsPath \ "type", s"Invalid type: $other")
      }

    def writes (in: LogoutResponse):JsObject =
      in match {
        case in: LogoutSuccess => LogoutSuccessFormat.writes(in) ++ Json.obj("type" -> "LogoutSuccess")
        case in: LogoutError   => LogoutErrorFormat.writes(in)   ++ Json.obj("type" -> "LogoutError")
      }
  }

  // Whoami endpoint ----------------------------

  final val SESSION_ID_NOT_FOUND: String = "SessionId NOT found!"

  sealed trait WhoamiResponse
  final case class Credentials(sessionId: SessionId, username: Username) extends WhoamiResponse
  final case class SessionNotFound(sessionId: SessionId) extends WhoamiResponse

  val CredentialsFormat: OFormat[Credentials]         = Json.format[Credentials]
  val SessionNotFoundFormat: OFormat[SessionNotFound] = Json.format[SessionNotFound]

  implicit object WhoamiResponseFormat extends OFormat[WhoamiResponse] {
    def reads(in: JsValue):JsResult[WhoamiResponse] =
      (in \ "type").as[JsString] match {
        case JsString("Credentials")     => CredentialsFormat.reads(in)
        case JsString("SessionNotFound") => SessionNotFoundFormat.reads(in)
        case other                       => JsError(JsPath \ "type", s"Invalid type: $other")
      }

    def writes(in: WhoamiResponse):JsObject = in match {
      case in: Credentials     => CredentialsFormat.writes(in)     ++ Json.obj("type" -> "Credentials")
      case in: SessionNotFound => SessionNotFoundFormat.writes(in) ++ Json.obj("type" -> "SessionNotFound")
    }
  }

}
