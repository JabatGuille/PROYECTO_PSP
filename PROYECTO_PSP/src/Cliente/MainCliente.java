package Cliente;

import Clases.Encryption;
import Clases.Singleton;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainCliente {
    public JPanel panel_main;
    private JButton loginButton;
    private JButton registerButton;
    private JTextField DNIText;
    private JPasswordField contraText;
    private JLabel usuarioLabel;
    private JLabel contraLabel;

    private static JFrame frame = null;

    public MainCliente(JFrame loginFrame) {
        registerButton.addActionListener(e -> {
            frame = loginFrame;
            JFrame registroFrame = new JFrame("REGISTRO");
            registroFrame.setContentPane(new Register(registroFrame).panel_main);
            registroFrame.pack();
            registroFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(false);
            registroFrame.setLocationRelativeTo(null);
            registroFrame.setVisible(true);
        });
        loginButton.addActionListener(e -> {
            if (!DNIText.getText().equals("") && !contraText.getText().equals("")) {
                Pattern pattern = Pattern.compile("[0-9]{8}[A-Z]");
                Matcher matcher = pattern.matcher(DNIText.getText());
                if (!matcher.matches()) {
                    JOptionPane.showMessageDialog(null, "DNI no valido", "Error DNI", JOptionPane.WARNING_MESSAGE);
                    DNIText.setText("");
                } else {
                    frame = loginFrame;
                    try {
                        Socket cliente = new Socket("localhost", 5555);
                        ObjectOutputStream outObjeto = new ObjectOutputStream(cliente.getOutputStream());
                        ObjectInputStream inObjeto = new ObjectInputStream(cliente.getInputStream());
                        outObjeto.writeObject("LOGIN");
                        outObjeto.writeObject(DNIText.getText());
                        outObjeto.writeObject(Encryption.cifra(contraText.getText()));
                        Boolean bol = (Boolean) inObjeto.readObject();
                        cliente.close();
                        Singleton singleton = Singleton.getInstance();
                        singleton.DNI = DNIText.getText();
                        if (bol) {
                            JFrame MenuFrame = new JFrame("MENU");
                            MenuFrame.setContentPane(new MenuCliente(MenuFrame).panel_main);
                            MenuFrame.pack();
                            MenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            frame.setVisible(false);
                            MenuFrame.setLocationRelativeTo(null);
                            MenuFrame.setVisible(true);
                        }else {
                            JOptionPane.showMessageDialog(null, "DNI o contraseña incorrectos", "Error Login", JOptionPane.WARNING_MESSAGE);

                        }
                    } catch (IOException ex) {
                        System.out.println("Error conectar con el servidor");
                    } catch (Exception ex) {
                        System.out.println("Error cifrar contraseñas");
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        frame = new JFrame("LOGIN");
        frame.setContentPane(new MainCliente(frame).panel_main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


}
