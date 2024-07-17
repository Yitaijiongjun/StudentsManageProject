package foreignkeyeditorwithbutton;

import metadatafetch.FetchData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

import static java.awt.BorderLayout.*;
import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;
import static studentsmanageproject.OptimizeColumnRendering.ocr;

public class ButtonEditor extends DefaultCellEditor {
    Window parent;
    JPanel panel = new JPanel(new BorderLayout());
    JButton button = new JButton("...");
    String referenceTableName;
    int row, column;
    public ButtonEditor(JTextField textField, Window parent, String referenceTableName) {
        super(textField);
        java.awt.Font defaultFont = new Font("Dialog",Font.BOLD,24);
        // 将系统默认字体应用到按钮上
        button.setFont(defaultFont);
        button.setMargin(new Insets(0, 0, 0, 0));
        panel.add(editorComponent, CENTER);

        button.addActionListener(e -> showForeignKeyDialog(row, column));
        panel.add(button, EAST);

        setClickCountToStart(1);// 为避免渲染器死按钮影响操作, 调整点击次数为 1

        this.parent = parent;
        this.referenceTableName = referenceTableName;
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
        JDialog dialog = new JDialog(parent, "选择院系", APPLICATION_MODAL);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        JPanel rightHeader = new JPanel(new FlowLayout());
        JPanel leftHeader = new JPanel(new FlowLayout());
        JButton flush = new JButton("刷新");
        flush.setMargin(new Insets(0,0,0,0));
        JButton fullDisplay = new JButton("全显");
        fullDisplay.setMargin(new Insets(0,0,0,0));
        JTextField siftTextFiled = new JTextField(8);
        rightHeader.add(flush);
        rightHeader.add(fullDisplay);
        rightHeader.add(siftTextFiled);
        JToggleButton showCloumn = new JToggleButton("显示列名");
        showCloumn.setPreferredSize(new Dimension(60, 25));
        showCloumn.setMargin(new Insets(0,0,0,0));
        showCloumn.addActionListener(e -> {
            if (showCloumn.isSelected()) {
            } else {
            }
        });
        leftHeader.add(showCloumn);
        header.add(rightHeader, EAST);
        header.add(leftHeader, WEST);

        List<String> columnNames = new Vector<>();
        List<Class<?>> columnTypes = new Vector<>();
        List<List<Object>> data = new Vector<>();

        FetchData.fetchData(columnNames, columnTypes, data, referenceTableName);

        DefaultTableModel referenceTableModel = new DefaultTableModel((Vector) data, (Vector) columnNames){
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // 返回 false表示所有单元格都不可编辑
                return false;
            }
        };
        JTable referenceTable = new JTable(referenceTableModel);
        referenceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        referenceTable.setRowHeight(30);
        dialog.setSize(ocr(referenceTable), 900);
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
                Object selectedDepartment = referenceTableModel.getValueAt(selectedRow, 0);
                delegate.setValue(selectedDepartment);
                stopCellEditing(); // 传播更新事件到表格模型,相当于:tableModel.setValueAt(selectedDepartment, row, column);
                dialog.dispose();
            }
        });

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel footer = new JPanel(new BorderLayout());
        JPanel rightFooter = new JPanel(new FlowLayout());
        rightFooter.add(selectButton);
        rightFooter.add(cancelButton);
        JLabel status = new JLabel();
        footer.add(rightFooter, EAST);
        footer.add(status, WEST);

        dialog.add(header, NORTH);
        dialog.add(tableScrollPane, CENTER);
        dialog.add(footer, SOUTH);

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
