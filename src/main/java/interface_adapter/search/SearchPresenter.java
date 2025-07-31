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
        SearchState newState = new SearchState();
        newState.setLoading(false);
        newState.setErrorMessage(null);
        newState.setResults(output.getResults());
        viewModel.publishState(newState);
    }

    @Override
    public void prepareFailView(String error) {
        SearchState newState = new SearchState();
        newState.setLoading(false);
        newState.setErrorMessage(error);
        viewModel.publishState(newState);
    }

    public SearchViewModel getViewModel() {
        return viewModel;
    }

}
