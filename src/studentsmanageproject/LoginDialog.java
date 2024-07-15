package studentsmanageproject;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDialog extends JDialog {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private boolean authenticated;
    Object username;
    String password;
    String identity;
    public LoginDialog(JFrame parent) {
        super(parent, "用户登录", true);
        setLayout(new GridLayout(3, 2));
        setSize(400, 200);

        add(new JLabel("用户名:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("密码:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JComboBox<String> identityComboBox = new JComboBox<>(new String[]{"学生","教师","管理员"});
        add(identityComboBox);
        identityComboBox.setSelectedIndex(0);

        JButton loginButton = new JButton("登录");
        add(loginButton);
        // 添加事件监听
        loginButton.addActionListener(e -> {
            username = usernameField.getText();
            password = new String(passwordField.getPassword());
            identity = (String) identityComboBox.getSelectedItem();
            try{
                String queryUser = "SELECT * FROM `" + identity + "帐密` WHERE `账号` = ?";
                PreparedStatement stmtUser = StudentManagementSystem.conn.prepareStatement(queryUser);
                stmtUser.setObject(1, username);
                ResultSet rsUser = stmtUser.executeQuery();

                if (!rsUser.next()) {
                    JOptionPane.showMessageDialog(parent, "账号不存在!");
                } else {
                    String queryPassword = "SELECT * FROM `" + identity + "帐密` WHERE `账号` = ? AND `密码` = ?";
                    PreparedStatement stmtPassword = StudentManagementSystem.conn.prepareStatement(queryPassword);
                    stmtPassword.setObject(1, username);
                    stmtPassword.setString(2, password);
                    ResultSet rsPassword = stmtPassword.executeQuery();
                    if (rsPassword.next()) {
                        authenticated = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(parent, "密码错误!");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        setVisible(true);
    }
    public boolean isAuthenticated() {
        return authenticated;
    }
}

