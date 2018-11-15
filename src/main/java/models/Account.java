package models;

import annotations.Attribute;
import annotations.Model;

@Model(table = "accounts")
public class Account extends ModelBase {

    @Attribute(name = "identifier")
    public String identifier;

    @Attribute(name = "password")
    protected String password;

    @Attribute(name = "balance")
    public float balance;

    public Account() {

    }

    public Account(String identifier, String password, float balance) {
        this.identifier = identifier;
        this.password = password;
        this.balance = balance;
    }
}
