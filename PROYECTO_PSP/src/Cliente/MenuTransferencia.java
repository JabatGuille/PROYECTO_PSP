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
    private JButton salirButton;
    private JTable tablaTransferencias;
    public JPanel panel_main;
    private JButton crearCuentaButton;
    JFrame frame = null;

    /**
     * Constructor de la vista para poder ver los datos y que funcionen los botones
     *
     * @param menuFrame
     */
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
                JOptionPane.showMessageDialog(null, "Error desconocido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Error de conexion", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });
    }

    /**
     * Rellena los datos de la tabla
     */
    public void setTablaTransferencias() {
        try {
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            };
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
            JOptionPane.showMessageDialog(null, "Error desconocido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error con la BBDD", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
