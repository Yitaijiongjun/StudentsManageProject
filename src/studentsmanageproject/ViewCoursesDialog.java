package studentsmanageproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static studentsmanageproject.OptimizeColumnRendering.ocr;
@Deprecated
public class ViewCoursesDialog extends JDialog {
    public ViewCoursesDialog(JFrame parent){
        super(parent, "查看课程", ModalityType.MODELESS);
        ViewCoursesDialog dialog = this; // 创建对外部类实例的引用
        if (StudentManagementSystem.conn == null) {
            JOptionPane.showMessageDialog(dialog, "数据库未连接");
        } else {
            setLayout(new BorderLayout());
            try {
                String query = "SELECT * FROM `课程视图`";
                Statement stmt = StudentManagementSystem.conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                String[] columnNames = {"课程号", "课程名", "开设院系", "学分", "学时", "课时类型"};
                DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
                JTable table = new JTable(tableModel);

                // 设置表格行高
                table.setRowHeight(30); // 设置行高为30像素
/*
                // 设置表格列宽
                table.getColumnModel().getColumn(0).setPreferredWidth(70);
                table.getColumnModel().getColumn(1).setPreferredWidth(220);
                table.getColumnModel().getColumn(2).setPreferredWidth(280);
                table.getColumnModel().getColumn(3).setPreferredWidth(50);
                table.getColumnModel().getColumn(4).setPreferredWidth(50);
                table.getColumnModel().getColumn(5).setPreferredWidth(90);
                // 设置列的渲染器以进行对齐
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER); // 居中对齐
                table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
*/

                JScrollPane scrollPane = new JScrollPane(table);
                add(scrollPane, BorderLayout.CENTER);

                while (rs.next()) {
                    String id = rs.getString(1);
                    String name = rs.getString(2);
                    String faculty = rs.getString(3);
                    String credits = rs.getString(4);
                    String hours = rs.getString(5);
                    String type = rs.getString(6);
                    tableModel.addRow(new Object[]{id, name, faculty, credits, hours, type});
                }

                setSize(ocr(table),1200);
                setLocationRelativeTo(parent);

                // 监听表格模型的更改事件
                tableModel.addTableModelListener(e -> {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    if (column == 1) {
                        Object newData = tableModel.getValueAt(row, column);
                        // 获取更新后的数据
                        String id = tableModel.getValueAt(row, 0).toString();
                        String columnName = tableModel.getColumnName(column);

                        try {
                            String execute = "UPDATE `课程视图` SET `" + columnName + "` = ? WHERE `课程号` = ?";
                            PreparedStatement stmt1 = StudentManagementSystem.conn.prepareStatement(execute);
                            stmt1.setObject(1, newData);
                            stmt1.setString(2, id);
                            int rs1 = stmt1.executeUpdate();

                            if (rs1 > 0) {
                                JOptionPane.showMessageDialog(dialog, "记录更新成功: 课程号 " + id + ", " + columnName + " = " + newData);
                            } else {
                                JOptionPane.showMessageDialog(dialog, "更新失败");
                            }
                            stmt1.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, "数据库更新错误: " + ex.getMessage());
                        }
                    }
                });
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            setVisible(true);
        }
    }
}

