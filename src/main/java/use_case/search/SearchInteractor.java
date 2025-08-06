package use_case.search;

import java.io.IOException;

public class SearchInteractor implements SearchInputBoundary  {

    private final SearchDataAccessInterface gateway;
    private final SearchOutputBoundary presenter;

    public SearchInteractor(SearchDataAccessInterface gateway,
                            SearchOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchInputData inputData) {
        try {
            var results = gateway.searchByDish(
                    inputData.getQuery(), inputData.getFilters());
            presenter.prepareSuccessView(new SearchOutputData(results));
        } catch (IOException e) {
            presenter.prepareFailView(e.getMessage());
        }
    }
}
