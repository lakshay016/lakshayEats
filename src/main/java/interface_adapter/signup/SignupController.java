package interface_adapter.signup;

import use_case.signup.*;

public class SignupController {
    private final SignupInputBoundary interactor;

    public SignupController(SignupInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void signup(String username, String password) {
        interactor.execute(new SignupInputData(username, password));
    }
}