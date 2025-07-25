package use_case.RecipeEdit;

import entity.Recipe;

public interface RecipeEditInputBoundary {
    void executeSave(Recipe recipe);
    void executeEdit(Recipe recipe);
}
