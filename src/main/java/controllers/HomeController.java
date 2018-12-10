package controllers;

import models.Account;
import views.HomeView;

import java.awt.event.ActionListener;
import java.util.HashMap;

public class HomeController {
    HomeView view;
    Account account;

    HomeController(Account account) {
        HashMap<String, ActionListener> handlers = new HashMap<>();
        handlers.put("summary", e -> this.summary());
        handlers.put("transactions", e -> this.transactions());
        handlers.put("withdraw", e -> this.withdraw());
        handlers.put("transfer", e -> this.transfer());
        handlers.put("delete", e -> this.delete());
        handlers.put("logout", e -> this.logout());

        this.account = account;
        this.view = new HomeView(account, handlers);
    }

    private void summary() {
        System.out.println("Summary");
    }

    private void transactions() {
        System.out.println("transactions");
    }

    private void withdraw() {
        System.out.println("Withdraw");
    }

    private void transfer() {
        System.out.println("transfer");
    }

    private void delete() {
        System.out.println("delete");
    }

    private void logout() {
        System.out.println("[INFO] User " + this.account.getIdentifier() + " just logged out");
        this.view.setVisible(false);
        this.view.dispose();
    }
}
