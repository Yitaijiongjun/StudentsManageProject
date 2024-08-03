package studentsmanageproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UpdateStudentsPanel extends JPanel {
    // 修改学生面板的组件
    JTextField studentIdField = new JTextField();
    JTextField nameField = new JTextField();
    JTextField genderField = new JTextField();
    JTextField facultyField = new JTextField();
    JTextField majorField = new JTextField();
    JTextField classField = new JTextField();
    JTextField enrollmentDateField = new JTextField();
    JTextField ageField = new JTextField();
    JTextField brithField = new JTextField();
    JTextField dormField = new JTextField();
    JComboBox<String> facultycomboBox = new JComboBox<>();
    JComboBox<String> majorcomboBox = new JComboBox<>();
    JComboBox<String> dormcomboBox = new JComboBox<>();
    String studentId, name, gender, faculty, major, classNumber, enrollmentDate, dorm, birth;
    public UpdateStudentsPanel() {
        setLayout(new GridLayout(13, 2));

        facultycomboBox.setEditable(false); // 设置为不可编辑，使得能够显示输入框
        facultycomboBox.setVisible(true); // 初始设置为可见

        majorcomboBox.setEditable(true);
        majorcomboBox.setVisible(false);

        dormcomboBox.setEditable(true);
        dormcomboBox.setVisible(false);

        JButton clearButton = new JButton("清空");
        JButton searchButton = new JButton("查询");
        JButton updateButton = new JButton("更新");
        JButton addButton = new JButton("添加");
        JButton deleteButton = new JButton("删除");

        JCheckBox nullCheckBox = new JCheckBox("isNull");

        add(new JLabel("学号:")); add(studentIdField);
        add(new JLabel("姓名:")); add(nameField);
        add(new JLabel("性别:")); add(genderField);
        add(new JLabel("院系:")); add(facultyField);
        add(new JLabel("专业:")); add(majorField);
        add(new JLabel("班级:")); add(classField);
        add(new JLabel("入学时间(只读):")); add(enrollmentDateField);
        add(new JLabel("年龄(只读):")); add(ageField);
        add(new JLabel("出生日期:")); add(brithField);
        add(new JLabel("宿舍代码:")); add(dormField);
        add(nullCheckBox); add(clearButton);
        add(searchButton); add(updateButton);
        add(addButton); add(deleteButton);

         /*添加文本框事件监听器
        facultyField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateComboBox();
            }
            public void removeUpdate(DocumentEvent e) {
                updateComboBox();
            }
            public void changedUpdate(DocumentEvent e) {
                updateComboBox();
            }
            private void updateComboBox() {
                String input = facultyField.getText();
                if (input.isEmpty()) {
                    facultycomboBox.setVisible(false);
                } else {
                    ArrayList<String> matchingItems = getMatchingItems(input);
                    if (!matchingItems.isEmpty()) {
                        facultycomboBox.setModel(new DefaultComboBoxModel<>(matchingItems.toArray(new String[0])));
                        SwingUtilities.invokeLater(() ->{
                            if(facultycomboBox.isShowing()) {
                                facultycomboBox.showPopup();
                            }
                            facultycomboBox.setVisible(true);
                        });
                    } else {
                        facultycomboBox.setVisible(false);
                    }
                }
            }
            private ArrayList<String> getMatchingItems(String input) {
                String query = "SELECT * FROM `院系`";
                ArrayList<String> matchingItems = new ArrayList<>();
                try {
                    Statement stmt = studentsmanagesystem.StudentManagementSystem.conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        matchingItems.add(rs.getString("院系全称"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return matchingItems;
            }
        });*/
        clearButton.addActionListener(e -> {
            studentIdField.setText("");
            nameField.setText("");
            genderField.setText("");
            classField.setText("");
            dormField.setText("");
            brithField.setText("");
        });
        searchButton.addActionListener(e -> {
            studentId = studentIdField.getText();
            searchStudent(studentId);
        });
        updateButton.addActionListener(e -> {
            studentId = studentIdField.getText();
            name = nameField.getText();
            gender = genderField.getText();
            classNumber = classField.getText();
            dorm = dormField.getText();
            birth = brithField.getText();
            updateStudent(studentId, name, gender, classNumber, dorm, birth);
        });
        addButton.addActionListener(e -> {
            studentId = studentIdField.getText();
            name = nameField.getText();
            gender = genderField.getText();
            classNumber = classField.getText();
            dorm = dormField.getText();
            birth = brithField.getText();
            updateStudent(studentId, name, gender, classNumber, dorm, birth);
        });
        deleteButton.addActionListener(e -> {
            studentId = studentIdField.getText();
            deleteStudent(studentId);
        });
    }
    private void searchStudent(String studentId) {
        try {
            String query = "SELECT * FROM `学生` WHERE `学号` = ?";
            PreparedStatement stmt = StudentManagementSystem.conn.prepareStatement(query);
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString(2));
                genderField.setText(rs.getString(3));
                classField.setText(rs.getString(4));
                dormField.setText(rs.getString(5));
                brithField.setText(rs.getString(6));
            } else {
                JOptionPane.showMessageDialog(this, "学号不存在");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库错误: " + e.getMessage());
        }
    }
    private void updateStudent(String studentId, String name, String gender ,String classNumber, String dorm, String birth) {
        try {
            String query = "SELECT * FROM `学生` WHERE `学号` = ?", execute;
            PreparedStatement stmt = StudentManagementSystem.conn.prepareStatement(query);
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // 检查null
                String currentName = rs.getString(2);
                String currentGender = rs.getString(3);
                String currentClassNumber = rs.getString(4);
                String currentDorm = rs.getString(5);
                execute = "UPDATE `学生` SET `姓名` = ?, `性别` = ?, `班级` = ? , `宿舍` = ?, `出生日期` = ? WHERE `学号` = ?";
                stmt = StudentManagementSystem.conn.prepareStatement(execute);
                if(name.isEmpty()) if(currentName == null) stmt.setNull(1, Types.VARCHAR); else stmt.setString(1, currentName); else stmt.setString(1, name);
                if(gender.isEmpty()) if(currentGender == null) stmt.setNull(2, Types.VARCHAR); else stmt.setString(2, currentGender); else stmt.setString(2, gender);
                if(classNumber.isEmpty()) if(currentClassNumber == null) stmt.setNull(3, Types.INTEGER); else stmt.setInt(3, Integer.parseInt(currentClassNumber)); else stmt.setInt(3, Integer.parseInt(classNumber));
                if(dorm.isEmpty()) if(currentDorm == null) stmt.setNull(4, Types.VARCHAR); else stmt.setString(4, currentDorm); else stmt.setString(4, dorm);
                if(birth.isEmpty()) stmt.setNull(5, Types.DATE); else stmt.setDate(5, Date.valueOf(birth));
                if(studentId.isEmpty()) stmt.setNull(6, Types.VARCHAR); else stmt.setString(6, studentId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "学生存在，信息已更新!");
            } else {
                execute = "INSERT INTO `学生` (`学号`, `姓名`, `性别`, `班级`, `宿舍`, `出生日期`) VALUES (?, ?, ?, ?, ?, ?)";
                stmt = StudentManagementSystem.conn.prepareStatement(execute);
                if(studentId.isEmpty()) stmt.setNull(1, Types.VARCHAR); else stmt.setString(1, studentId);
                if(name.isEmpty()) stmt.setNull(2, Types.VARCHAR); else stmt.setString(2, name);
                if(gender.isEmpty()) stmt.setNull(3, Types.VARCHAR); else stmt.setString(3, gender);
                if(classNumber.isEmpty()) stmt.setNull(4, Types.INTEGER); else stmt.setInt(4, Integer.parseInt(classNumber));
                if(dorm.isEmpty()) stmt.setNull(5, Types.VARCHAR); else stmt.setString(5, dorm);
                if(birth.isEmpty()) stmt.setNull(6, Types.DATE); else stmt.setDate(6, Date.valueOf(birth));
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "学生不存在，信息已添加!");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库更新错误: " + e.getMessage());
        }
    }
    private void deleteStudent(String studentId) {
        try {
            String execute = "DELETE FROM `学生` WHERE `学号` = ?";
            PreparedStatement stmt = StudentManagementSystem.conn.prepareStatement(execute);
            stmt.setString(1, studentId);
            int rs = stmt.executeUpdate();
            if (rs > 0) {
                JOptionPane.showMessageDialog(this, "学号" + studentId + "成功删除");
            } else {
                JOptionPane.showMessageDialog(this, "学号不存在");
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库错误: " + e.getMessage());
        }
    }
}
