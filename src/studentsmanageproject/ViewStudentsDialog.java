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
public class ViewStudentsDialog extends JDialog {
    public ViewStudentsDialog(JFrame parent) {
        super(parent, "查看学生视图", ModalityType.MODELESS);
        ViewStudentsDialog dialog = this; // 创建对外部类实例的引用
        if (StudentManagementSystem.conn == null) {
            JOptionPane.showMessageDialog(dialog, "数据库未连接");
        } else {
            setLayout(new BorderLayout());
            try {
                String query = "SELECT * FROM `学生视图`";
                Statement stmt = StudentManagementSystem.conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                String[] columnNames = {"学号", "姓名", "性别", "院系", "专业", "班级", "入学时间", "年龄", "宿舍", "出生日期"};
                DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
                JTable table = new JTable(tableModel);

                // 设置表格行高
                table.setRowHeight(30); // 设置行高为30像素
/*
                // 设置表格列宽
                table.getColumnModel().getColumn(0).setPreferredWidth(160);
                table.getColumnModel().getColumn(1).setPreferredWidth(80);
                table.getColumnModel().getColumn(2).setPreferredWidth(50);
                table.getColumnModel().getColumn(3).setPreferredWidth(240);
                table.getColumnModel().getColumn(4).setPreferredWidth(210);
                table.getColumnModel().getColumn(5).setPreferredWidth(60);
                table.getColumnModel().getColumn(6).setPreferredWidth(150);
                table.getColumnModel().getColumn(7).setPreferredWidth(50);
                table.getColumnModel().getColumn(8).setPreferredWidth(340);
                table.getColumnModel().getColumn(9).setPreferredWidth(120);
                // 设置列的渲染器以进行对齐
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER); // 居中对齐
                table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
                table.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);

 */
                JScrollPane scrollPane = new JScrollPane(table);
                add(scrollPane, BorderLayout.CENTER);

                while (rs.next()) {
                    String id = rs.getString(1);
                    String name = rs.getString(2);
                    String gender = rs.getString(3);
                    String faculty = rs.getString(4);
                    String major = rs.getString(5);
                    String classNumber = rs.getString(6);
                    String enrollmentDate = rs.getString(7);
                    String age = rs.getString(8);
                    String dorm = rs.getString(9);
                    String birth = rs.getString(10);
                    tableModel.addRow(new Object[]{id, name, gender, faculty, major, classNumber, enrollmentDate, age, dorm, birth});
                }

                setSize(ocr(table), 500);
                setLocationRelativeTo(parent);

                // 监听表格模型的更改事件
                tableModel.addTableModelListener(e -> {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    if (column <= 2 || column == 9) {
                        Object newData = tableModel.getValueAt(row, column);
                        // 获取更新后的数据
                        String studentId = tableModel.getValueAt(row, 0).toString();
                        String columnName = tableModel.getColumnName(column);

                        try {
                            String execute = "UPDATE `学生视图` SET `" + columnName + "` = ? WHERE `学号` = ?";
                            PreparedStatement stmt1 = StudentManagementSystem.conn.prepareStatement(execute);
                            stmt1.setObject(1, newData);
                            stmt1.setString(2, studentId);
                            int rs1 = stmt1.executeUpdate();

                            if (rs1 > 0) {
                                JOptionPane.showMessageDialog(dialog, "视图更新成功: 学号 " + studentId + ", " + columnName + " = " + newData);
                            } else {
                                JOptionPane.showMessageDialog(dialog, "更新失败");
                            }
                            stmt1.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, "数据库更新错误: " + ex.getMessage());
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "该列不可通过该视图更新", "提示", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            setVisible(true);
        }
    }
}
