package app;

import data_access.SpoonacularAPIClient;
import entity.FilterOptions;
import entity.SearchResult;
import entity.Ingredients;
import entity.Nutrition;
import entity.Instructions;

import java.util.List;
import java.util.Scanner;

public class SearchDriver {
    public static void main(String[] args) throws Exception {
        // 0) grab your key
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null) {
            System.err.println("Error: set SPOONACULAR_API_KEY in your Run config");
            System.exit(1);
        }

        // set up client + filters
        SpoonacularAPIClient client = new SpoonacularAPIClient(apiKey);
        FilterOptions opts = new FilterOptions.Builder()
                .pagination(0, 10)
                .requireInstructions()
                .build();

        // get query
        System.out.print("\nWhat are you craving: ");
        Scanner scanner = new Scanner(System.in);
        String query = scanner.nextLine();

        // fetch & list
        List<SearchResult> results = client.searchByDish(query, opts);
        System.out.println("Found:");
        for (SearchResult r : results) {
            System.out.printf("  %d: %s (%d min)%n",
                    r.getId(), r.getTitle(), r.getReadyInMinutes());
        }

        // prompt the user
        System.out.print("\nEnter the recipe ID you want details for: ");
        int chosenId = scanner.nextInt();

        // find the matching SearchResult
        SearchResult sel = null;
        for (SearchResult r : results) {
            if (r.getId() == chosenId) {
                sel = r;
                break;
            }
        }
        if (sel == null) {
            System.err.println("⛔ No recipe with ID " + chosenId + " in that list.");
            System.exit(2);
        }

        // print a very simple detail view
        System.out.println("\n=== " + sel.getTitle() + " ===");
        System.out.printf("Serves: %d people  •  Ready in: %d min%n",
                sel.getServings(), sel.getReadyInMinutes());

        // Ingredients
        System.out.println("\nIngredients:");
        for (Ingredients ing : sel.getIngredients()) {
            System.out.printf("  • %s – %.2f %s%n",
                    ing.getName(), ing.getAmount(), ing.getUnit());
        }

        // Nutrition
        System.out.println("\nNutrition (per serving):");
        for (Nutrition nut : sel.getNutrition()) {
            System.out.printf("  • %s: %.2f%s (%.1f%% DV)%n",
                    nut.getName(), nut.getAmount(), nut.getUnit(),
                    nut.getPercentOfDailyNeeds());
        }

        // Instructions
        System.out.println("\nInstructions:");
        for (Instructions step : sel.getInstructions()) {
            System.out.printf("  %d. %s%n",
                    step.getNumber(), step.getStep());
        }
    }
}
