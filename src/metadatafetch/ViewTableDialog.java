package metadatafetch;

import foreignkeyeditorwithbutton.ButtonEditor;
import foreignkeyeditorwithbutton.ButtonRenderer;
import studentsmanageproject.StudentManagementSystem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.Vector;
import java.sql.*;

import static java.awt.BorderLayout.*;
import static java.awt.Toolkit.getDefaultToolkit;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static metadatafetch.FetchData.*;
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
    String iconDirectory = "C:/Users/21056/IdeaProjects/DatabaseProject/iconImage/";

    static int rowIndex = 0;
    static int columnIndex = 1;
    static int newData = 2;
    public ViewTableDialog(Frame parent, String title, ModalityType modal, String tableName) {
        super(parent, title, modal);
        setIconImage((new ImageIcon
                ( iconDirectory + "ViewTableDialog.png")).getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        FetchData.fetchData(columnNames, columnTypes, data, tableName);

        FetchData.fetchTableInformation(primaryKeys, foreignKeys, tableName);

        StringJoiner pkStringJoiner = new StringJoiner(" AND ");
        for (List<String> pk : primaryKeys) pkStringJoiner.add("`" + pk.get(COLUMN_NAME) + "`" + " = ?");
        pkClause = pkStringJoiner.toString();

        DefaultTableModel tableModel = new DefaultTableModel((Vector) data, (Vector) columnNames);

        JTable table = new JTable(tableModel);

        table.isEditing();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, CENTER);

        for(List<String> foreignKey : foreignKeys){
            int index = columnNames.indexOf(foreignKey.get(FKCOLUMN_NAME));
            TableColumn fkcolumn = table.getColumnModel().getColumn(index);
            fkcolumn.setCellRenderer(new ButtonRenderer());
            fkcolumn.setCellEditor(new ButtonEditor(new JTextField(), this, table,
            tableName, foreignKey.get(FKCOLUMN_NAME), foreignKey.get(FK_NAME),
            foreignKey.get(PKTABLE_NAME), foreignKey.get(PKCOLUMN_NAME)));
            TableColumn column = table.getColumnModel().getColumn(index);
            column.setPreferredWidth(column.getPreferredWidth() + 3);
        }

        // 设置表格行高
        table.setRowHeight(34);

        setMinimumSize(new Dimension(ocr(table), 600));

        table.revalidate();
        table.repaint();
        setLocationRelativeTo(parent);
        Insets insets = new Insets(4,4,4,4);
        // 创建按钮面板
        Image originalImage = getDefaultToolkit().getImage("path/to/large/image.png");
        Icon scaledIcon = new ImageIcon(originalImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        JButton addButton = new JButton(new ImageIcon((getDefaultToolkit().getImage(iconDirectory + "addButton.png")).getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        addButton.setToolTipText("添加记录");
        addButton.setMargin(insets);
        JButton deleteButton = new JButton(new ImageIcon((getDefaultToolkit().getImage(iconDirectory + "deleteButton.png")).getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        deleteButton.setToolTipText("删除记录");
        deleteButton.setMargin(insets);
        JButton submitButton = new JButton(new ImageIcon((getDefaultToolkit().getImage(iconDirectory + "submitButton.png")).getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        submitButton.setToolTipText("应用更改");
        submitButton.setMargin(insets);
        JButton cancelButton = new JButton(new ImageIcon((getDefaultToolkit().getImage(iconDirectory + "cancelButton.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        cancelButton.setToolTipText("放弃更改");
        cancelButton.setMargin(new Insets(6,6,6,6));
        JButton flashButton = new JButton(new ImageIcon((getDefaultToolkit().getImage(iconDirectory + "flashButton.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        flashButton.setToolTipText("刷新");
        flashButton.setMargin(new Insets(6,6,6,6));
        buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(flashButton);
        buttonPanel.add(Box.createHorizontalGlue());
        add(buttonPanel, SOUTH);

        // 添加按钮事件监听器
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        });

        Object[] o = new Object[columnNames.size()];

        addButton.addActionListener(e -> {
            if(table.isEditing())table.getCellEditor(table.getSelectedRow(), table.getEditingColumn()).stopCellEditing();
            tableModel.addRow(o);
            table.setEditingColumn(0);
            table.setColumnSelectionInterval(0, 0);
            int rowCount = tableModel.getRowCount() - 1;
            table.setEditingRow(rowCount);
            table.setRowSelectionInterval(rowCount, rowCount);
            table.editCellAt(rowCount, 0); // 开始编辑指定的单元格
        });

        List<List<Object>> valueCache = new ArrayList<>();
/*
        submitButton.addActionListener(e -> {
            // 将缓存的更改应用到源数据
            for (List<Object> rowData : valueCache) {
                for (Object value : rowData) {
                    tableModel.setValueAt(value, valueCache.indexOf(rowData), rowData.indexOf(value));
                }
                // 判断是否为新增行
                if (column != -1) {
                    Object newData = tableModel.getValueAt(row, column);
                    // 获取更新的列名
                    String columnName = tableModel.getColumnName(column);
                    String query = "SELECT * FROM `" + tableName + "` WHERE " + pkClause;
                    String update = "UPDATE `" + tableName + "` SET `" + columnName + "` = ? WHERE " + pkClause;
                    //String insert = "INSERT INTO `"　+ tableName + "` (`" + columnName + "`) VALUES (?)";
                    try {
                        try (PreparedStatement queryStmt = conn.prepareStatement(query);
                             PreparedStatement updateStmt = conn.prepareStatement(update);
                             //PreparedStatement insertStmt = conn.prepareStatement(execute);
                        ) {
                            // 设置更新行的主键
                            for (int i = 0; i < primaryKeys.size(); i++) {
                                queryStmt.setObject(i + 1, tableModel.getValueAt(row, columnNames.indexOf(primaryKeys.get(i).get(COLUMN_NAME))));
                                updateStmt.setObject(i + 2, tableModel.getValueAt(row, columnNames.indexOf(primaryKeys.get(i).get(COLUMN_NAME))));
                            }
                            ResultSet queryRs = queryStmt.executeQuery();

                            // 设置新值
                            updateStmt.setObject(1, newData);

                            int executeRs = updateStmt.executeUpdate();

                            if (executeRs > 0) {
                                //JOptionPane.showMessageDialog(this, "更新成功: " + newData);
                                System.out.println("更新成功: " + newData);
                            } else if (primaryKeys.size() <= 1) {
                                //JOptionPane.showMessageDialog(this, "更新失败");
                                System.out.println("更新失败: ");
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        StudentManagementSystem.showMessageDialog(this, "数据库更新错误: " + ex.getMessage(), "消息", INFORMATION_MESSAGE);
                    }
                }
            }

            valueCache.clear();
        });

 */

        cancelButton.addActionListener(e -> {
            // 还原表格到修改前的状态
        });

        flashButton.addActionListener(e -> {
            FetchData.fetchData(columnNames, columnTypes, data, tableName);
            tableModel.setDataVector((Vector) data, (Vector) columnNames);
        });

        //监听表格模型的更改事件
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column != -1) {
                Object value = tableModel.getValueAt(row, column);
                List<Object> valueInformation = new ArrayList<>();
                valueInformation.add(row);
                valueInformation.add(column);
                valueInformation.add(value);
                valueCache.add(valueInformation);
                System.out.println((row+1) + "行" + (column+1) + "列有更改:" + tableModel.getValueAt(row, column));
            } else System.out.println((row+1) + "行" + "新添加");
        });
        setVisible(true);
    }
}
class CacheData{
    int row;
    int column;
    Object data;
}
