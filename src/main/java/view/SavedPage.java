package view;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import data_access.DBFriendRequestDataAccessObject;
import data_access.DBRecipeDataAccessObject;
import data_access.DBUserDataAccessObject;
import data_access.SpoonacularAPIClient;
import entity.CommonUserFactory;
import entity.SearchResult;
import interface_adapter.FriendRequest.FriendRequestController;
import interface_adapter.save.SaveController;
import interface_adapter.save.SaveViewModel;
import use_case.friendRequest.FriendRequestInteractor;
import use_case.friendRequest.FriendRequestOutputBoundary;
import use_case.friendRequest.FriendRequestOutputData;
import view.search.RecipeCardPanel;

public class SavedPage extends JPanel implements PropertyChangeListener {
    private final String userId;
    private final JPanel resultsPanel;
    private final SpoonacularAPIClient client;
    private final DBRecipeDataAccessObject dao;
    private final SaveController saveController;
    private final SaveViewModel saveViewModel;
    private final FriendRequestController friendRequestController;

    private List<SearchResult> cachedRecipes = null;
    private boolean needsRefresh = true;

    public SavedPage(String userId,
                     SaveController saveController,
                     DBRecipeDataAccessObject dao,
                     SpoonacularAPIClient client, SaveViewModel saveViewModel) {
        this.userId = userId;
        this.saveController = saveController;
        this.dao = dao;
        this.client = client;
        this.saveViewModel = saveViewModel;
        this.saveViewModel.addPropertyChangeListener(this);

        // Create FriendRequestController directly here (no need to modify AppShellFactory)
        DBFriendRequestDataAccessObject friendRequestDao = new DBFriendRequestDataAccessObject(
                new DBUserDataAccessObject(new CommonUserFactory()));

        // Simple output boundary that just handles recipe sharing
        FriendRequestOutputBoundary simplePresenter = new FriendRequestOutputBoundary() {
            @Override
            public void presentFriendRequestResult(FriendRequestOutputData outputData) {
                // Recipe shared successfully - no need to show popup
                // The RecipeDetailDialog will show its own success message
            }
        };

        FriendRequestInteractor interactor = new FriendRequestInteractor(simplePresenter, friendRequestDao);
        this.friendRequestController = new FriendRequestController(interactor);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        fetchAndDisplaySavedRecipes();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("saveState".equals(evt.getPropertyName())) {
            //only fetch missing recipes
            updateCacheWithMissingRecipes();
        }
    }

    private void updateCacheWithMissingRecipes() {
        try {
            // Get current saved recipe IDs from database

            var savedRecipeIds = dao.fetchSavedRecipes(userId);

            if (cachedRecipes != null) {
                cachedRecipes.removeIf(recipe ->
                        !savedRecipeIds.contains(recipe.getId()));
            }

            if (savedRecipeIds == null || savedRecipeIds.isEmpty()) {
                cachedRecipes = new ArrayList<>();
                displayResults(cachedRecipes);
                return;
            }

            // Find which recipes are missing from cache
            List<Integer> missingRecipeIds = findMissingRecipeIds(savedRecipeIds);

            if (missingRecipeIds.isEmpty()) {
                // All recipes are in cache, just update display
                displayResults(cachedRecipes);
            } else {
                // Only fetch the missing recipes
                List<SearchResult> newRecipes = new ArrayList<>();
                for(Integer id : missingRecipeIds) {
                    SearchResult r = fetchRecipeById(id);
                    if (r != null) newRecipes.add(r);
                }

                // Add new recipes to existing cache
                if (cachedRecipes == null) {
                    cachedRecipes = new ArrayList<>();
                }
                cachedRecipes.addAll(newRecipes);

                // Update display with updated cache
                displayResults(cachedRecipes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> findMissingRecipeIds(List<Integer> allSavedIds) {
        List<Integer> missingIds = new ArrayList<>();

        if (cachedRecipes == null) {
            // No cache, need to fetch everything
            return allSavedIds;
        }

        for (Integer savedId : allSavedIds) {
            boolean existsInCache = cachedRecipes.stream()
                    .anyMatch(recipe -> recipe.getId() == savedId);

            if (!existsInCache) {
                missingIds.add(savedId);
            }
        }

        return missingIds;
    }


    private void fetchAndDisplaySavedRecipes() {
        if (cachedRecipes != null && !needsRefresh) {
            displayResults(cachedRecipes);
            return;
        }
        SwingWorker<List<SearchResult>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<SearchResult> doInBackground() throws Exception {
                List<SearchResult> recipes = new ArrayList<>();
                var savedRecipeId = dao.fetchSavedRecipes(userId); // List<Integer>
                Set<Integer> noDupes = new HashSet<>(savedRecipeId);
                var savedRecipeIds = noDupes;
                if (savedRecipeIds == null || savedRecipeIds.isEmpty()) {
                    return recipes;
                }
                for (Integer id : savedRecipeIds) {
                    SearchResult r = fetchRecipeById(id);
                    if (r != null) recipes.add(r);
                }
                return recipes;
            }

            @Override
            protected void done() {
                try {
                    List<SearchResult> recipes = get();
                    cachedRecipes = recipes; // Cache the results
                    needsRefresh = false;    // Mark as fresh
                    displayResults(recipes);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(SavedPage.this,
                            "Failed to load saved recipes.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private SearchResult fetchRecipeById(int id) {
        try {
            List<SearchResult> results = client.loadIDs(List.of(id));
            return results.isEmpty() ? null : results.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayResults(List<SearchResult> recipes) {
        resultsPanel.removeAll();

        if (recipes == null || recipes.isEmpty()) {
            JLabel noResults = new JLabel("You have no saved recipes.", SwingConstants.LEFT);
            noResults.setForeground(Color.GRAY);
            resultsPanel.add(noResults);
        } else {
            for (SearchResult r : recipes) {
                RecipeCardPanel card = new RecipeCardPanel(r, saveController, friendRequestController, userId);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
                card.setPreferredSize(new Dimension(700, 120));
                resultsPanel.add(card);
                resultsPanel.add(Box.createVerticalStrut(8));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }
}
