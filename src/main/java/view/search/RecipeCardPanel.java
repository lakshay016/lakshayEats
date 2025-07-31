package view.search;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Logger;

import entity.SearchResult;

public class RecipeCardPanel extends JPanel {
    private final SearchResult result;
    private final JLabel imageLabel;

    public RecipeCardPanel(SearchResult result) {
        this.result = result;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Image placeholder, replaced asynchronously
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        add(imageLabel, BorderLayout.WEST);
        loadImageAsync();

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        JLabel titleLabel = new JLabel(result.getTitle());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        infoPanel.add(titleLabel);
        infoPanel.add(new JLabel("Ready in: " + result.getReadyInMinutes() + " mins"));
        add(infoPanel, BorderLayout.CENTER);

        // Click listener to open detail view
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RecipeDetailDialog detail = new RecipeDetailDialog(
                        SwingUtilities.getWindowAncestor(RecipeCardPanel.this),
                        result);
                detail.setVisible(true);
            }
        });
    }

    /**
     * Loads the recipe image on a background thread,
     * then sets it on the imageLabel when done.
     */
    private void loadImageAsync() {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                URL url = new URL(result.getImage());
                BufferedImage img = ImageIO.read(url);
                Dimension preferredSize = imageLabel.getPreferredSize();
                Image scaled = img.getScaledInstance(
                        preferredSize.width, preferredSize.height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }

            @Override
            protected void done() {
                try {
                    imageLabel.setIcon(get());
                } catch (Exception e) {
                    Logger LOGGER = null;
                    LOGGER.warning("Failed to load recipe image: " + e.getMessage());
                    // Keep placeholder if loading fails
                }
            }
        }.execute();
    }
}
