package metadatafetch;

import foreignkeyeditorwithbutton.ButtonEditor;
import foreignkeyeditorwithbutton.ButtonRenderer;
import studentsmanageproject.StudentManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.Vector;
import java.sql.*;

import static java.awt.BorderLayout.*;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static studentsmanageproject.OptimizeColumnRendering.ocr;
import static studentsmanageproject.StudentManagementSystem.conn;

public class ViewTableDialog extends JDialog {
    List<String> columnNames = new Vector<>();
    List<Class<?>> columnTypes = new Vector<>();
    List<List<Object>> data = new Vector<>();
    List<List<String>> primaryKeys = new ArrayList<>();
    List<List<String>> foreignKeys = new ArrayList<>();
    final String pkClause;
    String insertClause;
    String updateClause;

    public ViewTableDialog(Frame parent, String title, ModalityType modal, String tableName) {
        super(parent, title, modal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        System.out.println("窗口构造");

        FetchData.fetchData(columnNames, columnTypes, data, tableName);

        FetchData.fetchTableInformation(primaryKeys, foreignKeys, tableName);

        StringJoiner pkStringJoiner = new StringJoiner(" AND ");
        for (List<String> pk : primaryKeys) pkStringJoiner.add("`" + pk.get(0) + "`" + " = ?");
        pkClause = pkStringJoiner.toString();

        DefaultTableModel tableModel = new DefaultTableModel((Vector) data, (Vector) columnNames);

        JTable table = new JTable(tableModel);

        // 设置表格行高
        table.setRowHeight(30);

        for(List<String> foreignKey : foreignKeys){
            TableColumn pkcolumn = table.getColumnModel().getColumn(columnNames.indexOf(foreignKey.get(0)));
            pkcolumn.setCellRenderer(new ButtonRenderer());
            pkcolumn.setCellEditor(new ButtonEditor(new JTextField(), this, foreignKey.get(2)));
        }

        setSize(ocr(table), 600);
        setLocationRelativeTo(parent);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton("删除");
        JButton addButton = new JButton("插入");
        JButton submitButton = new JButton("确认");
        JButton cancelButton = new JButton("取消");
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, SOUTH);

        // 添加按钮事件监听器
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        });

        addButton.addActionListener(e -> tableModel.addRow(new Object[]{"", "", ""}));
/*
        submitButton.addActionListener(e -> {
            // 将缓存的更改应用到源数据
            for (Object[] rowData : rowDataCache) {
                int index = (int) rowData[0];
                model.setValueAt(rowData[1], index, 1);
                model.setValueAt(rowData[2], index, 2);
            }
            rowDataCache.clear();
        });

        cancelButton.addActionListener(e -> {
            // 还原表格到修改前的状态
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }
            for (Object[] rowData : rowDataCache) {
                model.addRow(rowData);
            }
            rowDataCache.clear();
        });

         */


        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column != -1) {
                System.out.println(row + "行" + column + "列有更改:" + tableModel.getValueAt(row, column));
            }
        });
        //监听表格模型的更改事件
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            // 判断是否为新增行
            if (column != -1) {
                Object newData = tableModel.getValueAt(row, column);
                // 获取更新的列名
                String columnName = tableModel.getColumnName(column);
                String query = "SELECT * FROM `" + tableName + "` WHERE " + pkClause;
                String update = "UPDATE `"+ tableName +"` SET `" + columnName + "` = ? WHERE " + pkClause;
                //String insert = "INSERT INTO `"　+ tableName + "` (`" + columnName + "`) VALUES (?)";
                try {
                    try (PreparedStatement queryStmt = conn.prepareStatement(query);
                         PreparedStatement updateStmt = conn.prepareStatement(update);
                         //PreparedStatement insertStmt = conn.prepareStatement(execute);
                         ) {
                        // 设置更新行的主键
                        for (int i = 0; i < primaryKeys.size(); i++) {
                            queryStmt.setObject(i + 1, tableModel.getValueAt(row, columnNames.indexOf(primaryKeys.get(i).get(0))));
                            updateStmt.setObject(i + 2, tableModel.getValueAt(row, columnNames.indexOf(primaryKeys.get(i).get(0))));
                        }
                        ResultSet queryRs = queryStmt.executeQuery();

                        // 设置新值
                        updateStmt.setObject(1, newData);

                        int executeRs = updateStmt.executeUpdate();

                        if (executeRs > 0) {
                            JOptionPane.showMessageDialog(this, "更新成功: " + newData);
                        } else if (primaryKeys.size() <= 1){
                            JOptionPane.showMessageDialog(this, "更新失败");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    StudentManagementSystem.showMessageDialog(this, "数据库更新错误: " + ex.getMessage(), "消息", INFORMATION_MESSAGE);
                }
            }
        });

        setVisible(true);
    }
}
