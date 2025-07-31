package app;

import entity.Recipe;
import interface_adapter.edit.EditController;
import interface_adapter.edit.EditPresenter;
import data_access.DBRecipeDataAccessObject;
import use_case.RecipeEdit.RecipeEditInteractor;

import java.util.Arrays;
import java.util.UUID;

public class EditDriver {
    public static void main(String[] args) {
        DBRecipeDataAccessObject dataAccess = new DBRecipeDataAccessObject();
        EditPresenter presenter = new EditPresenter();
        RecipeEditInteractor interactor = new RecipeEditInteractor(dataAccess, presenter);
        EditController controller = new EditController(interactor);

        Recipe recipe = new Recipe(
                UUID.randomUUID().toString(),
                "Cake",
                Arrays.asList("Eggs", "Flour", "Milk", "Sugar"),
                "Mix ingredients and bake at 300 degrees for 20 minutes.",
                "Sara"
        );

        controller.saveRecipe(recipe);

        recipe.setSteps("Mix ingredients, let batter rest 5 minutes, then bake.");
        controller.editRecipe(recipe);
    }
}