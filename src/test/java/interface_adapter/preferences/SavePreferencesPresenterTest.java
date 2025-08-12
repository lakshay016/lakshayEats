import interface_adapter.preferences.PreferencesViewModel;
import interface_adapter.preferences.SavePreferencesPresenter;
import org.junit.Test;
import static org.junit.Assert.*;

public class SavePreferencesPresenterTest {
    @Test
    public void testPrepareSuccessView() {
        PreferencesViewModel viewModel = new PreferencesViewModel();
        final boolean[] fired = {false};
        viewModel.addPropertyChangeListener(evt -> fired[0] = true);
        SavePreferencesPresenter presenter = new SavePreferencesPresenter(viewModel);
        presenter.prepareSuccessView("done");
        assertEquals("done", viewModel.getMessage());
        assertTrue(fired[0]);
    }

    @Test
    public void testPrepareFailView() {
        PreferencesViewModel viewModel = new PreferencesViewModel();
        final boolean[] fired = {false};
        viewModel.addPropertyChangeListener(evt -> fired[0] = true);
        SavePreferencesPresenter presenter = new SavePreferencesPresenter(viewModel);
        presenter.prepareFailView("err");
        assertEquals("err", viewModel.getMessage());
        assertTrue(fired[0]);
    }
}
