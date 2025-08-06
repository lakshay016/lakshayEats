package view;

import javax.swing.*;
import java.awt.*;


public class LabelTextPanel extends JPanel {
    public LabelTextPanel(JLabel label, JComponent field) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(label);
        this.add(field);
        this.setOpaque(true);
        this.setBackground(new Color(255, 150, 255));
    }
}