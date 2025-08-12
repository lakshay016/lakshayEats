import entity.Preferences;
import interface_adapter.preferences.GetPreferencesPresenter;
import interface_adapter.preferences.PreferencesViewModel;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;

public class GetPreferencesPresenterTest {
    @Test
    public void testPresentPreferences() {
        PreferencesViewModel viewModel = new PreferencesViewModel();
        final boolean[] fired = {false};
        viewModel.addPropertyChangeListener(evt -> fired[0] = true);
        GetPreferencesPresenter presenter = new GetPreferencesPresenter(viewModel);
        Preferences prefs = new Preferences(new HashMap<>(), new HashMap<>());
        presenter.presentPreferences(prefs);
        assertTrue(fired[0]);
        assertEquals(prefs, viewModel.getPreferences());
    }
}
