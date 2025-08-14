package view.search;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import entity.SearchResult;
import interface_adapter.FriendRequest.FriendRequestController;
import interface_adapter.save.SaveController;

public class RecipeCardPanel extends JPanel {
   // private static final Logger LOGGER = Logger.getLogger(RecipeCardPanel.class.getName());

    private final SearchResult result;
    private final JLabel imageLabel = new JLabel();
    private final SaveController saveController; // nullable
    private final String username;               // nullable
    private final FriendRequestController friendRequestController; // Add this


    // Used by SearchView (no save controller / username)
    public RecipeCardPanel(SearchResult result) {
        this(result, null, null, null);
    }

    // Used by SavedPage (has save controller and username)
    public RecipeCardPanel(SearchResult result, SaveController saveController, String username) {
        this.result = result;
        this.saveController = saveController;
        this.username = username;
        this.friendRequestController = null;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // --- Left: image (loaded async) ---
        imageLabel.setPreferredSize(new Dimension(100, 100));
        add(imageLabel, BorderLayout.WEST);
        loadImageAsync(result.getImage());

        // --- Center: info ---
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        JLabel titleLabel = new JLabel(result.getTitle());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        infoPanel.add(titleLabel);
        infoPanel.add(new JLabel("Ready in: " + result.getReadyInMinutes() + " mins"));
        add(infoPanel, BorderLayout.CENTER);

        // --- Interaction: open details (don’t hide parent) ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openDetails();
            }
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public RecipeCardPanel(SearchResult result, SaveController saveController,
                           FriendRequestController friendRequestController, String username) {
        this.result = result;
        this.saveController = saveController;
        this.friendRequestController = friendRequestController;
        this.username = username;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // --- Left: image (loaded async) ---
        imageLabel.setPreferredSize(new Dimension(100, 100));
        add(imageLabel, BorderLayout.WEST);
        loadImageAsync(result.getImage());

        // --- Center: info ---
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        JLabel titleLabel = new JLabel(result.getTitle());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        infoPanel.add(titleLabel);
        infoPanel.add(new JLabel("Ready in: " + result.getReadyInMinutes() + " mins"));
        add(infoPanel, BorderLayout.CENTER);

        // --- Interaction: open details (don't hide parent) ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openDetails();
            }
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void openDetails() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (saveController != null && username != null) {
            new RecipeDetailDialog(owner, result, saveController, username, friendRequestController).setVisible(true);
        } else {
            new RecipeDetailDialog(owner, result).setVisible(true);
        }
    }

    /**
     * Loads the recipe image on a background thread,
     * then sets it on the imageLabel when done.
     */
    private void loadImageAsync(String urlString) {
        // show text placeholder immediately
        imageLabel.setText("No image available");
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(120, 90)); // keeps layout stable
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imageLabel.setIcon(null); // ensure no old image stays

        // if there's no URL, just leave the placeholder
        if (urlString == null || urlString.isBlank()) {
            return;
        }
        final int boxW = 120, boxH = 90;
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    BufferedImage img = ImageIO.read(new URL(urlString));
                    if (img == null) throw new IOException("ImageIO.read returned null");
                    // scale image
                    Image scaled = img.getScaledInstance(boxW, boxH, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                } catch (Exception e) {
                    // Fail silently; placeholder stays
                    Logger.getLogger(RecipeDetailDialog.class.getName())
                            .log(Level.WARNING, "Failed to load image: {0}", e.toString());
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        imageLabel.setIcon(icon);
                        imageLabel.setText(null); // remove placeholder text
                    }
                } catch (Exception e) {
                    // If we can't get the image, we just keep the placeholder
                }
            }
        }.execute();
    }
}

