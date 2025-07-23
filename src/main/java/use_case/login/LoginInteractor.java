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
    public void execute(LoginInputData inputData) {
        User user = userDataAccess.get(inputData.getUsername());

        if (user == null || !user.getPassword().equals(inputData.getPassword())) {
            presenter.prepareFailView("Incorrect username or password");
        } else {
            presenter.prepareSuccessView(new LoginOutputData(inputData.getUsername(),"Login Successful!"));
        }
    }
}