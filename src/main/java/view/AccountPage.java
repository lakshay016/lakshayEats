package view;

import entity.Preferences;
import interface_adapter.preferences.PreferencesController;
import interface_adapter.preferences.PreferencesViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class AccountPage extends JPanel implements PropertyChangeListener {
    private final PreferencesController controller;
    private final PreferencesViewModel viewModel;
    private final Runnable onChangePassword; // new
    private final JCheckBox[] dietBoxes;
    private final JCheckBox[] intoleranceBoxes;

    // Keep these in sync with DB/FilterDialog
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

    public AccountPage(PreferencesController controller,
                       PreferencesViewModel viewModel,
                       Runnable onChangePassword
                       ) { // new ctor param
        this.controller = controller;
        this.viewModel = viewModel;
        this.onChangePassword = onChangePassword;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // --- Tabs ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Preferences", buildPreferencesTab());
        tabs.addTab("Security", buildSecurityTab());
        add(tabs, BorderLayout.CENTER);

        // Listen for VM updates (e.g., save success/fail, loaded prefs)
        viewModel.addPropertyChangeListener(this);

        // Build arrays after panels are created (used by setSelected)
        dietBoxes = extractBoxesFromContainer(dietsPanel);
        intoleranceBoxes = extractBoxesFromContainer(intolerancesPanel);
    }

    // -------- Preferences tab UI --------
    private JPanel dietsPanel;
    private JPanel intolerancesPanel;

    private JComponent buildPreferencesTab() {
        JPanel container = new JPanel(new BorderLayout(8,8));

        JLabel title = new JLabel("Your Preferences");
        title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize()+2f));
        container.add(title, BorderLayout.NORTH);

        // Two columns side-by-side with independent scroll
        JPanel twoCols = new JPanel(new GridLayout(1, 2, 8, 8));

        dietsPanel = new JPanel(new GridLayout(0, 1));
        dietsPanel.setBorder(BorderFactory.createTitledBorder("Diets"));
        for (String d : DIETS) dietsPanel.add(new JCheckBox(d));

        intolerancesPanel = new JPanel(new GridLayout(0, 1));
        intolerancesPanel.setBorder(BorderFactory.createTitledBorder("Intolerances"));
        for (String i : INTOLERANCES) intolerancesPanel.add(new JCheckBox(i));

        twoCols.add(wrapScroll(dietsPanel));
        twoCols.add(wrapScroll(intolerancesPanel));
        container.add(twoCols, BorderLayout.CENTER);

        // Save button pinned at the bottom
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Save Preferences");
        save.addActionListener(e -> savePreferences());
        footer.add(save);
        container.add(footer, BorderLayout.SOUTH);

        return container;
    }

    private static JScrollPane wrapScroll(JComponent c) {
        JScrollPane sp = new JScrollPane(c,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }

    // -------- Security tab UI --------
    private JComponent buildSecurityTab() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int r = 0;
        JLabel title = new JLabel("Account Security");
        title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize()+2f));
        gbc.gridx = 0; gbc.gridy = r++; gbc.gridwidth = 2;
        p.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = r; p.add(new JLabel("Username"), gbc);
        JTextField user = new JTextField(viewModel.getUsername() != null ? viewModel.getUsername() : "");
        user.setEditable(false);
        gbc.gridx = 1; p.add(user, gbc);
        r++;

        JButton change = new JButton("Change Password…");
        change.addActionListener(e -> {
            if (onChangePassword != null) onChangePassword.run();
        });
        gbc.gridx = 1; gbc.gridy = r;
        p.add(change, gbc);

        return p;
    }

    // -------- Public API --------
    public void loadPreferencesForUser(String username) {
        // Keep username in VM for the save call
        viewModel.setUsername(username);
        // Ask the use case to load; presenter will set VM.preferences and fire change
        controller.loadPreferences(username);
    }

    // -------- Internal helpers --------
    private JCheckBox[] extractBoxesFromContainer(JPanel container) {
        int n = container.getComponentCount();
        JCheckBox[] arr = new JCheckBox[n];
        for (int i = 0; i < n; i++) arr[i] = (JCheckBox) container.getComponent(i);
        return arr;
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private void setSelected(JCheckBox[] boxes, Map<String, Integer> map) {
        if (map == null) return;

        Map<String,Integer> normMap = new HashMap<>();
        for (var e : map.entrySet()) {
            normMap.put(norm(e.getKey()), e.getValue());
        }

        for (JCheckBox cb : boxes) {
            Integer v = normMap.get(norm(cb.getText()));
            boolean on = v != null && v != 0;
            cb.setSelected(on);
        }
    }


    private Map<String, Integer> getSelectedMap(JCheckBox[] boxes) {
        Map<String, Integer> map = new HashMap<>();
        for (JCheckBox cb : boxes) {
            map.put(cb.getText(), cb.isSelected() ? 1 : 0);
        }
        return map;
    }

    private void savePreferences() {
        Map<String, Integer> dietsMap = getSelectedMap(dietBoxes);
        Map<String, Integer> intolerancesMap = getSelectedMap(intoleranceBoxes);
        Preferences prefs = new Preferences(dietsMap, intolerancesMap);
        controller.savePreferences(viewModel.getUsername(), prefs);
        controller.loadPreferences(viewModel.getUsername());
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            Preferences prefs = viewModel.getPreferences();
            if (prefs != null) {
                setSelected(dietBoxes, prefs.getDiets());
                setSelected(intoleranceBoxes, prefs.getIntolerances());
                revalidate();
                repaint();
            }
            String msg = viewModel.getMessage();
            if (msg != null && !msg.isBlank()) {
                JOptionPane.showMessageDialog(this, msg, "Preferences", JOptionPane.INFORMATION_MESSAGE);
                viewModel.setMessage(null);
            }
        });
    }
}
