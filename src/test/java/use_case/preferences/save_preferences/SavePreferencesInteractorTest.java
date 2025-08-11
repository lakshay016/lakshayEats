package use_case.preferences.save_preferences;

import entity.Preferences;
import org.junit.Test;
import use_case.preferences.PreferencesDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SavePreferencesInteractorTest {
    private static class InMemoryDAO implements PreferencesDataAccessInterface {
        Map<String, Preferences> store = new HashMap<>();
        boolean throwError = false;
        @Override
        public void savePreferences(String username, Preferences preferences) {
            if (throwError) {
                throw new RuntimeException("fail");
            }
            store.put(username, preferences);
        }
        @Override
        public Preferences getPreferences(String username) {
            return store.get(username);
        }
    }
    private static class TestPresenter implements SavePreferencesOutputBoundary {
        String message;
        @Override public void prepareSuccessView(String msg) { message = msg; }
        @Override public void prepareFailView(String msg) { message = msg; }
    }

    @Test
    public void savesPreferencesOnSuccess() {
        InMemoryDAO dao = new InMemoryDAO();
        TestPresenter presenter = new TestPresenter();
        SavePreferencesInteractor interactor = new SavePreferencesInteractor(dao, presenter);
        Preferences prefs = new Preferences(Map.of("vegan",1), Map.of());
        interactor.execute("alice", prefs);
        assertEquals(prefs, dao.getPreferences("alice"));
        assertEquals("Preferences saved successfully!", presenter.message);
    }

    @Test
    public void reportsFailureWhenDaoThrows() {
        InMemoryDAO dao = new InMemoryDAO();
        dao.throwError = true;
        TestPresenter presenter = new TestPresenter();
        SavePreferencesInteractor interactor = new SavePreferencesInteractor(dao, presenter);
        Preferences prefs = new Preferences(Map.of(), Map.of());
        interactor.execute("bob", prefs);
        assertTrue(presenter.message.startsWith("Failed to save preferences"));
    }
}
