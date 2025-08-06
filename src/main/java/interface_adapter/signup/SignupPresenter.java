package interface_adapter.signup;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import use_case.signup.*;

public class SignupPresenter implements SignupOutputBoundary {

    private final SignupViewModel signupViewModel;
    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;

    public SignupPresenter(ViewManagerModel viewManagerModel, SignupViewModel signupViewModel, LoginViewModel loginViewModel) {
    this.signupViewModel = signupViewModel;
    this.loginViewModel = loginViewModel;
    this.viewManagerModel = viewManagerModel;
    }



    @Override
    public void prepareSuccessView(SignupOutputData data) {
        final LoginState loginState = loginViewModel.getState();
        loginState.setUsername(data.getUsername());
        this.loginViewModel.setState(loginState);
        loginViewModel.firePropertyChanged();

        viewManagerModel.setState(loginViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
        System.out.println(data.getMessage());
    }

    public SignupPresenter() {
        loginViewModel = new LoginViewModel();
        signupViewModel = new SignupViewModel();
        viewManagerModel = new ViewManagerModel();
    }

    @Override
    public void prepareFailView(String error) {
        final SignupState signupState = signupViewModel.getState();
        signupState.setUsernameError(error);
        signupViewModel.firePropertyChanged();
        System.err.println("Signup failed: " + error);
    }
    public void switchToLoginView() {
        viewManagerModel.setState(loginViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}
