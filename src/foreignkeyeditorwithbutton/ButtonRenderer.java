package foreignkeyeditorwithbutton;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ButtonRenderer extends DefaultTableCellRenderer{
    JPanel panel = new JPanel();
    JButton button = new JButton("...");// 该按钮仅有外观意义
    DefaultTableCellRenderer label = new DefaultTableCellRenderer();
    public ButtonRenderer() {
        super();
        java.awt.Font defaultFont = new Font("Dialog",Font.BOLD,24);
        // 将系统默认字体应用到按钮上
        button.setFont(defaultFont);
        panel.setLayout(new BorderLayout());
        button.setMargin(new Insets(0, 0, 0, 0));
        panel.add(label, CENTER);
        panel.add(button, BorderLayout.EAST);
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        label.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setText(value == null ? "" : value.toString());
        return panel;
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(preferredSize().width + 6, preferredSize().height);
    }
}




/*
@Deprecated
class ButtonRenderer extends JPanel implements TableCellRenderer {
    JButton button = new JButton("•••");// 该按钮仅有外观意义
    DefaultTableCellRenderer label = new DefaultTableCellRenderer();;
    public ButtonRenderer() {
        super();
        // 获取系统默认字体
        java.awt.Font defaultFont = new Font("Dialog",Font.PLAIN,12);
        // 将系统默认字体应用到按钮上
        button.setFont(defaultFont);
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
 */
