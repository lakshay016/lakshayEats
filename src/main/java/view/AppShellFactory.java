package view;

import javax.swing.*;

import data_access.DBRecipeDataAccessObject;
import data_access.SpoonacularAPIClient;
import data_access.DBUserPreferenceDataAccessObject;

import interface_adapter.save.SaveController;
import interface_adapter.save.SavePresenter;
import interface_adapter.save.SaveViewModel;

import interface_adapter.preferences.PreferencesController;
import interface_adapter.preferences.GetPreferencesPresenter;
import interface_adapter.preferences.SavePreferencesPresenter;
import interface_adapter.preferences.PreferencesViewModel;

import use_case.save.SaveDataAccessInterface;
import use_case.save.SaveInteractor;

import use_case.preferences.PreferencesDataAccessInterface;
import use_case.preferences.get_preferences.GetPreferencesInputBoundary;
import use_case.preferences.get_preferences.GetPreferencesInteractor;
import use_case.preferences.get_preferences.GetPreferencesOutputBoundary;

import use_case.preferences.save_preferences.SavePreferencesInputBoundary;
import use_case.preferences.save_preferences.SavePreferencesInteractor;
import use_case.preferences.save_preferences.SavePreferencesOutputBoundary;

import view.AccountPage;

public final class AppShellFactory {
    private AppShellFactory() {}

    public static JPanel create(String currentUsername) {
        // === SAVE STACK ===
        SaveViewModel saveVm = new SaveViewModel();
        SaveDataAccessInterface saveDao = new DBRecipeDataAccessObject();
        SavePresenter savePresenter = new SavePresenter(saveVm);
        SaveController saveController = new SaveController(new SaveInteractor(saveDao, savePresenter));

        // === SHARED API CLIENT ===
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null) throw new RuntimeException("Missing SPOONACULAR_API_KEY");
        SpoonacularAPIClient client = new SpoonacularAPIClient(apiKey);

        // === PREFERENCES STACK ===
        PreferencesViewModel prefVm = new PreferencesViewModel();
        PreferencesDataAccessInterface prefDao = new DBUserPreferenceDataAccessObject();

        // Get Preferences
        GetPreferencesOutputBoundary getPresenter = new GetPreferencesPresenter(prefVm);
        GetPreferencesInputBoundary getInteractor = new GetPreferencesInteractor(prefDao, getPresenter);

        // Save Preferences
        SavePreferencesOutputBoundary savePrefPresenter = new SavePreferencesPresenter(prefVm);
        SavePreferencesInputBoundary saveInteractor = new SavePreferencesInteractor(prefDao, savePrefPresenter);

        // Controller for both
        PreferencesController prefController = new PreferencesController(getInteractor, saveInteractor);

        // === PAGES ===
        var searchPage = new view.search.SearchView(
                currentUsername,
                saveController,
                prefDao);
        var savedPage   = new view.SavedPage(currentUsername, saveController,
                new DBRecipeDataAccessObject(),
                new SpoonacularAPIClient(apiKey));
        var feedPage    = new FeedPage();
        var friendsPage = new FriendsPage();

        // in AppShellFactory.create(...)
        var accountPage = new AccountPage(prefController, prefVm, () -> {
            JOptionPane.showMessageDialog(
                    null,
                    "Change password flow coming soon.",
                    "Security",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        accountPage.loadPreferencesForUser(currentUsername);

        // Load preferences immediately for the user
        accountPage.loadPreferencesForUser(currentUsername);

        return new AppShell(searchPage, savedPage, feedPage, friendsPage, accountPage);
    }
}
