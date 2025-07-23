package interface_adapter.signup;

import use_case.signup.*;

public class SignupPresenter implements SignupOutputBoundary {
    @Override
    public void prepareSuccessView(SignupOutputData data) {
        System.out.println(data.getMessage());
    }

    @Override
    public void prepareFailView(String error) {
        System.err.println("Signup failed: " + error);
    }
}
