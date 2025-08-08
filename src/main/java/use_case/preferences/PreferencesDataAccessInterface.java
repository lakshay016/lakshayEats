package use_case.preferences;

import entity.Preferences;

public interface PreferencesDataAccessInterface {
    Preferences getPreferences(String username);
    void savePreferences(String username, Preferences preferences);
}
