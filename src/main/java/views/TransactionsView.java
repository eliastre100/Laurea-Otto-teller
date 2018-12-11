package views;

import models.Account;
import models.Transaction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction view. Allow the user to see the summary of it's transactions
 * @see controllers.TransactionController
 * @author Antoine FORET
 * @version 1.0
 */
public class TransactionsView extends JFrame {

    /*
     * Dimensions of the windows
     */
    static final int width = 500;
    static final int height = 500;


    /**
     * Constructor of the view. add all the elements to the window
     * @param account the account the summary is for
     * @param transactions the list of all the user's transactions
     */
    public TransactionsView(Account account, List<Transaction> transactions) {
        super("Otto-Teller Home - " + account.getIdentifier());

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        setBounds(0, 0, width, height);
        setLocationRelativeTo(null);

        this.addTabs(account, transactions);

        setVisible(true);
    }

    /**
     * Add the tab element to the window
     * @param account the account the summary is for
     * @param transactions the list of all the transactions
     */
    private void addTabs(Account account, List<Transaction> transactions) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 0, width, height);
        tabbedPane.addTab("Withdraws", new WithdrawTab(transactions));
        tabbedPane.addTab("Transfers", new TransferTab(account, transactions));
        getContentPane().add(tabbedPane);
    }
}

/**
 * The withdraw summary tab
 * @author Antoine FORET
 * @version 1.0
 */
class WithdrawTab extends JPanel {

    /**
     * Add all the transactions element on the tab
     * @param transactions the list of all the transactions from the user
     */
    public WithdrawTab(List<Transaction> transactions) {
        JList list = new JList(WithdrawFormater.format(transactions));
        add(list);
        setVisible(true);
    }
}

/**
 * The transfer summary tab
 * @author Antoine FORET
 * @version 1.0
 */
class TransferTab extends JPanel {

    /**
     * Add all the transactions element on the tab
     * @param transactions the list of all the transactions from the user
     */
    public TransferTab(Account account, List<Transaction> transactions) {
        JList list = new JList(TransferFormater.format(account, transactions));
        add(list);
    }
}

/**
 * Formatter to create string list from the transaction list
 * @author Antoine FORET
 * @version 1.0
 */
class WithdrawFormater {

    /**
     * Filter and convert all the transaction into a list of string containing the amount of each withdraws
     * @param transactions the list of all the user's transactions
     * @return a formatted list of withdraws as strings
     */
    static String[] format(List<Transaction> transactions) {
        List<String> res = new ArrayList<>();
        transactions.forEach(elem -> {
            if (elem.getType() == Transaction.TransactionType.Withdraw) {
                res.add(String.format("%f", elem.getAmount()));
            }
        });
        return res.toArray(new String[]{});
    }
}


/**
 * Formatter to create string list from the transaction list
 * @author Antoine FORET
 * @version 1.0
 */
class TransferFormater {

    /**
     * Filter and convert all the transaction into a list of string containing the amount of each transfers
     * @param transactions the list of all the user's transactions
     * @return a formatted list of transfers as strings
     */
    static String[] format(Account account, List<Transaction> transactions) {
        List<String> res = new ArrayList<>();
        transactions.forEach(elem -> {
            if (elem.getType() == Transaction.TransactionType.Transfer) {
                if (elem.getInitiator().getId() == account.getId()) {
                    res.add(String.format("You sent %f to %s", elem.getAmount(), elem.getRecipient().getIdentifier()));
                } else {
                    res.add(String.format("You received %f from %s", elem.getAmount(), elem.getInitiator().getIdentifier()));
                }
            }
        });
        return res.toArray(new String[]{});
    }
}