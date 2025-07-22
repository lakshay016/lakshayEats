package use_case.login;

public interface LoginOutputBoundary {

    void prepareSucessView(LoginOutputData outputData);

    void prepareFailView(String errorMessage);
}
