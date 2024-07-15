package studentsmanageproject;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class GreetingPanel extends JPanel {
    JLabel greetingLabel = new JLabel(getGreetingMessage());
    GreetingPanel(){
        super(new GridBagLayout());
        add(greetingLabel);
    }
    private String getGreetingMessage() {
        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(new java.util.Date());
        // 根据时间返回不同的问候语
        int hour = Integer.parseInt(currentTime.substring(0, 2));
        if (hour < 8) {
            return "早上好！";
        } else if (hour < 12) {
            return "上午好！";
        } else if (hour < 18) {
            return "下午好！";
        } else {
            return "晚上好！";
        }
    }
}