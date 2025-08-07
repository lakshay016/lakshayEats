package interface_adapter.search;

import use_case.search.SearchOutputBoundary;
import use_case.search.SearchOutputData;

public class SearchPresenter implements SearchOutputBoundary {

    private final SearchViewModel viewModel;

    public SearchPresenter(SearchViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(SearchOutputData output) {
        SearchState state = new SearchState();
        //SearchState state = viewModel.getState();
        state.setLoading(false);
        state.setErrorMessage(null);
        state.setResults(output.getResults());
        viewModel.publishState(state);
    }

    @Override
    public void prepareFailView(String error) {
        SearchState state = new SearchState();
        //SearchState state = viewModel.getState();
        state.setLoading(false);
        state.setErrorMessage(error);
        viewModel.publishState(state);
    }

    public SearchViewModel getViewModel() {
        return viewModel;
    }

}
