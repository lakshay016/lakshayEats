package app;

import data_access.SpoonacularAPIClient;
import entity.FilterOptions;
import entity.SearchResult;
import entity.Ingredients;
import entity.Nutrition;
import entity.Instructions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

// TODO: DELETE
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

        formatter(sel);

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("\nEnter the recipe ID(s) you want details for (comma‑separated): ");
            String line = sc.nextLine();                            // read the whole line
            List<Integer> ids = Arrays.stream(line.split(","))      // split on commas
                    .map(String::trim)                                  // remove extra whitespace
                    .filter(s -> !s.isEmpty())                         // skip blank entries
                    .map(Integer::parseInt)                            // parse to Integer
                    .collect(Collectors.toList());

            List<SearchResult> results2 = client.loadIDs(ids);
            if (results.isEmpty()) {
                System.out.println("No recipe found for ID(s): " + ids);
            } else {
                // print each result
                for (SearchResult res : results2) {
                    formatter(res);
                }
            }
        } catch (NumberFormatException nfe) {
            System.err.println("One of the IDs wasn’t a valid integer: " + nfe.getMessage());
        } catch (IOException e) {
            System.err.println("Error fetching recipe details: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static void formatter(SearchResult sel) {
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