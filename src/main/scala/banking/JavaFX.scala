package banking

import banking.Main.viewLastTransactionGui
import banking.QueryFunctions.getLastTransactions
import scalafx.Includes.handle
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.scene.control.CheckBox
import scalafx.scene.layout.VBox
//import banking.GlobalVariables._
import scalafx.animation.PauseTransition
import scalafx.scene.control._
import scalafx.util.Duration
object JavaFX extends JFXApp3 {

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Banking System GUI"

      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 20

          val adminCheckBox = new CheckBox("Admin Login")
          val customerCheckBox = new CheckBox("Customer Login")

          val usernameField = new TextField {
            promptText = "Username"
          }

          val passwordField = new PasswordField {
            promptText = "Password"
          }

          val customerIdField = new TextField {
            promptText = "Customer ID"
          }
          val customerIdLabel: Label = new Label("Customer ID")
          val usernameLabel: Label = new Label("Username:")
          val passwordLabel: Label = new Label("Password:")

          val loginButton = new Button("Login") {
            onAction = _ => {
              val username = usernameField.text.value
              val password = passwordField.text.value
              val customerId = customerIdField.text.value

              if (adminCheckBox.selected.value) {
                // Admin login
                if (Main.adminLoginGui(username, password)) {
                  val alert = new Alert(Alert.AlertType.Information) {
                    title = "Login Success"
                    headerText = "Admin Log In Success"
                  }
                  alert.showAndWait()

                  val pause = new PauseTransition(Duration.apply(3))
                  pause.setOnFinished(_ => adminMenu())
                  pause.play()
                } else {
                  val alert = new Alert(Alert.AlertType.Error) {
                    title = "Login Failed"
                    headerText = "Login failed. Please check your credentials."
                  }
                  alert.showAndWait()
                }
              } else if (customerCheckBox.selected.value) {
                // Customer login
                // Implement customer login logic here using customerId
                // For now, let's assume customer login is successful
                if(Main.customerMenuGui(customerId)){
                  val alert = new Alert(Alert.AlertType.Information) {
                    title = "Login Success"
                    headerText = "Customer Log In Success"


                    customerMenu()
                  }
                  alert.showAndWait()
                }
                else
                {
                  // No checkbox selected
                  val alert = new Alert(Alert.AlertType.Warning) {
                    title = "Login Failed"
                    headerText = "Customer Not Found"
                  }
                  alert.showAndWait()
                }

              }
            }
          }

          adminCheckBox.selected.onChange { (_, _, newValue) =>
            if (newValue) {
              // Admin selected, show username and password fields
              usernameLabel.visible = true
              usernameField.visible = true
              passwordLabel.visible = true
              passwordField.visible = true
              customerIdField.visible = false
              customerCheckBox.selected.value = false
              customerIdLabel.visible = false
            } else {
              // Admin deselected, hide username and password fields
              customerIdField.visible = true
              usernameField.visible = true
              usernameLabel.visible = true
              passwordLabel.visible = true
              passwordField.visible = true
              customerIdLabel.visible = true
            }
          }

          customerCheckBox.selected.onChange { (_, _, newValue) =>
            if (newValue) {
              // Customer selected, show customer ID field
              customerIdField.visible = true
              adminCheckBox.selected.value = false
              usernameField.visible = false
              usernameLabel.visible = false
              passwordLabel.visible = false
              passwordField.visible = false
            } else {
              // Customer deselected, hide customer ID field
              customerIdField.visible = true
              usernameField.visible = true
              usernameLabel.visible = true
              passwordLabel.visible = true
              passwordField.visible = true
              customerIdLabel.visible = true
            }
          }

          children = Seq(
            adminCheckBox,
            customerCheckBox,
            usernameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            customerIdLabel,
            customerIdField,
            loginButton
          )
        }
      }
    }
  }


  def adminMenu(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Admin Menu"
      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 10

          val createCustomerButton: Button  = new Button("Create Customer") {
            onAction = _ => createCustomer()
          }
          val deleteCustomerButton: Button  = new Button("Delete Customer") {
            onAction = _ => deleteCustomer()
          }

          val exitButton: Button  = new Button("Exit") {
            onAction = _ => stage.close()
          }

          children = Seq(
            new Label("Hello, Admin!"),
            createCustomerButton,
            deleteCustomerButton,
            exitButton
          )
        }
      }
    }
  }
  import scalafx.scene.control.Alert.AlertType
  import scalafx.scene.control.{Alert, ButtonType}

  // Inside your Main object or class
  def createCustomer(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 20
          title = "Create Customer"

          val customerIdField: TextField = new TextField {
            promptText = "Customer ID"
          }

          val nameField: TextField = new TextField {
            promptText = "Name"
          }

          val addressField: TextField = new TextField {
            promptText = "Address"
          }

          val saveButton: Button = new Button("Save") {
            onAction = _ => {
              val customerId = customerIdField.text.value
              val customerName = nameField.text.value
              val customerAddress = addressField.text.value

              if (customerId.nonEmpty && customerName.nonEmpty && customerAddress.nonEmpty && Main.createCustomer(customerId, customerName, customerAddress) ) {
                // Call a function to create a customer in the banking system
                Main.createCustomer(customerId, customerName, customerAddress)

                val alert = new Alert(AlertType.Information) {
                  title = "Success"
                  headerText = ""
                  contentText = "Customer created successfully."
                }
                alert.showAndWait()

                customerIdField.text = ""
                nameField.text = ""
                addressField.text = ""
              } else {
                val alert = new Alert(AlertType.Error) {
                  title = "Error"
                  headerText = ""
                  contentText = "Failed to add Customer"
                }
                alert.showAndWait()
              }
            }
          }

          val backButton: Button = new Button("Back") {
            onAction = _ => {
              adminMenu()
            }
          }

          children = Seq(
            new Label("Create Customer"),
            new Label("Customer ID:"),
            customerIdField,
            new Label("Name:"),
            nameField,
            new Label("Address:"),
            addressField,
            saveButton,
            backButton,
          )
        }
      }
    }
  }




  // Inside your Main object or class
  def deleteCustomer(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 20
          title = "Delete Customer"

          val customerIdField: TextField = new TextField {
            promptText = "Customer ID to be deleted"
          }

          val deleteButton: Button = new Button("Delete Customer") {
            onAction = _ => {
              val customerId = customerIdField.text.value

              if (customerId.nonEmpty && Main.deleteCustomer(customerId)) {
                // Call a function to delete a customer in the banking system


                val alert = new Alert(AlertType.Information) {
                  title = "Success"
                  headerText = ""
                  contentText = s"Customer with ID $customerId deleted successfully."
                }
                alert.showAndWait()

                customerIdField.text = ""
              } else {
                val alert = new Alert(AlertType.Error) {
                  title = "Error"
                  headerText = ""
                  contentText = "Please enter a valid Customer ID."
                }
                alert.showAndWait()
              }
            }
          }

          val backButton: Button = new Button("Back") {
            onAction = _ => {
              adminMenu()
            }
          }

          children = Seq(
            new Label("Delete Customer"),
            new Label("Customer ID:"),
            customerIdField,
            deleteButton,
            backButton
          )
        }
      }
    }
  }


  def viewAccountInfo(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "View Account"

      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 20

//          val customerIdField: TextField = new TextField{
//            promptText = "Address"
//          }
          val viewAccountButton: Button = new Button("View Account") {
            onAction = _ => {

              if (Main.viewAccountInformationGui(Main.customer.get.customerId)) {
                val alert = new Alert(Alert.AlertType.Information) {
                  title = "Account Created Successfully"
                  headerText = "Account Created Successfully"
                  contentText =
                    s"""
                       |Account ID: ${Main.account.get.accountId}

                       |Customer: ${Main.account.get.customer.name}

                       |Balance: $$${Main.account.get.balance}
                   """.stripMargin
                }
                alert.showAndWait()
              }
            }
          }
          val backButton: Button = new Button("Back") {
            onAction = _ => {
             customerMenu()
            }
          }


          children = Seq(
            new Label("View Account Information"),
//            customerIdField,
            viewAccountButton,
            backButton
          )
        }
      }
    }
  }



  def createAccount(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 20
          title = "Create Account"




          val createAccountButton: Button = new Button("Create An Account") {
            onAction = _ => {
              if (Main.createAccount()) {
                val alert = new Alert(Alert.AlertType.Information) {
                  title = "Account Created Successfully"
                  headerText = "Account Created Successfully"
                }
                alert.showAndWait()
              }
              else {
                val alert = new Alert(Alert.AlertType.Information) {
                  title = "Account not created"
                  headerText = "Account not created"
                }
                alert.showAndWait()
              }
            }
          }
          val backButton: Button = new Button("Back") {
            onAction = _ => {
              customerMenu()
            }
          }

          children = Seq(
            createAccountButton,
            backButton,
          )
        }
      }
    }
  }

  def performDeposit(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Perform Deposit"

      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 20

          val depositField: TextField = new TextField {
            promptText = "Enter the amount to deposit"
          }

          val depositButton: Button = new Button("Deposit") {
            onAction = _ => {
              val depositAmount = depositField.text.value.toDoubleOption.getOrElse(0.0)

              // Call a function to perform the deposit in the banking system
              if (Main.performDepositGui(depositAmount)) {
                val alert = new Alert(Alert.AlertType.Information) {
                  title = "Successful Transaction"
                  headerText = s"Successfully deposited $depositAmount"
                }
                alert.showAndWait()
              } else {
                val alert = new Alert(Alert.AlertType.Error) {
                  title = "Transaction Failed"
                  headerText = "Failed to deposit. Please try again."
                }
                alert.showAndWait()
              }
            }
          }

          val backButton: Button = new Button("Back") {
            onAction = _ => {
              customerMenu()
            }
          }

          children = Seq(
            new Label("Perform Deposit"),
            new Label("Enter Deposit Amount: "),
            depositField,
            depositButton,
            backButton
          )
        }
      }
    }
  }


  def performWithdrawal(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Perform Withdrawal"

      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 20

          val withdrawalField: TextField = new TextField {
            promptText = "Enter the amount to withdraw"
          }

          val withdrawalButton: Button = new Button("Withdraw") {
            onAction = _ => {
              val withdrawalAmount = withdrawalField.text.value.toDoubleOption.getOrElse(0.0)

              // Call a function to perform the withdrawal in the banking system
              if (Main.performWithdrawalGui(withdrawalAmount)) {
                val alert = new Alert(Alert.AlertType.Information) {
                  title = "Successful Transaction"
                  headerText = s"Successfully withdrew $withdrawalAmount"
                }
                alert.showAndWait()
              } else {
                val alert = new Alert(Alert.AlertType.Error) {
                  title = "Transaction Failed"
                  headerText = "Failed to withdraw. Please check your balance and try again."
                }
                alert.showAndWait()
              }
            }
          }

          val backButton: Button = new Button("Back") {
            onAction = _ => {
              customerMenu()
            }
          }

          children = Seq(
            new Label("Perform Withdrawal"),
            new Label("Enter Withdraw Amount: "),
            withdrawalField,
            withdrawalButton,
            backButton
          )
        }
      }
    }
  }


  def transferMoney(): Boolean = {
    stage = new JFXApp3.PrimaryStage {
      title = "Transfer Funds"

      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 20

          val targetCustomerIdField: TextField = new TextField {
            promptText = "Enter target customer ID for transfer"
          }

          val amountField: TextField = new TextField {
            promptText = "Enter the amount to transfer"
          }

          val transferButton: Button = new Button("Transfer Funds") {
            onAction =_  => {
              val targetCustomerId = targetCustomerIdField.text.value
              val amount = amountField.text.value.toDoubleOption.getOrElse(0.0)

              val transferResult = Main.transferFundsGui(targetCustomerId, amount)

              if (transferResult) {
                val alert = new Alert(Alert.AlertType.Information) {
                  title = "Transfer Successful"
                  headerText = s"Transfer of $$${amount} to customer ID $targetCustomerId successful."
                }
                alert.showAndWait()
              } else {
                val alert = new Alert(Alert.AlertType.Error) {
                  title = "Transfer Failed"
                  headerText = "Transfer failed. Please check your inputs and try again."
                }
                alert.showAndWait()
              }
            }
          }

          val backButton: Button = new Button("Back") {
            onAction = _ => {
              customerMenu()
            }
          }

          children = Seq(
            new Label("Enter Target Customer ID:"),
            targetCustomerIdField,
            new Label("Enter Amount to Be Transfered:"),
            amountField,
            transferButton,
            backButton
          )
        }
      }
    }

    // Return true for successful transfer, false otherwise
    true
  }
  def viewTransactions(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "View last transaction"

      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 20

          val viewTransactionButton = new Button("View Last Transaction")
          val transactionTextArea = new TextArea()
          transactionTextArea.editable = false
          transactionTextArea.wrapText = true

          viewTransactionButton.onAction = handle {
            val id = Main.customer.get.customerId
            if (id.nonEmpty) {
              val result = viewLastTransactionGui(id)
              if (result) {
                val lastTransactions = getLastTransactions(id)
                if (lastTransactions.nonEmpty) {
                  val transactionText = lastTransactions.map { transaction =>
                    s"Transaction ID: ${transaction.transactionId}\n" +
                      s"Customer ID: ${transaction.customerId}\n" +
                      s"Amount: $$${transaction.amount}\n" +
                      s"Transaction Type: ${transaction.transactionType}\n" +
                      s"Timestamp: ${transaction.timestamp}\n" +
                      "-------------\n"
                  }.mkString("\n")
                  transactionTextArea.text = transactionText
                } else {
                  transactionTextArea.text = "No transactions found for the customer within the last 24 hours."
                }
              } else {
                transactionTextArea.text = "Customer not found."
              }
            } else {
              transactionTextArea.text = "Please enter a valid Customer ID."
            }
          }
          val backButton: Button = new Button("Back") {
            onAction = _ => {
              customerMenu()
            }
          }
          children = Seq(viewTransactionButton, transactionTextArea, backButton)
        }
      }
    }


  }

  def customerMenu () : Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Customer Menu"

      scene = new Scene {
        content = new VBox {
          padding = Insets(20)
          spacing = 10

          val createAccountButton: Button = new Button("Create Account") {
            onAction = _ => createAccount()
          }
          val depositButton: Button  = new Button("Deposit") {
            onAction = _ => performDeposit()
          }
          val withdrawalButton: Button  = new Button("Withdrawal") {
            onAction = _ => performWithdrawal()
          }
          val transferButton: Button  = new Button("Transfer Money") {
            onAction = _ => transferMoney()
          }
          val viewAccountButton: Button = new Button("View Account") {
            onAction = _ => viewAccountInfo
          }
          val viewTransactionsButton: Button  = new Button("View Transactions") {
            onAction = _ => viewTransactions()
          }

          val exitButton: Button = new Button("Exit") {
            onAction = _ => stage.close()
          }


          children = Seq(
            createAccountButton,
            depositButton,
            withdrawalButton,
            transferButton,
            viewAccountButton,
            viewTransactionsButton,
            exitButton
          )
        }
      }
    }
  }
}
