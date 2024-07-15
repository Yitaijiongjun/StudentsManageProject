package metadatafetch;

import studentsmanageproject.StudentManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.Vector;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static studentsmanageproject.OptimizeColumnRendering.ocr;
import static studentsmanageproject.StudentManagementSystem.conn;

public class ViewTableDialog extends JDialog {
    public ViewTableDialog(Frame parent, String title, ModalityType modal, String tableName) {
        super(parent, title, modal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        System.out.println("窗口构造");

        List<String> columnNames = new Vector<>();
        List<Class<?>> columnTypes = new Vector<>();
        List<List<Object>> data = new Vector<>();
        List<List<String>> primaryKeys = new ArrayList<>();
        List<List<String>> foreignKeys = new ArrayList<>();

        FetchData.fetchData(columnNames, columnTypes, data, tableName);

        FetchData.fetchTableInformation(primaryKeys, foreignKeys, tableName);

        final String pkStatement;
        StringJoiner pkClause = new StringJoiner(" AND ");
        for (List<String> pk : primaryKeys) pkClause.add("`" + pk.get(0) + "`" + " = ?");
        pkStatement = pkClause.toString();

        DefaultTableModel tableModel = new DefaultTableModel((Vector) data, (Vector) columnNames);

        JTable table = new JTable(tableModel);

        // 设置表格行高
        table.setRowHeight(30);
/*
        int totalWidth = 0;

        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            TableColumn column = table.getColumnModel().getColumn(columnIndex);
            // 初始为列名宽度
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComponent = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, columnIndex);
            int maxWidth = headerComponent.getPreferredSize().width;
            // 遍历单元格宽度
            for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(rowIndex, columnIndex);
                Component c = table.prepareRenderer(cellRenderer, rowIndex, columnIndex);
                int width = c.getPreferredSize().width;
                maxWidth = Math.max(maxWidth, width);
            }
            column.setPreferredWidth(maxWidth);

            totalWidth += maxWidth + 25;

            // 设置居中显示
            TableCellRenderer renderer = column.getCellRenderer();
            if (renderer == null) {
                renderer = table.getDefaultRenderer(Object.class);
                column.setCellRenderer(renderer);
            }
            if (renderer instanceof DefaultTableCellRenderer) {
                ((DefaultTableCellRenderer) renderer).setHorizontalAlignment(JLabel.CENTER);
            }
        }
 */
        setSize(ocr(table), 900);
        setLocationRelativeTo(parent);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

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
        add(buttonPanel, BorderLayout.SOUTH);

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


        // 监听表格模型的更改事件
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            // 判断是否为新增行
            if (column != -1) {
                Object newData = tableModel.getValueAt(row, column);
                // 获取更新的列名
                String columnName = tableModel.getColumnName(column);
                String query = "SELECT * FROM `" + tableName + "` WHERE " + pkStatement;
                String update = "UPDATE `"+ tableName +"` SET `" + columnName + "` = ? WHERE " + pkStatement;
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
