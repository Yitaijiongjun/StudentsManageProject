package commandline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class CommandLineDialog extends JDialog {
    private JTextArea commandArea;
    private JTextPane textPane;
    private JButton executeButton;
    private Connection connection;
    private List<String> commandHistory;
    private int historyIndex;

    public CommandLineDialog(Connection connection) {
        this.connection = connection;
        this.commandHistory = new ArrayList<>();
        this.historyIndex = 0;
        initUI();
    }

    private void initUI() {
        setTitle("SQL Command Line");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        commandArea = new JTextArea(5, 70);
        commandArea.setText("select * from 学生");
        textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);

        executeButton = new JButton("Execute");
        executeButton.addActionListener(new ExecuteButtonListener());

        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.add(new JScrollPane(commandArea), BorderLayout.CENTER);
        commandPanel.add(executeButton, BorderLayout.EAST);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(new JScrollPane(textPane), BorderLayout.CENTER);

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
        textPane.setText(""); // 清空之前的结果
        try (Statement stmt = connection.createStatement()) {
            boolean isResultSet = stmt.execute(command);

            if (isResultSet) {
                ResultSet rs = stmt.getResultSet();
                textPane.setText(generateHtmlTable(rs));
            } else {
                int updateCount = stmt.getUpdateCount();
                textPane.setText("Update Count: " + updateCount);
            }
        } catch (SQLException e) {
            textPane.setText("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateHtmlTable(ResultSet rs) {
        StringBuilder htmlBuilder = new StringBuilder();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            List<List<Object>> data = new ArrayList<>();
            // 获取列名
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
            // 读取结果集到列表
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    row.add((value != null) ? value : "null");
                }
                data.add(row);
            }
            // 生成HTML表格
            htmlBuilder.append("<html><body>");
            htmlBuilder.append("<table border='1' cellspacing='0' style='border-collapse: collapse; border: 1px solid black;'>");
            // 表头
            htmlBuilder.append("<thead><tr>");
            for (String columnName : columnNames) {
                htmlBuilder.append("<th style='text-align: left;padding-left: 8px; padding-right: 8px; '>")
                        .append(columnName)
                        .append("</th>");
            }
            htmlBuilder.append("</tr></thead>");
            // 数据行
            htmlBuilder.append("<tbody>");
            for (List<Object> row : data) {
                htmlBuilder.append("<tr>");
                for (Object value : row) {
                    htmlBuilder.append("<td style='text-align:" + ((value instanceof Number)? "right" : "left") + ";padding-left: 8px; padding-right: 8px; '>")
                            .append(value)
                            .append("</td>");
                }
                htmlBuilder.append("</tr>");
            }
            htmlBuilder.append("</tbody>");
            htmlBuilder.append("</table>");
            htmlBuilder.append("</body></html>");
        } catch (SQLException e) {
            e.printStackTrace();
            return "<html><body>Error generating table</body></html>";
        }
        return htmlBuilder.toString();
    }
    /*
@Deprecated
    private String generateTableData(ResultSet rs) {
        StringBuilder headerBuilder = null;
        StringBuilder separatorBuilder = null;
        StringBuilder rowBuilder;
        StringBuilder dataBuilder = new StringBuilder();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<List<Object>> data = new ArrayList<>();
            List<String> columnNames = new ArrayList<>();
            List<Integer> maxColumnWidths = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                columnNames.add(columnName);
                maxColumnWidths.add(getDisplayWidth(columnName));//初始化长度为表头长度
            }
            // 读取结果集到列表并找出每列最大宽度
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    row.add((value != null) ? value : "null");
                    int width = getDisplayWidth(value.toString());
                    if (width > maxColumnWidths.get(i - 1)) {
                        maxColumnWidths.set(i - 1, width);
                    }
                }
                data.add(row);
            }
            headerBuilder = new StringBuilder();
            headerBuilder.append("| ");
            for (int i = 0; i < columnNames.size(); i++) {
                String columnName = columnNames.get(i);
                headerBuilder.append(columnName);
                int width = getDisplayWidth(columnName);
                int padding = maxColumnWidths.get(i) - width;
                for (int j = 0; j <= padding; j++) {
                    headerBuilder.append(" ");
                }
                headerBuilder.append("| ");
            }
            headerBuilder.append("\n");
            for (List<Object> row : data) {
                rowBuilder = new StringBuilder();
                rowBuilder.append("| ");
                int i = 0;
                for (Object value : row) {
                    int width = getDisplayWidth(value.toString());
                    int padding = maxColumnWidths.get(i) - width;
                    if (value instanceof Number) {
                        for (int j = 0; j <= padding; j++) rowBuilder.append(" ");
                        rowBuilder.append(value);
                    } else {
                        rowBuilder.append(value);
                        for (int j = 0; j <= padding; j++) rowBuilder.append(" ");
                    }
                    rowBuilder.append("| ");
                    i++;
                }
                dataBuilder.append(rowBuilder + "\n");
            }

            separatorBuilder = new StringBuilder();
            separatorBuilder.append("+");
            for (Integer columnLength : maxColumnWidths) {
                separatorBuilder.append(String.format("%-" + (columnLength + 2) + "s+", repeat("-", columnLength + 2)));
            }
            separatorBuilder.append("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return separatorBuilder.toString() + headerBuilder + separatorBuilder + dataBuilder + separatorBuilder;
    }
    private static String repeat(String str, int times) {
        if (times <= 0) {
            return "";
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < times; i++) s.append(str);
        return s.toString();
    }
    // 计算字符串的显示宽度，中文字符按两个宽度计算
    private static int getDisplayWidth(String s) {
        int width = 0;
        for (char c : s.toCharArray()) {
            if (isChinese(c)) width += 2;
            else width += 1;
        }
        return width;
    }
    // 判断是否是中文字符
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

     */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CommandLineDialog ex = new CommandLineDialog(getConnection());
            ex.setVisible(true);
        });
    }

    static Connection getConnection() {
        try {
            String USER = "root";
            String PASSWORD = "210569";
            String URL = "jdbc:mysql://192.168.188.138:3306/students_manage";
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            return null;
        }
    }
}

