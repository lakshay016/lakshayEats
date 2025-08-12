package view;

import javax.swing.*;
import java.awt.*;


public class LabelTextPanel extends JPanel {
    public LabelTextPanel(JLabel label, JComponent field) {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 2));
        this.add(label);
        this.add(field);
        this.setOpaque(true);
        this.setBackground(new Color(238, 255, 238)); // Light fern
    }
}