package controllers;

import models.Account;
import models.Transaction;
import repositories.AccountRepository;
import repositories.TransactionRepository;
import utils.Pair;
import views.HomeView;

import java.awt.event.ActionListener;
import java.util.*;

public class HomeController {
    HomeView view;
    Account account;
    AccountRepository accountRepository;
    TransactionRepository transactionRepository;

    HomeController(Account account) {
        HashMap<String, ActionListener> handlers = new HashMap<>();
        handlers.put("summary", e -> this.summary());
        handlers.put("transactions", e -> this.transactions());
        handlers.put("withdraw", e -> this.withdraw());
        handlers.put("transfer", e -> this.transfer());
        handlers.put("delete", e -> this.delete());
        handlers.put("logout", e -> this.logout());

        this.account = account;
        this.accountRepository = new AccountRepository();
        this.transactionRepository = new TransactionRepository();
        this.view = new HomeView(account, handlers);
    }

    private void summary() {
        this.updateAccount();
        this.view.showSummary(this.account);
    }

    private void transactions() {
        this.updateAccount();
        List<Transaction> transactions = this.transactionRepository.findBy("initiator_id", this.account);
        List<Transaction> recipientTransactions = this.transactionRepository.findBy("recipient_id", this.account);
        List<Transaction> duplicated = new ArrayList<>();
        recipientTransactions.forEach(elem -> {
            if (elem.getInitiator().getId() == elem.getRecipient().getId())
                duplicated.add(elem);
        });
        recipientTransactions.removeAll(duplicated);
        transactions.addAll(recipientTransactions);
        Collections.sort(transactions, (elem1, elem2) -> elem2.getId() - elem1.getId());
        new TransactionController(this.account, transactions);
    }

    private void withdraw() {
        this.updateAccount();

        String amountRequested = this.view.askWithdraw(this.account);
        if (amountRequested == null) return;
        float amount = Float.parseFloat(amountRequested);

        if (amount <= 0 || amount > this.account.getBalance()) {
            this.view.showError("Invalid answer.");
        } else {
            Transaction transaction = new Transaction(this.account, amount);

            account.debit(amount);
            account.save();
            transaction.save();
            System.out.println("[INFO] User " + this.account.getIdentifier() + " just withdraw " + amount + ". New balance " + account.getBalance());
        }
    }

    private void transfer() {
        try {
            this.updateAccount();
            Pair<String, Float> result = this.view.askTransfer();
            Account recipient = (Account) this.accountRepository.findBy("identifier", result.left).get(0);
            float amount = result.right;
            if (recipient != null && amount > 0 && amount <= this.account.getBalance()) {
                Transaction transaction = new Transaction(this.account, recipient, amount);
                this.account.debit(amount);
                recipient.credit(amount);
                recipient.save();
                account.save();
                transaction.save();
            } else {
                this.view.showError("Bad input");
            }
        } catch (IndexOutOfBoundsException e) {
            this.view.showError("Recipient not found");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[ERROR] Unexpected error occurred");
        }
    }

    private void delete() {
        System.out.println("[INFO] User " + this.account.getIdentifier() + " just deleted it's account!");
        this.account.destroy();
        this.view.setVisible(false);
        this.view.dispose();
    }

    private void logout() {
        System.out.println("[INFO] User " + this.account.getIdentifier() + " just logged out");
        this.view.setVisible(false);
        this.view.dispose();
    }

    private void updateAccount() {
        this.account = this.accountRepository.find(this.account.getId());
    }
}
