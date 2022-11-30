package Cliente;

import javax.swing.*;

public class MenuTransferencia {
    private JButton buscarButton;
    private JTextField textField1;
    private JButton salirButton;
    private JTable tablaTransferencias;
    public JPanel panel_main;
JFrame frame = null;
    public MenuTransferencia(JFrame menuFrame) {
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
    }
}
