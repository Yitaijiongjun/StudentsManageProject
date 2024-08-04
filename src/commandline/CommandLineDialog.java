package commandline;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static studentsmanageproject.StudentManagementSystem.conn;

public class CommandLineDialog extends JDialog {
    private JTextArea commandArea;
    private JTextPane textPane;
    private JButton executeButton;
    private JButton clearButton;
    private List<String> commandHistory = new ArrayList<>();
    private int historyIndex = 0;

    StringBuilder text = new StringBuilder();
    String prompt = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>MySQL 常用命令提示</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: Arial, sans-serif;\n" +
            "            margin: 20px;\n" +
            "            background-color: #f4f4f4;\n" +
            "        }\n" +
            "        h1 {\n" +
            "            color: #333;\n" +
            "        }\n" +
            "        .command {\n" +
            "            background-color: #282c34;\n" +
            "            color: #61dafb;\n" +
            "            padding: 10px;\n" +
            "            border-radius: 5px;\n" +
            "            margin-bottom: 10px;\n" +
            "        }\n" +
            "        .description {\n" +
            "            color: #555;\n" +
            "            margin-bottom: 20px;\n" +
            "        }\n" +
            "        hr {\n" +
            "            border: none;\n" +
            "            height: 1px;\n" +
            "            background-color: #ddd;\n" +
            "            margin: 20px 0;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h1>MySQL 常用命令提示</h1>\n" +
            "    <div class=\"description\"></div>\n" +
            "\n" +
            "    <div class=\"command\">\n" +
            "        <strong>显示数据库：</strong><br>\n" +
            "        <code>SHOW DATABASES;</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>选择数据库：</strong><br>\n" +
            "        <code>USE database_name;</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>显示表：</strong><br>\n" +
            "        <code>SHOW TABLES;</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>描述表结构：</strong><br>\n" +
            "        <code>DESCRIBE table_name;</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>创建数据库：</strong><br>\n" +
            "        <code>CREATE DATABASE database_name;</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>删除数据库：</strong><br>\n" +
            "        <code>DROP DATABASE database_name;</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>创建表：</strong><br>\n" +
            "        <code>CREATE TABLE table_name (column1 datatype, column2 datatype, ...);</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>删除表：</strong><br>\n" +
            "        <code>DROP TABLE table_name;</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>插入数据：</strong><br>\n" +
            "        <code>INSERT INTO table_name (column1, column2, ...) VALUES (value1, value2, ...);</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>更新数据：</strong><br>\n" +
            "        <code>UPDATE table_name SET column1 = value1, column2 = value2, ... WHERE condition;</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>删除数据：</strong><br>\n" +
            "        <code>DELETE FROM table_name WHERE condition;</code>\n" +
            "    </div>\n" +
            "    <div class=\"command\">\n" +
            "        <strong>查询数据：</strong><br>\n" +
            "        <code>SELECT column1, column2, ... FROM table_name WHERE condition;</code>\n" +
            "    </div>\n" +
            "\n" +
            "    <hr>\n" +
            "\n" +
            "    <p style=\"margin-top: 0; color: #888;\"></p>\n" +
            "</body>\n" +
            "</html>\n";

    public CommandLineDialog(Frame parent, String title, ModalityType modal) {
        super(parent, title, modal);
        setSize(2400, 1500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        commandArea = new JTextArea(7, 70);
        commandArea.setText("select * from 学生");
        commandArea.setFont(new Font("SimHei", Font.PLAIN, 24));
        textPane = new JTextPane();
        textPane.setForeground(Color.GREEN);
        textPane.setCaretColor(Color.RED);
        textPane.setContentType("text/html");
        text.append(prompt);
        textPane.setText(text.toString());
        textPane.setEditable(false);

        executeButton = new JButton("执行");
        clearButton = new JButton("清屏");
        executeButton.addActionListener(new ExecuteButtonListener());
        clearButton.addActionListener(e -> {
            text.delete(0, text.length());
            text.append(prompt);
            textPane.setText(text.toString());
        });

        JPanel commandPanel = new JPanel();
        JPanel promptPanel = new JPanel(new BorderLayout());
        promptPanel.add(new Label("mysql>"),BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel(new GridLayout(2,1));
        buttonPanel.add(executeButton);
        buttonPanel.add(clearButton);
        commandPanel.setLayout(new BorderLayout());
        commandPanel.add(promptPanel, BorderLayout.WEST);
        commandPanel.add(new JScrollPane(commandArea), BorderLayout.CENTER);
        commandPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel resultPanel = new JPanel(new BorderLayout());

        resultPanel.add(new JScrollPane(textPane), BorderLayout.CENTER);

        getContentPane().add(commandPanel, BorderLayout.NORTH);
        getContentPane().add(resultPanel, BorderLayout.CENTER);

        commandArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    new ExecuteButtonListener().actionPerformed
                            (new ActionEvent(executeButton, ActionEvent.ACTION_PERFORMED, ""));
                } else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
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
        });
        setVisible(true);
    }
    private class ExecuteButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = commandArea.getText().trim();
            if (!command.isEmpty()) {
                commandHistory.add(command);
                historyIndex = commandHistory.size();
                // 获取当前的HTML内容并保存<body>部分的内容
                int bodyStart = text.indexOf("<body>") + 6;
                int bodyEnd = text.indexOf("</body>");
                String bodyContent = text.substring(bodyStart, bodyEnd).trim();
                // 使用StringBuilder拼接新的内容
                StringBuilder newContent = new StringBuilder(bodyContent);
                newContent.append("<hr>"); // 添加分隔符
                try (Statement stmt = conn.createStatement()) {
                    boolean isResultSet = stmt.execute(command);
                    if (isResultSet) {
                        ResultSet rs = stmt.getResultSet();
                        // 在生成的表格HTML中添加样式
                        newContent.append("<div style=\"font-size:14px; font-weight:bold;\">")
                                .append(generateHtmlTable(rs))
                                .append("</div>");
                    } else {
                        int updateCount = stmt.getUpdateCount();
                        newContent.append("<p style=\"font-size:14px;\">Update Count: ").append(updateCount).append("</p>");
                    }
                } catch (SQLException sqle) {
                    newContent.append("<p style=\"font-size:14px; color:red;\">Error: ").append(sqle.getMessage()).append("</p>");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // 重新构建完整的HTML内容
                String finalContent = "<html><head></head><body>" + newContent.toString() + "</body></html>";
                // 更新textPane的内容
                text.replace(0, text.length(), finalContent);
                textPane.setText(finalContent);
            }
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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CommandLineDialog ex = new CommandLineDialog(getConnection());
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
*/
}

