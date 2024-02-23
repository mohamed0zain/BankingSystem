// BankingService.scala
package banking

object BankingService {
  def transferFunds(sourceAccount: Account, targetAccount: Account, amount: Double): Unit = {
    // Implement fund transfer functionality
    val transaction = new Transaction(sourceAccount, targetAccount, amount)
    transaction.execute()
  }

  def viewAccountInformation(account: Account): Unit = {
    // Implement account information viewing
    println(s"Account ID: ${account.accountId}")
    println(s"Customer: ${account.customer.name}")
    println(s"Balance: ${account.balance}")
    // Additional information can be added
  }
}
