package Cliente;

import Clases.Cliente;
import Clases.Encryption;
import Clases.Singleton;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PublicKey;
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

    /**
     * Constructor de la vista para el funcionamiento de los botones y tratamiento de datos
     *
     * @param registroFrame
     */
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
            pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$");
            matcher = pattern.matcher(ContraTexto.getText());
            if (!matcher.matches()) {
                JOptionPane.showMessageDialog(null, "Contraseña no valida el formato es de entre 8 y 20 caracteres, obligatorio usar numeros, minusculas, mayusculas y simbolos", "Error Contraseña", JOptionPane.WARNING_MESSAGE);
                ContraTexto.setText("");
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
                        ObjectInputStream inObjeto = new ObjectInputStream(clienteSocket.getInputStream());
                        outObjeto.writeObject("REGISTRO");
                        Singleton singleton = Singleton.getInstance();
                        singleton.clavePublica = (PublicKey) inObjeto.readObject();
                        byte[] mensajeFirmado = (byte[]) inObjeto.readObject();
                        boolean comprobacion = Encryption.verificarRegistroCliente(mensajeFirmado);
                        if (comprobacion) {
                            outObjeto.writeObject(true);
                            singleton.secretKey = (SecretKey) inObjeto.readObject();
                            Encryption.encriptarCliente(cliente, outObjeto);
                            clienteSocket.close();
                            JOptionPane.showMessageDialog(null, "Usted a firmado las politicas de privacidad");
                            JFrame loginFrame = new JFrame("LOGIN");
                            loginFrame.setContentPane(new MainCliente(loginFrame).panel_main);
                            loginFrame.pack();
                            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            loginFrame.setLocationRelativeTo(null);
                            registroFrame.setVisible(false);
                            loginFrame.setVisible(true);
                        } else {
                            clienteSocket.close();
                            JOptionPane.showMessageDialog(null, "La firma no es correcta", "Error Inesperado", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Debe aceptar las politicas de privacidad", "Error Privacidad", JOptionPane.WARNING_MESSAGE);

                }
            }
        });
        politicasDePrivacidadButton.addActionListener(e -> {
            Singleton singleton = Singleton.getInstance();
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(new URI(singleton.url));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error inesperado", "Error Inesperado", JOptionPane.WARNING_MESSAGE);
                    } catch (URISyntaxException ex) {
                        JOptionPane.showMessageDialog(null, "URL no aceptada", "Error Privacidad", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
    }
}
