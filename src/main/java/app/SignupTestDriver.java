package app;

import data_access.DBUserDataAccessObject;
import entity.CommonUserFactory;
import entity.UserFactory;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.*;

public class SignupTestDriver {
    public static void main(String[] args) {
        UserFactory factory = new CommonUserFactory();
        LoginUserDataAccessInterface db = new DBUserDataAccessObject(factory);


        SignupOutputBoundary presenter = new SignupPresenter();
        SignupInputBoundary interactor = new SignupInteractor(db, presenter, factory);
        SignupController controller = new SignupController(interactor);

        String testUsername = "d";
        String testPassword = "12345";

        controller.signup(testUsername, testPassword);
    }
}
