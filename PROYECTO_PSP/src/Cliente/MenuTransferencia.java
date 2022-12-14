package Cliente;

import Clases.Encryption;
import Clases.Singleton;
import Clases.Transaccion;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MenuTransferencia {
    private JButton buscarButton;
    private JTextField textField1;
    private JButton salirButton;
    private JTable tablaTransferencias;
    public JPanel panel_main;
    private JButton crearCuentaButton;
    JFrame frame = null;

    public MenuTransferencia(JFrame menuFrame) {
        setTablaTransferencias();

        salirButton.addActionListener(e -> {
            frame = menuFrame;
            JFrame MenuFrame = new JFrame("MENU");
            MenuFrame.setContentPane(new MenuCliente(MenuFrame).panel_main);
            MenuFrame.pack();
            MenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(false);
            MenuFrame.setLocationRelativeTo(null);
            MenuFrame.setVisible(true);
        });
        crearCuentaButton.addActionListener(e -> {
            try {
                Socket cliente = new Socket("localhost", 5555);
                ObjectOutputStream outObjeto = new ObjectOutputStream(cliente.getOutputStream());
                ObjectInputStream inObjeto = new ObjectInputStream(cliente.getInputStream());
                outObjeto.writeObject("CREARCUENTA");
                Singleton singleton = Singleton.getInstance();
                singleton.secretKey = (SecretKey) inObjeto.readObject();
                Encryption.encriparDNI(outObjeto);
                JOptionPane.showMessageDialog(null, "Cuenta creada");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        });
    }

    public void setTablaTransferencias() {
        try {
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("IBAN Origen");
            model.addColumn("IBAN destino");
            model.addColumn("dinero");
            Socket cliente = new Socket("localhost", 5555);
            ObjectOutputStream outObjeto = new ObjectOutputStream(cliente.getOutputStream());
            ObjectInputStream inObjeto = new ObjectInputStream(cliente.getInputStream());
            outObjeto.writeObject("VERTRASFERENCIAS");
            Singleton singleton = Singleton.getInstance();
            singleton.secretKey = (SecretKey) inObjeto.readObject();
            Encryption.encriparDNI(outObjeto);
            int totalTransacciones = (int) inObjeto.readObject();
            ArrayList<Transaccion> transaccions = Encryption.desEncriparVerTransferencia(totalTransacciones, inObjeto);
            System.out.println(transaccions.size());
            for (int i = 0; i < transaccions.size(); i++) {
                model.addRow(new Object[]{transaccions.get(i).getIBAN_ORIGEN(), transaccions.get(i).getIBAN_DESTINO(), transaccions.get(i).getPrecio()});
            }
            tablaTransferencias.setModel(model);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
