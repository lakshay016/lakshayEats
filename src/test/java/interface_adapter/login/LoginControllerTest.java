import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInputData;
import use_case.login.LoginOutputData;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoginControllerTest {
    private static class StubInteractor implements LoginInputBoundary {
        LoginInputData received;
        @Override public void prepareSuccessView(LoginOutputData outputData) {}
        @Override public void prepareFailView(String errorMessage) {}
        @Override public void execute(LoginInputData loginInputData) { this.received = loginInputData; }
    }

    @Test
    public void testExecutePassesInputData() {
        StubInteractor interactor = new StubInteractor();
        ViewManagerModel manager = new ViewManagerModel();
        LoginController controller = new LoginController(interactor, manager);
        controller.execute("user", "pass");
        assertEquals("user", interactor.received.getUsername());
        assertEquals("pass", interactor.received.getPassword());
    }

    @Test
    public void testSwitchToSignupView() {
        StubInteractor interactor = new StubInteractor();
        ViewManagerModel manager = new ViewManagerModel();
        LoginController controller = new LoginController(interactor, manager);
        controller.switchToSignupView();
        assertEquals("sign up", manager.getState());
    }
}
