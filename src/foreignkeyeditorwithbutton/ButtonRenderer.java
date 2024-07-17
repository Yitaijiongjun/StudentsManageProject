package foreignkeyeditorwithbutton;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;

public class ButtonRenderer extends JPanel implements TableCellRenderer {
    JButton button = new JButton("...");// 该按钮仅有外观意义
    DefaultTableCellRenderer label = new DefaultTableCellRenderer();;
    public ButtonRenderer() {
        super();
        setLayout(new BorderLayout());
        button.setMargin(new Insets(0, 0, 0, 0));
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
