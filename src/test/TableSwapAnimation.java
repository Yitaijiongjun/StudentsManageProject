package test;

import javax.swing.*;

public class TableSwapAnimation extends JFrame {
    private JTable table1;
    private JTable table2;
    private JPanel panel;
    private JButton swapButton;
    private Timer timer;
    private int offset;
    private final int step = 10;
    private final int delay = 30;
    private boolean showingTable1 = true;

    public TableSwapAnimation() {
        setTitle("Table Swap Animation");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel(null);
        table1 = createTable(new String[]{"A", "B", "C"}, new Object[][]{{"1", "2", "3"}, {"4", "5", "6"}, {"7", "8", "9"}});
        table2 = createTable(new String[]{"X", "Y", "Z"}, new Object[][]{{"10", "11", "12"}, {"13", "14", "15"}, {"16", "17", "18"}});

        table1.setBounds(0, 0, 500, 250);
        table2.setBounds(500, 0, 500, 250); // Initially off-screen

        panel.add(table1);
        panel.add(table2);

        swapButton = new JButton("Swap Tables");
        swapButton.setBounds(200, 250, 100, 30);
        swapButton.addActionListener(e -> startAnimation());

        panel.add(swapButton);
        add(panel);
    }

    private JTable createTable(String[] columnNames, Object[][] data) {
        return new JTable(data, columnNames);
    }

    private void startAnimation() {
        if (timer != null && timer.isRunning()) {
            return; // Prevent multiple animations at once
        }
        offset = 0;
        timer = new Timer(delay, e -> {
            offset += step;
            if (offset >= 500) {
                offset = 500;
                timer.stop();
                showingTable1 = !showingTable1;
            }
            if (showingTable1) {
                table1.setBounds(-offset, 0, 500, 250);
                table2.setBounds(500 - offset, 0, 500, 250);
            } else {
                table1.setBounds(500 - offset, 0, 500, 250);
                table2.setBounds(-offset, 0, 500, 250);
            }
            panel.repaint();
        });
        timer.start();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TableSwapAnimation().setVisible(true));
    }
}
