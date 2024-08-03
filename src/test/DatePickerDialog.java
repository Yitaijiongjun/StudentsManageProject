package test;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;

public class DatePickerDialog extends JDialog {
    private Calendar calendar;
    private JLabel lblMonthYear;
    private JTable table;
    private DefaultTableModel tableModel;

    public DatePickerDialog(JFrame parent) {
        super(parent, "选择日期", true);
        calendar = Calendar.getInstance();

        // 设置顶部按钮
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton btnPrevMonth = new JButton("<");
        JButton btnNextMonth = new JButton(">");
        lblMonthYear = new JLabel("", JLabel.CENTER);
        JButton btnChangeView = new JButton("切换");

        btnPrevMonth.addActionListener(e -> changeMonth(-1));
        btnNextMonth.addActionListener(e -> changeMonth(1));
        btnChangeView.addActionListener(e -> switchView());

        topPanel.add(btnPrevMonth, BorderLayout.WEST);
        topPanel.add(lblMonthYear, BorderLayout.CENTER);
        topPanel.add(btnNextMonth, BorderLayout.EAST);
        topPanel.add(btnChangeView, BorderLayout.SOUTH);

        // 设置日期表格
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                int day = (int) table.getValueAt(row, col);
                selectDate(day);
            }
        });

        updateCalendar();

        // 布局管理
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
    }

    private void changeMonth(int delta) {
        calendar.add(Calendar.MONTH, delta);
        updateCalendar();
    }

    private void switchView() {
        // 切换视图的实现（年视图，十年视图等）
    }

    private void updateCalendar() {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        lblMonthYear.setText(String.format("%d年 %d月", year, month + 1));

        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar prevMonth = (Calendar) calendar.clone();
        prevMonth.add(Calendar.MONTH, -1);
        int daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);

        String[] columnNames = {"日", "一", "二", "三", "四", "五", "六"};
        Object[][] data = new Object[6][7];

        int day = 1;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == 0 && j < startDayOfWeek) {
                    data[i][j] = daysInPrevMonth - (startDayOfWeek - j - 1);
                } else if (day > daysInMonth) {
                    day = 1;
                    data[i][j] = day++;
                } else {
                    data[i][j] = day++;
                }
            }
        }

        tableModel.setDataVector(data, columnNames);
        table.setDefaultRenderer(Object.class, new TableCellRenderer());
    }

    private void selectDate(int day) {
        calendar.set(Calendar.DAY_OF_MONTH, day);
        JOptionPane.showMessageDialog(this, "选择的日期: " + calendar.getTime());
    }

    class TableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            Calendar tempCal = (Calendar) calendar.clone();
            tempCal.set(Calendar.DAY_OF_MONTH, (int) value);
            if (tempCal.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
                c.setForeground(Color.GRAY);
            } else if ((int) value == currentDay) {
                c.setBackground(Color.YELLOW);
            } else {
                c.setForeground(Color.BLACK);
                c.setBackground(Color.WHITE);
            }
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);

            JButton btn = new JButton("选择日期");
            btn.addActionListener(e -> {
                DatePickerDialog dialog = new DatePickerDialog(frame);
                dialog.setVisible(true);
            });

            frame.add(btn, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
