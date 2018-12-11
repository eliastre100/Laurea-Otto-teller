package controllers;

import models.Account;
import models.Transaction;
import repositories.AccountRepository;
import repositories.TransactionRepository;
import utils.Pair;
import views.HomeView;

import java.awt.event.ActionListener;
import java.util.*;

/**
 * Main controller of the application. This class handle all the logic from actions of an authenticated user.
 * @author Antoine FORET
 * @version 1.0
 */
public class HomeController {

    /**
     * The home view (swing components goes there)
     */
    private HomeView view;

    /**
     * The current account we manage. This property is defined by the constructor and as such is a mandatory parameter for this controller. It is also updated every time we use it.
     */
    private Account account;

    /**
     * Repository to retrieve accounts. It is used to update the account parameter.
     */
    private AccountRepository accountRepository;

    /**
     * Repository to retrieve the account's transactions (the relationship is unidirectional)
     */
    private TransactionRepository transactionRepository;

    /**
     * Constructor of the controller. It need the account it will manage, then it update the view and all it"s properties
     * @param account the account to manage
     */
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

    /**
     * Ask the view to display the account summary
     */
    private void summary() {
        this.updateAccount();
        this.view.showSummary(this.account);
    }

    /**
     * Open the page with the summary of all the user's transactions.
     * We need to remove some duplicate entries as a withdraw is a transaction that have the account as both initiator and recipient.
     */
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

    /**
     * Ask the view the different data need to create a new withdraw, then validate the datas and create the withdraw transaction while updating the account balance.
     */
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

    /**
     * Ask the view the different parameters from the user to create a new transfer, then validate the datas and create the transfer in base while updating the concerned accounts
     */
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

    /**
     * Delete the user account. This action is not reversible and will log the user off.
     */
    private void delete() {
        System.out.println("[INFO] User " + this.account.getIdentifier() + " just deleted it's account!");
        this.account.destroy();
        this.view.setVisible(false);
        this.view.dispose();
    }

    /**
     * Log out the user by closing the windows as no data are persisted else for the session
     */
    private void logout() {
        System.out.println("[INFO] User " + this.account.getIdentifier() + " just logged out");
        this.view.setVisible(false);
        this.view.dispose();
    }

    /**
     * Helper to update the account property and keep it up to date.
     */
    private void updateAccount() {
        this.account = this.accountRepository.find(this.account.getId());
    }
}
