package view;

import com.fasterxml.jackson.databind.JsonNode;
import data_access.SpoonacularAPIClient;
import data_access.DBRecipeDataAccessObject;
import entity.Ingredients;
import entity.Instructions;
import entity.Nutrition;
import entity.SearchResult;
import interface_adapter.save.SaveController;
import interface_adapter.save.SavePresenter;
import interface_adapter.save.SaveViewModel;
import use_case.save.SaveDataAccessInterface;
import use_case.save.SaveInteractor;
import use_case.save.SaveInputData;
import use_case.save.SaveOutputBoundary;
import view.search.RecipeCardPanel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SaveView extends JFrame {
    private final String userId;
    private final JPanel resultsPanel;
    private final String spoonacularApiKey;
    private SaveController saveController;
    private SaveView saveViewInstance = null;

    public SaveView(String userId, SaveController saveController) {
        this.userId = userId;
        this.spoonacularApiKey = System.getenv("SPOONACULAR_API_KEY");
        if (spoonacularApiKey == null) {
            JOptionPane.showMessageDialog(this,
                    "Please set SPOONACULAR_API_KEY in your environment.",
                    "Missing API Key", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Missing SPOONACULAR_API_KEY");
        }

        setTitle("My Saved Recipes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        fetchAndDisplaySavedRecipes();

        setVisible(true);
    }

    private void fetchAndDisplaySavedRecipes() {
        DBRecipeDataAccessObject dao = new DBRecipeDataAccessObject();

        SwingWorker<List<SearchResult>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<SearchResult> doInBackground() throws Exception {
                List<SearchResult> recipes = new ArrayList<>();
                List<Integer> savedRecipeIds = dao.fetchSavedRecipes(userId);
                if (savedRecipeIds == null || savedRecipeIds.isEmpty()) {
                    return recipes;
                }

                for (Integer id : savedRecipeIds) {
                    SearchResult r = fetchRecipeById(id);
                    if (r != null) {
                        recipes.add(r);
                    }
                }
                return recipes;
            }

            @Override
            protected void done() {
                try {
                    List<SearchResult> recipes = get();
                    displayResults(recipes);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(SaveView.this,
                            "Failed to load saved recipes.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private SearchResult fetchRecipeById(int id) {
        try {
            SpoonacularAPIClient apiClient = new SpoonacularAPIClient(spoonacularApiKey);
            List<SearchResult> results = apiClient.loadIDs(List.of(id));
            return results.isEmpty() ? null : results.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }


    private void displayResults(List<SearchResult> recipes) {
        resultsPanel.removeAll();

        if (recipes.isEmpty()) {
            JLabel noResults = new JLabel("You have no saved recipes.", SwingConstants.CENTER);
            noResults.setForeground(Color.GRAY);
            resultsPanel.add(noResults);
        } else {
            for (SearchResult r : recipes) {
                RecipeCardPanel card = new RecipeCardPanel(r, saveController, userId);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
                card.setPreferredSize(new Dimension(700, 120));
                resultsPanel.add(card);
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String userId = "demo_user";

            SaveDataAccessInterface dataAccess = new DBRecipeDataAccessObject();

            SaveViewModel viewModel = new SaveViewModel();

            SavePresenter presenter = new SavePresenter(viewModel);

            SaveInteractor interactor = new SaveInteractor(dataAccess, presenter);

            SaveController saveController = new SaveController(interactor);
            new SaveView("demo_user", saveController);
        });
    }
}