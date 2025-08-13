package use_case.save;

import entity.SearchResult;

public class SaveInputData {
    private String username;
    private final SearchResult searchResult;
    public SaveInputData(String username, SearchResult searchResult) {
        this.username = username;
        this.searchResult = searchResult;
    }
    public String getUsername() {
        return username;
    }
    public SearchResult getRecipe() {
        return searchResult;
    }
}
