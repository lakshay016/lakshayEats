package use_case.login;

import entity.CommonUser;
import entity.User;

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

    @Override
    public void execute(LoginInputData inputData) {
        User user = userDataAccess.get(inputData.getUsername());

        if (user == null || !user.getPassword().equals(inputData.getPassword())) {
            presenter.prepareFailView("Incorrect username or password");
        } else {
            presenter.prepareSuccessView(new LoginOutputData(inputData.getUsername(),"Login Successful!"));
        }
        final String username = inputData.getUsername();
        final String password = inputData.getPassword();
        if (!userDataAccess.existsByName(username)) {
            presenter.prepareFailView(username + ": Account is not in database.");
        }
        else {
            final String pwd = userDataAccess.get(username).getPassword();
            if (!password.equals(pwd)) {
                presenter.prepareFailView("Wrong password for \"" + username + "\".");
            }
            else {

                userDataAccess.setCurrentUsername(user.getUsername());

                final LoginOutputData loginOutputData = new LoginOutputData(user.getUsername(),
                        "Successfully logged in!");
                presenter.prepareSuccessView(loginOutputData);
            }
        }
    }
}
