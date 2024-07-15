package studentsmanageproject;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

public class OptimizeColumnRendering {
    public static int ocr(JTable table){
        int totalWidth = 0;
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            TableColumn column = table.getColumnModel().getColumn(columnIndex);
            // 初始为列名宽度
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComponent = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, columnIndex);
            int maxWidth = headerComponent.getPreferredSize().width;
            // 遍历单元格宽度
            for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(rowIndex, columnIndex);
                Component c = table.prepareRenderer(cellRenderer, rowIndex, columnIndex);
                int width = c.getPreferredSize().width;
                maxWidth = Math.max(maxWidth, width);
            }
            column.setPreferredWidth(maxWidth + 20);
            totalWidth += maxWidth + 30;
            // 设置居中显示
            TableCellRenderer renderer = column.getCellRenderer();
            if (renderer == null) {
                renderer = table.getDefaultRenderer(Object.class);
                column.setCellRenderer(renderer);
            }
            if (renderer instanceof DefaultTableCellRenderer) {
                ((DefaultTableCellRenderer) renderer).setHorizontalAlignment(JLabel.CENTER);
            }
        }
        return totalWidth;
    }
}
/*
getColumnDisplaySize 是 Java 的 JDBC API 中 ResultSetMetaData 接口的一个方法，用于返回指定列的最大字符数。
这个值通常是数据库设计时设定的列宽，比如 VARCHAR(255) 的值将是 255。这个方法的主要目的是帮助布局数据库数据的界面，例如在 GUI 表格组件中。
然而，这个值并不总是反映实际的数据内容的长度。它只是数据库设计时预定义的最大字符数，而不考虑列中实际存储的数据长度。
例如，一个定义为 VARCHAR(255) 的列可能实际存储的最长字符串仅为 50 个字符。
相比之下，遍历单元格并调用 getPreferredSize 更具实际意义。getPreferredSize 返回的是组件显示数据所需的理想尺寸。
通过遍历每个单元格并计算其 getPreferredSize，可以获取每个单元格实际显示内容的最佳尺寸。
这种方法虽然更耗时，但能更精确地反映每列中数据的实际宽度，从而在 GUI 中更合理地调整列宽，提供更好的用户体验。
*/