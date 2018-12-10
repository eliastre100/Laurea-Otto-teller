package views;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Login view. It display the user interface in order to log the user in. This class don't have any logic. The login logic is handled by the LoginController
 * @see controllers.LoginController
 * @author Antoine FORET
 * @version 1.0
 */
public class LoginView extends JFrame {

    /*
     * Screen position definitions
     */

    /*
     * Window size
     */
    static final int width = 350;
    static final int height = 200;

    /*
     * Login button sizes and positions
     */
    static final int buttonWidth = (int)((double) width / 100.0 * 80.0);
    static final int buttonHeigth = (int)((double) height / 100.0 * 10.0);
    static final int buttonX = (int)((double) width / 100.0 * 10.0);
    static final int buttonY = height - (int)((double) height / 100.0 * 30.0);

    /*
     * Identifier text field position and sizes
     */
    static final int identifierWidth = (int)((double) width / 100.0 * 80.0);
    static final int identifierHeigth = (int)((double) height / 100.0 * 10.0);
    static final int identifierX = (int)((double) width / 100.0 * 10.0);
    static final int identifierY = (int)((double) height / 100.0 * 20.0);

    /*
     * Password text field position and sizes
     */
    static final int passwordWidth = (int)((double) width / 100.0 * 80.0);
    static final int passwordHeigth = (int)((double) height / 100.0 * 10.0);
    static final int passwordX = (int)((double) width / 100.0 * 10.0);
    static final int passwordY = (int)((double) height / 100.0 * 40.0);

    /**
     * Identifier text field for accessing the data
     */
    private JTextField identifierField;

    /**
     * Password text field to access the data from the user
     */
    private JPasswordField passwordField;

    /**
     * Constructor. The class need a callback for the login action.
     * @param loginCallback Action handler for the login action
     */
    public LoginView(ActionListener loginCallback) {
        super("Otto-Teller login");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        setBounds(0, 0, width, height);
        setLocationRelativeTo(null);

        this.addLoginButton(loginCallback);
        this.addIdentifierField();
        this.addPasswordField();

        setVisible(true);
    }

    /**
     * Allow the controller in control of this view to access the username entered by the user
     * @return the username entered by the user
     */
    public String getUsername() {
        return this.identifierField.getText();
    }

    /**
     * Allow the controller in control of this view to access the password entered by the user
     * @return the password entered by the user
     */
    public String getPassword() {
        return this.passwordField.getText();
    }

    /**
     * Display a popup warning the user that the couple username / password entered is invalid. This function is triggered by the controller in charge of this view
     */
    public void showInvalidCredentials() {
        JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Reset all data entered by the user
     */
    public void resetFields() {
        this.identifierField.setText("");
        this.passwordField.setText("");
    }

    /**
     * Add the login button on the user interface. This method is called by the view constructor
     * @param loginCallback the callback to call when the user click on login
     */
    private void addLoginButton(ActionListener loginCallback) {
        JButton submitBtn = new JButton("Login");
        submitBtn.addActionListener(loginCallback);
        submitBtn.setBounds(buttonX, buttonY, buttonWidth, buttonHeigth);
        getContentPane().add(submitBtn);
    }

    /**
     * Add the identifier filed and it's label on the user interface. This method is called by the view constructor
     */
    private void addIdentifierField() {
        JLabel label = new JLabel("Identifier");
        this.identifierField = new JTextField();
        this.identifierField.setBounds(identifierX, identifierY, identifierWidth, identifierHeigth);
        label.setBounds(identifierX, identifierY - identifierHeigth, identifierWidth, identifierHeigth);
        getContentPane().add(this.identifierField);
        getContentPane().add(label);
    }

    /**
     * Add the password filed and it's label on the user interface. This method is called by the view constructor
     */
    private void addPasswordField() {
        JLabel label = new JLabel("Password");
        this.passwordField = new JPasswordField();
        this.passwordField.setBounds(passwordX, passwordY, passwordWidth, passwordHeigth);
        label.setBounds(passwordX, passwordY - passwordHeigth, passwordWidth, passwordHeigth);
        getContentPane().add(this.passwordField);
        getContentPane().add(label);
    }
}
