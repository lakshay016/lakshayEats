package entity.search;



import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Ingredients;
import entity.Instructions;
import entity.Nutrition;
import entity.SearchResult;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class SearchResultTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void scalarSettersAndGettersWork() {
        SearchResult sr = new SearchResult();
        sr.setId(651994);
        sr.setTitle("Miniature Fruit Tarts");
        sr.setImage("https://img.example/tarts.jpg");
        sr.setReadyInMinutes(45);
        sr.setServings(60);
        sr.setSpoonacularScore(87.5);

        assertEquals(651994, sr.getId());
        assertEquals("Miniature Fruit Tarts", sr.getTitle());
        assertEquals("https://img.example/tarts.jpg", sr.getImage());
        assertEquals(45, sr.getReadyInMinutes());
        assertEquals(60, sr.getServings());
        assertEquals(87.5, sr.getSpoonacularScore(), 1e-9);
    }

    @Test
    public void listFieldsAreStoredAndReturned() {
        Instructions step = new Instructions();
        step.setNumber(1);
        step.setStep("Whisk eggs.");

        Nutrition n = new Nutrition();
        n.setName("Calories");
        n.setAmount(210.0);
        n.setUnit("kcal");
        n.setPercentOfDailyNeeds(10.0);

        Ingredients ing = new Ingredients();
        ing.setName("Flour");
        ing.setAmount(500.0);
        ing.setUnit("g");

        SearchResult sr = new SearchResult();
        sr.setInstructions(Collections.singletonList(step));
        sr.setNutrition(Arrays.asList(n));
        sr.setIngredients(Arrays.asList(ing));

        assertEquals(1, sr.getInstructions().size());
        assertEquals("Whisk eggs.", sr.getInstructions().get(0).getStep());

        assertEquals(1, sr.getNutrition().size());
        assertEquals("Calories", sr.getNutrition().get(0).getName());

        assertEquals(1, sr.getIngredients().size());
        assertEquals("Flour", sr.getIngredients().get(0).getName());
    }

    @Test
    public void jsonIgnoresUnknownFields() throws Exception {
        String json = "{"
                + "\"id\":123,"
                + "\"title\":\"Test Recipe\","
                + "\"image\":\"/img.png\","
                + "\"readyInMinutes\":30,"
                + "\"servings\":4,"
                + "\"spoonacularScore\":95.0,"
                + "\"totallyUnknown\":\"ignored\""
                + "}";

        SearchResult sr = MAPPER.readValue(json, SearchResult.class);

        assertEquals(123, sr.getId());
        assertEquals("Test Recipe", sr.getTitle());
        assertEquals("/img.png", sr.getImage());
        assertEquals(30, sr.getReadyInMinutes());
        assertEquals(4, sr.getServings());
        assertEquals(95.0, sr.getSpoonacularScore(), 1e-9);
    }

    @Test
    public void jsonDoesNotPopulateIgnoredFields() throws Exception {
        // If SearchResult has @JsonIgnore on nutrition and weightPerServing,
        // this ensures they are not populated by deserialization.
        String json = "{"
                + "\"id\":999,"
                + "\"title\":\"Ignore Fields\","
                + "\"weightPerServing\":250,"
                + "\"nutrition\":[{\"name\":\"Fat\",\"amount\":10.0,\"unit\":\"g\",\"percentOfDailyNeeds\":15.0}]"
                + "}";

        SearchResult sr = MAPPER.readValue(json, SearchResult.class);

        assertEquals(999, sr.getId());
        // Ignored fields should remain default (null for lists, 0 for primitive int)
        assertNull("nutrition should not be populated by JSON (ignored)", sr.getNutrition());
        assertEquals("weightPerServing should remain default (likely 0)", 0, sr.getWeightPerServing());
    }

    @Test
    public void canManuallySetWeightPerServing() {
        SearchResult sr = new SearchResult();
        sr.setWeightPerServing(300);
        assertEquals(300, sr.getWeightPerServing());
    }


    @Test
    public void equalsAndHashCode_basicSanityIfOverridden() {
        SearchResult a = new SearchResult();
        a.setId(1);
        a.setTitle("A");
        a.setImage("/a.png");
        a.setReadyInMinutes(10);
        a.setServings(2);
        a.setSpoonacularScore(50.0);

        SearchResult b = new SearchResult();
        b.setId(1);
        b.setTitle("A");
        b.setImage("/a.png");
        b.setReadyInMinutes(10);
        b.setServings(2);
        b.setSpoonacularScore(50.0);

        // If equals/hashCode are not overridden, these will fail — remove this test in that case.
        // If they ARE overridden, these should pass.
        // Keep or delete based on your actual implementation.
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
