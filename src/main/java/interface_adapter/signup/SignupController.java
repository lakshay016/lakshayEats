package interface_adapter.signup;

import use_case.signup.*;

public class SignupController {
    private final SignupInputBoundary interactor;

    public SignupController(SignupInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void signup(String username, String password1, String password2) {
        interactor.execute(new SignupInputData(username, password1, password2));
    }

    public void execute(String username, String password, String repeatPassword) {
        interactor.execute(new SignupInputData(username, password, repeatPassword));
    }

    public void switchToLoginView() {
        interactor.switchToLoginView();

    }
}