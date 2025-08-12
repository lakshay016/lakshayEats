package interface_adapter.save;

import use_case.save.SaveOutputBoundary;
import use_case.save.SaveOutputData;

public class SavePresenter implements SaveOutputBoundary {

    private final SaveViewModel viewModel;

    public SavePresenter(SaveViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(SaveOutputData output) {
        SaveState state = viewModel.getSaveState();
        viewModel.setSaveState(state);
        state.setMessage("Saved!");
        viewModel.setSaveState(state);
    }

    @Override
    public void prepareErrorView(String error) {
        SaveState newState = viewModel.getSaveState();
        newState.setMessage(error);
        viewModel.setSaveState(newState);
        viewModel.setSaveState(newState);
    }

    public SaveViewModel getViewModel() {
        return viewModel;
    }

}
