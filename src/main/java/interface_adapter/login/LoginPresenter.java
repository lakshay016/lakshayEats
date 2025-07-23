package interface_adapter.login;

import use_case.login.*;

public class LoginPresenter implements LoginOutputBoundary {
    @Override
    public void prepareSuccessView(LoginOutputData outputData) {
        System.out.println(outputData.getMessage());
    }

    @Override
    public void prepareFailView(String error) {
        System.err.println("Login failed: " + error);
    }
}
