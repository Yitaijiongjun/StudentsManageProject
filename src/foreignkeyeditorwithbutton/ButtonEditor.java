package foreignkeyeditorwithbutton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;

class ButtonEditor extends DefaultCellEditor {
    JFrame parent;
    JPanel panel;
    JButton button;
    int row, column;
    public ButtonEditor(JTextField textField, JFrame parent) {
        super(textField);
        panel = new JPanel(new BorderLayout());
        button = new JButton("...");
        button.setMargin(new Insets(0, 0, 0, 0));
        panel.add(editorComponent, CENTER);

        button.addActionListener(e -> showForeignKeyDialog(row, column));
        panel.add(button, BorderLayout.EAST);

        setClickCountToStart(1);// 为避免渲染器死按钮影响操作, 调整点击次数为 1

        this.parent = parent;
    }
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        delegate.setValue(value);
        this.row = row;
        this.column = column;
        return panel;
    }

    public void showForeignKeyDialog(int row, int column) {
        JDialog dialog = new JDialog(parent, "选择院系", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        JPanel rightHeader = new JPanel(new FlowLayout());
        JButton flush = new JButton("刷新");
        JButton fullDisplay = new JButton("全显");
        JTextField siftTextFiled = new JTextField(8);
        rightHeader.add(flush);
        rightHeader.add(fullDisplay);
        rightHeader.add(siftTextFiled);
        header.add(rightHeader, BorderLayout.EAST);

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

        DefaultTableModel referenceTableModel = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // 返回 false表示所有单元格都不可编辑
                return false;
            }
        };
        JTable referenceTable = new JTable(referenceTableModel);
        referenceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(referenceTable);

        JPanel checkBoxPanel = new JPanel(new FlowLayout());
        JCheckBox showColumnNameCheckBox = new JCheckBox("显示院系全称");
        showColumnNameCheckBox.setSelected(true);
        showColumnNameCheckBox.addItemListener(e -> {
            TableColumnModel columnModel = referenceTable.getColumnModel();
            TableColumn selectColumn = columnModel.getColumn(1);
            if (showColumnNameCheckBox.isSelected()) {
                selectColumn.setMinWidth(100);
                selectColumn.setMaxWidth(200);
                selectColumn.setPreferredWidth(150);
            } else {
                selectColumn.setMinWidth(0);
                selectColumn.setMaxWidth(0);
                selectColumn.setPreferredWidth(0);
            }
        });
        checkBoxPanel.add(showColumnNameCheckBox);

        JButton selectButton = new JButton("确定");
        selectButton.addActionListener(e -> {
            int selectedRow = referenceTable.getSelectedRow();
            if (selectedRow != -1) {
                String selectedDepartment = (String) referenceTableModel.getValueAt(selectedRow, 0);
                delegate.setValue(selectedDepartment);
                stopCellEditing(); // 传播更新事件到表格模型,相当于:tableModel.setValueAt(selectedDepartment, row, column);
                dialog.dispose();
            }
        });

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);

        dialog.add(checkBoxPanel, BorderLayout.NORTH);
        dialog.add(tableScrollPane, CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
