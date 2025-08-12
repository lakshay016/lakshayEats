import entity.Preferences;
import interface_adapter.preferences.PreferencesController;
import org.junit.Test;
import use_case.preferences.get_preferences.GetPreferencesInputBoundary;
import use_case.preferences.save_preferences.SavePreferencesInputBoundary;
import static org.junit.Assert.*;
import java.util.HashMap;

public class PreferencesControllerTest {
    private static class StubGet implements GetPreferencesInputBoundary {
        String username;
        @Override public void execute(String username) { this.username = username; }
    }
    private static class StubSave implements SavePreferencesInputBoundary {
        String username; Preferences prefs;
        @Override public void execute(String username, Preferences preferences) { this.username = username; this.prefs = preferences; }
    }

    @Test
    public void testLoadPreferences() {
        StubGet g = new StubGet();
        StubSave s = new StubSave();
        PreferencesController controller = new PreferencesController(g, s);
        controller.loadPreferences("alice");
        assertEquals("alice", g.username);
    }

    @Test
    public void testSavePreferences() {
        StubGet g = new StubGet();
        StubSave s = new StubSave();
        PreferencesController controller = new PreferencesController(g, s);
        Preferences p = new Preferences(new HashMap<>(), new HashMap<>());
        controller.savePreferences("bob", p);
        assertEquals("bob", s.username);
        assertEquals(p, s.prefs);
    }
}
