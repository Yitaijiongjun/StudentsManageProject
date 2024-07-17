package foreignkeyeditorwithbutton;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;

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
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            System.out.println(row + "行" + column + "列有更改:" + tableModel.getValueAt(row, column));
        });

        TableColumn column = table.getColumnModel().getColumn(2);
        // 设置自定义表格单元格呈现器和编辑器
        column.setCellRenderer(new ButtonRenderer());
        column.setCellEditor(new ButtonEditor(new JTextField(), this));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ForeignKeyEditorWithButton editor = new ForeignKeyEditorWithButton();
            editor.setVisible(true);
        });
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton button;// 该按钮仅有外观意义
        private DefaultTableCellRenderer label;
        public ButtonRenderer() {
            super();
            setLayout(new BorderLayout());
            button = new JButton("...");
            button.setMargin(new Insets(0, 0, 0, 0));
            label = new DefaultTableCellRenderer();
            add(label, CENTER);
            add(button, BorderLayout.EAST);
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            label.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setText(value == null ? "" : value.toString());
            return this;
        }
    }


}
