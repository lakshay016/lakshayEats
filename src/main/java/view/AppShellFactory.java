package view;

import javax.swing.*;

import data_access.*;

import entity.CommonUserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.change_password.ChangePasswordController;
import interface_adapter.change_password.ChangePasswordPresenter;
import interface_adapter.change_password.LoggedInViewModel;
import interface_adapter.save.SaveController;
import interface_adapter.save.SavePresenter;
import interface_adapter.save.SaveViewModel;

import interface_adapter.preferences.PreferencesController;
import interface_adapter.preferences.GetPreferencesPresenter;
import interface_adapter.preferences.SavePreferencesPresenter;
import interface_adapter.preferences.PreferencesViewModel;

import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
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

    public static JPanel create(String currentUsername, DBUserDataAccessObject dbUserDataAccessObject,
                                ViewManagerModel viewManagerModel) {
        // === SAVE STACK ===
        SaveViewModel saveVm = new SaveViewModel();
        SaveDataAccessInterface saveDao = new DBRecipeDataAccessObject();
        SavePresenter savePresenter = new SavePresenter(saveVm);
        SaveController saveController = new SaveController(new SaveInteractor(saveDao, savePresenter));

        // === SHARED API CLIENT ===
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null) throw new RuntimeException("Missing SPOONACULAR_API_KEY");
        SpoonacularAPIClient client = new SpoonacularAPIClient(apiKey);

        // === CHANGE PASSWORD STACK ===
        LoggedInViewModel loggedInViewModel = new LoggedInViewModel();
        ChangePasswordOutputBoundary changePasswordPresenter = new ChangePasswordPresenter(viewManagerModel, loggedInViewModel);
        CommonUserFactory userFactory = new CommonUserFactory();
        ChangePasswordInputBoundary changePasswordInteractor = new ChangePasswordInteractor(dbUserDataAccessObject, changePasswordPresenter, userFactory);
        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);

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

        // Build Account first and hydrate prefs once so other views (e.g., SearchView) can use defaults.
        var accountPage = new AccountPage(
                prefController, prefVm, changePasswordController,
                loggedInViewModel, currentUsername, viewManagerModel);

        // Sets VM.username and triggers controller.loadPreferences(username)
        accountPage.loadPreferencesForUser(currentUsername);

        var searchPage = new view.search.SearchView(
                currentUsername,
                saveController,
                prefVm);

        var savedPage   = new view.SavedPage(currentUsername, saveController,
                new DBRecipeDataAccessObject(),
                new SpoonacularAPIClient(apiKey), saveVm);
        var feedPage    = new FeedPage();
        var friendsPage = new FriendsPage(currentUsername, dbUserDataAccessObject,
                new DBFriendRequestDataAccessObject(dbUserDataAccessObject));

        return new AppShell(searchPage, savedPage, feedPage, friendsPage, accountPage);
    }
}
