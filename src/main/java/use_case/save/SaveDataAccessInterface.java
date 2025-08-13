package use_case.save;

import entity.SearchResult;

public interface SaveDataAccessInterface {
    boolean unsave(String username, int recipeId);
    boolean save(String username, SearchResult recipe);
}
