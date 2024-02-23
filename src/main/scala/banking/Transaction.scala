package banking

class Transaction(val sourceAccount: Account, val targetAccount: Account, val amount: Double) {
  // Additional properties and methods specific to transactions, if needed

  def execute(): Unit = {
    // Implement transaction execution
    sourceAccount.withdraw(amount)
    targetAccount.deposit(amount)
  }
}
