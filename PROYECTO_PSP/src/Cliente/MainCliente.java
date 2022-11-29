package Cliente;

import javax.swing.*;

public class MainCliente {
    public JPanel panel_main;
    private JButton loginButton;
    private JButton registerButton;
    private JTextField usuarioText;
    private JPasswordField contraText;
    private JLabel usuarioLabel;
    private JLabel contraLabel;

    private static JFrame frame = null;

    public MainCliente() {
        registerButton.addActionListener(e -> {
            JFrame registroFrame = new JFrame("REGISTRO");
            registroFrame.setContentPane(new Register(registroFrame).panel_main);
            registroFrame.pack();
            registroFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(false);
            registroFrame.setLocationRelativeTo(null);
            registroFrame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        frame = new JFrame("CLIENTE");
        frame.setContentPane(new MainCliente().panel_main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
