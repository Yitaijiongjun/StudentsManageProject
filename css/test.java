private void updateStudent(String studentId, String name, String age, String gender, String className) {
        try {
            String query = "SELECT * FROM `学生` WHERE `学号` = ?";
            PreparedStatement stmt = StudentManagementSystem.conn.prepareStatement(query);
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                query = "UPDATE `学生` SET `姓名` = ?, `性别` = ?, `班级` = ? , `宿舍` = ? WHERE `学号` = ?";
                stmt = StudentManagementSystem.conn.prepareStatement(query);
                stmt.setString(1, name.isEmpty() ? rs.getString("name") : name);
                stmt.setString(2, age.isEmpty() ? rs.getString("age") : age);
                stmt.setString(3, gender.isEmpty() ? rs.getString("gender") : gender);
                stmt.setString(4, className.isEmpty() ? rs.getString("class") : className);
                stmt.setString(5, studentId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "学生信息已更新!");
            } else {
                query = "INSERT INTO students (student_id, name, age, gender, class) VALUES (?, ?, ?, ?, ?)";
                stmt = StudentManagementSystem.conn.prepareStatement(query);
                stmt.setString(1, studentId);
                stmt.setString(2, name);
                stmt.setString(3, age);
                stmt.setString(4, gender);
				stmt.setString(2, name);
                stmt.setString(3, age);
                stmt.setString(4, gender);
                stmt.setString(5, className);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "学生信息已添加!");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }