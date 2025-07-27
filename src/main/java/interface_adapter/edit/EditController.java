package interface_adapter.edit;

import entity.Recipe;
import use_case.RecipeEdit.RecipeEditInputBoundary;

public class EditController {

    private final RecipeEditInputBoundary interactor;

    public EditController(RecipeEditInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void saveRecipe(Recipe recipe) {
        interactor.executeSave(recipe);
    }

    public void editRecipe(Recipe recipe) {
        interactor.executeEdit(recipe);
    }
}
