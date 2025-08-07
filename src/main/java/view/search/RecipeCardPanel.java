package view.search;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Logger;

import entity.SearchResult;
import interface_adapter.save.SaveController;
import view.LoggedInView;
import view.search.RecipeDetailDialog;

public class RecipeCardPanel extends JPanel {
    private final SearchResult result;
    private final JLabel imageLabel;
    private static final Logger LOGGER = Logger.getLogger(view.search.RecipeCardPanel.class.getName());
    private final SaveController saveController;


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
        saveController = null;
    }

    public RecipeCardPanel(SearchResult result, SaveController saveController, String username) {
        this.result = result;
        this.saveController = saveController;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        add(imageLabel, BorderLayout.WEST);
        loadImageAsync();

        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        JLabel titleLabel = new JLabel(result.getTitle());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        infoPanel.add(titleLabel);
        infoPanel.add(new JLabel("Ready in: " + result.getReadyInMinutes() + " mins"));
        add(infoPanel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.getWindowAncestor(RecipeCardPanel.this).setVisible(false);

                view.search.RecipeDetailDialog detail = new view.search.RecipeDetailDialog(
                        SwingUtilities.getWindowAncestor(RecipeCardPanel.this),
                        result, saveController, username);
                detail.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        SwingUtilities.getWindowAncestor(RecipeCardPanel.this).setVisible(true);
                    }
                });
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