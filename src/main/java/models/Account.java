package models;

import annotations.Attribute;
import annotations.Model;

/**
 * Account model. Use to access data concerning users accounts
 * @author Antoine FORET
 * @version 1.0
 */
@Model(table = "accounts")
public class Account extends ModelBase {

    /**
     * User identifier
     */
    @Attribute(name = "identifier")
    protected String identifier;

    /**
     * User password. There is no accessor for this field as we don't want the password to be accessible to the user.
     */
    @Attribute(name = "password")
    protected String password;

    /**
     * User balance
     */
    @Attribute(name = "balance")
    protected float balance;

    /**
     * Default constructor of the model. Needed with the model annotation
     */
    public Account() { }

    /**
     * Constructor in order to initialize all field directly.
     * @param identifier identifier of the user
     * @param password password of the user
     * @param balance balance of the user
     */
    public Account(String identifier, String password, float balance) {
        this.identifier = identifier;
        this.password = password;
        this.balance = balance;
    }

    /**
     * Access the current user account identifier
     * @return identifier of the user
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Define the user account identifier
     * @param identifier identifier to define for the user account
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Define the user account password.
     * @param password password to define for the user account
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Access the user account balance
     * @return user's current account balance
     */
    public float getBalance() {
        return balance;
    }

    /**
     * Credit the given amount to the user account. If the amount is negative, does nothing. If you want to debit the account see the debit method.
     * @see models.Account#debit(float)
     * @param amount amount to credit to the user account
     */
    public void credit(float amount) {
        if (amount <= 0) return;
        this.balance += amount;
    }

    /**
     * Debit the given amount to the user account. If the amount is negative does nothing. If you want to credit the account see the credit method.
     * @see models.Account#credit(float)
     * @param amount
     */
    public void debit(float amount) {
        if (amount <= 0) return;
        this.balance -= amount;
    }
}
