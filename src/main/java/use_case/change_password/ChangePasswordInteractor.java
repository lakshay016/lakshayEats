package use_case.change_password;

import entity.User;
import entity.UserFactory;

/**
 * The Change Password Interactor.
 */
public class ChangePasswordInteractor implements ChangePasswordInputBoundary {
    private final ChangePasswordUserDataAccessInterface userDataAccessObject;
    private final ChangePasswordOutputBoundary userPresenter;
    private final UserFactory userFactory;

    public ChangePasswordInteractor(ChangePasswordUserDataAccessInterface userDataAccessObject,
                                    ChangePasswordOutputBoundary userPresenter,
                                    UserFactory userFactory) {
        this.userDataAccessObject = userDataAccessObject;
        this.userPresenter = userPresenter;
        this.userFactory = userFactory;
    }

    @Override
    public void execute(ChangePasswordInputData inputData) {
        // Create a new user with updated password
        User updatedUser = userFactory.createUser(inputData.getUsername(), inputData.getPassword());

        // Pass the new user to DAO to handle persistence
        userDataAccessObject.changePassword(updatedUser);

        // Notify the presenter
        ChangePasswordOutputData outputData = new ChangePasswordOutputData(updatedUser.getUsername(), false);
        userPresenter.prepareSuccessView(outputData);
    }
}
