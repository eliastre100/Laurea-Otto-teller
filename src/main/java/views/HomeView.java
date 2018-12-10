package views;

import javax.swing.*;

public class HomeView extends JFrame {

    public HomeView() {
        super("Otto-Teller Home");

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        setBounds(0, 0, 350, 200);
        setLocationRelativeTo(null);

        setVisible(true);
    }
}
