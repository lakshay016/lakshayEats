package app;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import data_access.DBUserDataAccessObject;
import data_access.SpoonacularAPIClient;
import entity.CommonUserFactory;
import entity.FilterOptions;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginViewModel;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchPresenter;
import interface_adapter.search.SearchViewModel;
import interface_adapter.signup.SignupViewModel;
import use_case.search.SearchInteractor;
import use_case.signup.SignupUserDataAccessInterface;
import view.LoggedInView;
import view.LoginView;
import view.SignupView;
import view.ViewManager;
import view.FeedView;


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

        final LoggedInView loggedInView = ChangePasswordUseCaseFactory.create(viewManagerModel,
                loggedInViewModel, userDataAccessObject, userDataAccessObject);;

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

        //SearchView searchView = new SearchView(searchViewModel, searchController, new FilterOptions());
        //views.add(searchView, "search");

        viewManagerModel.setState(signupView.getViewName());
        viewManagerModel.firePropertyChanged();

        application.pack();
        application.setVisible(true);


    }

}
