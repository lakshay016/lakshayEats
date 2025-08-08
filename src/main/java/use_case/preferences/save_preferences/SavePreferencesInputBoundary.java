package use_case.preferences.save_preferences;

import entity.Preferences;

public interface SavePreferencesInputBoundary {
    void execute(String username, Preferences preferences);
}
