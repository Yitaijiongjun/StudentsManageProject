package foreignkeyeditorwithbutton;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class ForeignKeyEditorWithButton extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    public ForeignKeyEditorWithButton() {
        setTitle("Foreign Key Editor Example with Button");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boolean test = true;
        String[] columnNames = {"课程号", "课程名", "院系ID"};
        Object[][] data = {
                {"280", "数据结构", "CSAI"},
                {"281", "算法分析与设计", "CSAI"},
                {"282", "计算机组成原理", "CSAI"},
                {"283", test, "CSAI"},
                {"284", "数据库系统原理", "CSAI"}
        };

        tableModel = new DefaultTableModel(data, columnNames);
        table = new JTable(tableModel);

        TableColumn column = table.getColumnModel().getColumn(2);
        // 设置自定义表格单元格呈现器和编辑器
        column.setCellRenderer(new ButtonRenderer());
        column.setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ForeignKeyEditorWithButton editor = new ForeignKeyEditorWithButton();
            editor.setVisible(true);
        });
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton button;
        private DefaultTableCellRenderer label;
        public ButtonRenderer() {
            super();
            setLayout(new BorderLayout());
            button = new JButton("...");
            button.setMargin(new Insets(0, 0, 0, 0));
            add(button, BorderLayout.EAST);

            label = new DefaultTableCellRenderer();
            add(label, BorderLayout.CENTER);
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            label.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setText(value == null ? "" : value.toString());

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private String label;
        private boolean isPushed;
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            setClickCountToStart(1);
        }
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            JPanel panel = new JPanel(new BorderLayout());

            JLabel cellLabel = new DefaultTableCellRenderer();
            cellLabel.setText(label);

            JButton button = new JButton("...");
            button.setMargin(new Insets(0, 0, 0, 0));
            button.addActionListener(e -> {
                fireEditingStopped();
                showForeignKeyDialog(row, column);
            });
            panel.add(cellLabel, BorderLayout.CENTER);
            panel.add(button, BorderLayout.EAST);
            isPushed = true;
            return panel;
        }

        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

        private void showForeignKeyDialog(int row, int column) {
            JDialog dialog = new JDialog(ForeignKeyEditorWithButton.this, "选择院系", true);
            dialog.setSize(400, 300);
            dialog.setLayout(new BorderLayout());

            String[] columnNames = {"院系ID", "院系全称"};
            Object[][] data = {
                    {"CS", "网络安全学院"},
                    {"CSAI", "计算机与人工智能学院"},
                    {"EDU", "教育学院"},
                    {"EE", "电气工程学院"},
                    {"FA", "美术学院"},
                    {"FL", "外国语学院"},
                    {"HIS", "历史学院"},
                    {"IE", "信息工程学院"},
                    {"IM", "信息管理学院"},
                    {"JMC", "新闻与传播学院"},
                    {"LAW", "法学院"}
            };

            DefaultTableModel departmentTableModel = new DefaultTableModel(data, columnNames){
                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    // 直接返回 false表示所有单元格都不可编辑
                    return false;
                }
            };
            JTable departmentTable = new JTable(departmentTableModel);
            departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane tableScrollPane = new JScrollPane(departmentTable);

            JPanel checkBoxPanel = new JPanel(new FlowLayout());
            JCheckBox showDeptNameCheckBox = new JCheckBox("显示院系全称");
            showDeptNameCheckBox.setSelected(true);
            showDeptNameCheckBox.addItemListener(e -> {
                TableColumnModel columnModel = departmentTable.getColumnModel();
                TableColumn deptNameColumn = columnModel.getColumn(1);
                if (showDeptNameCheckBox.isSelected()) {
                    deptNameColumn.setMinWidth(100);
                    deptNameColumn.setMaxWidth(200);
                    deptNameColumn.setPreferredWidth(150);
                } else {
                    deptNameColumn.setMinWidth(0);
                    deptNameColumn.setMaxWidth(0);
                    deptNameColumn.setPreferredWidth(0);
                }
            });
            checkBoxPanel.add(showDeptNameCheckBox);

            JButton selectButton = new JButton("确定");
            selectButton.addActionListener(e -> {
                int selectedRow = departmentTable.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedDepartment = (String) departmentTableModel.getValueAt(selectedRow, 0);
                    tableModel.setValueAt(selectedDepartment, row, column);
                    dialog.dispose();
                }
            });

            JButton cancelButton = new JButton("取消");
            cancelButton.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(selectButton);
            buttonPanel.add(cancelButton);

            dialog.add(checkBoxPanel, BorderLayout.NORTH);
            dialog.add(tableScrollPane, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setLocationRelativeTo(ForeignKeyEditorWithButton.this);
            dialog.setVisible(true);
        }
    }
}
