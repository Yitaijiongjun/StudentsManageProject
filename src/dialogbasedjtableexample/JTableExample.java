package dialogbasedjtableexample;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class JTableExample {
    private static List<Object[]> updatedRows = new ArrayList<>();
    // 设置全局字体


    public static void main(String[] args) {
        JFrame frame = new JFrame("JTable Example");
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Name", "Age"}, 0);
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Add some sample data
        model.addRow(new Object[]{1, "Alice", 30});
        model.addRow(new Object[]{2, "Bob", 25});

        // Add TableModelListener to record changes
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int id = (int) model.getValueAt(row, 0);
                    String name = (String) model.getValueAt(row, 1);
                    int age = (int) model.getValueAt(row, 2);

                    // Record the updated row
                    updatedRows.add(new Object[]{id, name, age});
                }
            }
        });

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!updatedRows.isEmpty()) {
                    updateDatabase(updatedRows);
                    updatedRows.clear(); // Clear the list after updating the database
                } else {
                    JOptionPane.showMessageDialog(frame, "No changes to save.");
                }
            }
        });

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(new JScrollPane(table));
        frame.add(saveButton);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void updateDatabase(List<Object[]> updatedRows) {
    }
}
/*int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int row : selectedRows) {
                        int id = (int) model.getValueAt(row, 0);
                        String name = (String) model.getValueAt(row, 1);
                        int age = (int) model.getValueAt(row, 2);

                        // Update database with the new row data
                        updateDatabase(id, name, age);
                    }
                }

 */