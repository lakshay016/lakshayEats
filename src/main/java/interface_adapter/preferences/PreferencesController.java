package interface_adapter.preferences;

import entity.Preferences;
import use_case.preferences.get_preferences.GetPreferencesInputBoundary;
import use_case.preferences.save_preferences.SavePreferencesInputBoundary;

public class PreferencesController {
    private final GetPreferencesInputBoundary getInteractor;
    private final SavePreferencesInputBoundary saveInteractor;

    public PreferencesController(GetPreferencesInputBoundary getInteractor,
                                 SavePreferencesInputBoundary saveInteractor) {
        this.getInteractor = getInteractor;
        this.saveInteractor = saveInteractor;
    }

    public void loadPreferences(String username) {
        getInteractor.execute(username);
    }

    public void savePreferences(String username, Preferences prefs) {
        saveInteractor.execute(username, prefs);
    }
}

