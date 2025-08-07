package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import entity.Review;

public class ReviewView extends JFrame {
    private final JTextField recipeIdField = new JTextField(10);
    private final JTextField authorField = new JTextField(15);
    private final JSpinner ratingSpinner;
    private final JTextArea reviewTextArea = new JTextArea(5, 30);
    private final JButton submitButton = new JButton("Submit Review");
    private final JButton viewAllButton = new JButton("View All Reviews");
    private final JPanel reviewsDisplayPanel;

    // Mock data for testing
    private final List<Review> mockReviews = new ArrayList<>();

    public ReviewView() {
        setTitle("Review System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

        reviewsDisplayPanel = new JPanel();
        reviewsDisplayPanel.setLayout(new BoxLayout(reviewsDisplayPanel, BoxLayout.Y_AXIS));

        setLayout(new BorderLayout(10, 10));

        add(createInputPanel(), BorderLayout.NORTH);
        add(createReviewsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        submitButton.addActionListener(this::handleSubmit);
        viewAllButton.addActionListener(this::handleViewAll);

        addMockReviews();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Write a Review"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Recipe ID:"), gbc);
        gbc.gridx = 1;
        panel.add(recipeIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Rating (1-10):"), gbc);
        gbc.gridx = 1;
        panel.add(ratingSpinner, gbc);

        // Review text
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Review:"), gbc);
        gbc.gridx = 1;
        JScrollPane textScrollPane = new JScrollPane(reviewTextArea);
        textScrollPane.setPreferredSize(new Dimension(300, 100));
        panel.add(textScrollPane, gbc);

        // Submit button
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(submitButton, gbc);

        return panel;
    }

    private JPanel createReviewsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reviews"));

        JScrollPane scrollPane = new JScrollPane(reviewsDisplayPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(viewAllButton);
        return panel;
    }

    private void handleSubmit(ActionEvent e) {
        try {
            int recipeId = Integer.parseInt(recipeIdField.getText());
            String author = authorField.getText();
            int rating = (Integer) ratingSpinner.getValue();
            String text = reviewTextArea.getText();

            if (author.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an author name.");
                return;
            }

            if (text.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a review text.");
                return;
            }

            // Create new review
            Review review = new Review(recipeId, rating, author, text, LocalDateTime.now());
            mockReviews.add(review);

            // Clear fields
            recipeIdField.setText("");
            authorField.setText("");
            ratingSpinner.setValue(5);
            reviewTextArea.setText("");

            // Show success message
            JOptionPane.showMessageDialog(this, "Review submitted successfully!");

            // Update display
            displayReviews();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid recipe ID (number).");
        }
    }

    private void handleViewAll(ActionEvent e) {
        displayReviews();
    }

    private void displayReviews() {
        reviewsDisplayPanel.removeAll();

        if (mockReviews.isEmpty()) {
            JLabel noReviewsLabel = new JLabel("No reviews yet. Be the first to write one!");
            noReviewsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            reviewsDisplayPanel.add(noReviewsLabel);
        } else {
            for (Review review : mockReviews) {
                JPanel reviewPanel = createReviewPanel(review);
                reviewsDisplayPanel.add(reviewPanel);
                reviewsDisplayPanel.add(Box.createVerticalStrut(10)); // Spacing
            }
        }

        reviewsDisplayPanel.revalidate();
        reviewsDisplayPanel.repaint();
    }

    private JPanel createReviewPanel(Review review) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.setBackground(Color.WHITE);

        // Header with recipe ID, author, and rating
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));

        JLabel recipeLabel = new JLabel("Recipe #" + review.getRecipeId());
        recipeLabel.setFont(recipeLabel.getFont().deriveFont(Font.BOLD));

        JLabel authorLabel = new JLabel("by " + review.getAuthor());
        authorLabel.setForeground(Color.DARK_GRAY);

        JLabel ratingLabel = new JLabel("★ " + review.getRating() + "/10");
        ratingLabel.setForeground(Color.ORANGE);
        ratingLabel.setFont(ratingLabel.getFont().deriveFont(Font.BOLD));

        headerPanel.add(recipeLabel);
        headerPanel.add(authorLabel);
        headerPanel.add(ratingLabel);

        // Review text
        JTextArea textArea = new JTextArea(review.getText());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.WHITE);

        // Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        JLabel dateLabel = new JLabel("Reviewed on " + review.getLastReviewedAt().format(formatter));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setFont(dateLabel.getFont().deriveFont(Font.ITALIC, 10f));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.add(dateLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void addMockReviews() {
        // Add some sample reviews for testing
        mockReviews.add(new Review(12345, 8, "Alice", "Great recipe! Easy to follow and delicious.", LocalDateTime.now().minusDays(2)));
        mockReviews.add(new Review(12345, 6, "Bob", "Good but a bit too spicy for my taste.", LocalDateTime.now().minusDays(1)));
        mockReviews.add(new Review(67890, 9, "Charlie", "Absolutely love this recipe! Will make it again.", LocalDateTime.now().minusHours(5)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReviewView reviewView = new ReviewView();
            reviewView.setVisible(true);
        });
    }
}