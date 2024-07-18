package foreignkeyeditorwithbutton;

import metadatafetch.FetchData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.awt.BorderLayout.*;
import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;
import static metadatafetch.FetchData.FKCOLUMN_NAME;
import static studentsmanageproject.OptimizeColumnRendering.ocr;

public class ButtonEditor extends DefaultCellEditor {
    JDialog parent;
    JTable maintable;
    JPanel panel = new JPanel(new BorderLayout());
    JButton button = new JButton("...");
    String referenceTableName, fkTableName, fkColumnName, fkName, referenceColumnName;
    int row, column;
    public ButtonEditor(JTextField textField, JDialog parent, JTable maintable, String fkTableName,
    String fkColumnName, String fkName, String referenceTableName, String referenceColumnName) {
        super(textField);
        Font defaultFont = new Font("Dialog",Font.BOLD,24);
        // 将系统默认字体应用到按钮上
        button.setFont(defaultFont);
        button.setMargin(new Insets(0, 0, 0, 0));
        panel.add(editorComponent, CENTER);

        button.addActionListener(e -> showForeignKeyDialog(row, column));
        panel.add(button, EAST);

        setClickCountToStart(2);// 渲染器为死按钮需注意

        this.parent = parent;
        this.maintable = maintable;
        this.fkTableName = fkTableName;
        this.fkColumnName = fkColumnName;
        this.fkName = fkName;
        this.referenceTableName = referenceTableName;
        this.referenceColumnName = referenceColumnName;
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
        dialog.setLayout(new BorderLayout());
        dialog.setIconImage((new ImageIcon
                ("C:/Users/21056/Pictures/Saved Pictures/屏幕截图 2024-07-18 223540.png")).getImage());
        JPanel header = new JPanel(new BorderLayout());
        JPanel rightHeader = new JPanel(new FlowLayout());
        JPanel leftHeader = new JPanel(new FlowLayout());

        JPanel leftGuide = new JPanel();
        leftGuide.setLayout(new BoxLayout(leftGuide, BoxLayout.Y_AXIS));

        JButton flush = new JButton("刷新");
        flush.setMargin(new Insets(0,0,0,0));
        JButton fullDisplay = new JButton("全显");
        fullDisplay.setMargin(new Insets(0,0,0,0));
        JTextField siftTextFiled = new JTextField(8);
        rightHeader.add(flush);
        rightHeader.add(fullDisplay);
        rightHeader.add(siftTextFiled);
        JToggleButton showCloumn = new JToggleButton("列名");
        showCloumn.setMargin(new Insets(0,0,0,0));
        showCloumn.setSelected(true);
        showCloumn.addActionListener(e -> {
            if (showCloumn.isSelected()) leftGuide.setVisible(true);
            else leftGuide.setVisible(false);});
        leftHeader.add(showCloumn);
        header.add(rightHeader, EAST);
        header.add(leftHeader, WEST);

        List<String> columnNames = new Vector<>();
        List<Class<?>> columnTypes = new Vector<>();
        List<List<Object>> data = new Vector<>();

        FetchData.fetchData(columnNames, columnTypes, data, referenceTableName);

        dialog.setTitle(fkName + " : " + fkTableName + "(" + fkColumnName + ") - " + referenceTableName + "(" + referenceColumnName + ")");

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
        referenceTable.getTableHeader().setReorderingAllowed(false);
        referenceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        List<Integer> columnPreferredSize = new ArrayList<>();

        dialog.setSize(ocr(referenceTable, columnPreferredSize) + 100, 600);
        dialog.setMinimumSize(dialog.getSize());
        JScrollPane tableScrollPane = new JScrollPane(referenceTable);

        TableColumnModel columnModel = referenceTable.getColumnModel();
        for(String columnName : columnNames) {
            JCheckBox showColumnNameCheckBox = new JCheckBox(columnName);
            if(columnName.equals(referenceColumnName)) {
                showColumnNameCheckBox.setSelected(true);
            }
            TableColumn currentColumn = columnModel.getColumn(columnNames.indexOf(columnName));
            showColumnNameCheckBox.addItemListener(e -> {
                if (showColumnNameCheckBox.isSelected()) {
                    currentColumn.setMinWidth(columnPreferredSize.get(columnNames.indexOf(columnName)));
                    currentColumn.setMaxWidth(columnPreferredSize.get(columnNames.indexOf(columnName)) * 20);
                    currentColumn.setPreferredWidth(columnPreferredSize.get(columnNames.indexOf(columnName)));
                } else {
                    currentColumn.setMinWidth(0);
                    currentColumn.setMaxWidth(0);
                    currentColumn.setPreferredWidth(0);
                }
            });
            if (showColumnNameCheckBox.isSelected()) {
                currentColumn.setMinWidth(columnPreferredSize.get(columnNames.indexOf(columnName)));
                currentColumn.setMaxWidth(columnPreferredSize.get(columnNames.indexOf(columnName)) * 20);
                currentColumn.setPreferredWidth(columnPreferredSize.get(columnNames.indexOf(columnName)));
            } else {
                currentColumn.setMinWidth(0);
                currentColumn.setMaxWidth(0);
                currentColumn.setPreferredWidth(0);
            }
            leftGuide.add(showColumnNameCheckBox);
        }

        JButton selectButton = new JButton("确定");
        selectButton.addActionListener(e -> {
            int selectedRow = referenceTable.getSelectedRow();
            if (selectedRow != -1) {
                Object selectedPk = referenceTableModel.getValueAt(selectedRow, columnNames.indexOf(referenceColumnName));
                delegate.setValue(selectedPk);
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
        dialog.add(leftGuide, WEST);
        dialog.add(tableScrollPane, CENTER);
        dialog.add(footer, SOUTH);

        Rectangle r = maintable.getCellRect(row, column, true);
        Point location =r.getLocation();
        SwingUtilities.convertPointToScreen(location, parent);
        dialog.setLocation( location.x, location.y + r.height + 85);
        dialog.setVisible(true);
    }
}
