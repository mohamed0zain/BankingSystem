package banking

import banking.GlobalVariables._
import banking.QueryFunctions.{LogTransaction, insertTransactionIntoDatabase, updateBalanceInDatabase}
import scalafx.scene.Scene
import scalafx.scene.layout.VBox

import java.sql.Timestamp
import java.util.Calendar
import scala.io.StdIn
object Main extends App {
  var customer: Option[Customer] = None
  var account: Option[Account] = None
  //  var isAdmin: Boolean = false
  //  val adminUsername = "admin"
  //  val adminPassword = "admin"

  // Admin login function
  def adminLoginGui(username: String, password: String): Boolean = {
    //    println(s"Input: $username, $password") // Print the input values for debugging

    // Print the expected admin credentials
    //    println(s"Expected Admin Credentials: $adminUsername, $adminPassword")

    if (username == adminUsername && password == adminPassword) {
      isAdmin = true
      println("Admin login successful.")
      //      adminMenu()
      true
    } else {
      println(s"Invalid admin credentials. Access denied.")
      false
    }
  }

  def adminLogin(username: String, password: String): Boolean = {
    //    println(s"Input: $username, $password") // Print the input values for debugging

    // Print the expected admin credentials
    //    println(s"Expected Admin Credentials: $adminUsername, $adminPassword")

    if (username == adminUsername && password == adminPassword) {
      isAdmin = true
      println("Admin login successful.")
      adminMenu()
      true
    } else {
      println(s"Invalid admin credentials. Access denied.")
      false
    }
  }
  // Admin menu function
  def adminMenu(): Scene = {
    var adminContinue = true
    while (adminContinue) {
      println("\nAdmin Options:")
      println("1. Create Customer")
      println("2. Delete Customer")
      println("3. Exit Admin Menu")
      print("Enter your choice: ")

      val adminChoice = StdIn.readInt()

      adminChoice match {
        case 1 => val customerIdInput = StdIn.readLine("Enter Customer ID: ")
          val customerNameInput = StdIn.readLine("Enter Customer Name: ")
          val customerAddressInput = StdIn.readLine("Enter Customer Address: ")
          createCustomer(customerIdInput, customerNameInput, customerAddressInput)
        case 2 => println("Enter customer ID to delete: ")
          val customerIdToDelete = StdIn.readLine()
          deleteCustomer(customerIdToDelete)
        case 3 => sys.exit()
        case _ => println("Invalid choice. Please enter a valid option.")
      }
    }

    // Return a placeholder Scene or customize as needed
    new Scene(new VBox())
  }
  // Customer menu function
  def customerMenu(customerID: String): Unit = {
    // Check if the customer exists in the database


    // Fetch customer details if it exists
    val existingCustomer = QueryFunctions.getCustomerFromDatabase(customerID)

    if (existingCustomer.isDefined) {
      println("Customer found. Logging in...")

      // Set the current customer to the fetched customer
      customer = existingCustomer

      var customerContinue = true
      while (customerContinue) {
        println("Customer Options:")
        println("1. Create Account")
        println("2. Deposit")
        println("3. Withdraw")
        println("4. Transfer Money")
        println("5. View Account Info")
        println("6. View Last Transaction")
        println("7. Exit")

        print("Enter your choice: ")

        val customerChoice = StdIn.readInt()

        customerChoice match {
          case 1 => createAccount()
          case 2 =>  print("Enter the amount to deposit: $")
            val amount = StdIn.readDouble()
            performDeposit(amount)
          case 3 => print("Enter the amount to deposit: $")
            val amount = StdIn.readDouble()
            performWithdrawal(amount)
          case 4 => transferFunds()
          case 5 => viewAccountInformation()
          case 6 => viewLastTransaction()
          case 7 => sys.exit()
          case _ => println("Invalid choice. Please enter a valid option.")
        }
      }
    } else {
      println("Customer not found. Please try again.")
    }
  }

  def customerMenuGui(customerID: String): Boolean = {
    // Check if the customer exists in the database


    // Fetch customer details if it exists
    val existingCustomer = QueryFunctions.getCustomerFromDatabase(customerID)

    if (existingCustomer.isDefined) {
      println("Customer found. Logging in...")

      // Set the current customer to the fetched customer
      customer = existingCustomer
      true
    }
    else {
      println("Customer not found. Please try again.")
      false
    }
  }
  // Delete customer function
  def deleteCustomer(customerID: String): Boolean = {
    try {
      // Check if the customer exists in the database
      val existingCustomer = QueryFunctions.getCustomerFromDatabase(customerID)

      if (existingCustomer.isDefined) {
        // Implement logic to delete customer from the database
        QueryFunctions.deleteTransactionsByCustomerID(customerID)
        QueryFunctions.deleteCustomerFromDatabase(customerID)
        println(s"Customer with ID $customerID deleted successfully.")
        true
      } else {
        println(s"Customer with ID $customerID not found in the database.")
        false
      }
    } catch {
      case e: Exception =>
        println(s"Error deleting customer with ID $customerID: ${e.getMessage}")
        false
    }
  }



  // Function to create a new customer
  def createCustomer(customerId: String, name: String, address: String): Boolean = {
    val existingCustomer = QueryFunctions.getCustomerFromDatabase(customerId)
    if (existingCustomer.isEmpty) {
    customer = Some(new Customer(customerId, name, address))
    QueryFunctions.insertCustomerIntoDatabase(customer.get)
    println("Customer created successfully.")
      true
    }
    else {
      println(s"Customer with ID $customerId already found in the database.")
      false
    }
  }

  // Function to create a new account
  def createAccount(): Boolean = {
    if (customer.isDefined) {
      // Check if the customer already has an account
      if (QueryFunctions.customerHasAccount(customer.get.customerId)) {
        println("You have already created an account.")
        false
      } else {
        // Generate a new 4-digit accountID
        val accountId = generate4DigitAccountId()

        // Create and save the account
        account = Some(new Account(accountId, customer.get))
        QueryFunctions.insertAccountIntoDatabase(account.get)

        println("Account created successfully.")
        true
      }
    } else {
      println("Customer not found. Cannot create account.")
      false
    }
  }

  // Function to generate a random 4-digit account ID
  def generate4DigitAccountId(): String = {
    val random = new scala.util.Random
    val accountId = random.nextInt(9000) + 1000 // Ensures a 4-digit number
    accountId.toString
  }

  // Function to perform deposit
  def performDeposit(amountdeposit: Double): Boolean = {
    account = QueryFunctions.getAccountByCustomerID(customer.get.customerId)
    if (account.isDefined) {

      account.get.deposit(amountdeposit)
      println(s"Deposit of $$${amountdeposit} successful. New balance: $$${account.get.balance}")

      // Update the balance in the database
      updateBalanceInDatabase(account.get.accountId, account.get.balance)
      // Log the deposit transaction
      val transactionId = generateTransactionId()
      val timestamp = getCurrentTimestamp()
      val depositTransaction = LogTransaction(transactionId, account.get.customer.customerId, amountdeposit, "deposit", timestamp)
      insertTransactionIntoDatabase(depositTransaction)
      true
    } else {
      println("Account not found.\n")
      false
    }
  }

  def performDepositGui(amountdeposit: Double): Boolean = {
    if (amountdeposit <= 0) {
      println("Error: Please enter a positive amount for deposit.")
      return false
    }

    account = QueryFunctions.getAccountByCustomerID(customer.get.customerId)

    if (account.isDefined) {
      account.get.deposit(amountdeposit)

      println(s"Deposit of $$${amountdeposit} successful. New balance: $$${account.get.balance}")

      // Update the balance in the database
      updateBalanceInDatabase(account.get.accountId, account.get.balance)

      // Log the deposit transaction
      val transactionId = generateTransactionId()
      val timestamp = getCurrentTimestamp()
      val depositTransaction = LogTransaction(transactionId, account.get.customer.customerId, amountdeposit, "deposit", timestamp)
      insertTransactionIntoDatabase(depositTransaction)

      true
    } else {
      println("Account not found.\n")
      false
    }
  }


  // Function to generate a transaction ID
  def generateTransactionId(): String = {
    val random = new scala.util.Random
    random.nextInt(10000).toString
  }

  // Function to get the current timestamp
  def getCurrentTimestamp(): Timestamp = {
    new Timestamp(Calendar.getInstance().getTime.getTime)
  }


  def performWithdrawalGui(amountWithdrawal:Double): Boolean = {
    account = QueryFunctions.getAccountByCustomerID(customer.get.customerId)
    if (account.isDefined) {
      try {
        //        print("Enter the amount to withdraw: $")
        //        val amount = StdIn.readDouble()

        if (amountWithdrawal <= 0) {
          println("Error: Please enter a positive amount for withdrawal.")
          false
        } else if (amountWithdrawal <= account.get.balance) {
          val withdrawalAmount = -amountWithdrawal // Use negative value to represent withdrawal
          account.get.withdraw(amountWithdrawal)
          println(s"Withdrawal of $$${amountWithdrawal} successful. New balance: $$${account.get.balance}")

          // Update the balance in the database after withdrawal
          updateBalanceInDatabase(account.get.accountId, account.get.balance)
          val transactionId = generateTransactionId()
          val timestamp = getCurrentTimestamp()
          val withdrawalTransaction = LogTransaction(transactionId, account.get.customer.customerId, amountWithdrawal, "withdraw", timestamp)
          insertTransactionIntoDatabase(withdrawalTransaction)
          true
        } else {
          println("Insufficient funds. Withdrawal failed.")
          false
        }
      } catch {
        case _: NumberFormatException =>
          println("Error: Please enter a valid numerical value for withdrawal.")
          false
        case e: Exception =>
          e.printStackTrace()
          println("Error: Unable to perform withdrawal.")
          false
      }
    } else {
      println("Account not found.\n")
      false
    }
  }

  def performWithdrawal(amountWithdrawal:Double): Boolean = {
    account = QueryFunctions.getAccountByCustomerID(customer.get.customerId)
    if (account.isDefined) {
      try {
        //        print("Enter the amount to withdraw: $")
        //        val amount = StdIn.readDouble()

        if (amountWithdrawal <= 0) {
          println("Error: Please enter a positive amount for withdrawal.")
          false
        } else if (amountWithdrawal <= account.get.balance) {
          val withdrawalAmount = -amountWithdrawal // Use negative value to represent withdrawal
          account.get.withdraw(amountWithdrawal)
          println(s"Withdrawal of $$${amountWithdrawal} successful. New balance: $$${account.get.balance}")

          // Update the balance in the database after withdrawal
          updateBalanceInDatabase(account.get.accountId, account.get.balance)
          val transactionId = generateTransactionId()
          val timestamp = getCurrentTimestamp()
          val withdrawalTransaction = LogTransaction(transactionId, account.get.customer.customerId, amountWithdrawal, "withdraw", timestamp)
          insertTransactionIntoDatabase(withdrawalTransaction)
          true
        } else {
          println("Insufficient funds. Withdrawal failed.")
          false
        }
      } catch {
        case _: NumberFormatException =>
          println("Error: Please enter a valid numerical value for withdrawal.")
          false
        case e: Exception =>
          e.printStackTrace()
          println("Error: Unable to perform withdrawal.")
          false
      }
    } else {
      println("Account not found.\n")
      false
    }
  }


  def transferFunds(): Unit = {
    account = QueryFunctions.getAccountByCustomerID(customer.get.customerId)
    if (account.isDefined) {
      try {
        print("Enter target customer ID for transfer: ")
        val targetCustomerId = StdIn.readLine()

        print("Enter the amount to transfer: $")
        val amount = StdIn.readDouble()

        // Check if the target customer exists in the database
        val targetCustomer = QueryFunctions.getCustomerFromDatabase(targetCustomerId)

        if (targetCustomer.isDefined) {
          // Perform the transfer
          val sourceAccountId = account.get.accountId
          val targetAccountId = QueryFunctions.getAccountIdByCustomerId(targetCustomerId)

          if (targetAccountId.isDefined) {
            if (amount > 0 && amount <= account.get.balance) {
              // Deduct amount from source account
              account.get.withdraw(amount)
              updateBalanceInDatabase(sourceAccountId, account.get.balance)

              // Deposit amount to target account
              QueryFunctions.depositToAccount(targetAccountId.get, amount)

              // Log the transfer transactions
              val transactionId = generateTransactionId()
              val timestamp = getCurrentTimestamp()
              val transferTransaction = LogTransaction(transactionId, account.get.customer.customerId, amount, "transfer", timestamp)
              insertTransactionIntoDatabase(transferTransaction)

              println(s"Transfer of $$${amount} to customer ID ${targetCustomerId} successful.")
            } else {
              println("Invalid amount or insufficient funds. Transfer failed.")
            }
          } else {
            println(s"Target customer ID ${targetCustomerId} does not have an associated account.")
          }
        } else {
          println(s"Target customer with ID ${targetCustomerId} not found.")
        }
      } catch {
        case _: NumberFormatException =>
          println("Error: Please enter valid numerical values.")
        case e: Exception =>
          e.printStackTrace()
          println("Error: Unable to perform funds transfer.")
      }
    } else {
      println("Account not found.\n")
    }
  }

  def transferFundsGui(targetCustomerId: String, amount: Double): Boolean = {
    account = QueryFunctions.getAccountByCustomerID(customer.get.customerId)
    if (account.isDefined) {
      try {
        // Check if the target customer exists in the database
        val targetCustomer = QueryFunctions.getCustomerFromDatabase(targetCustomerId)

        if (targetCustomer.isDefined) {
          // Perform the transfer
          val sourceAccountId = account.get.accountId
          val targetAccountId = QueryFunctions.getAccountIdByCustomerId(targetCustomerId)

          if (targetAccountId.isDefined) {
            if (amount > 0 && amount <= account.get.balance) {
              // Deduct amount from source account
              account.get.withdraw(amount)
              updateBalanceInDatabase(sourceAccountId, account.get.balance)

              // Deposit amount to target account
              QueryFunctions.depositToAccount(targetAccountId.get, amount)

              // Log the transfer transactions
              val transactionId = generateTransactionId()
              val timestamp = getCurrentTimestamp()
              val transferTransaction = LogTransaction(transactionId, account.get.customer.customerId, amount, "transfer", timestamp)
              insertTransactionIntoDatabase(transferTransaction)

              println(s"Transfer of $$${amount} to customer ID ${targetCustomerId} successful.")
              true
            } else {
              println("Invalid amount or insufficient funds. Transfer failed.")
              false
            }
          } else {
            println(s"Target customer ID ${targetCustomerId} does not have an associated account.")
            false
          }
        } else {
          println(s"Target customer with ID ${targetCustomerId} not found.")
          false
        }
      } catch {
        case _: NumberFormatException =>
          println("Error: Please enter valid numerical values.")
          false
        case e: Exception =>
          e.printStackTrace()
          println("Error: Unable to perform funds transfer.")
          false
      }
    } else {
      println("Account not found.\n")
      false
    }
  }

  // Function to view account information
  def viewAccountInformation(): Unit = {
    account = QueryFunctions.getAccountByCustomerID(customer.get.customerId)
    if (account.isDefined) {
      println("Account Information:")
      println(s"Account ID: ${account.get.accountId}")
      println(s"Customer: ${account.get.customer.name}")
      println(s"Balance: $$${account.get.balance}")
      // Return true if the account is defined
    } else {
      println("Account not found.")
       // Return false if the account is not defined
    }
  }

  def viewAccountInformationGui(customerid: String): Boolean = {
    account = QueryFunctions.getAccountByCustomerID(customer.get.customerId)
    if (account.isDefined) {
      println("Account Information:")
      println(s"Account ID: ${account.get.accountId}")
      println(s"Customer: ${account.get.customer.name}")
      println(s"Balance: $$${account.get.balance}")
      true
      // Return true if the account is defined
    } else {
      println("Account not found.")
      // Return false if the account is not defined
      true
    }
  }

  // Function to view last transaction
  def viewLastTransaction(): Boolean = {
    account = QueryFunctions.getAccountByCustomerID(customer.get.customerId)
    if (customer.isDefined) {
      // Retrieve the last transactions for the current customer from the database
      val lastTransactions = QueryFunctions.getLastTransactions(customer.get.customerId)

      if (lastTransactions.nonEmpty) {
        println("Last Transactions:")
        lastTransactions.foreach { transaction =>
          println(s"Transaction ID: ${transaction.transactionId}")
          println(s"Customer ID: ${transaction.customerId}")
          println(s"Amount: $$${transaction.amount}")
          println(s"Transaction Type: ${transaction.transactionType}")
          println(s"Timestamp: ${transaction.timestamp}")
          println("-------------")
        }
        true
      } else {
        println("No transactions found for the customer within the last 24 hours.")
        false
      }
      true
    } else {
      println("Customer not found.\n")
      false
    }
  }

  def viewLastTransactionGui(customerId: String): Boolean = {
    account = QueryFunctions.getAccountByCustomerID(customer.get.customerId)
    if (customer.isDefined) {
      // Retrieve the last transactions for the current customer from the database
      val lastTransactions = QueryFunctions.getLastTransactions(customer.get.customerId)

      if (lastTransactions.nonEmpty) {
        println("Last Transactions:")
        lastTransactions.foreach { transaction =>
          println(s"Transaction ID: ${transaction.transactionId}")
          println(s"Customer ID: ${transaction.customerId}")
          println(s"Amount: $$${transaction.amount}")
          println(s"Transaction Type: ${transaction.transactionType}")
          println(s"Timestamp: ${transaction.timestamp}")
          println("-------------")
        }
        true
      } else {
        println("No transactions found for the customer within the last 24 hours.")
        false
      }
      true
    } else {
      println("Customer not found.\n")
      false
    }
  }

  // Display options
  var continue = true
  while (continue) {
    println("Login Options:")
    println("1. Admin Login")
    println("2. Customer Login")
    print("Enter your choice: ")
    val loginChoice = StdIn.readInt()

    loginChoice match {
      case 1 => val usernameInput = StdIn.readLine("Enter Admin username: ")
        val passwordInput = StdIn.readLine("Enter Admin password: ")
        adminLogin(usernameInput, passwordInput)
      case 2 => print("Enter customer ID: ")
        val customerIdToCheck = StdIn.readLine()
        customerMenu(customerIdToCheck)
      case _ =>
        println("Invalid choice. Please enter a valid option.")
    }
  }

}