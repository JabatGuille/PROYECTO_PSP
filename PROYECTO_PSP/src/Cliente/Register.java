package Cliente;

import javax.swing.*;

public class Register {
    private JTextField NombreTexto;
    private JTextField ApellidoTexto;
    private JTextField EmailTexto;
    private JTextField UsuarioTexto;
    private JButton registroButton;
    private JSpinner EdadSpinner;
    private JButton salirButton;
    private JPasswordField ContraTexto;
    public JPanel panel_main;

    public Register(JFrame registroFrame) {
        salirButton.addActionListener(e -> {
            JFrame loginFrame = new JFrame("LOGIN");
            loginFrame.setContentPane(new MainCliente().panel_main);
            loginFrame.pack();
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setLocationRelativeTo(null);
            registroFrame.setVisible(false);
            loginFrame.setVisible(true);
        });
    }
}
