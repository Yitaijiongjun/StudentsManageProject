package generictablemodel;

import javax.swing.table.AbstractTableModel;
import java.util.List;
@Deprecated
public class GenericTableModel extends AbstractTableModel {
    private List<String> columnNames;
    //每一列可能对应 Java中的不同数据类型（如String、Integer、Date等）
    //columnTypes可以用来存储这些数据类型的 Class对象，以便在运行时通过反射机制动态地处理这些类型。
    private List<Class<?>> columnTypes;
    private List<List<Object>> data;

    public GenericTableModel(List<String> columnNames, List<Class<?>> columnTypes, List<List<Object>> data) {
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.data = data;
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row).get(col);
    }

    @Override
    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return columnTypes.get(col);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        data.get(row).set(col, value);
        fireTableCellUpdated(row, col);
    }
}



