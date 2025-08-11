import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginViewModel;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import org.junit.Test;
import use_case.signup.SignupOutputData;
import static org.junit.Assert.*;

public class SignupPresenterTest {
    @Test
    public void testPrepareSuccessView() {
        ViewManagerModel manager = new ViewManagerModel();
        SignupViewModel signupVM = new SignupViewModel();
        LoginViewModel loginVM = new LoginViewModel();
        final boolean[] managerFired = {false};
        final boolean[] loginFired = {false};
        manager.addPropertyChangeListener(evt -> managerFired[0] = true);
        loginVM.addPropertyChangeListener(evt -> loginFired[0] = true);
        SignupPresenter presenter = new SignupPresenter(manager, signupVM, loginVM);
        presenter.prepareSuccessView(new SignupOutputData("bob", "ok"));
        assertEquals("bob", loginVM.getState().getUsername());
        assertEquals(loginVM.getViewName(), manager.getState());
        assertTrue(managerFired[0]);
        assertTrue(loginFired[0]);
    }

    @Test
    public void testPrepareFailView() {
        ViewManagerModel manager = new ViewManagerModel();
        SignupViewModel signupVM = new SignupViewModel();
        LoginViewModel loginVM = new LoginViewModel();
        final boolean[] signupFired = {false};
        signupVM.addPropertyChangeListener(evt -> signupFired[0] = true);
        SignupPresenter presenter = new SignupPresenter(manager, signupVM, loginVM);
        presenter.prepareFailView("err");
        assertEquals("err", signupVM.getState().getUsernameError());
        assertTrue(signupFired[0]);
    }

    @Test
    public void testSwitchToLoginView() {
        ViewManagerModel manager = new ViewManagerModel();
        SignupViewModel signupVM = new SignupViewModel();
        LoginViewModel loginVM = new LoginViewModel();
        SignupPresenter presenter = new SignupPresenter(manager, signupVM, loginVM);
        presenter.switchToLoginView();
        assertEquals(loginVM.getViewName(), manager.getState());
    }
}
