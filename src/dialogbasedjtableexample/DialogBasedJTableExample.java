package dialogbasedjtableexample;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class DialogBasedJTableExample extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private List<Object[]> rowDataCache; // 缓存表格数据的更改

    public DialogBasedJTableExample() {
        setTitle("Dialog-based JTable Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        rowDataCache = new ArrayList<>();

        // 初始数据
        Object[][] data = {
                {"1", "Alice", 23},
                {"2", "Bob", 25},
                {"3", "Charlie", 30}
        };

        // 列名
        String[] columnNames = {"ID", "Name", "Age"};

        // 创建表格模型
        model = new DefaultTableModel(data, columnNames);
        table = new JTable(model);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton("删除选中行");
        JButton addButton = new JButton("新增行并插入数据");
        JButton submitButton = new JButton("确认提交");
        JButton cancelButton = new JButton("取消操作");

        // 添加按钮事件监听器
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    model.removeRow(selectedRow);
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addRow(new Object[]{"", "", ""});
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 将缓存的更改应用到源数据
                for (Object[] rowData : rowDataCache) {
                    int index = (int) rowData[0];
                    model.setValueAt(rowData[1], index, 1);
                    model.setValueAt(rowData[2], index, 2);
                }
                rowDataCache.clear();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 还原表格到修改前的状态
                while (model.getRowCount() > 0) {
                    model.removeRow(0);
                }
                for (Object[] rowData : rowDataCache) {
                    model.addRow(rowData);
                }
                rowDataCache.clear();
            }
        });

        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        // 添加表格和按钮面板到主界面
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DialogBasedJTableExample());
    }
}

