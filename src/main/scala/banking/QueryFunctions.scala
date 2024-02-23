package banking


import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, Statement, Timestamp}

object QueryFunctions {
   def insertCustomerIntoDatabase(customer: Customer): Unit = {
    val connection: Connection = banking.DatabaseConnection.getConnection

    try {
      val insertQuery = "INSERT INTO customer (customerID, name, address) VALUES (?, ?, ?)"
      val preparedStatement: PreparedStatement = connection.prepareStatement(insertQuery)

      preparedStatement.setString(1, customer.customerId)
      preparedStatement.setString(2, customer.name)
      preparedStatement.setString(3, customer.address)

      preparedStatement.executeUpdate()
    } finally {
      connection.close()
    }
  }

  def deleteCustomerFromDatabase(customerIdToDelete: String): Unit = {
    var statement: Statement = null
    val connection: Connection = banking.DatabaseConnection.getConnection

    try {
      // Create a connection to the database
      statement = connection.createStatement()

      // Delete associated accounts first
      val deleteAccountsQuery = s"DELETE FROM account WHERE customerID = '$customerIdToDelete'"
      statement.executeUpdate(deleteAccountsQuery)

      // Now delete the customer
      val deleteCustomerQuery = s"DELETE FROM customer WHERE customerID = '$customerIdToDelete'"
      statement.executeUpdate(deleteCustomerQuery)

      println(s"Customer with ID $customerIdToDelete and associated accounts deleted from the database.")
    } catch {
      case e: Exception =>
        e.printStackTrace()
        println("Error: Unable to delete customer and associated accounts from the database.")
    } finally {
      // Close resources in the reverse order of their creation
      if (statement != null) statement.close()
      if (connection != null) connection.close()
    }
  }


  def getCustomerFromDatabase(customerId: String): Option[Customer] = {
    val connection: Connection = banking.DatabaseConnection.getConnection

    try {
      val selectQuery = "SELECT * FROM customer WHERE customerID = ?"
      val preparedStatement: PreparedStatement = connection.prepareStatement(selectQuery)

      preparedStatement.setString(1, customerId)

      val resultSet: ResultSet = preparedStatement.executeQuery()

      if (resultSet.next()) {
        // Extract customer details from the ResultSet
        val fetchedCustomerId = resultSet.getString("customerID")
        val name = resultSet.getString("name")
        val address = resultSet.getString("address")

        // Create a Customer object with the fetched details
        Some(new Customer(fetchedCustomerId, name, address))
      } else {
        None // Return None if the customer is not found
      }
    } finally {
      connection.close()
    }
  }

  def getAccountByCustomerID(customerId: String): Option[Account] = {
    val connection: Connection = banking.DatabaseConnection.getConnection

    try {
      val selectQuery = "SELECT * FROM account WHERE customerID = ?"
      val preparedStatement: PreparedStatement = connection.prepareStatement(selectQuery)

      preparedStatement.setString(1, customerId)

      val resultSet: ResultSet = preparedStatement.executeQuery()

      if (resultSet.next()) {
        // Extract account details from the ResultSet
        val accountId = resultSet.getString("accountID")
        val balance = resultSet.getDouble("balance")

        // Retrieve customer details associated with the account
        val customer = getCustomerFromDatabase(customerId)

        // Create an Account object with the fetched details
        customer.map(c => new Account(accountId, c, balance))
      } else {
        None // Return None if the account is not found
      }
    } finally {
      connection.close()
    }
  }


  def insertAccountIntoDatabase(account: Account): Unit = {
    val connection: Connection = DatabaseConnection.getConnection

    try {
      val insertQuery = "INSERT INTO account (accountId, customerID, name, balance) VALUES (?, ?, ?, ?)"
      val preparedStatement: PreparedStatement = connection.prepareStatement(insertQuery)

      preparedStatement.setString(1, account.accountId)
      preparedStatement.setString(2, account.customer.customerId)
      preparedStatement.setString(3, account.customer.name)
      preparedStatement.setDouble(4, account.balance)

      preparedStatement.executeUpdate()
    } finally {
      connection.close()
    }
  }
  def updateBalanceInDatabase(accountId: String, newBalance: Double): Unit = {
    val connection: Connection = DatabaseConnection.getConnection

    try {
      val updateQuery = "UPDATE account SET balance = ? WHERE accountId = ?"
      val preparedStatement: PreparedStatement = connection.prepareStatement(updateQuery)

      preparedStatement.setDouble(1, newBalance)
      preparedStatement.setString(2, accountId)

      preparedStatement.executeUpdate()
    } finally {
      connection.close()
    }
  }
  def customerHasAccount(customerId: String): Boolean = {
    val connection: Connection = DatabaseConnection.getConnection

    try {
      val selectQuery = "SELECT COUNT(*) as count FROM account WHERE customerID = ?"
      val preparedStatement: PreparedStatement = connection.prepareStatement(selectQuery)

      preparedStatement.setString(1, customerId)

      val resultSet: ResultSet = preparedStatement.executeQuery()

      resultSet.next() && resultSet.getInt("count") > 0
    } finally {
      connection.close()
    }
  }
  case class LogTransaction (transactionId: String, customerId: String, amount: Double, transactionType: String, timestamp: Timestamp)
  def insertTransactionIntoDatabase(transaction: LogTransaction): Unit = {
    val connection: Connection = DatabaseConnection.getConnection

    try {
      val insertQuery = "INSERT INTO transaction (transactionID, customerID, amount, transactionType, transactionTime) VALUES (?, ?, ?, ?, ?)"
      val preparedStatement: PreparedStatement = connection.prepareStatement(insertQuery)

      preparedStatement.setString(1, transaction.transactionId)
      preparedStatement.setString(2, transaction.customerId)
      preparedStatement.setDouble(3, transaction.amount)
      preparedStatement.setString(4, transaction.transactionType)
      preparedStatement.setTimestamp(5, transaction.timestamp)

      preparedStatement.executeUpdate()
    } finally {
      connection.close()
    }
  }


  def deleteTransactionsByCustomerID(customerID: String): Unit = {
    val connection: Connection = DatabaseConnection.getConnection
    var preparedStatement: PreparedStatement = null

    try {
      // Get a database connection (you need to implement getConnection())
      DatabaseConnection.getConnection

      // SQL statement to delete transactions for a given customerID
      val sql = "DELETE FROM transaction WHERE customerID = ?"

      // Create a prepared statement
      preparedStatement = connection.prepareStatement(sql)

      // Set the parameter values
      preparedStatement.setString(1, customerID)

      // Execute the delete statement
      preparedStatement.executeUpdate()

      println(s"Transactions for customer ID $customerID deleted successfully.")
    } catch {
      case e: Exception =>
        e.printStackTrace()
        println("Error: Unable to delete transactions.")
    } finally {
      // Close resources in the reverse order of their creation
      if (preparedStatement != null) preparedStatement.close()
      if (connection != null) connection.close()
    }
  }
  def getLastTransactions(customerId: String): List[LogTransaction] = {
  val connection: Connection = DatabaseConnection.getConnection

  try {
    val selectQuery =
      """SELECT * FROM transaction
        |WHERE customerID = ? AND transactionTime >= NOW() - INTERVAL 1 DAY
        |ORDER BY transactionTime DESC""".stripMargin

    val preparedStatement: PreparedStatement = connection.prepareStatement(selectQuery)

    preparedStatement.setString(1, customerId)

    val resultSet: ResultSet = preparedStatement.executeQuery()

    var transactions: List[LogTransaction] = List.empty

    while (resultSet.next()) {
      val transactionId = resultSet.getString("transactionID")
      val customerId = resultSet.getString("customerID")
      val amount = resultSet.getDouble("amount")
      val transactionType = resultSet.getString("transactionType")
      val timestamp = resultSet.getTimestamp("transactionTime")

      transactions = LogTransaction(transactionId, customerId, amount, transactionType, timestamp) :: transactions
    }

    transactions
  } finally {
    connection.close()
  }
}
  // Get account ID by customer ID
  def getAccountIdByCustomerId(customerId: String): Option[String] = {
    var statement: Statement = null
    val connection: Connection = DatabaseConnection.getConnection

    try {
      statement = connection.createStatement()

      // Execute the SELECT SQL query to get the account ID
      val query = s"SELECT accountID FROM Account WHERE customerID = '$customerId'"
      val resultSet = statement.executeQuery(query)

      if (resultSet.next()) {
        Some(resultSet.getString("accountID"))
      } else {
        None
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        None
    } finally {
      if (statement != null) statement.close()
      if (connection != null) connection.close()
    }
  }


  // Deposit to account
  def depositToAccount(accountId: String, amount: Double): Unit = {
    var statement: Statement = null
    val connection: Connection = DatabaseConnection.getConnection

    try {
      statement = connection.createStatement()

      // Execute the UPDATE SQL query to deposit to the account
      val updateQuery = s"UPDATE Account SET balance = balance + $amount WHERE accountID = '$accountId'"
      statement.executeUpdate(updateQuery)

      println("Deposit to account successful.")
    } catch {
      case e: Exception =>
        e.printStackTrace()
        println("Error: Unable to deposit to account.")
    } finally {
      if (statement != null) statement.close()
      if (connection != null) connection.close()
    }
  }
}



