package use_case.search;

import data_access.SpoonacularAPIClient;
import entity.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SearchInteractorTest {

    private String apiKey;
    private SearchDataAccessInterface gateway;
    private SearchOutputBoundary presenter;
    private SearchInteractor interactor;

    // captures
    private SearchOutputData resultData;
    private String errorMessage;

    // expected results
    private SearchResult expectedBoozy;
    private SearchResult expectedBruschetta;
    private SearchResult expectedParm;
    private SearchResult expectedSoupMinimal;
    private SearchResult expectedSoupDetail;

    @Before
    public void setUp() {
        apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null) {
            throw new IllegalStateException("Set SPOONACULAR_API_KEY in your env");
        }


        presenter = new SearchOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchOutputData output) {
                resultData = output;
            }

            @Override
            public void prepareFailView(String error) {
                errorMessage = error;
            }
        };

        List<Instructions> emptyInstructions = new ArrayList<>();
        List<Nutrition> emptyNutrition = new ArrayList<>();
        List<Ingredients> emptyIngredients = new ArrayList<>();

        // build expectedBoozy
        expectedBoozy = new SearchResult(
                635675,
                "Boozy Bbq Chicken",
                "https://img.spoonacular.com/recipes/635675-312x231.jpg",
                0,
                0,
                0.0,
                emptyInstructions,
                emptyNutrition,
                emptyIngredients,
                0);

        // build expectedBruschetta
        expectedBruschetta = new SearchResult(
                715538,
                "What to make for dinner tonight?? Bruschetta Style Pork & Pasta",
                "https://img.spoonacular.com/recipes/715538-312x231.jpg",
                0,
                0,
                0.0,
                emptyInstructions,
                emptyNutrition,
                emptyIngredients,
                0
        );

        // build expectedParm (full‐detail)

        List<Instructions> parmInstructions = new ArrayList<>();
        List<Nutrition>    parmNutrition    = new ArrayList<>();
        List<Ingredients>  parmIngredients   = new ArrayList<>();

        parmInstructions.addAll(Arrays.asList(
                new Instructions(1,
                        "Preheat oven to 350.Start with cutting your chicken breasts into halves. " +
                                "Season both sides of your chicken with salt and pepper, " +
                                "then dredge each breast in flour. Be sure to coat each side and the edges."),
                new Instructions(2,
                        "Heat vegetable oil in a large skillet over medium-high heat.In a small sauce pan, " +
                                "warm your pasta sauce.Lightly beat 1 egg in a bowl. " +
                                "Put about 1 cup of bread crumbs in another bowl and season with a " +
                                "little garlic powder. Take your floured chicken and dip into the egg wash, " +
                                "coating all over, then into the bread crumbs. Bread all four chicken breasts."
                ),
                new Instructions(3,
                        "Put the chicken into the skillet and cook for three minutes on each side, " +
                                "or until golden brown on each side."
                ),
                new Instructions(4,
                        "Remove the chicken to a baking sheet. Finish the chicken in the 350 degree oven " +
                                "for 10-15 minutes. {Depending on how thick your chicken breasts, add more " +
                                "or less time.}In a large pot, boil the water needed for your pasta. We used " +
                                "angel hair which only takes 3-5 minutes to cook. But if youre using a penne " +
                                "or something denser youll need more time for the pasta.Pull the chicken out and top " +
                                "with mozzarella cheese, put back into the oven to melt the cheese." +
                                "Plate the pasta first, topped with chicken and the cover with sauce. " +
                                "If youre feeling zesty add a little fresh shaved Pecorino-Romano or grated Parmesan " +
                                "cheese!"
                )
        ));

        parmNutrition.addAll(Arrays.asList(
                new Nutrition("Calories", 556.8, "kcal", 27.84),
                new Nutrition("Fat", 17.02, "g", 26.19),
                new Nutrition("Saturated Fat", 5.21, "g", 32.55),
                new Nutrition("Carbohydrates", 57.2, "g", 19.07),
                new Nutrition("Net Carbohydrates", 53.02, "g", 19.28),
                new Nutrition("Sugar", 6.86, "g", 7.63),
                new Nutrition("Cholesterol", 131.32, "mg", 43.77),
                new Nutrition("Sodium", 29466.58, "mg", 1281.16),
                new Nutrition("Alcohol", 0.0, "g", 100.0),
                new Nutrition("Alcohol %", 0.0, "%", 100.0),
                new Nutrition("Protein", 42.52, "g", 85.05),
                new Nutrition("Selenium", 82.59, "µg", 117.99),
                new Nutrition("Vitamin B3", 14.66, "mg", 73.31),
                new Nutrition("Vitamin B6", 1.12, "mg", 55.76),
                new Nutrition("Phosphorus", 549.95, "mg", 54.99),
                new Nutrition("Manganese", 0.84, "mg", 42.19),
                new Nutrition("Calcium", 298.46, "mg", 29.85),
                new Nutrition("Potassium", 984.62, "mg", 28.13),
                new Nutrition("Vitamin B5", 2.48, "mg", 24.85),
                new Nutrition("Vitamin B2", 0.4, "mg", 23.76),
                new Nutrition("Magnesium", 91.15, "mg", 22.79),
                new Nutrition("Copper", 0.4, "mg", 20.09),
                new Nutrition("Zinc", 2.89, "mg", 19.25),
                new Nutrition("Iron", 3.37, "mg", 18.71),
                new Nutrition("Vitamin E", 2.77, "mg", 18.45),
                new Nutrition("Vitamin B1", 0.26, "mg", 17.55),
                new Nutrition("Fiber", 4.18, "g", 16.72),
                new Nutrition("Vitamin K", 17.37, "µg", 16.54),
                new Nutrition("Vitamin A", 759.61, "IU", 15.19),
                new Nutrition("Vitamin C", 9.94, "mg", 12.05),
                new Nutrition("Folate", 44.57, "µg", 11.14),
                new Nutrition("Vitamin B12", 0.59, "µg", 9.84),
                new Nutrition("Vitamin D", 0.42, "µg", 2.79)
        ));

        parmIngredients.addAll(Arrays.asList(
                new Ingredients("angel hair pasta", 1.0, "servings"),
                new Ingredients("breadcrumbs", 1.0, "servings"),
                new Ingredients("breasts of chicken", 0.5, ""),
                new Ingredients("egg", 0.25, ""),
                new Ingredients("flour", 0.25, "cup"),
                new Ingredients("garlic powder", 0.38, "teaspoons"),
                new Ingredients("mozzarella cheese", 0.25, "cup"),
                new Ingredients("pasta sauce", 1.0, "servings"),
                new Ingredients("salt and pepper", 1.0, "servings"),
                new Ingredients("vegetable oil", 0.5, "tablespoons")
        ));

        expectedParm = new SearchResult(
                638235,
                "Chicken Parmesan With Pasta",
                "https://img.spoonacular.com/recipes/638235-312x231.jpg",
                45,
                4,
                72.15814208984375,
                parmInstructions,
                parmNutrition,
                parmIngredients,
                422);

        // build expectedSoupMinimal
        expectedSoupMinimal = new SearchResult(
                715415,
                "Red Lentil Soup with Chicken and Turnips",
                "https://img.spoonacular.com/recipes/715415-312x231.jpg",
                0,
                0,
                0.0,
                emptyInstructions,
                emptyNutrition,
                emptyIngredients,
                0
        );


        // build expectedSoupDetail

        List<Instructions> soupInstructions = Arrays.asList(
                new Instructions(1,
                        "To a large dutch oven or soup pot, heat the olive oil over medium heat."),
                new Instructions(2,
                        "Add the onion, carrots and celery and cook for 8-10 minutes or until tender, " +
                                "stirring occasionally."),
                new Instructions(3,
                        "Add the garlic and cook for an additional 2 minutes, or until fragrant. " +
                                "Season conservatively with a pinch of salt and black pepper." +
                                "To the pot, add the tomatoes, turnip and red lentils. Stir to combine. " +
                                "Stir in the vegetable stock and increase the heat on the stove to high. " +
                                "Bring the soup to a boil and then reduce to a simmer. Simmer for 20 minutes or " +
                                "until the turnips are tender and the lentils are cooked through."),
                new Instructions(4,
                        "Add the chicken breast and parsley. Cook for an additional 5 minutes. " +
                                "Adjust seasoning to taste."),
                new Instructions(5,
                        "Serve the soup immediately garnished with fresh parsley and any additional toppings. " +
                                "Enjoy!")
        );

        expectedSoupDetail = new SearchResult(
                715415,
                "Red Lentil Soup with Chicken and Turnips",
                "https://img.spoonacular.com/recipes/715415-312x231.jpg",
                55,
                8,
                99.42810821533203,
                soupInstructions,
                emptyNutrition,
                emptyIngredients,
                0

        );
    }

    // helper for minimal filters
    private FilterOptions minimalOpts() {
        FilterOptions opts = new FilterOptions();
        opts.setFillIngredients(false);
        opts.setAddRecipeInformation(false);
        opts.setAddRecipeInstructions(false);
        opts.setAddRecipeNutrition(false);
        opts.setNumber(1);
        return opts;
    }

    @Test
    public void nonsenseQuery_yieldsNoResults() throws IOException {
        gateway    = new SpoonacularAPIClient(apiKey);
        interactor = new SearchInteractor(gateway, presenter);
        SearchInputData input = new SearchInputData("asdfasdfxyz", minimalOpts());
        interactor.execute(input);
        assertTrue(resultData.getResults().isEmpty());
    }

    @Test
    public void emptyQuery_returnsSoup() throws IOException {
        gateway    = new SpoonacularAPIClient(apiKey);
        interactor = new SearchInteractor(gateway, presenter);
        SearchInputData input = new SearchInputData("", minimalOpts());
        interactor.execute(input);
        assertEquals(expectedSoupMinimal, resultData.getResults().get(0));
    }

    @Test
    public void chickenQuery_returnsBoozy() throws IOException {
        gateway    = new SpoonacularAPIClient(apiKey);
        interactor = new SearchInteractor(gateway, presenter);
        SearchInputData input = new SearchInputData("chicken", minimalOpts());
        interactor.execute(input);
        System.out.println(expectedBoozy);
        System.out.println(resultData.getResults().get(0));
        assertEquals(expectedBoozy, resultData.getResults().get(0));
    }

    @Test
    public void pastaCuisine_returnsBruschetta() throws IOException {
        gateway    = new SpoonacularAPIClient(apiKey);
        interactor = new SearchInteractor(gateway, presenter);
        FilterOptions opts = minimalOpts();
        opts.setCuisines(Collections.singletonList("Italian"));
        SearchInputData input = new SearchInputData("pasta", opts);
        interactor.execute(input);
        assertEquals(expectedBruschetta, resultData.getResults().get(0));
    }

    @Test
    public void chickenParm_fullDetails_returnsParm() throws IOException {
        gateway    = new SpoonacularAPIClient(apiKey);
        interactor = new SearchInteractor(gateway, presenter);
        FilterOptions opts = new FilterOptions();
        opts.setNumber(1);
        opts.setAddRecipeInformation(true);
        opts.setAddRecipeInstructions(true);
        opts.setAddRecipeNutrition(true);

        SearchInputData input = new SearchInputData("chicken parm", opts);
        interactor.execute(input);
        assertEquals(expectedParm, resultData.getResults().get(0));
    }

    @Test
    public void searchByIngredient_returnsSoup() throws IOException {
        gateway    = new SpoonacularAPIClient(apiKey);
        interactor = new SearchInteractor(gateway, presenter);
        FilterOptions opts = minimalOpts();
        opts.setAddRecipeInformation(true);
        opts.setIncludeIngredients(Collections.singletonList("tomato"));
        SearchInputData input = new SearchInputData("", opts);
        interactor.execute(input);
        assertEquals(expectedSoupDetail, resultData.getResults().get(0));
    }

    @Test
    public void dataAccessException_triggersFailPresenter() throws IOException {
        gateway    = (q, f) -> { throw new IOException("simulated"); };
        interactor = new SearchInteractor(gateway, presenter);
        SearchInputData input = new SearchInputData("anything", minimalOpts());
        interactor.execute(input);
        assertEquals("simulated", errorMessage);
    }
}