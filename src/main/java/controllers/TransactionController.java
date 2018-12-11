package controllers;

import models.Account;
import models.Transaction;
import views.TransactionsView;

import java.util.List;

public class TransactionController {
    Account account;
    List<Transaction> transactions;
    TransactionsView view;

    public TransactionController(Account account, List<Transaction> transactions) {
        this.account = account;
        this.transactions = transactions;
        this.view = new TransactionsView(account, transactions);
    }
}
