package use_case.save;

import entity.SearchResult;

public class SaveOutputData {
    private String message;
    private SearchResult recipe;
    public SaveOutputData(String message, SearchResult recipe) {
        this.message = message;
        this.recipe = recipe;
    }
    public String getMessage() {
        return message;
    }
    public SearchResult getRecipe() {
        return recipe;
    }
}
