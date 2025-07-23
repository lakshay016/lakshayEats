package use_case.signup;

import entity.UserFactory;
import entity.User;
import use_case.login.LoginUserDataAccessInterface;

public class SignupInteractor implements SignupInputBoundary {
    private final LoginUserDataAccessInterface userDataAccess;
    private final SignupOutputBoundary presenter;
    private final UserFactory factory;

    public SignupInteractor(LoginUserDataAccessInterface userDataAccess,
                            SignupOutputBoundary presenter,
                            UserFactory factory) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
        this.factory = factory;
    }

    @Override
    public void execute(SignupInputData inputData) {
        if (userDataAccess.existsByName(inputData.getUsername())) {
            presenter.prepareFailView("Username already exists.");
        } else {
            User newUser = factory.createUser(inputData.getUsername(), inputData.getPassword());
            userDataAccess.save(newUser);
            presenter.prepareSuccessView(new SignupOutputData("Signup successful!"));
        }
    }
}