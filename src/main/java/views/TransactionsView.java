package views;

import models.Account;
import models.Transaction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionsView extends JFrame {

    static final int width = 500;
    static final int height = 500;

    List<Transaction> transactions;

    public TransactionsView(Account account, List<Transaction> transactions) {
        super("Otto-Teller Home - " + account.getIdentifier());

        this.transactions = transactions;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        setBounds(0, 0, width, height);
        setLocationRelativeTo(null);

        this.addTabs(account, transactions);

        setVisible(true);
    }

    private void addTabs(Account account, List<Transaction> transactions) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 0, width, height);
        tabbedPane.addTab("Withdraws", new WithdrawTab(transactions));
        tabbedPane.addTab("Transfers", new TransferTab(account, transactions));
        getContentPane().add(tabbedPane);
    }
}

class WithdrawTab extends JPanel {

    public WithdrawTab(List<Transaction> transactions) {
        this.addList(transactions);
        setVisible(true);
    }

    private void addList(List<Transaction> transactions) {
        JList list = new JList(WithdrawFormater.format(transactions));
        add(list);
    }
}

class TransferTab extends JPanel {

    public TransferTab(Account account, List<Transaction> transactions) {
        JList list = new JList(TransferFormater.format(account, transactions));
        add(list);
    }
}

class WithdrawFormater {
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

class TransferFormater {
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