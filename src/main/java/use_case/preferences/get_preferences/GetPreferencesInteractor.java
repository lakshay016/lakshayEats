package use_case.preferences.get_preferences;

import entity.Preferences;
import use_case.preferences.PreferencesDataAccessInterface;

public class GetPreferencesInteractor implements GetPreferencesInputBoundary {
    private final PreferencesDataAccessInterface preferencesDAO;
    private final GetPreferencesOutputBoundary presenter;

    public GetPreferencesInteractor(PreferencesDataAccessInterface preferencesDAO,
                                    GetPreferencesOutputBoundary presenter) {
        this.preferencesDAO = preferencesDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(GetPreferencesInputData inputData) {
        Preferences prefs = preferencesDAO.getPreferences(inputData.getUsername());
        presenter.presentPreferences(prefs);
    }
}
