package services

import akka.actor.ActorSystem
import services.EmployeeServiceMessage._
import play.api.db.Database
import java.sql._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Document: EmployeeApiService.scala <br>
  * Description: Service to handle request to data base <br>
  * Date: 2018/07/05 <br>
  * @author Jhon Edwin Zambrano Ortiz
  * @param db Data Base instance
  */
case class EmployeeApiService(db: Database) {

  // Thread pool for handle the data base tasks
  private val dispatcher: ExecutionContext =
    ActorSystem().
    dispatchers.
    lookup("play.akka.actor.database.develop.dispatcher")

  /**
    * Method: allEmployees <br>
    * Description: Get all employees registered on data base <br>
    * Date: 2018/07/05 <br>
    * @return Future[EmployeeResponse]
    */
  def allEmployees: Future[EmployeeResponse] = Future {
    db.withConnection[EmployeeResponse](false){ connection =>
      val query = "SELECT * FROM empleados ORDER BY dsnombre"
      val statement: Statement = connection.createStatement()
      fetchData(query, statement)(setEmployeeResponse)
    }
  }(dispatcher)

  /**
    * Method: employee <br>
    * Description: Get one employee from data base by its document <br>
    * Date: 2018/07/05 <br>
    * @param document Employee legalId
    * @return Future[EmployeeResponse]
    */
  def employee(document: String): Future[EmployeeResponse] = Future {
    db.withConnection[EmployeeResponse](false) { connection =>
      val query = s"SELECT * FROM empleados where cdempleado LIKE '$document' LIMIT 1"
      val statement: Statement = connection.createStatement()
      fetchData(query, statement)(setEmployeeResponse)
    }
  }(dispatcher)

  /**
    * Method: add <br>
    * Description: Add one employee on data base <br>
    * Date: 2018/07/05 <br>
    * @param employee to add
    * @return Future[EmployeeResponse]
    */
  def add(employee: Employee): Future[EmployeeResponse] = Future{
    db.withConnection[EmployeeResponse](false){ connection =>
      val query = "INSERT INTO empleados (nmempleado, cdempleado, dsnombre, feregistro, snactivo) VALUES (" +
                  employee.number + ", '" +
                  employee.code + "' , '" +
                  employee.name + "' , '" +
                  employee.registryDate + "', 'S')"
      val statement: Statement = connection.createStatement()
      statement.execute(query)
      connection.commit()
      EmployeeSuccess(List(employee))
    }
  }(dispatcher)

  /**
    * Method: update <br>
    * Description: Update one employee on data base <br>
    * Date: 2018/07/05 <br>
    * @param employee to update
    * @return Future[EmployeeResponse]
    */
  def update(employee: Employee): Future[EmployeeResponse] = Future{
    db.withConnection[EmployeeResponse](false){ connection =>
      val query = "UPDATE empleados SET " +
                  "cdempleado = '" + employee.code + "', " +
                  "dsnombre = '"   + employee.name + "'," +
                  "feregistro = '" + employee.registryDate + "', " +
                  "snactivo = "    + (if(employee.active) "'S' " else "'N' ") +
                  "WHERE nmempleado  = " + employee.number

      val statement: Statement = connection.createStatement()
      statement.execute(query)
      connection.commit()
      EmployeeSuccess(List(employee))
    }
  }(dispatcher)

  /**
    * Method: delete <br>
    * Description: Delete one employee from data base <br>
    * Date: 2018/07/05 <br>
    * @param document Employee legalId
    * @return Future[Boolean]
    */
  def delete(document: String) : Future[EmployeeResponse] = Future {
    var rsp: EmployeeResponse = EmployeeError("Fail on delete employee!!")
    db.withConnection[EmployeeResponse](false) { connection =>
      val query = "DELETE FROM empleados WHERE cdempleado LIKE '" + document + "'"
      val statement: Statement = connection.createStatement()
      statement.execute(query)
      connection.commit()
      rsp = EmployeeSuccess(Seq[Employee]())
      rsp
    }
  }(dispatcher)

  /**
    * Method: fetchData <br>
    * Description: <br>
    * Date: 2018/07/05 <br>
    * @param sql query
    * @param statement to execute
    * @param f function that performs an EmployeeResponse
    * @return EmployeeResponse
    */
  private def fetchData(sql: String, statement: Statement)(f: ResultSet => EmployeeResponse): EmployeeResponse = {
    statement.executeQuery(sql) match {
      case rsp: ResultSet => f(rsp)
      case _              => EmployeeError("Fail to execute query!")
    }
  }

  /**
    * Method: setEmployeeResponse <br>
    * Description: Fills EmployeeResponse from data base ResultSet<br>
    * Date: 2018/07/05 <br>
    * @param resultSet incoming from data base
    * @return EmployeeResponse
    */
  private def setEmployeeResponse(resultSet: ResultSet): EmployeeResponse = {
    if(!resultSet.next()){
      EmployeeError("No employees found!")
    }else{
      var employees = Seq[Employee]()
      do{
        employees = employees :+ Employee (
          resultSet.getInt("nmempleado"),
          resultSet.getString("cdempleado"),
          resultSet.getString("dsnombre"),
          resultSet.getString("feregistro"),
          resultSet.getString("snactivo") match {
                                                      case "S" => true
                                                      case _   => false
                                                    }
        )
      }while(resultSet.next())
      EmployeeSuccess(employees)
    }
  }
}
