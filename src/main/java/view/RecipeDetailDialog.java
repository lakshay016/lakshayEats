package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import entity.SearchResult;
import entity.Ingredients;
import entity.Instructions;
import entity.Nutrition;
import interface_adapter.save.SaveController;

/**
 * Dialog showing full recipe details.
 */
public class RecipeDetailDialog extends JDialog {
    private final JLabel imageLabel;
    private final String username;

    public RecipeDetailDialog(Window parent, SearchResult result, SaveController saveController, String username) {
        super(parent, "Recipe Details", ModalityType.APPLICATION_MODAL);
        this.username = username;
        setSize(600, 700);
        setLocationRelativeTo(parent);

        // Top panel: back button and title
        JPanel topPanel = new JPanel(new BorderLayout(5,5));
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> dispose());
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
            // Replace with real user if applicable
            saveController.save(username, result);
            JOptionPane.showMessageDialog(this, "Recipe saved successfully!");
        });
        bottomPanel.add(saveButton);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        // Show dialog
        setVisible(true);
    }

    /**
     * Loads an image from URL off the EDT and sets it scaled to the imageLabel.
     */
    private void loadImageAsync(String urlString) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                URL url = new URL(urlString);
                BufferedImage img = ImageIO.read(url);
                Image scaled = img.getScaledInstance(
                        imageLabel.getPreferredSize().width,
                        imageLabel.getPreferredSize().height,
                        Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
            @Override
            protected void done() {
                try {
                    imageLabel.setIcon(get());
                } catch (Exception e) {
                    Logger.getLogger(RecipeDetailDialog.class.getName()).log(Level.SEVERE, "Failed to load image", e);
                    imageLabel.setIcon(new ImageIcon(new BufferedImage(
                            imageLabel.getPreferredSize().width,
                            imageLabel.getPreferredSize().height,
                            BufferedImage.TYPE_INT_ARGB)));
                }
            }
        }.execute();
    }
}