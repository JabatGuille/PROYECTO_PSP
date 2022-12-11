package Cliente;

import Clases.Cuenta;
import Clases.Singleton;
import Clases.Transaccion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentAdapter;
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


    public void setTablaCuentas() {
        try {
            cuentaHashMap = new HashMap<>();
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Nº Cuenta");
            model.addColumn("Saldo");
            Socket cliente = new Socket("localhost", 5555);
            ObjectOutputStream outObjeto = new ObjectOutputStream(cliente.getOutputStream());
            ObjectInputStream inObjeto = new ObjectInputStream(cliente.getInputStream());
            outObjeto.writeObject("LISTARCUENTAS");
            Singleton singleton = Singleton.getInstance();
            outObjeto.writeObject(singleton.DNI);
            ArrayList<Cuenta> cuentas = (ArrayList<Cuenta>) inObjeto.readObject();
            System.out.println(cuentas.size());
            double total = 0;
            for (int i = 0; i < cuentas.size(); i++) {
                model.addRow(new Object[]{cuentas.get(i).getIBAN(), cuentas.get(i).getDinero()});
                total = total + cuentas.get(i).getDinero();
                cuentaHashMap.put(cuentas.get(i).getIBAN(), new Cuenta(cuentas.get(i).getIBAN(), cuentas.get(i).getDinero()));
            }
            setTextoTotal(total);
            tablaCuentas.setModel(model);
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

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
            if (cuentaHashMap.containsKey(Integer.parseInt(OrigenText.getText())) && !DestinoText.getText().equals("") && !spinnerCantidad.getValue().equals(0)) {
                try {
                    Socket cliente = new Socket("localhost", 5555);
                    ObjectOutputStream outObjeto = new ObjectOutputStream(cliente.getOutputStream());
                    ObjectInputStream inObjeto = new ObjectInputStream(cliente.getInputStream());
                    outObjeto.writeObject("HACER_TRANSFERENCIA");
                    outObjeto.writeObject(new Transaccion(Integer.parseInt(OrigenText.getText()), Integer.parseInt(DestinoText.getText()), Double.parseDouble(spinnerCantidad.getValue().toString())));
                    JOptionPane.showMessageDialog(null, "Intentando hacer transferencia");
                    setTablaCuentas();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Faltan datos", "Error Transferencia", JOptionPane.WARNING_MESSAGE);

            }
        });
        spinnerCantidad.addComponentListener(new ComponentAdapter() {
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
