package Cliente;

import Clases.Cuenta;
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
import java.util.HashMap;

public class VistaSaldo {
    public JPanel panel_main;
    private JTextField textoTotal;
    private JButton salirButton;
    private JTable tablaCuentas;
    private JButton hacerTransferenciaButton;
    private JTextField DestinoText;
    private JTextField OrigenText;
    private JSpinner spinnerCantidad;
    JFrame frame = null;
    HashMap<Integer, Cuenta> cuentaHashMap;

    public JTextField getTextoTotal() {
        return textoTotal;
    }

    public void setTextoTotal(double total) {
        this.textoTotal.setText(String.valueOf(total));
    }

    public JButton getSalirButton() {
        return salirButton;
    }

    public void setSalirButton(JButton salirButton) {
        this.salirButton = salirButton;
    }

    /**
     * Rellena la tabla de las cuentas
     */
    public void setTablaCuentas() {
        try {
            cuentaHashMap = new HashMap<>();
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            };
            model.addColumn("Nº Cuenta");
            model.addColumn("Saldo");
            Socket cliente = new Socket("localhost", 5555);
            ObjectOutputStream outObjeto = new ObjectOutputStream(cliente.getOutputStream());
            ObjectInputStream inObjeto = new ObjectInputStream(cliente.getInputStream());
            outObjeto.writeObject("LISTARCUENTAS");
            Singleton singleton = Singleton.getInstance();
            singleton.secretKey = (SecretKey) inObjeto.readObject();
            Encryption.encriparDNI(outObjeto);
            int totalCuentas = (int) inObjeto.readObject();
            ArrayList<Cuenta> cuentas = Encryption.desEncriparListaCuentas(totalCuentas, inObjeto);
            double total = 0;
            for (int i = 0; i < cuentas.size(); i++) {
                model.addRow(new Object[]{cuentas.get(i).getIBAN(), cuentas.get(i).getDinero()});
                total = total + cuentas.get(i).getDinero();
                cuentaHashMap.put(cuentas.get(i).getIBAN(), new Cuenta(cuentas.get(i).getIBAN(), cuentas.get(i).getDinero()));
            }
            setTextoTotal(total);
            tablaCuentas.setModel(model);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error desconocido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error Con el servidor", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Constructor de la vista
     *
     * @param saldoFrame
     */
    public VistaSaldo(JFrame saldoFrame) {
        setTablaCuentas();
        SpinnerModel modeltau = new SpinnerNumberModel(0.00, 0.00, 10000.00, 1.00);
        spinnerCantidad.setModel(modeltau);
        ((JSpinner.NumberEditor) spinnerCantidad.getEditor()).getFormat().setMaximumFractionDigits(8);
        salirButton.addActionListener(e -> {
            frame = saldoFrame;
            JFrame MenuFrame = new JFrame("MENU");
            MenuFrame.setContentPane(new MenuCliente(MenuFrame).panel_main);
            MenuFrame.pack();
            MenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(false);
            MenuFrame.setLocationRelativeTo(null);
            MenuFrame.setVisible(true);
        });
        hacerTransferenciaButton.addActionListener(e -> {
            if (cuentaHashMap.containsKey(Integer.parseInt(OrigenText.getText()))) {
                if (!OrigenText.getText().equals("") && !DestinoText.getText().equals("") && !spinnerCantidad.getValue().equals(0)) {
                    try {
                        Singleton singleton = Singleton.getInstance();
                        Socket cliente = new Socket("localhost", 5555);
                        ObjectOutputStream outObjeto = new ObjectOutputStream(cliente.getOutputStream());
                        ObjectInputStream inObjeto = new ObjectInputStream(cliente.getInputStream());
                        outObjeto.writeObject("HACER_TRANSFERENCIA");
                        singleton.secretKey = (SecretKey) inObjeto.readObject();
                        Encryption.encriparHacerTransferencia(new Transaccion(singleton.DNI, Integer.parseInt(OrigenText.getText()), Integer.parseInt(DestinoText.getText()), Double.parseDouble(spinnerCantidad.getValue().toString())), outObjeto);
                        JOptionPane.showMessageDialog(null, "Intentando hacer transferencia");
                        byte[] eCodigo = (byte[]) inObjeto.readObject();
                        int codigo = Encryption.desEncriptarCodigo(eCodigo);
                        JOptionPane.showMessageDialog(null, "El codigo para realizar la transferencia es " + codigo);
                        Encryption.encriptarCodigo(String.valueOf(codigo), outObjeto);
                        boolean comprobacion = (boolean) inObjeto.readObject();
                        if (!comprobacion) {
                            JOptionPane.showMessageDialog(null, "Error con el codigo", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        setTablaCuentas();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error desconocido", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, "Error Con el servidor", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Faltan datos", "Error Transferencia", JOptionPane.WARNING_MESSAGE);

                }
            } else {
                JOptionPane.showMessageDialog(null, "Debe usar como cuenta origen una de su propiedad", "Error Transferencia", JOptionPane.ERROR_MESSAGE);
            }
            setTablaCuentas();
        });
    }
}
