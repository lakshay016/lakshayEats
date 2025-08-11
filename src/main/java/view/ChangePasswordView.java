package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import data_access.DBUserDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.change_password.ChangePasswordController;
import interface_adapter.change_password.LoggedInState;
import interface_adapter.change_password.LoggedInViewModel;
import data_access.DBFriendDataAccessObject;
import org.json.JSONObject;


public class ChangePasswordView extends JPanel implements ActionListener, PropertyChangeListener {

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

    public ChangePasswordView(LoggedInViewModel loggedInViewModel, ChangePasswordController changePasswordController,
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


        setPreferredSize(new Dimension(600, 400));
        setMinimumSize(new Dimension(500, 300));


        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));


        passwordInputField.setPreferredSize(new Dimension(200, 30));

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


    private void updateFriendRelationship(String currentUser, String friendId, boolean follow) throws Exception {
        DBFriendDataAccessObject dao = new DBFriendDataAccessObject();
        JSONObject json = dao.fetch(currentUser);

        java.util.Set<String> friends = new java.util.HashSet<>();
        java.util.Set<String> requests = new java.util.HashSet<>();
        java.util.Set<String> blocked  = new java.util.HashSet<>();

        if (json != null) {
            if (json.has("friends"))  friends.addAll(dao.toSet(json.getJSONArray("friends")));
            if (json.has("requests")) requests.addAll(dao.toSet(json.getJSONArray("requests")));
            if (json.has("blocked"))  blocked.addAll(dao.toSet(json.getJSONArray("blocked")));
        }
        if (follow) friends.add(friendId); else friends.remove(friendId);
        dao.save(currentUser, friends, requests, blocked);
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
