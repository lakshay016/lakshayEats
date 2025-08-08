package view;

import entity.Preferences;
import interface_adapter.preferences.PreferencesController;
import interface_adapter.preferences.PreferencesViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AccountPage extends JPanel {
    private final PreferencesController controller;
    private final PreferencesViewModel viewModel;

    private final JCheckBox[] dietBoxes;
    private final JCheckBox[] intoleranceBoxes;

    // Full lists — match DBUserPreferenceDataAccessObject
    private static final String[] DIETS = {
            "Gluten Free", "Ketogenic", "Vegetarian", "Lacto-Vegetarian",
            "Ovo-vegetarian", "Vegan", "Pescetarian", "Paleo", "Primal",
            "Low FODMAP", "Whole30"
    };
    private static final String[] INTOLERANCES = {
            "Dairy", "Egg", "Gluten", "Grain", "Peanut",
            "Seafood", "Sesame", "Shellfish", "Soy", "Sulfite",
            "Tree Nut", "Wheat"
    };

    public AccountPage(PreferencesController controller, PreferencesViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new JLabel("Your Preferences"));
        add(Box.createVerticalStrut(10));

        // Diet section
        add(new JLabel("Diets"));
        dietBoxes = createCheckboxGroup(DIETS);
        for (JCheckBox cb : dietBoxes) add(cb);

        add(Box.createVerticalStrut(10));

        // Intolerances section
        add(new JLabel("Intolerances"));
        intoleranceBoxes = createCheckboxGroup(INTOLERANCES);
        for (JCheckBox cb : intoleranceBoxes) add(cb);

        // Save button
        JButton saveButton = new JButton("Save Preferences");
        saveButton.addActionListener(e -> savePreferences());
        add(Box.createVerticalStrut(10));
        add(saveButton);
    }

    private JCheckBox[] createCheckboxGroup(String[] labels) {
        JCheckBox[] boxes = new JCheckBox[labels.length];
        for (int i = 0; i < labels.length; i++) {
            boxes[i] = new JCheckBox(labels[i]);
        }
        return boxes;
    }

    public void loadPreferencesForUser(String username) {
        controller.loadPreferences(username);
        Preferences prefs = viewModel.getPreferences();
        setSelected(dietBoxes, prefs.getDiets());
        setSelected(intoleranceBoxes, prefs.getIntolerances());
    }

    // Sets checkboxes based on Map<String, Integer>
    private void setSelected(JCheckBox[] boxes, Map<String, Integer> map) {
        for (JCheckBox cb : boxes) {
            cb.setSelected(map.getOrDefault(cb.getText(), 0) == 1);
        }
    }

    private void savePreferences() {
        Map<String, Integer> dietsMap = getSelectedMap(dietBoxes);
        Map<String, Integer> intolerancesMap = getSelectedMap(intoleranceBoxes);
        Preferences prefs = new Preferences(dietsMap, intolerancesMap);
        controller.savePreferences(viewModel.getUsername(), prefs);
    }

    private Map<String, Integer> getSelectedMap(JCheckBox[] boxes) {
        Map<String, Integer> map = new HashMap<>();
        for (JCheckBox cb : boxes) {
            map.put(cb.getText(), cb.isSelected() ? 1 : 0);
        }
        return map;
    }
}
