package Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;

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

    public JTextField getTextoTotal() {
        return textoTotal;
    }

    public void setTextoTotal(JTextField textoTotal) {
        this.textoTotal = textoTotal;
    }

    public JButton getSalirButton() {
        return salirButton;
    }

    public void setSalirButton(JButton salirButton) {
        this.salirButton = salirButton;
    }


    public void setTablaCuentas() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nº Cuenta");
        model.addColumn("Saldo");
        model.setRowCount(20);
        tablaCuentas.setModel(model);
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
        hacerTransferenciaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        spinnerCantidad.addComponentListener(new ComponentAdapter() {
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
