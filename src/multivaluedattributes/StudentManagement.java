package multivaluedattributes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentManagement extends JFrame {
    private JTextField idField, nameField, ageField, contactField;
    private DefaultListModel<String> contactListModel;
    private JList<String> contactList;
    private JButton addContactButton, removeContactButton, addButton, deleteButton, updateButton, searchButton;
    private Map<String, Student> studentDatabase;

    public StudentManagement() {
        super("学生管理系统喵");
        studentDatabase = new HashMap<>();

        setLayout(new BorderLayout());

        // 顶部表单部分
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("学号(ID)喵:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("姓名(Name)喵:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("年龄(Age)喵:"));
        ageField = new JTextField();
        inputPanel.add(ageField);

        inputPanel.add(new JLabel("联系方式(Contacts)喵:"));
        contactField = new JTextField();
        inputPanel.add(contactField);

        addContactButton = new JButton("添加联系方式(Add Contact)喵");
        addContactButton.addActionListener(new AddContactListener());
        inputPanel.add(addContactButton);

        removeContactButton = new JButton("移除选中联系方式(Remove Contact)喵");
        removeContactButton.addActionListener(new RemoveContactListener());
        inputPanel.add(removeContactButton);

        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        inputPanel.add(new JScrollPane(contactList));

        add(inputPanel, BorderLayout.CENTER);

        // 底部按钮部分
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("添加学生(Add Student)喵");
        addButton.addActionListener(new AddButtonListener());
        buttonPanel.add(addButton);

        deleteButton = new JButton("删除学生(Delete Student)喵");
        deleteButton.addActionListener(new DeleteButtonListener());
        buttonPanel.add(deleteButton);

        updateButton = new JButton("更新学生(Update Student)喵");
        updateButton.addActionListener(new UpdateButtonListener());
        buttonPanel.add(updateButton);

        searchButton = new JButton("搜索学生(Search Student)喵");
        searchButton.addActionListener(new SearchButtonListener());
        buttonPanel.add(searchButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class Student {
        String id;
        String name;
        int age;
        ArrayList<String> contacts;

        Student(String id, String name, int age, ArrayList<String> contacts) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.contacts = contacts;
        }
    }

    private class AddContactListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String contact = contactField.getText();
            if (!contact.isEmpty()) {
                contactListModel.addElement(contact);
                contactField.setText("");
            }
        }
    }

    private class RemoveContactListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = contactList.getSelectedIndex();
            if (selectedIndex != -1) {
                contactListModel.remove(selectedIndex);
            }
        }
    }

    private class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            ArrayList<String> contacts = new ArrayList<>();
            for (int i = 0; i < contactListModel.size(); i++) {
                contacts.add(contactListModel.getElementAt(i));
            }

            Student student = new Student(id, name, age, contacts);
            studentDatabase.put(id, student);

            JOptionPane.showMessageDialog(null, "学生添加成功喵！");
        }
    }

    private class DeleteButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            if (studentDatabase.remove(id) != null) {
                JOptionPane.showMessageDialog(null, "学生删除成功喵！");
            } else {
                JOptionPane.showMessageDialog(null, "未找到该学生喵！");
            }
        }
    }

    private class UpdateButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            Student student = studentDatabase.get(id);
            if (student != null) {
                student.name = nameField.getText();
                student.age = Integer.parseInt(ageField.getText());
                student.contacts.clear();
                for (int i = 0; i < contactListModel.size(); i++) {
                    student.contacts.add(contactListModel.getElementAt(i));
                }
                JOptionPane.showMessageDialog(null, "学生更新成功喵！");
            } else {
                JOptionPane.showMessageDialog(null, "未找到该学生喵！");
            }
        }
    }

    private class SearchButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            Student student = studentDatabase.get(id);
            if (student != null) {
                nameField.setText(student.name);
                ageField.setText(String.valueOf(student.age));
                contactListModel.clear();
                for (String contact : student.contacts) {
                    contactListModel.addElement(contact);
                }
                JOptionPane.showMessageDialog(null, "学生信息加载成功喵！");
            } else {
                JOptionPane.showMessageDialog(null, "未找到该学生喵！");
            }
        }
    }

    public static void main(String[] args) {
        new StudentManagement();
    }
}

