package app;

import java.awt.CardLayout;

import javax.swing.*;

import data_access.DBUserDataAccessObject;
import data_access.SpoonacularAPIClient;
import entity.CommonUserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.change_password.LoggedInViewModel;
import interface_adapter.login.LoginViewModel;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchPresenter;
import interface_adapter.search.SearchViewModel;
import interface_adapter.signup.SignupViewModel;
import use_case.login.LoginUseCaseFactory;
import use_case.search.SearchInteractor;
import use_case.signup.SignupUseCaseFactory;
import view.ChangePasswordView;
import view.LoginView;
import view.SignupView;
import view.ViewManager;
import use_case.change_password.ChangePasswordUseCaseFactory;


public class Main {


    public static void main(String[] args) {

        final JFrame application = new JFrame("LAKSHAYEATS");
        application.setSize(1000, 1000);
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final CardLayout cardLayout = new CardLayout();

        final JPanel views = new JPanel(cardLayout);
        application.add(views);

        final ViewManagerModel viewManagerModel = new ViewManagerModel();
        new ViewManager(views, cardLayout, viewManagerModel);


        final LoginViewModel loginViewModel = new LoginViewModel();
        final LoggedInViewModel loggedInViewModel = new LoggedInViewModel();
        String u = loggedInViewModel.getState().getUsername();

        final SignupViewModel signupViewModel = new SignupViewModel();

        final DBUserDataAccessObject userDataAccessObject = new DBUserDataAccessObject(new CommonUserFactory());

        final SignupView signupView = SignupUseCaseFactory.create(viewManagerModel, loginViewModel,
                signupViewModel, userDataAccessObject);
        views.add(signupView, signupView.getViewName());

        final LoginView loginView = LoginUseCaseFactory.create(viewManagerModel, loginViewModel, loggedInViewModel,
                userDataAccessObject);
        views.add(loginView, loginView.getViewName());

        final ChangePasswordView loggedInView = ChangePasswordUseCaseFactory.create(viewManagerModel,
                loggedInViewModel, userDataAccessObject, userDataAccessObject);

        // When we land on LoggedInView, immediately build the App Shell and switch to it.
        viewManagerModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())
                    && loggedInView.getViewName().equals(evt.getNewValue())) {

                String currentUsername = loggedInViewModel.getState().getUsername();

                // Build the shell using your factory
                JPanel shell = view.AppShellFactory.create(currentUsername, userDataAccessObject, viewManagerModel);

                // Register it with the same CardLayout container
                views.add(shell, "app");

                // Switch to it on the next tick so CardLayout registers the new card
                SwingUtilities.invokeLater(() -> {
                    viewManagerModel.setState("app");
                    viewManagerModel.firePropertyChanged();
                });
            }
        });


        views.add(loggedInView, loggedInView.getViewName());

        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null) {
            System.err.println("Please set SPOONACULAR_API_KEY in your environment.");
            System.exit(1);
        }

        SpoonacularAPIClient client = new SpoonacularAPIClient(apiKey);
        SearchViewModel searchViewModel = new SearchViewModel();
        SearchPresenter searchPresenter = new SearchPresenter(searchViewModel);
        SearchInteractor searchInteractor = new SearchInteractor(client, searchPresenter);
        SearchController searchController = new SearchController(searchInteractor);


        viewManagerModel.setState(signupView.getViewName());
        viewManagerModel.firePropertyChanged();

        application.pack();
        application.setVisible(true);


    }

}
