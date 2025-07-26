package use_case.RecipeEdit;

import entity.Recipe;

public interface RecipeDataAccessInterface {
    Recipe getRecipe(int id) throws Exception;
    void edit(Recipe recipe) throws Exception;
    void save(Recipe recipe) throws Exception;


}
