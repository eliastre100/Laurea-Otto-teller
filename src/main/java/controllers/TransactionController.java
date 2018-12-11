package controllers;

import models.Account;
import models.Transaction;
import views.TransactionsView;

import java.util.List;

/**
 * Transaction controller. Used to display the transaction summary to the user. It doesn't contain any logic but to keep our code clean, we split the logic and display parts anyway.
 * @author Antoine FORET
 * @version 1.0
 */
public class TransactionController {

    /**
     * The actual view
     */
    TransactionsView view;

    /**
     * Initiate the view to display all the transactions.
     * @param account the account we display the transactions
     * @param transactions the transaction list from the user.
     */
    public TransactionController(Account account, List<Transaction> transactions) {
        this.view = new TransactionsView(account, transactions);
    }
}
