package use_case.login;

public interface LoginInputBoundary  {
    void prepareSuccessView(LoginOutputData outputData);
    void prepareFailView(String errorMessage);

    void execute(LoginInputData loginInputData);
}
