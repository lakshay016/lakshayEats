package use_case.preferences.save_preferences;

public interface SavePreferencesOutputBoundary {
    void prepareSuccessView(String message);
    void prepareFailView(String error);
}
