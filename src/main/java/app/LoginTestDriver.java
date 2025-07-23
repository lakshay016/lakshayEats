import data_access.InMemoryUserDataAccessObject;
import entity.CommonUserFactory;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginUserDataAccessInterface;
import entity.UserFactory;

public class LoginTestDriver {
    public static void main(String[] args) {
        UserFactory factory = new CommonUserFactory();

        LoginUserDataAccessInterface dataAccess = new InMemoryUserDataAccessObject(factory);

        LoginOutputBoundary presenter = new LoginPresenter();
        LoginInputBoundary interactor = new LoginInteractor(dataAccess, presenter);


        LoginController controller = new LoginController(interactor);


        controller.login("admin", "1234");


        controller.login("admin", "wrong");
    }
}
