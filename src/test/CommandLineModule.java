package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandLineModule extends JFrame {
    private JTextArea commandArea;
    private JTextArea resultArea;
    private JButton executeButton;
    private Connection connection;
    private List<String> commandHistory;
    private int historyIndex;

    public CommandLineModule(Connection connection) {
        this.connection = connection;
        this.commandHistory = new ArrayList<>();
        this.historyIndex = 0;
        initUI();
    }

    private void initUI() {
        setTitle("SQL Command Line");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        commandArea = new JTextArea(5, 70);
        resultArea = new JTextArea(20, 70);
        resultArea.setEditable(false);

        executeButton = new JButton("Execute");
        executeButton.addActionListener(new ExecuteButtonListener());

        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.add(new JScrollPane(commandArea), BorderLayout.CENTER);
        commandPanel.add(executeButton, BorderLayout.EAST);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        getContentPane().add(commandPanel, BorderLayout.NORTH);
        getContentPane().add(resultPanel, BorderLayout.CENTER);

        commandArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                commandAreaKeyPressed(evt);
            }
        });
    }

    private void commandAreaKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
            if (historyIndex > 0) {
                historyIndex--;
                commandArea.setText(commandHistory.get(historyIndex));
            }
        } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
            if (historyIndex < commandHistory.size() - 1) {
                historyIndex++;
                commandArea.setText(commandHistory.get(historyIndex));
            } else {
                commandArea.setText("");
            }
        }
    }

    private class ExecuteButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = commandArea.getText().trim();
            if (!command.isEmpty()) {
                commandHistory.add(command);
                historyIndex = commandHistory.size();
                executeCommand(command);
            }
        }
    }

    private void executeCommand(String command) {
        resultArea.setText(""); // 清空之前的结果

        try (Statement stmt = connection.createStatement()) {
            boolean isResultSet = stmt.execute(command);

            if (isResultSet) {
                ResultSet rs = stmt.getResultSet();
                displayResultSet(rs);
            } else {
                int updateCount = stmt.getUpdateCount();
                resultArea.setText("Update Count: " + updateCount);
            }
        } catch (SQLException e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    private void displayResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= columnCount; i++) {
            result.append(metaData.getColumnName(i)).append("\t");
        }
        result.append("\n");

        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                result.append(rs.getString(i)).append("\t");
            }
            result.append("\n");
        }
        resultArea.setText(result.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Replace this with your actual database connection code
            Connection connection = getDatabaseConnection();
            CommandLineModule ex = new CommandLineModule(connection);
            ex.setVisible(true);
        });
    }

    // Placeholder method for getting a database connection
    private static Connection getDatabaseConnection() {
        // Add your database connection logic here
        try {
            // Example for MySQL database
            String user = "root";
            String password = "210569";
            String url = "jdbc:mysql://192.168.188.135:3306/students_manage";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}