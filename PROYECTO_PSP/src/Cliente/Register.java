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
        SpinnerModel modeltau = new SpinnerNumberModel(0, 0, 100, 1);
        EdadSpinner.setModel(modeltau);
        ((JSpinner.NumberEditor) EdadSpinner.getEditor()).getFormat().setMaximumFractionDigits(3);
        salirButton.addActionListener(e -> {

            JFrame loginFrame = new JFrame("LOGIN");
            loginFrame.setContentPane(new MainCliente(loginFrame).panel_main);
            loginFrame.pack();
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setLocationRelativeTo(null);
            registroFrame.setVisible(false);
            loginFrame.setVisible(true);
        });
    }
}
