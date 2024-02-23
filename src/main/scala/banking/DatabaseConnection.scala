package banking

import java.sql.{Connection, DriverManager}

object DatabaseConnection {
  private val url = "jdbc:mysql://localhost:3306/bankingsystem"
  private val username = "root"
  private val password = "1AlaaloloM!!"

  def getConnection: Connection = {
    DriverManager.getConnection(url, username, password)
  }
}
