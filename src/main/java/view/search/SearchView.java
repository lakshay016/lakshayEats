package view.search;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import entity.FilterOptions;
import entity.SearchResult;
import interface_adapter.save.SaveController;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchViewModel;
import interface_adapter.search.SearchPresenter;
import use_case.preferences.PreferencesDataAccessInterface;
import use_case.search.SearchInteractor;
import data_access.SpoonacularAPIClient;

public class SearchView extends JPanel {
    private final SearchViewModel viewModel;
    private final SearchController controller;
    private FilterOptions currentFilters;
    private final IngredientPanel ingredientPanel;
    private final SaveController saveController;

    private FilterDialog filterDialog;

    private final JTextField searchField;
    private final JButton filtersButton;
    private final JButton searchButton;
    private final JPanel resultsPanel;
    private final String currentUsername;
    private final PreferencesDataAccessInterface prefDao;

    public SearchView(String currentUsername,
                      SaveController saveController,
                      PreferencesDataAccessInterface prefDao) {
        this.currentUsername = currentUsername;
        this.saveController = saveController;
        this.prefDao = prefDao;

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

        // Top bar: search field + filters button
        JPanel topBar = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        filtersButton = new JButton("Filters...");
        topBar.add(searchField, BorderLayout.CENTER);
        topBar.add(filtersButton, BorderLayout.EAST);

        // Ingredients label and panel
        JLabel ingredientsLabel = new JLabel("Ingredients");
        JPanel labelHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        labelHolder.setOpaque(false);
        labelHolder.add(ingredientsLabel);

        ingredientPanel = new IngredientPanel();
        ingredientPanel.setVisible(true);

        // North panel combining top bar and ingredients
        JPanel northPanel = new JPanel(new BorderLayout(5, 5));
        northPanel.setOpaque(false);
        northPanel.add(topBar, BorderLayout.NORTH);
        northPanel.add(labelHolder, BorderLayout.WEST);
        northPanel.add(ingredientPanel, BorderLayout.CENTER);

        // Results area
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane resultsScroll = new JScrollPane(resultsPanel);
        resultsScroll.setBorder(BorderFactory.createEmptyBorder());

        // Search button at bottom
        searchButton = new JButton("Search");

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

        FilterDialog dialog = new FilterDialog(owner, currentUsername, prefDao);
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
        FilterOptions filters = (currentFilters != null) ? currentFilters : new FilterOptions();
        filters.setIncludeIngredients(ingredientPanel.getIncludeIngredients());
        filters.setExcludeIngredients(ingredientPanel.getExcludeIngredients());
        controller.handleSearch(query, filters);
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
                        ? new RecipeCardPanel(r, saveController, currentUsername)
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