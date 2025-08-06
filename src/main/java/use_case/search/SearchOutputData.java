package use_case.search;

import entity.SearchResult;

import java.util.List;

public class SearchOutputData {
    private final List<SearchResult> results;

    public SearchOutputData(List<SearchResult> results) {
        this.results = results;
    }

    public List<SearchResult> getResults() {
        return results;
    }
}