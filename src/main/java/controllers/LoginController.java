package controllers;

import models.Account;
import repositories.AccountRepository;
import views.LoginView;

import java.util.List;

/**
 * Login controller. This class handle all the logic for the login view.
 * @see views.LoginView
 * @author Antoine FORET
 * @version 1.0
 */
public class LoginController {

    /**
     * The view displayed by the controller
     */
    private LoginView view;

    /**
     * Repository used to retrieve the users and check their username / password.
     */
    private AccountRepository accountRepository;

    /**
     * Constructor of the controller. Initiate all the view and repository elements
     */
    public LoginController() {
        this.view = new LoginView(e -> this.login());
        this.accountRepository = new AccountRepository();
    }

    /**
     * Handle all the login login. If the login succeeded, the method open the account management window, else it ask the view to display the bad credential dialog
     */
    private void login() {
        String username = this.view.getUsername();
        String password = this.view.getPassword();

        List<Account> accounts = accountRepository.findBy("identifier", username);
        if (accounts.size() < 1 || !accounts.get(0).validatePassword(password)) {
            this.view.showInvalidCredentials();
        } else {
            System.out.println("[INFO] User " + accounts.get(0).getIdentifier() + " just logged in");
            this.view.resetFields();
            new HomeController(accounts.get(0));
        }
    }
}
