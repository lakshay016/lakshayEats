package interface_adapter.login;

import interface_adapter.ViewManagerModel;
import interface_adapter.ViewModel;
import use_case.login.*;

public class LoginController {
    private final LoginInputBoundary interactor;
    private final ViewManagerModel viewManagerModel;


    public LoginController(LoginInputBoundary interactor, ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
        this.interactor = interactor;
    }

    public void execute(String username, String password) {
        LoginInputData inputData = new LoginInputData(username, password);
        interactor.execute(inputData);
    }
    public void switchToSignupView() {
        ViewModel<Object> viewManagerModel;
        this.viewManagerModel.setState("sign up");
        this.viewManagerModel.firePropertyChanged();
    }
    public void login(String username, String password) {
    }
}
