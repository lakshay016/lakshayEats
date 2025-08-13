package view;

import entity.Preferences;
import interface_adapter.ViewManagerModel;
import interface_adapter.change_password.ChangePasswordController;
import interface_adapter.change_password.LoggedInViewModel;
import interface_adapter.preferences.PreferencesController;
import interface_adapter.preferences.PreferencesViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountPage extends JPanel implements PropertyChangeListener {
    private final PreferencesController controller;
    private final PreferencesViewModel viewModel;
    private final JCheckBox[] dietBoxes;
    private final JCheckBox[] intoleranceBoxes;
    private final ViewManagerModel viewManagerModel;

    private final ChangePasswordController changePasswordController;
    private final LoggedInViewModel loggedInViewModel;
    private final String currentUsername;


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
                       PreferencesViewModel viewModel,ChangePasswordController changePasswordController,
                       LoggedInViewModel loggedInViewModel,
                       String currentUsername,
                       ViewManagerModel viewManagerModel)
    {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.changePasswordController = changePasswordController;
        this.loggedInViewModel = loggedInViewModel;
        this.currentUsername = currentUsername;

        loggedInViewModel.addPropertyChangeListener(this);



        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // --- Tabs ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Preferences", buildPreferencesTab());
        tabs.addTab("Security", buildSecurityTab());
        add(tabs, BorderLayout.CENTER);

        // Listen for VM updates (e.g., save success/fail, loaded prefs)
        viewModel.addPropertyChangeListener(this);

        // Build arrays after panels are created (used by applyVmToCheckboxes)
        dietBoxes = extractBoxesFromContainer(dietsPanel);
        intoleranceBoxes = extractBoxesFromContainer(intolerancesPanel);

        // If VM already has prefs (e.g., set before this page opens), apply them now on EDT
        SwingUtilities.invokeLater(this::applyVmToCheckboxes);
    }


    // -------- Preferences tab UI --------
    private JPanel dietsPanel;
    private JPanel intolerancesPanel;

    private JComponent buildPreferencesTab() {
        JPanel container = new JPanel(new BorderLayout(8, 8));

        JLabel title = new JLabel("Your Preferences");
        title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize() + 2f));
        container.add(title, BorderLayout.NORTH);

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
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int r = 0;
        JLabel title = new JLabel("Account Security");
        title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize() + 2f));
        gbc.gridx = 0; gbc.gridy = r++; gbc.gridwidth = 2;
        p.add(title, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = r;

        JLabel greeting = new JLabel("<html>Hello <b>" + currentUsername + "</b></html>");
        greeting.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(greeting, gbc);

        r++;




        JButton change = new JButton("Change Password…");
        change.addActionListener(e -> showChangePasswordDialog());

        gbc.gridx = 1; gbc.gridy = r;
        p.add(change, gbc);
        r++;

        // Add logout button
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            // Navigate back to login view
            viewManagerModel.setState("log in");
            viewManagerModel.firePropertyChanged();
        });
        gbc.gridx = 1; gbc.gridy = r;
        p.add(logout, gbc);

        return p;
    }

    // -------- Public API --------
    public void loadPreferencesForUser(String username) {
        viewModel.setUsername(username);
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

    // Centralized apply: ViewModel -> checkboxes
    private void applyVmToCheckboxes() {
        Preferences p = viewModel.getPreferences();
        if (p == null) return;

        // Normalize enabled names to lowercase so they match norm
        Set<String> onDiets = p.enabledDiets().stream()
                .map(AccountPage::norm)
                .collect(Collectors.toSet());
        Set<String> onInts = p.enabledIntolerances().stream()
                .map(AccountPage::norm)
                .collect(Collectors.toSet());

        for (JCheckBox cb : dietBoxes) {
            cb.setSelected(onDiets.contains(norm(cb.getText())));
        }
        for (JCheckBox cb : intoleranceBoxes) {
            cb.setSelected(onInts.contains(norm(cb.getText())));
        }
        revalidate();
        repaint();
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
        // Reload to hydrate VM from DB (and trigger propertyChange -> applyVmToCheckboxes)
        controller.loadPreferences(viewModel.getUsername());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Preferences p = viewModel.getPreferences();

        SwingUtilities.invokeLater(() -> {
            applyVmToCheckboxes();
            String msg = viewModel.getMessage();
            if (msg != null && !msg.isBlank()) {
                JOptionPane.showMessageDialog(this, msg, "Preferences", JOptionPane.INFORMATION_MESSAGE);
                viewModel.setMessage(null);
            }
        });
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Change Password", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // New password field
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPasswordField = new JPasswordField(20);
        mainPanel.add(newPasswordField, gbc);
// Confirm password field
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(20);
        mainPanel.add(confirmPasswordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmButton = new JButton("Change Password");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters long!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Execute the change password use case
            changePasswordController.execute(newPassword, currentUsername);
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        dialog.setSize(400, 200);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


}
