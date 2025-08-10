package app;

import data_access.DBChangePasswordDataAccessObject;
import entity.CommonUserFactory;
import entity.User;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.change_password.ChangePasswordPresenter;
import use_case.change_password.*;
import interface_adapter.change_password.ChangePasswordPresenter;
import interface_adapter.change_password.LoggedInViewModel;

// TODO: DELETE
public class ChangePasswordTestDriver {
    public static void main(String[] args) {
        // Setup
        UserFactory factory = new CommonUserFactory();
        ChangePasswordUserDataAccessInterface dao = new DBChangePasswordDataAccessObject();
        LoggedInViewModel viewModel = new LoggedInViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        ChangePasswordPresenter presenter = new ChangePasswordPresenter(viewManagerModel, viewModel);
        ChangePasswordInputBoundary interactor = new ChangePasswordInteractor(dao, presenter, factory);

        // Test Data
        String username = "mia";
        String newPassword = "newPass123";
        ChangePasswordInputData inputData = new ChangePasswordInputData(username, newPassword);
        interactor.execute(inputData);
    }
}
