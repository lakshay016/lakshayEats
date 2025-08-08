package interface_adapter.preferences;

import use_case.preferences.save_preferences.SavePreferencesOutputBoundary;

public class SavePreferencesPresenter implements SavePreferencesOutputBoundary {
    private final PreferencesViewModel viewModel;

    public SavePreferencesPresenter(PreferencesViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(String message) {
        viewModel.setMessage(message);
        viewModel.firePropertyChanged(); // refresh UI
    }

    @Override
    public void prepareFailView(String error) {
        viewModel.setMessage(error);
        viewModel.firePropertyChanged();
    }
}
