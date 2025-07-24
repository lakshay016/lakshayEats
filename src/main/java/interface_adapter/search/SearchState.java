package interface_adapter.search;

import entity.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchState {
    private boolean isLoading;
    private String errorMessage;
    private List<SearchResult> results = new ArrayList<>();

    public boolean isLoading() {
        return isLoading;
    }
    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<SearchResult> getResults() {
        return results;
    }
    public void setResults(List<SearchResult> results) {
        this.results = results;
    }
}

