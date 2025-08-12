package use_case.preferences.get_preferences;

import entity.Preferences;
import org.junit.Test;
import use_case.preferences.PreferencesDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class GetPreferencesInteractorTest {
    private static class InMemoryDAO implements PreferencesDataAccessInterface {
        Map<String, Preferences> store = new HashMap<>();
        @Override
        public void savePreferences(String username, Preferences preferences) {
            store.put(username, preferences);
        }
        @Override
        public Preferences getPreferences(String username) {
            return store.get(username);
        }
    }
    private static class TestPresenter implements GetPreferencesOutputBoundary {
        Preferences received;
        @Override
        public void presentPreferences(Preferences preferences) {
            this.received = preferences;
        }
    }
    @Test
    public void presenterReceivesPreferences() {
        InMemoryDAO dao = new InMemoryDAO();
        Preferences prefs = new Preferences(Map.of("vegan",1), Map.of());
        dao.savePreferences("alice", prefs);
        TestPresenter presenter = new TestPresenter();
        GetPreferencesInteractor interactor = new GetPreferencesInteractor(dao, presenter);
        interactor.execute("alice");
        assertEquals(prefs, presenter.received);
    }
}
