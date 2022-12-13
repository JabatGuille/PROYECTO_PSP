package Cliente;

import Clases.Cliente;
import Clases.Encryption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register {
    private JTextField DNITexto;
    private JTextField NombreTexto;
    private JTextField ApellidoTexto;
    private JTextField EmailTexto;
    private JButton registroButton;
    private JSpinner EdadSpinner;
    private JButton salirButton;
    private JPasswordField ContraTexto;
    public JPanel panel_main;
    private JCheckBox politicasCheckBox;
    private JButton politicasDePrivacidadButton;

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
        registroButton.addActionListener(e -> {

            Pattern pattern = Pattern.compile("[0-9]{8}[A-Z]");
            Matcher matcher = pattern.matcher(DNITexto.getText());
            if (!matcher.matches()) {
                JOptionPane.showMessageDialog(null, "DNI no valido", "Error DNI", JOptionPane.WARNING_MESSAGE);
                DNITexto.setText("");
            }
            pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(EmailTexto.getText());
            if (!matcher.matches()) {
                JOptionPane.showMessageDialog(null, "Email no valido", "Error Email", JOptionPane.WARNING_MESSAGE);
                EmailTexto.setText("");
            }
            if (!DNITexto.getText().equals("") &&
                    !NombreTexto.getText().equals("") &&
                    !ApellidoTexto.getText().equals("") &&
                    !EdadSpinner.getValue().equals(0) &&
                    !EmailTexto.getText().equals("") &&
                    !ContraTexto.getText().equals("")) {
                if (politicasCheckBox.isSelected()) {

                    try {
                        Cliente cliente = new Cliente(DNITexto.getText(), NombreTexto.getText(), ApellidoTexto.getText(), Integer.parseInt(EdadSpinner.getValue().toString()), EmailTexto.getText(), Encryption.cifra(ContraTexto.getText()));
                        Socket clienteSocket = new Socket("localhost", 5555);
                        ObjectOutputStream outObjeto = new ObjectOutputStream(clienteSocket.getOutputStream());
                        outObjeto.writeObject("REGISTRO");
                        outObjeto.writeObject(cliente);
                        clienteSocket.close();
                        JFrame loginFrame = new JFrame("LOGIN");
                        loginFrame.setContentPane(new MainCliente(loginFrame).panel_main);
                        loginFrame.pack();
                        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        loginFrame.setLocationRelativeTo(null);
                        registroFrame.setVisible(false);
                        loginFrame.setVisible(true);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Debe aceptar las politicas de privacidad", "Error Privacidad", JOptionPane.WARNING_MESSAGE);

                }
            }
        });
        politicasDePrivacidadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)){
                        try {
                            desktop.browse(new URI("https://www.egibide.org/privacidad/#:~:text=EGIBIDE%20facilita%20a%20las%20personas,la%20limitaci%C3%B3n%20de%20su%20tratamiento."));
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "Error inesperado", "Error Inesperado", JOptionPane.WARNING_MESSAGE);
                        } catch (URISyntaxException ex) {
                            JOptionPane.showMessageDialog(null, "URL no aceptada", "Error Privacidad", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });
    }
}
