package interface_adapter.change_password;

import interface_adapter.ViewManagerModel;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.change_password.ChangePasswordOutputData;


public class ChangePasswordPresenter implements ChangePasswordOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;

    public ChangePasswordPresenter(ViewManagerModel viewManagerModel,
                             LoggedInViewModel loggedInViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(ChangePasswordOutputData outputData) {
        loggedInViewModel.firePropertyChanged("password");

    }

    @Override
    public void prepareFailView(String error) {
    }
}
