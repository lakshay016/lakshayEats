package app;

import data_access.DBRecipeDataAccessObject;
import data_access.DBUserDataAccessObject;
import entity.*;
import interface_adapter.save.SaveController;
import interface_adapter.save.SavePresenter;
import interface_adapter.save.SaveViewModel;
import use_case.save.SaveInteractor;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class SaveDriver {
    public static void main(String[] args) {
        // === 1. Setup ViewModel, Presenter, DAO, Interactor, Controller ===
        SaveViewModel viewModel = new SaveViewModel();
        SavePresenter presenter = new SavePresenter(viewModel);
        DBRecipeDataAccessObject recipeDAO = new DBRecipeDataAccessObject();
        SaveInteractor interactor = new SaveInteractor(recipeDAO, presenter);
        SaveController controller = new SaveController(interactor);


        // === 2. Setup current user (mocked login) ===
        DBUserDataAccessObject userDAO = new DBUserDataAccessObject(new CommonUserFactory());
        String mockUsername = "a"; // Assume this exists in your Supabase
        userDAO.setCurrentUsername(mockUsername);


        // === 3. Create mock SearchResult ===
        String name1 = "Flour";
        double amount1 = 1.0;
        String unit1 = "cups";
        Ingredients flour = new Ingredients();
        flour.setName(name1);
        flour.setAmount(amount1);
        flour.setUnit(unit1);

        String name2 = "Baking Powder";
        double amount2 = 0.5;
        String unit2 = "tsp";
        Ingredients eggs = new Ingredients();
        eggs.setName(name2);
        eggs.setAmount(amount2);
        eggs.setUnit(unit2);

        String name3 = "Milk";
        double amount3 = 3.0;
        String unit3 = "cups";
        Ingredients milk = new Ingredients();
        milk.setName(name3);
        milk.setAmount(amount3);
        milk.setUnit(unit3);

        String name4 = "Calories";
        double amount4 = 1.5;
        String unit4 = "cal";
        double percent4 = 12.0;
        Nutrition calories = new Nutrition();
        calories.setUnit(unit4);
        calories.setAmount(amount4);
        calories.setPercentOfDailyNeeds(percent4);
        calories.setName(name4);

        String name5 = "Protein";
        double amount5 = 10;
        String unit5 = "g";
        double percent5 = 15.0;
        Nutrition protein = new Nutrition();
        protein.setUnit(unit5);
        protein.setAmount(amount5);
        protein.setPercentOfDailyNeeds(percent5);
        protein.setName(name5);

        String step1 = "Mix baking powder and flour.";
        Instructions mix = new Instructions();
        mix.setNumber(1);
        mix.setStep(step1);

        String step2 = "Heat oven to 345 degrees.";
        Instructions heat = new Instructions();
        heat.setNumber(2);
        heat.setStep(step2);

        String step3 = "Add milk.";
        Instructions pan = new Instructions();
        pan.setNumber(3);
        pan.setStep(step3);


        SearchResult mockRecipe = new SearchResult();

        mockRecipe.setId(123956);
        mockRecipe.setTitle("est");
        mockRecipe.setServings(3);
        mockRecipe.setReadyInMinutes(15);
        mockRecipe.setNutrition(Arrays.asList(calories, protein));
        mockRecipe.setInstructions(Arrays.asList(mix, heat, pan));
        mockRecipe.setIngredients(Arrays.asList(flour, eggs, milk));


        // === 4. Save the recipe ===
        controller.save(mockUsername, mockRecipe);

        // === 5. Output result ===
        System.out.println("\n Save Test:");
        System.out.println(viewModel.getSaveState().getMessage());
    }
}