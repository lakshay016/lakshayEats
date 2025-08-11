import interface_adapter.ViewManagerModel;
import interface_adapter.change_password.LoggedInViewModel;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import org.junit.Test;
import use_case.login.LoginOutputData;
import static org.junit.Assert.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginPresenterTest {
    @Test
    public void testPrepareSuccessViewUpdatesModels() {
        ViewManagerModel manager = new ViewManagerModel();
        LoginViewModel loginVM = new LoginViewModel();
        LoggedInViewModel loggedInVM = new LoggedInViewModel();
        final boolean[] managerFired = {false};
        final boolean[] loggedFired = {false};
        manager.addPropertyChangeListener(evt -> managerFired[0] = true);
        loggedInVM.addPropertyChangeListener(evt -> loggedFired[0] = true);
        LoginPresenter presenter = new LoginPresenter(manager, loginVM, loggedInVM);
        presenter.prepareSuccessView(new LoginOutputData("alice", "ok"));
        assertEquals("alice", loggedInVM.getState().getUsername());
        assertEquals(loggedInVM.getViewName(), manager.getState());
        assertTrue(managerFired[0]);
        assertTrue(loggedFired[0]);
    }

    @Test
    public void testPrepareFailViewUpdatesLoginError() {
        ViewManagerModel manager = new ViewManagerModel();
        LoginViewModel loginVM = new LoginViewModel();
        LoggedInViewModel loggedInVM = new LoggedInViewModel();
        final boolean[] loginFired = {false};
        loginVM.addPropertyChangeListener(evt -> loginFired[0] = true);
        LoginPresenter presenter = new LoginPresenter(manager, loginVM, loggedInVM);
        presenter.prepareFailView("bad");
        assertEquals("bad", loginVM.getState().getLoginError());
        assertTrue(loginFired[0]);
    }
}
