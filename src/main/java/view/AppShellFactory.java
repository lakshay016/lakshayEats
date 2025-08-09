package view;

import javax.swing.*;

import data_access.DBRecipeDataAccessObject;
import data_access.DBUserDataAccessObject;
import data_access.SpoonacularAPIClient;
import data_access.DBUserPreferenceDataAccessObject;

import entity.CommonUserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.LoggedInPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
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

import java.awt.*;

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

        // === CHANGE PASSWORD STACK ===
        LoggedInViewModel cpVm = new LoggedInViewModel();
        var cpState = cpVm.getState();
        cpState.setUsername(currentUsername);
        cpVm.setState(cpState);

        ChangePasswordOutputBoundary cpPresenter = new LoggedInPresenter(cpVm);
        ChangePasswordInputBoundary cpInteractor =
                new ChangePasswordInteractor(dbUserDataAccessObject, cpPresenter, new CommonUserFactory());
        ChangePasswordController cpController = new ChangePasswordController(cpInteractor);

        Runnable onLogout = () -> {
            dbUserDataAccessObject.setCurrentUsername(null);
            SwingUtilities.invokeLater(() -> {
                viewManagerModel.setState("log in");
                viewManagerModel.firePropertyChanged();
            });
        };



        // === PAGES ===
        var searchPage = new view.search.SearchView(
                currentUsername,
                saveController,
                prefDao);
        var savedPage   = new view.SavedPage(currentUsername, saveController,
                new DBRecipeDataAccessObject(),
                new SpoonacularAPIClient(apiKey));
        var feedPage    = new FeedPage();
        var friendsPage = new FriendsPage(currentUsername, dbUserDataAccessObject);


        var accountPage = new AccountPage(prefController, prefVm, cpController, cpVm, onLogout);




// Keep form at preferred height so scrolling can engage
        JPanel accountWrapper = new JPanel(new BorderLayout());
        accountWrapper.add(accountPage, BorderLayout.NORTH);

        var accountScroll = new JScrollPane(
                accountWrapper,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        accountScroll.setBorder(null);
        accountScroll.getVerticalScrollBar().setUnitIncrement(16);


        accountScroll.setBorder(null);
        accountScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Load preferences immediately for the user
        accountPage.loadPreferencesForUser(currentUsername);

        return new AppShell(searchPage, savedPage, feedPage, friendsPage, accountScroll);
    }
}
