package controllers

import org.scalatest.MustMatchers
import org.scalatestplus.play._
import play.api.libs.json._
import services.EmployeeServiceMessage._

/**
  * Document: EmployeeMessageTest.scala<br>
  * Description: Allows us testing the created wrappers between JSON objects and Scala objects.<br>
  * Date: 2018/07/04 <br>
  * @author Jhon Edwin Zambrano Ortiz
  */
class EmployeeMessageTest extends PlaySpec with MustMatchers{

  /**
    * Test: EmployeeFormatWrite
    */
  "EmployeeFormatWrite" must {
    "parse from scala object to JSON" in {
      val employee = new Employee(Some(1), "1127064277", "Jhon Zambrano", "2018-07-04", true)
      val jsonObj = Json.obj(
        "number" -> 1,
        "code" -> "1127064277",
        "name" -> "Jhon Zambrano",
        "registryDate" -> "2018-07-04",
        "active" -> true
      )

      Json.toJson(employee) mustEqual(jsonObj)
    }
  }

  /**
    * Test: EmployeeFormatRead
    */
  "EmployeeFormatRead" must {
    "parse from JSON to scala object" in {
      val employee = new Employee(Some(1), "1127064277", "Jhon Zambrano", "2018-07-04", true)
      val jsonObj = Json.obj(
        "number" -> 1,
        "code" -> "1127064277",
        "name" -> "Jhon Zambrano",
        "registryDate" -> "2018-07-04",
        "active" -> true
      )

      Json.fromJson[Employee](jsonObj).asOpt mustEqual(Some(employee))
    }
  }

  /**
    * Test: EmployeeSuccessFormatWrite
    */
  "EmployeeSuccessFormatWrite" must {
    "parse from scala object to JSON" in {
      val employee1 = new Employee(Some(1), "1127064277", "Jhon Zambrano", "2018-07-04", true)
      val employee2 = new Employee(Some(2), "1127064277", "Jhon Zambrano", "2018-07-04", true)

      val employees = new EmployeeSuccess(Seq(employee1, employee2))

      val jsonObj = Json.obj(
        "employees" -> Json.arr(
          Json.obj(
            "number" -> 1,
            "code" -> "1127064277",
            "name" -> "Jhon Zambrano",
            "registryDate" -> "2018-07-04",
            "active" -> true
          ),
          Json.obj(
            "number" -> 2,
            "code" -> "1127064277",
            "name" -> "Jhon Zambrano",
            "registryDate" -> "2018-07-04",
            "active" -> true
          )
        ),
        "type" -> "employeeSuccess"
      )


      Json.toJson(employees) mustEqual(jsonObj)
    }
  }

  /**
    * Test: EmployeeSuccessFormatRead
    */
  "EmployeeSuccessFormatRead" must {
    "parse from JSON to scala object" in {
      val employee1 = new Employee(Some(1), "1127064277", "Jhon Zambrano", "2018-07-04", true)
      val employee2 = new Employee(Some(2), "1127064277", "Jhon Zambrano", "2018-07-04", true)

      val employees = new EmployeeSuccess(Seq(employee1, employee2))

      val jsonObj = Json.obj(
        "employees" -> Json.arr(
          Json.obj(
            "number" -> 1,
            "code" -> "1127064277",
            "name" -> "Jhon Zambrano",
            "registryDate" -> "2018-07-04",
            "active" -> true
          ),
          Json.obj(
            "number" -> 2,
            "code" -> "1127064277",
            "name" -> "Jhon Zambrano",
            "registryDate" -> "2018-07-04",
            "active" -> true
          )
        ),
        "type" -> "employeeSuccess"
      )

      Json.fromJson[EmployeeResponse](jsonObj).asOpt mustEqual(Some(employees))
    }
  }

  /**
    * Test: EmployeeErrorFormatWrite
    */
  "EmployeeErrorFormatWrite" must {
    "parse from scala object to JSON" in {
      val employee = new EmployeeError("1127064277")

      val jsonObj = Json.obj(
        "document" -> "1127064277",
        "type" -> "employeeError"
      )

      Json.toJson(employee) mustEqual(jsonObj)
    }
  }

  /**
    * Test: EmployeeErrorFormatRead
    */
  "EmployeeErrorFormatRead" must {
    "parse from JSON to scala object" in {
      val employee = new EmployeeError("1127064277")

      val jsonObj = Json.obj(
        "document" -> "1127064277",
        "type" -> "employeeError"
      )

      Json.fromJson[EmployeeResponse](jsonObj).asOpt mustEqual(Some(employee))
    }
  }
}
