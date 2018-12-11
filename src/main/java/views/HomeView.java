package views;

import controllers.HomeController;
import models.Account;
import utils.Pair;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * Home view. Main view of the application, allow the user to act on it's account
 * @see controllers.LoginController
 * @author Antoine FORET
 * @version 1.0
 */
public class HomeView extends JFrame {

    /*
     * Screen position definitions
     */

    /*
     * Window size
     */
    static final int width = 500;
    static final int height = 500;

    /*
     * General button size definition
     */
    static final int buttonWidth = (int) ((((float) width * 0.8) / 2.0) * 0.95);
    static final int buttonHeight = (int) ((((float) height * 0.8) / 3) * 0.9);

    /*
     * Grid positions for the X axis
     */
    static final int columnWidth = (int) (((float) width * 0.8) / 2);
    static final int column1X = (int) ((float) width * 0.1);
    static final int column2X = (int) ((float) width * 0.1) + columnWidth;

    /*
     * Grid positions for the Y axis
     */
    static final int rowHeight = (int) (((float) height * 0.8) / 3);
    static final int row1Y = (int) ((float) height * 0.1);
    static final int row2Y = (int) ((float) height * 0.1) + rowHeight;
    static final int row3Y = (int) ((float) height * 0.1) + 2 * rowHeight;

    /**
     * Constructor of the view. Draw all the view elements
     * @param account the account concerned by the view. It is used to have a relevant window title as multiple account could be managed at the same time
     * @param handlers the different Action Handlers for each buttons.
     */
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

    /**
     * Add the summary button (top left)
     * @param callback the action to call when the button is pressed
     */
    private void addSummaryButton(ActionListener callback) {
        JButton button = new JButton("Summary");
        button.setBounds(column1X, row1Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    /**
     * Add the transactions button (top right)
     * @param callback the action to call when the button is pressed
     */
    private void addTransactionButton(ActionListener callback) {
        JButton button = new JButton("Transactions summary");
        button.setBounds(column2X, row1Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    /**
     * Add the withdraw button (middle left)
     * @param callback the action to call when the button is pressed
     */
    private void addWithdrawButton(ActionListener callback) {
        JButton button = new JButton("Withdraw");
        button.setBounds(column1X, row2Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    /**
     * Add the transfer button (middle right)
     * @param callback the action to call when the button is pressed
     */
    private void addTransferButton(ActionListener callback) {
        JButton button = new JButton("Transfer");
        button.setBounds(column2X, row2Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    /**
     * Add the delete account button (bottom left)
     * @param callback the action to call when the button is pressed
     */
    private void addDeleteAccountButton(ActionListener callback) {
        JButton button = new JButton("Delete account");
        button.setBounds(column1X, row3Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    /**
     * Add the logout button (bottom right)
     * @param callback the action to call when the button is pressed
     */
    private void addLogoutButton(ActionListener callback) {
        JButton button = new JButton("Logout");
        button.setBounds(column2X, row3Y, buttonWidth, buttonHeight);
        button.addActionListener(callback);
        getContentPane().add(button);
    }

    /**
     * Show a popup with the account summary
     * @param account the account to show the summary
     */
    public void showSummary(Account account) {
        JOptionPane.showMessageDialog(this, "Your account " + account.getIdentifier() + " have a balance of " + account.getBalance(), "Account summary", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Ask the different information to create a withdraw to the user through a popup window
     * @param account the account that ask a withdraw
     * @return the amount of the withdraw (no value verification by the view but done by the controller)
     * @see HomeController#withdraw()
     */
    public String askWithdraw(Account account) {
        return JOptionPane.showInputDialog(this, "How many would you withdraw from account " + account.getIdentifier() + " (maximum " + account.getBalance() + ") ?", "Account withdraw", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Display the given error to the user through a popup window
     * @param str the error message
     */
    public void showError(String str) {
        JOptionPane.showMessageDialog(this, str, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Ask the different information to create a transfer to the user through a popup window
     * @return The amount of the transfer and the recipient identifier (no value verification by the view but done by the controller)
     * @see HomeController#transfer()
     */
    public Pair<String, Float> askTransfer() {
        JLabel recipientLabel = new JLabel("Recipient");
        JLabel amountLabel = new JLabel("Amount");
        JTextField recipient = new JTextField();
        JTextField amount = new JTextField();
        JComponent[] components = new JComponent[] {
                recipientLabel,
                recipient,
                amountLabel,
                amount
        };
        int result = JOptionPane.showConfirmDialog(this, components, "Transfer order", JOptionPane.DEFAULT_OPTION);
        if (result == -1 || amount.getText().equals(""))
            return new Pair<>("", Float.NaN);
        try {
            return new Pair<>(recipient.getText(), Float.parseFloat(amount.getText()));
        } catch (Exception e) {
            return new Pair<>("", Float.NaN);
        }
    }
}
