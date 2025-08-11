import interface_adapter.change_password.ChangePasswordController;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInputData;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Field;

public class ChangePasswordControllerTest {
    private static class StubInteractor implements ChangePasswordInputBoundary {
        ChangePasswordInputData received;
        @Override public void execute(ChangePasswordInputData data) { this.received = data; }
    }

    @Test
    public void testExecuteCallsInteractor() throws Exception {
        StubInteractor interactor = new StubInteractor();
        ChangePasswordController controller = new ChangePasswordController(interactor);
        controller.execute("newPass", "user1");
        assertNotNull(interactor.received);
        Field username = ChangePasswordInputData.class.getDeclaredField("username");
        Field password = ChangePasswordInputData.class.getDeclaredField("password");
        username.setAccessible(true);
        password.setAccessible(true);
        assertEquals("user1", username.get(interactor.received));
        assertEquals("newPass", password.get(interactor.received));
    }
}
