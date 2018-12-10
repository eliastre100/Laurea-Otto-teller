package controllers;

import models.Account;
import models.Transaction;
import repositories.AccountRepository;
import views.HomeView;

import java.awt.event.ActionListener;
import java.util.HashMap;

public class HomeController {
    HomeView view;
    Account account;
    AccountRepository accountRepository;

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
        this.view = new HomeView(account, handlers);
    }

    private void summary() {
        this.updateAccount();
        this.view.showSummary(this.account);
    }

    private void transactions() {
        System.out.println("transactions");
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
        System.out.println("transfer");
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
