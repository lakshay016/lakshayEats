import interface_adapter.signup.SignupController;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInputData;
import org.junit.Test;
import static org.junit.Assert.*;

public class SignupControllerTest {
    private static class StubInteractor implements SignupInputBoundary {
        SignupInputData received;
        boolean switchCalled = false;
        @Override public void execute(SignupInputData inputData) { this.received = inputData; }
        @Override public void switchToLoginView() { switchCalled = true; }
    }

    @Test
    public void testSignupDelegatesToInteractor() {
        StubInteractor interactor = new StubInteractor();
        SignupController controller = new SignupController(interactor);
        controller.signup("u","p1","p2");
        assertEquals("u", interactor.received.getUsername());
        assertEquals("p1", interactor.received.getPassword());
        assertEquals("p2", interactor.received.getRepeatPassword());
    }

    @Test
    public void testSwitchToLoginView() {
        StubInteractor interactor = new StubInteractor();
        SignupController controller = new SignupController(interactor);
        controller.switchToLoginView();
        assertTrue(interactor.switchCalled);
    }
}
