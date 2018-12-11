package models;

import annotations.Attribute;
import annotations.Model;
import annotations.OneToOne;
import repositories.AccountRepository;

import java.security.InvalidParameterException;

/**
 * Transaction model. Use to access data concerning users transactions (transfers / withdraws)
 * @author Antoine FORET
 * @version 1.0
 */
@Model(table = "transactions")
public class Transaction extends ModelBase {

    public enum TransactionType {
        Transfer,
        Withdraw
    }

    /**
     * The amount of the transaction. Always positive.
     */
    @Attribute(name = "amount")
    protected float amount;

    /**
     * The initiator of the transaction
     */
    @OneToOne(name = "initiator_id", repository = AccountRepository.class)
    protected Account initiator;

    /**
     * The recipient of the transaction
     */
    @OneToOne(name = "recipient_id", repository = AccountRepository.class)
    protected Account recipient;

    /**
     * The type of transaction. Could be either transfer or withdraw. The type field is not modifiable.
     */
    @Attribute(name = "type")
    protected String type;

    /**
     * Default constructor needed by the model annotation. This constructor shouldn't be used directly.
     */
    public Transaction() { }

    /**
     * Constructor to initiate a transfer transaction.
     * @throws InvalidParameterException throw invalid parameter exception if at least one of the parameters is invalid
     * @param sender the initiator of the transfer. This value must be defined and not null.
     * @param receiver the recipient of the transfer. This value must be defined and not null.
     * @param amount the amount of the transfer. This value must be positive
     */
    public Transaction(Account sender, Account receiver, float amount) {
        if (sender == null) throw new InvalidParameterException("Sender shouldn't be null");
        if (receiver == null) throw new InvalidParameterException("Receiver shouldn't be null");
        if (amount <= 0) throw new InvalidParameterException("Amount must be strictly positive");
        this.amount = amount;
        this.initiator = sender;
        this.recipient = receiver;
        this.type = TransactionType.Transfer.name();
    }

    /**
     * Constructor to initiate a withdraw transaction.
     * @throws InvalidParameterException throw invalid parameter exception if at least one of the parameters is invalid
     * @param account the account concerned. This value must be defined and not null.
     * @param amount the amount of the withdraw. This value must be positive.
     */
    public Transaction(Account account, float amount) {
        if (account == null) throw new InvalidParameterException("Account shouldn't be null");
        if (amount <= 0) throw new InvalidParameterException("Amount must be strictly positive");
        this.initiator = account;
        this.recipient = account;
        this.amount = amount;
        this.type = TransactionType.Withdraw.name();
    }

    /**
     * Define the transaction initiator. If the given parameter is invalid no change occur
     * @param initiator the new initiator of the transaction
     */
    public void setInitiator(Account initiator) {
        if (initiator == null) return;
        this.initiator = initiator;
    }

    /**
     * Access to the defined transaction initiator
     * @return the current transaction initiator
     */
    public Account getInitiator() {
        return initiator;
    }

    /**
     * Define the recipient of the transaction. If the given parameter is invalid no change occur.
     * @param recipient the new recipient of the transaction
     */
    public void setRecipient(Account recipient) {
        this.recipient = recipient;
    }

    /**
     * Access the transaction's recipient
     * @return the current transaction recipient
     */
    public Account getRecipient() {
        return recipient;
    }

    /**
     * Define the amount of the transaction. If the amount is not strictly positive, no change occur
     * @param amount the new amount of the transaction
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * Access the amount of the transaction
     * @return the current amount of the transaction
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Get the type of the transaction.
     * @return the type of the transaction
     */
    public TransactionType getType() {
        return TransactionType.valueOf(this.type);
    }
}
