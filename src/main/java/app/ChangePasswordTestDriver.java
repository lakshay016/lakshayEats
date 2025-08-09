package app;

import data_access.DBChangePasswordDataAccessObject;
import entity.CommonUserFactory;
import entity.User;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInPresenter;
import use_case.change_password.*;
import interface_adapter.logged_in.LoggedInPresenter;
import interface_adapter.logged_in.LoggedInViewModel;

public class ChangePasswordTestDriver {
    public static void main(String[] args) {
        // Setup
        UserFactory factory = new CommonUserFactory();
        ChangePasswordUserDataAccessInterface dao = new DBChangePasswordDataAccessObject();
        LoggedInViewModel viewModel = new LoggedInViewModel();
        LoggedInPresenter presenter = new LoggedInPresenter(viewModel);
        ChangePasswordInputBoundary interactor = new ChangePasswordInteractor(dao, presenter, factory);

        // Test Data
        String username = "mia";
        String newPassword = "newPass123";
        ChangePasswordInputData inputData = new ChangePasswordInputData(username, newPassword);
        interactor.execute(inputData);
    }
}
