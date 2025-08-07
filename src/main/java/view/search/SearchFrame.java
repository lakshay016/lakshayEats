package view.search;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.beans.PropertyChangeEvent;

import data_access.DBRecipeDataAccessObject;
import entity.FilterOptions;
import entity.SearchResult;
import interface_adapter.save.SaveController;
import interface_adapter.save.SavePresenter;
import interface_adapter.save.SaveViewModel;
import use_case.save.SaveDataAccessInterface;
import use_case.save.SaveInputBoundary;
import use_case.save.SaveInteractor;
import use_case.save.SaveOutputBoundary;
import view.SaveView;
import view.search.FilterDialog;
import view.search.IngredientPanel;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchViewModel;
import interface_adapter.search.SearchPresenter;
import use_case.search.SearchInteractor;
import data_access.SpoonacularAPIClient;
import view.RecipeCardPanel;

public class SearchFrame extends JFrame {
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
    private SaveView saveViewInstance = null;
    private String userId;


    public SearchFrame(String userId, SaveController saveController) {
        this.saveController = saveController;
        this.userId = userId;
        // Load API key
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null) {
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

        // Build Clean Architecture components
        SearchPresenter presenter = new SearchPresenter(viewModel);
        SearchInteractor interactor = new SearchInteractor(client, presenter);
        controller = new SearchController(interactor);

        // Initialize UI components
        setTitle("Recipe Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Top bar: search field + filters button
        JPanel topBar = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        filtersButton = new JButton("Filters...");

        JButton savedRecipesButton = new JButton("My Saved Recipes");
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightButtons.add(filtersButton);
        rightButtons.add(savedRecipesButton);

        topBar.add(rightButtons, BorderLayout.EAST);

        topBar.add(searchField, BorderLayout.CENTER);

        savedRecipesButton.addActionListener(e -> openSaveView());


        // Ingredients label and panel
        JLabel ingredientsLabel = new JLabel("Ingredients");
        // wrap label to add left padding
        JPanel labelHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        labelHolder.add(ingredientsLabel);
        ingredientPanel = new IngredientPanel();
        ingredientPanel.setVisible(true);

        // North panel combining top bar and ingredients
        JPanel northPanel = new JPanel(new BorderLayout(5, 5));
        northPanel.add(topBar, BorderLayout.NORTH);
        northPanel.add(labelHolder, BorderLayout.WEST);
        northPanel.add(ingredientPanel, BorderLayout.CENTER);

        // Results area
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane resultsScroll = new JScrollPane(resultsPanel);

        // Search button at bottom
        searchButton = new JButton("Search");

        // Layout main frame
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout(10, 10));
        cp.add(northPanel, BorderLayout.NORTH);
        cp.add(resultsScroll, BorderLayout.CENTER);
        cp.add(searchButton, BorderLayout.SOUTH);

        // Attach event handlers
        filtersButton.addActionListener(e -> openFilterDialog());
        searchButton.addActionListener(e -> performSearch());

        // Show frame
        setVisible(true);
    }

    private void openFilterDialog() {
        FilterDialog dialog = new FilterDialog(this);
        dialog.setModal(true);
        dialog.setVisible(true);
        FilterOptions opts = dialog.getFilterOptions();
        if (opts != null) {
            currentFilters = opts;
        }
    }

    private void performSearch() {

        // build search
        String query = searchField.getText();
        FilterOptions filters = (currentFilters != null) ? currentFilters : new FilterOptions();
        filters.setIncludeIngredients(ingredientPanel.getIncludeIngredients());
        filters.setExcludeIngredients(ingredientPanel.getExcludeIngredients());
        controller.handleSearch(query, filters);

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
            JLabel noResults = new JLabel("No recipes found.", SwingConstants.CENTER);
            noResults.setForeground(Color.GRAY);
            resultsPanel.add(noResults);
        } else {
            for (SearchResult r : results) {
                RecipeCardPanel card = new RecipeCardPanel(r, this.saveController, userId);
                // constrain height
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
                card.setPreferredSize(new Dimension(600, 120));
                resultsPanel.add(card);
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void openSaveView() {
        if (saveViewInstance == null || !saveViewInstance.isDisplayable()) {
            saveViewInstance = new SaveView(userId, saveController);
            // Optional: position the SaveView window beside SearchFrame window
            saveViewInstance.setLocationRelativeTo(null);

            saveViewInstance.setVisible(true);
        } else {
            saveViewInstance.toFront();
            saveViewInstance.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create or get SaveController here (replace with your actual setup)
            SaveViewModel viewModel = new SaveViewModel();
            SaveDataAccessInterface dataAccess = new DBRecipeDataAccessObject();

            // Create the ViewModel

            // Create the Presenter
            SavePresenter presenter = new SavePresenter(viewModel);

            SaveInputBoundary interactor = new SaveInteractor(dataAccess, presenter);  // instantiate your interactor
            SaveController saveController = new SaveController(interactor);

            // Pass SaveController to SearchFrame
            new SearchFrame("demo_user", saveController);
        });
    }
}
