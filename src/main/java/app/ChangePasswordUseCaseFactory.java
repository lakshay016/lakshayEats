package app;

import data_access.DBUserDataAccessObject;
import entity.CommonUserFactory;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.change_password.ChangePasswordController;
import interface_adapter.change_password.ChangePasswordPresenter;
import interface_adapter.change_password.LoggedInViewModel;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import view.ChangePasswordView;


public final class ChangePasswordUseCaseFactory {

    private ChangePasswordUseCaseFactory() {

    }


    public static ChangePasswordView create(
            ViewManagerModel viewManagerModel,
            LoggedInViewModel loggedInViewModel,
            ChangePasswordUserDataAccessInterface userDataAccessObject, DBUserDataAccessObject dbUserDataAccessObject) {

        final ChangePasswordController changePasswordController =
                createChangePasswordUseCase(viewManagerModel, loggedInViewModel, userDataAccessObject);
        return new ChangePasswordView(loggedInViewModel, changePasswordController, viewManagerModel, dbUserDataAccessObject);

    }

    private static ChangePasswordController createChangePasswordUseCase(
            ViewManagerModel viewManagerModel,
            LoggedInViewModel loggedInViewModel,
            ChangePasswordUserDataAccessInterface userDataAccessObject) {

        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                loggedInViewModel);

        final UserFactory userFactory = new CommonUserFactory();

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        return new ChangePasswordController(changePasswordInteractor);
    }
}
