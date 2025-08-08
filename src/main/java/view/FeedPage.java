package view;

import javax.swing.*;
import java.awt.*;

public class FeedPage extends JPanel {
    public FeedPage() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        var title = new JLabel("Feed");
        title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize()+2f));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        add(title, BorderLayout.NORTH);

        var body = new JLabel("Your feed will appear here (coming soon).");
        body.setForeground(Color.GRAY);
        add(body, BorderLayout.CENTER);
    }
}
