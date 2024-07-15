package studentsmanageproject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AutoCompleteComboBox<T> extends JComboBox<T> {
    private String searchText = "";
    public AutoCompleteComboBox() {
        super();
        setEditable(true);
        Component editorComponent = getEditor().getEditorComponent();
        if (editorComponent instanceof JTextComponent) {
            JTextComponent editor = (JTextComponent) editorComponent;

            editor.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    updateComboBox();
                }
                public void removeUpdate(DocumentEvent e) {
                    updateComboBox();
                }
                public void changedUpdate(DocumentEvent e) { updateComboBox(); }
            });
        }
        addActionListener(e -> {
            if (isPopupVisible()) {
                JComboBox comboBox = (JComboBox) e.getSource();
                String selectedValue = (String) comboBox.getSelectedItem();
                if (selectedValue != null) {
                    JTextComponent editorComponent1 = (JTextComponent) comboBox.getEditor().getEditorComponent();
                    editorComponent1.setText(selectedValue);
                }
            }
        });
    }
    private void updateComboBox() {
        ArrayList<String> matchingItems = getMatchingItems();
        JComboBox<String> facultycomboBox = null;
        if (!matchingItems.isEmpty()) {
            facultycomboBox.setModel(new DefaultComboBoxModel<>(matchingItems.toArray(new String[0])));
            SwingUtilities.invokeLater(() ->{
                if(facultycomboBox.isShowing()) {
                    facultycomboBox.showPopup();
                }
                facultycomboBox.setVisible(true);
            });
        } else {
            facultycomboBox.setVisible(false);
        }
    }
    private ArrayList<String> getMatchingItems() {
        ArrayList<String> matchingItems = new ArrayList<>();
        String query = "SELECT * FROM `院系` ";
        try {
            Statement stmt = StudentManagementSystem.conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                matchingItems.add(rs.getString("院系全称"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchingItems;
    }
}
