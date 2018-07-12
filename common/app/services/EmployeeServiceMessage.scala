package services

import play.api.libs.json._

/**
  * Document: EmployeeServiceMessage.scala <br>
  * Description: Implicit JSON Serializers and Deserializers for EmployeeApi Messages <br>
  * Date: 2018/07/03 <br>
  * @author Jhon Edwin Zambrano Ortiz
  */
object EmployeeServiceMessage {

  // Employee endpoint
  final case class Employee(number: Int, code: String, name: String, registryDate: String, active: Boolean)

  implicit val employeeFormat: OFormat[Employee] = Json.format[Employee]

  // EmployeeResponse endpoint
  sealed trait EmployeeResponse
  final case class EmployeeSuccess(employees: Seq[Employee]) extends EmployeeResponse
  final case class EmployeeError(message: String) extends EmployeeResponse

  val employeeSuccessFormat: OFormat[EmployeeSuccess] = Json.format[EmployeeSuccess]
  val employeeErrorFormat: OFormat[EmployeeError]     = Json.format[EmployeeError]

  implicit object employeeResponseParser extends OFormat[EmployeeResponse]{
    def reads (in: JsValue):JsResult[EmployeeResponse] =
      (in \ "type").as[JsString] match {
        case JsString("employeeSuccess") => employeeSuccessFormat.reads(in)
        case JsString("employeeError")   => employeeErrorFormat.reads(in)
        case other                       => JsError(JsPath \ "type", s"Invalid type: $other")
      }

    def writes(out: EmployeeResponse):JsObject =
      out match {
        case res: EmployeeSuccess => employeeSuccessFormat.writes(res) ++ Json.obj("type" -> "EmployeeSuccess")
        case res: EmployeeError   => employeeErrorFormat.writes(res)   ++ Json.obj("type" -> "EmployeeError")
      }
  }

}
