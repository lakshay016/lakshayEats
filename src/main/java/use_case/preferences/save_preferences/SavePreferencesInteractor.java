package use_case.preferences.save_preferences;

import use_case.preferences.PreferencesDataAccessInterface;

public class SavePreferencesInteractor implements SavePreferencesInputBoundary {
    private final PreferencesDataAccessInterface preferencesDAO;
    private final SavePreferencesOutputBoundary presenter;

    public SavePreferencesInteractor(PreferencesDataAccessInterface preferencesDAO,
                                     SavePreferencesOutputBoundary presenter) {
        this.preferencesDAO = preferencesDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(SavePreferencesInputData inputData) {
        try {
            preferencesDAO.savePreferences(inputData.getUsername(), inputData.getPreferences());
            presenter.prepareSuccessView("Preferences saved successfully!");
        } catch (Exception e) {
            presenter.prepareFailView("Failed to save preferences: " + e.getMessage());
        }
    }
}
