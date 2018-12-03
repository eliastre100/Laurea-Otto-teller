package models;

import annotations.Attribute;
import annotations.Model;
import annotations.OneToOne;
import repositories.AccountRepository;

@Model(table = "transactions")
public class Transaction extends ModelBase {

    @Attribute(name = "amount")
    public float amount;

    @OneToOne(name = "sender_id", repository = AccountRepository.class)
    public Account sender;

    @OneToOne(name = "receiver_id", repository = AccountRepository.class)
    public Account receiver;

    public Transaction() {

    }

    public Transaction(Account sender, Account receiver, float amount) {
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
    }
}
