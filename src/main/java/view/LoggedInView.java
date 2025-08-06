package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import data_access.DBRecipeDataAccessObject;
import data_access.DBUserDataAccessObject;
import data_access.SpoonacularAPIClient;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.save.SaveController;
import interface_adapter.save.SavePresenter;
import interface_adapter.save.SaveViewModel;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchPresenter;
import interface_adapter.search.SearchViewModel;
import use_case.save.SaveDataAccessInterface;
import use_case.save.SaveInputBoundary;
import use_case.save.SaveInteractor;
import use_case.search.SearchInteractor;
import view.search.SearchFrame;


public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private final JLabel passwordErrorField = new JLabel();
    private final ChangePasswordController changePasswordController;
    private final ViewManagerModel viewManagerModel;
    private final DBUserDataAccessObject userDataAccessObject;

    private final JLabel username;

    //private final JButton logOut;

    private final JTextField passwordInputField = new JTextField(15);
    private final JButton changePassword;

    public LoggedInView(LoggedInViewModel loggedInViewModel, ChangePasswordController changePasswordController,
                        ViewManagerModel viewManagerModel, DBUserDataAccessObject userDataAccessObject) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);
        this.changePasswordController = changePasswordController;
        this.viewManagerModel = viewManagerModel;
        this.userDataAccessObject = userDataAccessObject;

        final JLabel title = new JLabel("Account");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final LabelTextPanel passwordInfo = new LabelTextPanel(
                new JLabel("Password"), passwordInputField);

        final JLabel usernameInfo = new JLabel("Currently logged in: ");
        username = new JLabel();

        final JPanel buttons = new JPanel();
        //logOut = new JButton("Log Out");
        //buttons.add(logOut);

        changePassword = new JButton("Change Password");
        buttons.add(changePassword);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            viewManagerModel.setState("search");
            viewManagerModel.firePropertyChanged();
        });
        buttons.add(searchButton);
        setPreferredSize(new Dimension(600, 400));
        setMinimumSize(new Dimension(500, 300));

        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));


        passwordInputField.setPreferredSize(new Dimension(200, 30));
        searchButton.addActionListener(e -> {
            // Create and show SearchFrame
            String apiKey = System.getenv("SPOONACULAR_API_KEY");
            if (apiKey == null) {
                JOptionPane.showMessageDialog(this,
                        "Please set SPOONACULAR_API_KEY in your environment.",
                        "Missing API Key", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SpoonacularAPIClient client = new SpoonacularAPIClient(apiKey);
            SearchViewModel searchViewModel = new SearchViewModel();
            SearchPresenter searchPresenter = new SearchPresenter(searchViewModel);
            SearchInteractor searchInteractor = new SearchInteractor(client, searchPresenter);
            SearchController searchController = new SearchController(searchInteractor);

            SaveViewModel saveViewModel = new SaveViewModel();
            SavePresenter savePresenter = new SavePresenter(saveViewModel);
            SaveDataAccessInterface saveDataAccess = new DBRecipeDataAccessObject();
            SaveInputBoundary saveInteractor = new SaveInteractor(saveDataAccess, savePresenter);
            SaveController saveController = new SaveController(saveInteractor);

            String currentUser = userDataAccessObject.getCurrentUsername();
            if (currentUser == null) {
                currentUser = "demo_user"; // fallback
            }
            SearchFrame searchFrame = new SearchFrame(currentUser, saveController);
            searchFrame.setVisible(true);
        });
        buttons.add(searchButton);

        //logOut.addActionListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final LoggedInState currentState = loggedInViewModel.getState();
                currentState.setPassword(passwordInputField.getText());
                loggedInViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });

        changePassword.addActionListener(
                evt -> {
                    if (evt.getSource().equals(changePassword)) {
                        final LoggedInState currentState = loggedInViewModel.getState();

                        this.changePasswordController.execute(
                                currentState.getUsername(),
                                currentState.getPassword()
                        );
                    }
                }
        );

        this.add(title);
        this.add(usernameInfo);
        this.add(username);

        this.add(passwordInfo);
        this.add(passwordErrorField);
        this.add(buttons);
    }


    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            username.setText(state.getUsername());
        }
        else if (evt.getPropertyName().equals("password")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            JOptionPane.showMessageDialog(null, "password updated for " + state.getUsername());
        }

    }

    public String getViewName() {
        return viewName;
    }
}
