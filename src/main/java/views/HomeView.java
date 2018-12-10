package views;

import models.Account;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.HashMap;

public class HomeView extends JFrame {

    static final int width = 500;
    static final int height = 500;

    static final int buttonWidth = (int) ((((float) width * 0.8) / 2.0) * 0.95);
    static final int buttonHeight = (int) ((((float) height * 0.8) / 3) * 0.9);

    static final int columnWidth = (int) (((float) width * 0.8) / 2);
    static final int column1X = (int) ((float) width * 0.1);
    static final int column2X = (int) ((float) width * 0.1) + columnWidth;

    static final int rowHeight = (int) (((float) height * 0.8) / 3);
    static final int row1Y = (int) ((float) height * 0.1);
    static final int row2Y = (int) ((float) height * 0.1) + rowHeight;
    static final int row3Y = (int) ((float) height * 0.1) + 2 * rowHeight;

    public HomeView(Account account, HashMap<String, ActionListener> handlers) {
        super("Otto-Teller Home - " + account.getIdentifier());

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        setBounds(0, 0, width, height);
        setLocationRelativeTo(null);

        this.addSummaryButton(handlers.get("summary"));
        this.addTransactionButton(handlers.get("transactions"));
        this.addWithdrawButton(handlers.get("withdraw"));
        this.addTransferButton(handlers.get("transfer"));
        this.addDeleteAccountButton(handlers.get("delete"));
        this.addLogoutButton(handlers.get("logout"));

        setVisible(true);
    }

    private void addSummaryButton(ActionListener callback) {
        JButton button = new JButton("Summary");
        button.setBounds(column1X, row1Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }


    private void addTransactionButton(ActionListener callback) {
        JButton button = new JButton("Transactions summary");
        button.setBounds(column2X, row1Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    private void addWithdrawButton(ActionListener callback) {
        JButton button = new JButton("Withdraw");
        button.setBounds(column1X, row2Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    private void addTransferButton(ActionListener callback) {
        JButton button = new JButton("Transfer");
        button.setBounds(column2X, row2Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    private void addDeleteAccountButton(ActionListener callback) {
        JButton button = new JButton("Delete account");
        button.setBounds(column1X, row3Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    private void addLogoutButton(ActionListener callback) {
        JButton button = new JButton("Logout");
        button.setBounds(column2X, row3Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    public void showSummary(Account account) {
        JOptionPane.showMessageDialog(this, "Your account " + account.getIdentifier() + " have a balance of " + account.getBalance(), "Account summary", JOptionPane.INFORMATION_MESSAGE);
    }
}
