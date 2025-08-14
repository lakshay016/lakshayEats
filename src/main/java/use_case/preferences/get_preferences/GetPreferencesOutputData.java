package use_case.preferences.get_preferences;

import entity.Preferences;

public class GetPreferencesOutputData {
    private Preferences preferences;

    public GetPreferencesOutputData(Preferences preferences) {
        this.preferences = preferences;
    }
    public Preferences getPreferences() {
        return preferences;
    }

}
