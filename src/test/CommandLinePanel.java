package test;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class CommandLinePanel extends JPanel {
    private JTextPane textPane;
    private StyledDocument doc;
    private SimpleAttributeSet promptStyle;
    private SimpleAttributeSet inputStyle;
    private String prompt = "> ";
    StringBuilder currentInput = new StringBuilder();

    public CommandLinePanel() {
        setLayout(new BorderLayout());

        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setBackground(Color.BLACK);
        textPane.setForeground(Color.GREEN);
        textPane.setCaretColor(Color.GREEN);
        textPane.setEditable(false);

        doc = textPane.getStyledDocument();

        // Style for prompt
        promptStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(promptStyle, Color.GREEN);

        // Style for user input
        inputStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(inputStyle, Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);

        textPane.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String input = currentInput.toString();
                    processCommand(input);
                    currentInput.setLength(0);
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (currentInput.length() > 0) {
                        currentInput.setLength(currentInput.length() - 1);
                        updateInputArea(currentInput.toString());
                    }
                } else {
                    char c = e.getKeyChar();
                    if (Character.isDefined(c) && !Character.isISOControl(c)) {
                        currentInput.append(c);
                        updateInputArea(currentInput.toString());
                    }
                }
            }
        });

        showPrompt();
    }

    private void showPrompt() {
        try {
            doc.insertString(doc.getLength(), prompt, promptStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void updateInputArea(String input) {
        try {
            int promptLength = prompt.length();
            int totalLength = doc.getLength();
            int inputLength = currentInput.length();

            // Check if the input length exceeds the document length minus prompt length
            if (inputLength <= totalLength - promptLength) {
                doc.remove(totalLength - inputLength, inputLength);
            }
            doc.insertString(doc.getLength(), input, inputStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void processCommand(String command) {
        try {
            // Append the current input to the document
            doc.insertString(doc.getLength(), command + "\n", inputStyle);

            // Process the command
            String result = analyzeCommand(command);

            // Append the result to the document
            doc.insertString(doc.getLength(), result + "\n", promptStyle);

            // Show the prompt for the next command
            showPrompt();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String analyzeCommand(String command) {
        // Dummy command analysis, you can implement your own logic here
        if (command.equals("help")) {
            return "Available commands: help, exit";
        } else if (command.equals("exit")) {
            System.exit(0);
            return "Exiting...";
        } else {
            return "Unknown command: " + command;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Command Line Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new CommandLinePanel());
        frame.setVisible(true);
    }
}
