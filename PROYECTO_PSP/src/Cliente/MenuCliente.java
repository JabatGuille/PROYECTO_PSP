package Cliente;

import javax.swing.*;

public class MenuCliente {
    private JButton verSaldoButton;
    public JPanel panel_main;
    private JButton verTransferenciasButton;
    private JButton salirButton;
    JFrame frame = null;

    /**
     * Constructor de la vista para poder mostrar los datos y movimiento de los botones
     *
     * @param menuFrame
     */
    public MenuCliente(JFrame menuFrame) {
        salirButton.addActionListener(e -> {
            frame = menuFrame;
            JFrame loginFrame = new JFrame("LOGIN");
            loginFrame.setContentPane(new MainCliente(loginFrame).panel_main);
            loginFrame.pack();
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setLocationRelativeTo(null);
            frame.setVisible(false);
            loginFrame.setVisible(true);
        });
        verSaldoButton.addActionListener(e -> {
            frame = menuFrame;
            JFrame saldoFrame = new JFrame("SALDO");
            saldoFrame.setContentPane(new VistaSaldo(saldoFrame).panel_main);
            saldoFrame.pack();
            saldoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            saldoFrame.setLocationRelativeTo(null);
            frame.setVisible(false);
            saldoFrame.setVisible(true);
        });
        verTransferenciasButton.addActionListener(e -> {
            frame = menuFrame;
            JFrame MenuFrame = new JFrame("TRASFENRENCIAS");
            MenuFrame.setContentPane(new MenuTransferencia(MenuFrame).panel_main);
            MenuFrame.pack();
            MenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(false);
            MenuFrame.setLocationRelativeTo(null);
            MenuFrame.setVisible(true);
        });
    }
}
