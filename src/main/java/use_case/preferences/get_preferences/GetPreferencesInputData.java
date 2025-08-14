package use_case.preferences.get_preferences;

public class GetPreferencesInputData {
    private String username;

    public GetPreferencesInputData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
