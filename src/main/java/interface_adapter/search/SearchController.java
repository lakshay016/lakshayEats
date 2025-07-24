package interface_adapter.search;

import entity.FilterOptions;
import use_case.search.SearchInputBoundary;
import use_case.search.SearchInputData;

public class SearchController {

    private final SearchInputBoundary interactor;

    public SearchController(SearchInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void handleSearch(String query, FilterOptions filters) {
        // convert filters to SearchInputData
        interactor.execute(new SearchInputData(query, filters));
    }
}
