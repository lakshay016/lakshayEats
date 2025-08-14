package use_case.preferences.save_preferences;

import entity.Preferences;

public class SavePreferencesInputData {
    private String username;
    private Preferences preferences;

    public SavePreferencesInputData(String username, Preferences preferences) {
        this.username = username;
        this.preferences = preferences;
    }

    public String getUsername() {
        return username;
    }

    public Preferences getPreferences() {
        return preferences;
    }
}
