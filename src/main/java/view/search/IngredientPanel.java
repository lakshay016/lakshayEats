package view.search;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IngredientPanel extends JPanel {
    private final JTextField includeField = new JTextField(28); // wider baseline
    private final JTextField excludeField = new JTextField(28);

    public IngredientPanel() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridy = 0;

        // "Include" label
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Include Ingredients (comma-separated):"), gbc);

        // Include field (stretch)
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(includeField, gbc);

        // Next row
        gbc.gridy++;

        // "Exclude" label
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Exclude Ingredients (comma-separated):"), gbc);

        // Exclude field (stretch)
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(excludeField, gbc);
    }

    // If you want to tweak later from SearchView:
    public void setTextFieldColumns(int cols) {
        includeField.setColumns(cols);
        excludeField.setColumns(cols);
        revalidate();
    }
    /**
     * Returns a list of included ingredients, split on commas and trimmed.
     */
    public List<String> getIncludeIngredients() {
        return parseCommaSeparated(includeField.getText());
    }

    /**
     * Returns a list of excluded ingredients, split on commas and trimmed.
     */
    public List<String> getExcludeIngredients() {
        return parseCommaSeparated(excludeField.getText());
    }

    private List<String> parseCommaSeparated(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
