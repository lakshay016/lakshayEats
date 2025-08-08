package view.search;

import javax.swing.*;
import java.awt.*;

import entity.FilterOptions;
import entity.Preferences;
import use_case.preferences.PreferencesDataAccessInterface;

import java.util.*;
import java.util.stream.Collectors;

public class FilterDialog extends JDialog {
    private FilterOptions filterOptions;

    private static final String[] CUISINES = {
            "African", "Asian", "American", "British", "Cajun", "Caribbean", "Chinese",
            "Eastern European", "European", "French", "German", "Greek", "Indian", "Irish",
            "Italian", "Japanese", "Jewish", "Korean", "Latin American", "Mediterranean",
            "Mexican", "Middle Eastern", "Nordic", "Southern", "Spanish", "Thai", "Vietnamese"
    };
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
    private static final String[] MEAL_TYPES = {
            "Any", "main course", "side dish", "dessert", "appetizer", "salad",
            "bread", "breakfast", "soup", "beverage", "sauce",
            "marinade", "fingerfood", "snack", "drink"
    };
    private static final String[] SORT_OPTIONS = {
            "Default", "meta-score", "popularity", "healthiness", "price",
            "max-used-ingredients", "min-missing-ingredients", "calories",
            "protein", "carbohydrates", "total-fat", "sugar", "sodium", "fiber"
    };

    private Map<String, JCheckBox> cuisineCheckboxes = new LinkedHashMap<>();
    private Map<String, JCheckBox> excludeCuisineCheckboxes = new LinkedHashMap<>();
    private Map<String, JCheckBox> dietCheckboxes = new LinkedHashMap<>();
    private Map<String, JCheckBox> intoleranceCheckboxes = new LinkedHashMap<>();
    private JComboBox<String> typeCombo;
    private JSpinner maxReadyTimeSpinner;
    private JSpinner minServingsSpinner;
    private JSpinner maxServingsSpinner;
    private JComboBox<String> sortCombo;
    private JComboBox<String> sortDirCombo;

    private final PreferencesDataAccessInterface preferencesDAO;
    private final String username;

    public FilterDialog(JFrame parent, String username, PreferencesDataAccessInterface preferencesDAO) {
        super(parent, "Filter Options", true);
        this.username = username;
        this.preferencesDAO = preferencesDAO;

        setSize(600, 500);
        setLocationRelativeTo(parent);

        initializeComponents();
        loadUserPreferences();
    }


    private void initializeComponents() {
        JTabbedPane tabs = new JTabbedPane();

        // Cuisine tab
        JPanel cuisinePanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel includePanel = new JPanel(new GridLayout(0, 2));
        includePanel.setBorder(BorderFactory.createTitledBorder("Include Cuisines"));
        for (String c : CUISINES) {
            JCheckBox cb = new JCheckBox(c);
            cuisineCheckboxes.put(c, cb);
            includePanel.add(cb);
        }
        JPanel excludePanel = new JPanel(new GridLayout(0, 2));
        excludePanel.setBorder(BorderFactory.createTitledBorder("Exclude Cuisines"));
        for (String c : CUISINES) {
            JCheckBox cb = new JCheckBox(c);
            excludeCuisineCheckboxes.put(c, cb);
            excludePanel.add(cb);
        }
        cuisinePanel.add(new JScrollPane(includePanel));
        cuisinePanel.add(new JScrollPane(excludePanel));
        tabs.addTab("Cuisines", cuisinePanel);

        // Diets & Intolerances tab
        JPanel dietPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel dietsPanel = new JPanel(new GridLayout(0, 1));
        dietsPanel.setBorder(BorderFactory.createTitledBorder("Diets"));
        for (String d : DIETS) {
            JCheckBox cb = new JCheckBox(d);
            dietCheckboxes.put(d, cb);
            dietsPanel.add(cb);
        }
        JPanel intolerancesPanel = new JPanel(new GridLayout(0, 1));
        intolerancesPanel.setBorder(BorderFactory.createTitledBorder("Intolerances"));
        for (String i : INTOLERANCES) {
            JCheckBox cb = new JCheckBox(i);
            intoleranceCheckboxes.put(i, cb);
            intolerancesPanel.add(cb);
        }
        dietPanel.add(new JScrollPane(dietsPanel));
        dietPanel.add(new JScrollPane(intolerancesPanel));
        tabs.addTab("Diets & Intolerances", dietPanel);

        // Meal Type & Sorting tab
        JPanel typeSortPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        typeSortPanel.add(new JLabel("Meal Type:"));
        typeCombo = new JComboBox<>(MEAL_TYPES);
        typeSortPanel.add(typeCombo);
        typeSortPanel.add(new JLabel("Sort By:"));
        sortCombo = new JComboBox<>(SORT_OPTIONS);
        typeSortPanel.add(sortCombo);
        typeSortPanel.add(new JLabel("Direction:"));
        sortDirCombo = new JComboBox<>(new String[]{"asc", "desc"});
        typeSortPanel.add(sortDirCombo);
        tabs.addTab("Type & Sort", typeSortPanel);

        // Time & Servings tab
        JPanel timePanel = new JPanel(new GridLayout(3, 2, 5, 5));
        timePanel.add(new JLabel("Max Ready Time (mins):"));
        maxReadyTimeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 5));
        timePanel.add(maxReadyTimeSpinner);
        timePanel.add(new JLabel("Min Servings:"));
        minServingsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        timePanel.add(minServingsSpinner);
        timePanel.add(new JLabel("Max Servings:"));
        maxServingsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        timePanel.add(maxServingsSpinner);
        tabs.addTab("Time & Servings", timePanel);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");
        JButton resetBtn = new JButton("Reset");
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(resetBtn);

        okBtn.addActionListener(e -> onSave());
        cancelBtn.addActionListener(e -> onCancel());
        resetBtn.addActionListener(e -> resetFilters());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUserPreferences() {
        try {
            Preferences prefs = preferencesDAO.getPreferences(username);

            if (prefs != null) {
                // Diets
                Map<String, Integer> diets = prefs.getDiets();
                for (String diet : DIETS) {
                    if (diets.getOrDefault(diet, 0) == 1) {
                        JCheckBox cb = dietCheckboxes.get(diet);
                        if (cb != null) cb.setSelected(true);
                    }
                }

                // Intolerances
                Map<String, Integer> intolerances = prefs.getIntolerances();
                for (String intolerance : INTOLERANCES) {
                    if (intolerances.getOrDefault(intolerance, 0) == 1) {
                        JCheckBox cb = intoleranceCheckboxes.get(intolerance);
                        if (cb != null) cb.setSelected(true);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading preferences for " + username + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void resetFilters() {
        // Clear all checkboxes
        cuisineCheckboxes.values().forEach(cb -> cb.setSelected(false));
        excludeCuisineCheckboxes.values().forEach(cb -> cb.setSelected(false));
        dietCheckboxes.values().forEach(cb -> cb.setSelected(false));
        intoleranceCheckboxes.values().forEach(cb -> cb.setSelected(false));

        // Reset combo boxes and spinners to defaults
        typeCombo.setSelectedIndex(0); // "Any"
        sortCombo.setSelectedIndex(0); // "Default"
        sortDirCombo.setSelectedIndex(0); // "asc"
        maxReadyTimeSpinner.setValue(0);
        minServingsSpinner.setValue(0);
        maxServingsSpinner.setValue(0);

        // Reload user preferences after reset
        loadUserPreferences();
    }

    private void onSave() {
        filterOptions = new FilterOptions();
        filterOptions.setCuisines(cuisineCheckboxes.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));
        filterOptions.setExcludeCuisines(excludeCuisineCheckboxes.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));
        filterOptions.setDiets(dietCheckboxes.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));
        filterOptions.setIntolerances(intoleranceCheckboxes.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));

        // Handle meal type - set to null if "Any" is selected
        String selectedType = (String) typeCombo.getSelectedItem();
        filterOptions.setType("Any".equals(selectedType) ? null : selectedType);

        // Handle numeric values - set to null if they are 0 (meaning no filter)
        Integer maxReadyTime = (Integer) maxReadyTimeSpinner.getValue();
        filterOptions.setMaxReadyTime(maxReadyTime == 0 ? null : maxReadyTime);
        Integer minServings = (Integer) minServingsSpinner.getValue();
        filterOptions.setMinServings(minServings == 0 ? null : minServings);
        Integer maxServings = (Integer) maxServingsSpinner.getValue();
        filterOptions.setMaxServings(maxServings == 0 ? null : maxServings);

        // Handle sort options - set to null if "Default" is selected
        String selectedSort = (String) sortCombo.getSelectedItem();
        filterOptions.setSort("Default".equals(selectedSort) ? null : selectedSort);

        // Always set sort direction (since you want "asc" as default)
        filterOptions.setSortDirection((String) sortDirCombo.getSelectedItem());

        dispose();
    }

    private void onCancel() {
        filterOptions = null;
        dispose();
    }

    public FilterOptions getFilterOptions() {
        return filterOptions;
    }
}