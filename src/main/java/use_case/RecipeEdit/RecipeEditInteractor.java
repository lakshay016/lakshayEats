package use_case.RecipeEdit;

import entity.Recipe;

public class RecipeEditInteractor implements RecipeEditInputBoundary{
    private final RecipeDataAccessInterface recipeDataAccess;
    private final RecipeEditOutputBoundary presenter;

    public RecipeEditInteractor(RecipeDataAccessInterface recipeDataAccess, RecipeEditOutputBoundary presenter) {
        this.recipeDataAccess = recipeDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void executeSave(Recipe recipe) {
        try {
            recipeDataAccess.save(recipe);
            presenter.prepareSuccessView("Recipe saved");
        } catch (Exception e) {
            presenter.prepareFailView("Failed to save recipe");
        }
    }

    @Override
    public void executeEdit(Recipe recipe) {
        try {
            recipeDataAccess.edit(recipe);
            presenter.prepareSuccessView("Recipe edited");
        } catch (Exception e) {
            presenter.prepareFailView("Failed to edit recipe");
        }
    }
}
