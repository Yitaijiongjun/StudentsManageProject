package test;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static test.MySQLSimulator.commandHistory;
import static test.MySQLSimulator.historyIndex;

public class MySQLSimulator extends JFrame {
    CMDTextArea textArea;
    static List<String> commandHistory = new ArrayList<>();
    static int historyIndex = -1;
    String sqlTerminator = ";";

    // MySQL 连接信息
    private static final String USER = "root";
    private static final String PASS = "210569";
    private static final String DB_URL = "jdbc:mysql://192.168.0.100:3306/students_manage";

    public MySQLSimulator() {
        setTitle("MySQL Command Line Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new CMDTextArea();
        textArea.setFont(new Font("等线", Font.BOLD, 20));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton clearButton = new JButton("Clear Screen");
        clearButton.addActionListener(e -> textArea.clear());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(clearButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        textArea.setInputListener(input -> processInput(input));
    }

    private void processInput(String input) {
        if (input.trim().isEmpty()) {
            textArea.appendPrompt();
            return;
        }

        commandHistory.add(input);
        historyIndex = commandHistory.size();
        textArea.append("\n");

        if (input.trim().endsWith(sqlTerminator)) {
            executeSQL(input.trim());
        } else if (input.trim().toLowerCase().startsWith("delimiter ")) {
            changeDelimiter(input.trim());
        } else {
            textArea.append("Incomplete SQL statement. End with \"" + sqlTerminator + "\" to execute.\n");
        }

        textArea.appendPrompt();
    }

    private void executeSQL(String sql) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            boolean isResultSet = stmt.execute(sql);
            if (isResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        textArea.append(metaData.getColumnName(i) + "\t");
                    }
                    textArea.append("\n");
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            textArea.append(rs.getString(i) + "\t");
                        }
                        textArea.append("\n");
                    }
                }
            } else {
                int updateCount = stmt.getUpdateCount();
                textArea.append("Query OK, " + updateCount + " rows affected.\n");
            }
        } catch (SQLException e) {
            textArea.append("Error executing SQL: " + e.getMessage() + "\n");
        }
    }

    private void changeDelimiter(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length == 2) {
            sqlTerminator = parts[1];
            textArea.append("Delimiter changed to " + sqlTerminator + "\n");
        } else {
            textArea.append("Error: Invalid delimiter command.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MySQLSimulator().setVisible(true));
    }

    interface InputListener {
        void onInput(String input);
    }
}

class CMDTextArea extends JTextArea {
    private static final String PROMPT = "mysql> ";
    private MySQLSimulator.InputListener inputListener;

    public CMDTextArea() {
        appendPrompt();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    handleEnterKey();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    e.consume();
                    showPreviousCommand();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.consume();
                    showNextCommand();
                }
            }
        });
    }

    public void setInputListener(MySQLSimulator.InputListener listener) {
        this.inputListener = listener;
    }

    public void appendPrompt() {
        append(PROMPT);
        setCaretPosition(getDocument().getLength());
    }

    public void clear() {
        setText("");
        appendPrompt();
    }

    private void handleEnterKey() {
        String input = getText().substring(PROMPT.length()).trim();
        if (inputListener != null) {
            inputListener.onInput(input);
        }
    }

    private void showPreviousCommand() {
        if (historyIndex > 0) {
            historyIndex--;
            replaceCurrentInput(commandHistory.get(historyIndex));
        } else if (historyIndex == 0) {
            replaceCurrentInput(commandHistory.get(historyIndex));
        }
    }

    private void showNextCommand() {
        if (historyIndex < commandHistory.size() - 1) {
            historyIndex++;
            replaceCurrentInput(commandHistory.get(historyIndex));
        } else if (historyIndex == commandHistory.size() - 1) {
            historyIndex++;
            replaceCurrentInput("");
        }
    }

    private void replaceCurrentInput(String command) {
        int promptPosition = getDocument().getLength() - PROMPT.length();
        try {
            getDocument().remove(promptPosition, getDocument().getLength() - promptPosition);
            getDocument().insertString(promptPosition, command, null);
            setCaretPosition(getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

}