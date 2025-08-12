package use_case.login;

import entity.User;
import util.PasswordHasher;

public class LoginInteractor implements LoginInputBoundary {
    private final LoginUserDataAccessInterface userDataAccess;
    private final LoginOutputBoundary presenter;

    public LoginInteractor(LoginUserDataAccessInterface userDataAccess, LoginOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }



    @Override
    public void prepareSuccessView(LoginOutputData outputData) {

    }

    @Override
    public void prepareFailView(String errorMessage) {

    }

    // ... existing code ...

    @Override
    public void execute(LoginInputData inputData) {
        final String username = inputData.getUsername();
        final String password = inputData.getPassword();

        // Check if user exists
        if (!userDataAccess.existsByName(username)) {
            presenter.prepareFailView("Account not found: " + username);
            return;
        }

        // Get user and check password
        User user = userDataAccess.get(username);
        if (user == null) {
            presenter.prepareFailView("Error retrieving user data");
            return;
        }

        if (!PasswordHasher.hash(password).equals(user.getPassword())) {
            presenter.prepareFailView("Incorrect password for " + username);
            return;
        }

        // Login successful
        userDataAccess.setCurrentUsername(user.getUsername());
        final LoginOutputData loginOutputData = new LoginOutputData(user.getUsername(), "Successfully logged in!");
        presenter.prepareSuccessView(loginOutputData);
    }

// ... existing code ...
}
