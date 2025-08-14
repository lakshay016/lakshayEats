package use_case.save;

import entity.SearchResult;

public class SaveInputData {
    private String username;
    private final SearchResult searchResult;
    private final boolean isUnsave;
    private final int recipeId;


    public SaveInputData(String username, SearchResult searchResult) {
        this.username = username;
        this.searchResult = searchResult;
        this.isUnsave = false; // Default to save
        this.recipeId = searchResult.getId();
    }

    public SaveInputData(String username, int recipeId, boolean isUnsave) {
        this.username = username;
        this.searchResult = null; // Not needed for unsave
        this.isUnsave = isUnsave;
        this.recipeId = recipeId;

    }
    public String getUsername() {
        return username;
    }
    public SearchResult getRecipe() {
        return searchResult;
    }
    public boolean isUnsave() {
        return isUnsave;
    }
    //getter for recipe ID when unsaving
    public int getRecipeId() {
        //extract it from searchResult
        return recipeId;
    }
}
