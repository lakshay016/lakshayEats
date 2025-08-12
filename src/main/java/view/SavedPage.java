package view;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data_access.DBRecipeDataAccessObject;
import data_access.SpoonacularAPIClient;
import entity.SearchResult;
import interface_adapter.save.SaveController;
import interface_adapter.save.SaveViewModel;
import view.search.RecipeCardPanel;

public class SavedPage extends JPanel implements PropertyChangeListener {
    private final String userId;
    private final JPanel resultsPanel;
    private final SpoonacularAPIClient client;
    private final DBRecipeDataAccessObject dao;
    private final SaveController saveController;
    private final SaveViewModel saveViewModel;

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

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        fetchAndDisplaySavedRecipes();
    }

    public void reload() {
        fetchAndDisplaySavedRecipes();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("saveState".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(this::reload);
        }
    }

    private void fetchAndDisplaySavedRecipes() {
        SwingWorker<List<SearchResult>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<SearchResult> doInBackground() throws Exception {
                List<SearchResult> recipes = new ArrayList<>();
                var savedRecipeIds = dao.fetchSavedRecipes(userId); // List<Integer>
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
                    displayResults(get()); // <-- factorized rendering
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
                RecipeCardPanel card = new RecipeCardPanel(r, saveController, userId);
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
