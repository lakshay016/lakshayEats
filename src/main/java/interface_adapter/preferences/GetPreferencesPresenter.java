package interface_adapter.preferences;

import entity.Preferences;
import use_case.preferences.get_preferences.GetPreferencesOutputBoundary;

public class GetPreferencesPresenter implements GetPreferencesOutputBoundary {
    private final PreferencesViewModel viewModel;

    public GetPreferencesPresenter(PreferencesViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentPreferences(Preferences preferences) {
        viewModel.setPreferences(preferences);
    }
}
