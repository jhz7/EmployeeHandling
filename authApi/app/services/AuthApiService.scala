package services

import akka.actor.ActorSystem
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

/**
  * Document: AuthApiService.scala <br>
  * Description: Rest API to manage all about the application authentication <br>
  * Date: 2018/07/10 <br>
  * @param db Data Base instance
  * @author Jhon Edwin Zambrano Ortiz
  */
case class AuthApiService (db: Database) {

  import services.AuthServiceMessage._

  /** Stores all active sessions in memory */
  private var sessions = Map[SessionId, Username]()

  /** Thread pool to handle data base tasks */
  private val dispatcher: ExecutionContext =
    ActorSystem().
      dispatchers.
      lookup(id = "play.akka.actor.database.develop.dispatcher")

  /**
    * Method: login <br>
    * Description: Validates the user existence on data base <br>
    * Date: 2018/07/10 <br>
    * @param loginRequest information
    * @return
    */
  def login(loginRequest: LoginRequest): Future[LoginResponse] = Future {
    db.withConnection[LoginResponse](false) { connection =>
      val query = "SELECT contrasenia FROM usuarios WHERE nombre = '" + loginRequest.username + "'"
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(query)
      if (!resultSet.next()) {
        UserNotFound(loginRequest.username)
      } else {
        if (loginRequest.password == resultSet.getString("contrasenia")) {
          val sessionId = generateSessionId
          sessions += (sessionId -> loginRequest.username)
          LoginSuccess(sessionId)
        } else {
          PasswordIncorrect(loginRequest.username)
        }
      }
    }
  }(dispatcher)

  /**
    * Method: logout <br>
    * Description: Deletes an active sessionId <br>
    * Date: 2018/07/12 <br>
    * @param sessionId to delete
    */
  def logout(sessionId: SessionId): Unit =
    sessions -= sessionId

  /**
    * Method: whoami <br>
    * Description: Verifies if sessionId parameter is active <br>
    * Date: 2018/07/10 <br>
    * @param sessionId to verify
    * @return
    */
  def whoami(sessionId: SessionId): WhoamiResponse =
    sessions.get(sessionId) match {
      case Some(username) => Credentials(sessionId, username)
      case None           => SessionNotFound(sessionId)
    }

  /**
    * Method: generateSessionId <br>
    * Description: Generates a randomly sessionId<br>
    * Date: 2018/07/10 <br>
    * @return sessionId
    */
  private def generateSessionId: String =
    java.util.UUID.randomUUID.toString
}
