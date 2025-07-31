package view.search;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IngredientPanel extends JPanel {
    private JTextField includeField;
    private JTextField excludeField;

    public IngredientPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Include Ingredients (comma-separated):"), gbc);

        gbc.gridx = 1;
        includeField = new JTextField(20);
        add(includeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Exclude Ingredients (comma-separated):"), gbc);

        gbc.gridx = 1;
        excludeField = new JTextField(20);
        add(excludeField, gbc);
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
