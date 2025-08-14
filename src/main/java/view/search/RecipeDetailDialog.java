package view.search;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

import data_access.DBReviewDataAccessObject;
import entity.*;
import interface_adapter.save.SaveController;

/**
 * Dialog showing full recipe details.
 */
public class RecipeDetailDialog extends JDialog {
    private final JLabel imageLabel;
    private final String username;
    private final SearchResult result;



    public RecipeDetailDialog(Window parent, SearchResult result, SaveController saveController, String username) {
        super(parent, "Recipe Details", ModalityType.APPLICATION_MODAL);
        this.result = result;
        this.username = username;
        setSize(600, 700);
        setLocationRelativeTo(parent);

        // Top panel: back button and title
        JPanel topPanel = new JPanel(new BorderLayout(5,5));
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> {
            // Get the owner window (SearchFrame) and show it
            Window owner = getOwner();
            if (owner != null) {
                owner.setVisible(true);
            }
            // Hide this dialog
            setVisible(false);
        });
        JLabel titleLabel = new JLabel(result.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Image placeholder
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(400, 300));
        // load asynchronously
        loadImageAsync(result.getImage());

        // Tabs for details
        JTabbedPane tabs = new JTabbedPane();

        // Ingredients Tab
        DefaultListModel<String> ingModel = new DefaultListModel<>();
        for (Ingredients ing : result.getIngredients()) {
            String text = String.format("%s: %.2f %s", ing.getName(), ing.getAmount(), ing.getUnit());
            ingModel.addElement(text);
        }
        JList<String> ingList = new JList<>(ingModel);
        tabs.addTab("Ingredients", new JScrollPane(ingList));

        // Instructions Tab
        DefaultListModel<String> instModel = new DefaultListModel<>();
        for (Instructions inst : result.getInstructions()) {
            String text = String.format("%d. %s", inst.getNumber(), inst.getStep());
            instModel.addElement(text);
        }
        JList<String> instList = new JList<>(instModel);
        tabs.addTab("Instructions", new JScrollPane(instList));

        // Nutrition Tab
        JTextArea nutritionArea = new JTextArea();
        nutritionArea.setEditable(false);
        nutritionArea.setLineWrap(true);
        nutritionArea.setWrapStyleWord(true);
        StringBuilder nutText = new StringBuilder();
        for (Nutrition n : result.getNutrition()) {
            nutText.append(String.format("%s: %.2f %s (%.0f%%)\n",
                    n.getName(), n.getAmount(), n.getUnit(), n.getPercentOfDailyNeeds()));
        }
        nutritionArea.setText(nutText.toString());
        tabs.addTab("Nutrition", new JScrollPane(nutritionArea));

        // Layout content: image at top of center, tabs below
        JPanel centerPanel = new JPanel(new BorderLayout(10,10));
        centerPanel.add(imageLabel, BorderLayout.NORTH);
        centerPanel.add(tabs, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout(10,10));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Recipe");
        saveButton.addActionListener(e -> {

            saveController.save(username, result);
            JOptionPane.showMessageDialog(this, "Recipe saved successfully!");
        });
        bottomPanel.add(saveButton);

        JButton unsaveButton = new JButton("Unsave Recipe");
        unsaveButton.addActionListener(e -> {
            saveController.unsave(username, result.getId());
            JOptionPane.showMessageDialog(this, "Recipe unsaved successfully!");
        });
        bottomPanel.add(unsaveButton);

        JButton viewReviewsButton = new JButton("View Reviews");
        viewReviewsButton.addActionListener(e -> {
            showReviewsDialog(result.getId());
        });

        JButton createReviewButton = new JButton("Create Review");
        createReviewButton.addActionListener(e -> {
            showCreateReviewDialog(result.getId());
        });

        bottomPanel.add(viewReviewsButton);
        bottomPanel.add(createReviewButton);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void showReviewsDialog(int recipeId) {
        DBReviewDataAccessObject reviewDAO = new DBReviewDataAccessObject();
        List<Review> reviews = reviewDAO.fetchByRecipeId(String.valueOf(recipeId));

        JDialog reviewsDialog = new JDialog(this, "Reviews for " + result.getTitle(), true);
        reviewsDialog.setSize(500, 400);
        reviewsDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (reviews.isEmpty()) {
            JLabel noReviewsLabel = new JLabel("No reviews yet for this recipe.", SwingConstants.CENTER);
            noReviewsLabel.setFont(noReviewsLabel.getFont().deriveFont(Font.ITALIC));
            mainPanel.add(noReviewsLabel, BorderLayout.CENTER);
        } else {
            DefaultListModel<String> reviewListModel = new DefaultListModel<>();
            for (Review review : reviews) {
                String reviewText = String.format("<html><b>%s</b> - %d/10 stars<br/>%s<br/><i>%s</i></html>",
                        review.getAuthor(),
                        review.getRating(),
                        review.getText(),
                        review.getLastReviewedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                );
                reviewListModel.addElement(reviewText);
            }

            JList<String> reviewList = new JList<>(reviewListModel);
            reviewList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                            index, isSelected, cellHasFocus);
                    label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    return label;
                }
            });

            JScrollPane scrollPane = new JScrollPane(reviewList);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> reviewsDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        reviewsDialog.add(mainPanel);
        reviewsDialog.setVisible(true);
    }
    public RecipeDetailDialog(Window parent, SearchResult result) {
        
        super(parent, "Recipe Details", ModalityType.APPLICATION_MODAL);
        setSize(600, 700);
        setLocationRelativeTo(parent);

        // Top panel: back button and title
        JPanel topPanel = new JPanel(new BorderLayout(5,5));
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> {
            // Get the owner window (SearchFrame) and show it
            Window owner = getOwner();
            if (owner != null) {
                owner.setVisible(true);
            }
            // Hide this dialog
            setVisible(false);
        });
        JLabel titleLabel = new JLabel(result.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Image placeholder
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(400, 300));
        // load asynchronously
        loadImageAsync(result.getImage());

        // Tabs for details
        JTabbedPane tabs = new JTabbedPane();

        // Ingredients Tab
        DefaultListModel<String> ingModel = new DefaultListModel<>();
        for (Ingredients ing : result.getIngredients()) {
            String text = String.format("%s: %.2f %s", ing.getName(), ing.getAmount(), ing.getUnit());
            ingModel.addElement(text);
        }
        JList<String> ingList = new JList<>(ingModel);
        tabs.addTab("Ingredients", new JScrollPane(ingList));

        // Instructions Tab
        DefaultListModel<String> instModel = new DefaultListModel<>();
        for (Instructions inst : result.getInstructions()) {
            String text = String.format("%d. %s", inst.getNumber(), inst.getStep());
            instModel.addElement(text);
        }
        JList<String> instList = new JList<>(instModel);
        tabs.addTab("Instructions", new JScrollPane(instList));

        // Nutrition Tab
        JTextArea nutritionArea = new JTextArea();
        nutritionArea.setEditable(false);
        nutritionArea.setLineWrap(true);
        nutritionArea.setWrapStyleWord(true);
        StringBuilder nutText = new StringBuilder();
        for (Nutrition n : result.getNutrition()) {
            nutText.append(String.format("%s: %.2f %s (%.0f%%)\n",
                    n.getName(), n.getAmount(), n.getUnit(), n.getPercentOfDailyNeeds()));
        }
        nutritionArea.setText(nutText.toString());
        tabs.addTab("Nutrition", new JScrollPane(nutritionArea));

        // Layout content: image at top of center, tabs below
        JPanel centerPanel = new JPanel(new BorderLayout(10,10));
        centerPanel.add(imageLabel, BorderLayout.NORTH);
        centerPanel.add(tabs, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout(10,10));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        // Show dialog
        setVisible(true);
        username = "";
        this.result = null;
    }

    private void showCreateReviewDialog(int recipeId) {
        JDialog createReviewDialog = new JDialog(this, "Create Review for " + result.getTitle(), true);
        createReviewDialog.setSize(400, 300);
        createReviewDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        // Author field
        formPanel.add(new JLabel("Your Name:"));
        JTextField authorField = new JTextField();
        formPanel.add(authorField);

        // Rating field
        formPanel.add(new JLabel("Rating (1-10):"));
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        formPanel.add(ratingSpinner);

        // Review text
        formPanel.add(new JLabel("Review:"));
        JTextArea reviewTextArea = new JTextArea(4, 20);
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        JScrollPane textScrollPane = new JScrollPane(reviewTextArea);
        formPanel.add(textScrollPane);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Submit Review");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            String author = authorField.getText().trim();
            int rating = (Integer) ratingSpinner.getValue();
            String reviewText = reviewTextArea.getText().trim();

            if (author.isEmpty() || reviewText.isEmpty()) {
                JOptionPane.showMessageDialog(createReviewDialog,
                        "Please fill in all fields.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create and save the review
            Review newReview = new Review(recipeId, rating, author, reviewText, java.time.LocalDateTime.now());
            DBReviewDataAccessObject reviewDAO = new DBReviewDataAccessObject();
            boolean success = reviewDAO.save(newReview);

            if (success) {
                JOptionPane.showMessageDialog(createReviewDialog,
                        "Review submitted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                createReviewDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(createReviewDialog,
                        "Failed to submit review. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> createReviewDialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        createReviewDialog.add(mainPanel);
        createReviewDialog.setVisible(true);
    }

    /**
     * Loads an image from URL off the EDT and sets it scaled to the imageLabel.
     */
    private void loadImageAsync(String urlString) {
        imageLabel.setText("No image available");
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(400, 300)); // keep layout stable
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imageLabel.setIcon(null);

        if (urlString == null || urlString.isBlank()) {
            return;
        }

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    var url = new URL(urlString);
                    var img = ImageIO.read(url);
                    if (img == null) throw new java.io.IOException("ImageIO.read returned null");
                    Image scaled = img.getScaledInstance(
                            imageLabel.getPreferredSize().width,
                            imageLabel.getPreferredSize().height,
                            Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                } catch (Exception ex) {
                    Logger.getLogger(RecipeDetailDialog.class.getName())
                            .log(Level.WARNING, "Failed to load image: {0}", ex.toString());
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        imageLabel.setIcon(icon);
                        imageLabel.setText(null);
                        imageLabel.setBorder(null);
                    }
                } catch (Exception ignored) {
                    // keep placeholder
                }
            }
        }.execute();
    }
}