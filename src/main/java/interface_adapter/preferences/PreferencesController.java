package interface_adapter.preferences;

import entity.Preferences;
import use_case.preferences.get_preferences.GetPreferencesInputBoundary;
import use_case.preferences.get_preferences.GetPreferencesInputData;
import use_case.preferences.save_preferences.SavePreferencesInputBoundary;
import use_case.preferences.save_preferences.SavePreferencesInputData;

public class PreferencesController {
    private final GetPreferencesInputBoundary getInteractor;
    private final SavePreferencesInputBoundary saveInteractor;

    public PreferencesController(GetPreferencesInputBoundary getInteractor,
                                 SavePreferencesInputBoundary saveInteractor) {
        this.getInteractor = getInteractor;
        this.saveInteractor = saveInteractor;
    }

    public void loadPreferences(String username) {
        getInteractor.execute(new GetPreferencesInputData(username));
    }

    public void savePreferences(String username, Preferences prefs) {

        saveInteractor.execute(new SavePreferencesInputData(username, prefs));
    }
}

