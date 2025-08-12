import interface_adapter.ViewManagerModel;
import interface_adapter.change_password.ChangePasswordPresenter;
import interface_adapter.change_password.LoggedInViewModel;
import org.junit.Test;
import use_case.change_password.ChangePasswordOutputData;
import static org.junit.Assert.*;

public class ChangePasswordPresenterTest {
    @Test
    public void testPrepareSuccessViewFiresPasswordChange() {
        LoggedInViewModel loggedIn = new LoggedInViewModel();
        ViewManagerModel manager = new ViewManagerModel();
        final String[] observed = {null};
        loggedIn.addPropertyChangeListener(evt -> observed[0] = evt.getPropertyName());
        ChangePasswordPresenter presenter = new ChangePasswordPresenter(manager, loggedIn);
        presenter.prepareSuccessView(new ChangePasswordOutputData("user", false));
        assertEquals("password", observed[0]);
    }

    @Test
    public void testPrepareFailViewDoesNothing() {
        LoggedInViewModel loggedIn = new LoggedInViewModel();
        ViewManagerModel manager = new ViewManagerModel();
        ChangePasswordPresenter presenter = new ChangePasswordPresenter(manager, loggedIn);
        presenter.prepareFailView("err");
        // no exception and no property change expected
    }
}
