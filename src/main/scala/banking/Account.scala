package banking

class Account(val accountId: String, val customer: Customer, var balance: Double = 0.0) {
  // Additional properties and methods specific to accounts, if needed

  def deposit(amount: Double): Unit = {

    balance += amount
  }

  def withdraw(amount: Double): Unit = {
    // Implement withdrawal functionality
    if (amount <= balance) {
      balance -= amount
    } else {
      println("Insufficient funds.")
    }
  }
}
