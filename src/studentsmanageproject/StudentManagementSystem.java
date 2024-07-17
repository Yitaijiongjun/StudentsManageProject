package studentsmanageproject;

import metadatafetch.FetchData;
import metadatafetch.ViewTableDialog;
import metadatafetch.ViewViewDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static java.awt.Font.BOLD;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

public class StudentManagementSystem {
    public static Connection conn;
    public static JFrame frame;
    Connection getConnection() {
        try {
            String USER = "root";
            String PASSWORD = "210569";
            String URL = "jdbc:mysql://192.168.188.128:3306/students_manage";
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            showMessageDialog(frame, "数据库连接错误: " + e.getMessage(), "消息", INFORMATION_MESSAGE);
            return null;
        }
    }
    public StudentManagementSystem() {
        // 初始化主框架
        frame = new JFrame("学生信息管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // 计算窗口的 x和 y位置，使其居中
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        // 设置窗口的位置
        frame.setLocation(x, y);
        /*
        //登陆窗口
        LoginDialog loginDialog = new LoginDialog(frame);
        if (!loginDialog.isAuthenticated()) {
            System.exit(0);
        }
         */

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // 创建菜单条
        JMenuBar menuBar = new JMenuBar();
        // 创建问候语面板
        JPanel greetingPanel = new GreetingPanel();
        // 创建修改学生子面板
        JPanel updateStudentsPanel = new UpdateStudentsPanel();
        // 创建修改课程子面板
        //JPanel updateCoursesPanel = new UpdateCoursesPanel();


        JMenu menu1 = new JMenu("学生管理");
        JMenuItem item1_1 = new JMenuItem("查看所有学生");
        JMenuItem item1_2 = new JMenuItem("增删改查学生");
        JMenuItem item1_3 = new JMenuItem("对话框测试");
        menu1.add(item1_1);
        menu1.add(item1_2);
        menu1.add(item1_3);
        //监听逻辑
        item1_1.addActionListener(e -> new ViewTableDialog(frame,"查看学生", Dialog.ModalityType.MODELESS, "学生"));
        item1_2.addActionListener(e -> cardLayout.show(mainPanel, "updateStudents"));
        item1_3.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame,"信息对话框");
            System.out.println(JOptionPane.showOptionDialog(frame,"选项对话框","选项",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE,null, new String[]{"选项1", "选项2"},1));
            System.out.println(JOptionPane.showConfirmDialog(frame,"确认对话框"));
            System.out.println(JOptionPane.showInputDialog(frame,"输入对话框"));
        });


        JMenu menu2 = new JMenu("课程管理");
        JMenuItem item2_1 = new JMenuItem("查看所有课程");
        JMenuItem item2_2 = new JMenuItem("增删改查课程");
        menu2.add(item2_1);
        menu2.add(item2_2);
        item2_1.addActionListener(e -> new ViewTableDialog(frame, "查看课程", Dialog.ModalityType.MODELESS, "课程"));


        JMenu menu3 = new JMenu("教学安排");

        JMenu menu4 = new JMenu("密码管理");

        JMenu menu5 = new JMenu("宿舍管理");

        JMenu menu6 = new JMenu("通知发布");

        JMenu menu8 = new JMenu("查看数据库");


        JMenu menu7 = new JMenu("连接状态") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(true);// 确保组件是不透明的，以便背景颜色被正确绘制
            }
            @Override
            public void setBackground(Color bg) {
                super.setBackground(Check.checkConnectionStatus(conn));
                repaint();
            }
        };
        JMenuItem item7_1 = new JMenuItem("重新连接数据库");
        menu7.add(item7_1);
        item7_1.addActionListener(e -> new Thread(() -> {
            conn = getConnection();
            menu7.setBackground(Color.RED);
            menu7.repaint();
        }).start());
        // 异步获取数据库连接
        new Thread(() -> {
            conn = getConnection();
            menu7.setBackground(Color.RED);
            menu7.repaint();
            List<List<String>> tables = FetchData.fetchDatabaseInformation();
            for (List<String> table : tables) {
                String tableName = table.get(0);
                String tableType = table.get(1);
                JMenuItem temp = new JMenuItem(tableName);
                if (tableType.equals("TABLE")) {
                    temp.addActionListener(e -> new ViewTableDialog(frame, "查看" + tableName, Dialog.ModalityType.MODELESS, tableName));
                } else if (tableType.equals("VIEW")) {
                    temp.addActionListener(e -> new ViewViewDialog(frame, "查看" + tableName, Dialog.ModalityType.MODELESS, tableName));
                }
                menu8.add(temp);
            }
        }).start();


        menuBar.add(menu1);
        menuBar.add(menu2);
        menuBar.add(menu3);
        menuBar.add(menu4);
        menuBar.add(menu5);
        menuBar.add(menu6);
        menuBar.add(menu7);
        menuBar.add(menu8);
        frame.setJMenuBar(menuBar);

        // 添加面板
        //mainPanel.add(addStudentPanel, "addStudent");
        mainPanel.add(updateStudentsPanel, "updateStudents");
        mainPanel.add(greetingPanel,"greeting");

        cardLayout.show(mainPanel, "greeting");

        frame.add(mainPanel);
        frame.setVisible(true);

        // 添加窗口监听器
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        // 设置全局字体
        setGlobalFont(new Font("宋体", BOLD, 24));
        // 启动主系统
        new StudentManagementSystem();
    }
    public static void setGlobalFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("FormattedTextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("List.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);
        // 如果有其他组件需要设置，可以继续添加
    }
    public static void showMessageDialog(Component parentComponent, Object message,
                                         String title, int messageType) {
        if (message instanceof String && ((String) message).length() > 20) {
            JTextArea textArea = new JTextArea((String) message);
            textArea.setEditable(false);
            textArea.setLineWrap(true);// 设置自动换行
            textArea.setWrapStyleWord(true);// 设置空格换行策略
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(800, 150));
            JOptionPane.showMessageDialog(parentComponent, scrollPane, title, messageType);
        } else {
            JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
        }
    }
}
class Check {
    public static Color checkConnectionStatus(Connection conn) {
        if (conn == null) return Color.RED; // 连接为空
        try {
            if (conn.isClosed()) return Color.RED; // 连接关闭
        } catch (SQLException e) {
            return Color.RED; // 发生 SQL 异常
        }
        return Color.GREEN; // 连接正常
    }
}
