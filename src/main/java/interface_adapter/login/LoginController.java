package interface_adapter.login;

import use_case.login.*;

public class LoginController {
    private final LoginInputBoundary interactor;

    public LoginController(LoginInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void login(String username, String password) {
        LoginInputData inputData = new LoginInputData(username, password);
        interactor.execute(inputData);
    }
}
