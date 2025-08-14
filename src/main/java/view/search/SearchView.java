package view.search;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import data_access.DBFriendRequestDataAccessObject;
import data_access.DBUserDataAccessObject;
import entity.CommonUserFactory;
import entity.FilterOptions;
import entity.SearchResult;
import entity.Preferences;
import interface_adapter.FriendRequest.FriendRequestController;
import interface_adapter.save.SaveController;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchViewModel;
import interface_adapter.search.SearchPresenter;
import interface_adapter.preferences.PreferencesViewModel;
import javax.swing.SwingUtilities;

import use_case.friendRequest.FriendRequestInteractor;
import use_case.friendRequest.FriendRequestOutputBoundary;
import use_case.friendRequest.FriendRequestOutputData;
import use_case.search.SearchInteractor;
import data_access.SpoonacularAPIClient;

public class SearchView extends JPanel {
    private final SearchViewModel viewModel;
    private final SearchController controller;
    private FilterOptions currentFilters;
    private final IngredientPanel ingredientPanel;
    private final SaveController saveController;

    private FilterDialog filterDialog;

    // UI bits for the top row
    private final JLabel searchLabel = new JLabel("Search recipes:");
    private final JTextField searchField = new JTextField(40);
    private final JButton filtersButton = new JButton("Filters...");

    // Bottom search button
    private final JButton searchButton = new JButton("Search");

    // Results panel
    private final JPanel resultsPanel = new JPanel();
    private final String currentUsername;
    private final PreferencesViewModel prefVm;
    private final FriendRequestController friendRequestController;

    public SearchView(String currentUsername,
                      SaveController saveController,
                      PreferencesViewModel prefVm) {
        this.currentUsername = currentUsername;
        this.saveController = saveController;
        this.prefVm = prefVm;
        // Create FriendRequestController directly here
        DBFriendRequestDataAccessObject friendRequestDao = new DBFriendRequestDataAccessObject(
                new DBUserDataAccessObject(new CommonUserFactory()));

        // Simple output boundary that just shows success/error messages
        FriendRequestOutputBoundary simplePresenter = new FriendRequestOutputBoundary() {
            @Override
            public void presentFriendRequestResult(FriendRequestOutputData outputData) {
                if (outputData.isSuccess()) {
                    // Recipe shared successfully - no need to show popup
                } else {
                    // Show error if something goes wrong
                    JOptionPane.showMessageDialog(SearchView.this,
                            outputData.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        FriendRequestInteractor interactor2 = new FriendRequestInteractor(simplePresenter, friendRequestDao);
        this.friendRequestController = new FriendRequestController(interactor2);
        // Load API key
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null) {
            // 'this' is fine for a JPanel parent in JOptionPane
            JOptionPane.showMessageDialog(this,
                    "Please set SPOONACULAR_API_KEY in your environment.",
                    "Missing API Key", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Missing API Key: Please set SPOONACULAR_API_KEY in your environment.");
        }
        SpoonacularAPIClient client = new SpoonacularAPIClient(apiKey);

        // Setup ViewModel and observe state changes
        viewModel = new SearchViewModel();
        viewModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                List<SearchResult> results = viewModel.getState().getResults();
                SwingUtilities.invokeLater(() -> displayResults(results));
            }
        });

        // Build Clean Architecture components (kept as-is)
        SearchPresenter presenter = new SearchPresenter(viewModel);
        SearchInteractor interactor = new SearchInteractor(client, presenter);
        controller = new SearchController(interactor);

        // ==== UI ====
        setLayout(new BorderLayout(10, 10));                // CHANGED: use panel's layout
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12)); // small padding

        // Top row: label | [ search field (stretches) ] | [ Filters... ]
        JPanel topBar = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        topBar.add(searchLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topBar.add(searchField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        topBar.add(filtersButton, gbc);

        // North panel: topRow + ingredients row
        JPanel northPanel = new JPanel(new BorderLayout(5, 5));
        northPanel.setOpaque(false);
        northPanel.add(topBar, BorderLayout.NORTH);

        JPanel ingredientsRow = new JPanel(new GridBagLayout());
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(4, 6, 4, 6);

        g2.gridx = 0; g2.gridy = 0;
        g2.weightx = 0; g2.fill = GridBagConstraints.NONE;
        ingredientsRow.add(new JLabel("Ingredients"), g2);

        ingredientPanel = new IngredientPanel();
        ingredientPanel.setVisible(true);
        g2.gridx = 1; g2.gridy = 0;
        g2.weightx = 1.0; g2.fill = GridBagConstraints.HORIZONTAL;
        ingredientsRow.add(ingredientPanel, g2);

        northPanel.add(ingredientsRow, BorderLayout.CENTER);

        // Results area
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane resultsScroll = new JScrollPane(resultsPanel);
        resultsScroll.setBorder(BorderFactory.createEmptyBorder());

        // Add to this panel (instead of getContentPane)
        add(northPanel, BorderLayout.NORTH);
        add(resultsScroll, BorderLayout.CENTER);
        add(searchButton, BorderLayout.SOUTH);

        // Attach event handlers
        filtersButton.addActionListener(e -> openFilterDialog());
        searchButton.addActionListener(e -> performSearch());
    }

    private void openFilterDialog() {
        java.awt.Window win = SwingUtilities.getWindowAncestor(this);
        JFrame owner = (win instanceof JFrame) ? (JFrame) win : null;
        Preferences prefs = prefVm.getPreferences();
        FilterDialog dialog = new FilterDialog(owner, currentUsername, prefs);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        FilterOptions opts = dialog.getFilterOptions();
        if (opts != null) {
            currentFilters = opts;
        }
    }


    private void performSearch() {
        String query = searchField.getText();

        FilterOptions filters = (currentFilters != null)
                ? currentFilters
                : buildDefaultFilters();

        filters.setIncludeIngredients(ingredientPanel.getIncludeIngredients());
        filters.setExcludeIngredients(ingredientPanel.getExcludeIngredients());
        controller.handleSearch(query, filters);
    }

    private FilterOptions buildDefaultFilters() {
        FilterOptions fo = new FilterOptions();
        Preferences prefs = prefVm.getPreferences();
        if (prefs != null) {
            // Only set if present
            var diets = prefs.enabledDietsList();
            var ints  = prefs.enabledIntolerancesList();
            if (diets != null && !diets.isEmpty()) fo.setDiets(diets);
            if (ints  != null && !ints.isEmpty())  fo.setIntolerances(ints);
        }
        return fo;
    }


    /**
     * Called by presenter via ViewModel when new results are available.
     */
    public void displayResults(List<SearchResult> results) {
        resultsPanel.removeAll();

        if (results.isEmpty()) {
            JLabel noResults = new JLabel("No recipes found.", SwingConstants.LEFT);
            noResults.setForeground(Color.GRAY);
            noResults.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            resultsPanel.add(noResults);
        } else {
            for (SearchResult r : results) {
                // ⚠️ use the save-enabled constructor so detail dialog shows Save/Reviews
                RecipeCardPanel card = (saveController != null && currentUsername != null)
                        ? new RecipeCardPanel(r, saveController, friendRequestController, currentUsername)
                        : new RecipeCardPanel(r);

                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
                card.setPreferredSize(new Dimension(600, 120));
                resultsPanel.add(card);
                resultsPanel.add(Box.createVerticalStrut(8));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }
}