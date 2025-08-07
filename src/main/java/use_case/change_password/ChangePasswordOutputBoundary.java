package use_case.change_password;


public interface ChangePasswordOutputBoundary {

    void prepareSuccessView(ChangePasswordOutputData outputData);


    void prepareFailView(String errorMessage);
}
