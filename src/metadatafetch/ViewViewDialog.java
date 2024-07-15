package metadatafetch;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

import static studentsmanageproject.OptimizeColumnRendering.ocr;

public class ViewViewDialog extends JDialog {
    public ViewViewDialog(Frame parent, String title, ModalityType modal, String tableName) {
        super(parent, title, modal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        List<String> columnNames = new Vector<>();
        List<Class<?>> columnTypes = new Vector<>();
        List<List<Object>> data = new Vector<>();

        FetchData.fetchData(columnNames, columnTypes, data, tableName);

        JTable table = new JTable(new DefaultTableModel((Vector) data, (Vector) columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置表格不可编辑
            }
        });

        // 设置表格行高
        table.setRowHeight(30);

        setSize(ocr(table), 900);
        setLocationRelativeTo(parent);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
