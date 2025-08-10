package entity.search;


import entity.FilterOptions;
import entity.Nutrients;
import entity.Range;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

public class FilterOptionsTest {

    @Test
    public void defaults_flagsAndCollections() {
        FilterOptions fo = new FilterOptions();

        // Flag defaults
        assertEquals(Boolean.TRUE,  fo.getInstructionsRequired());
        assertEquals(Boolean.FALSE, fo.getFillIngredients());
        assertEquals(Boolean.TRUE,  fo.getAddRecipeInformation());
        assertEquals(Boolean.TRUE,  fo.getAddRecipeInstructions());
        assertEquals(Boolean.TRUE,  fo.getAddRecipeNutrition());

        // Collections default (null until set)
        assertNull(fo.getCuisines());
        assertNull(fo.getExcludeCuisines());
        assertNull(fo.getDiets());
        assertNull(fo.getIntolerances());
        assertNull(fo.getIncludeIngredients());
        assertNull(fo.getExcludeIngredients());

        // Nutrients map default
        Map<Nutrients, Range<Double>> nutrients = fo.getAllNutrientRanges();
        assertNotNull(nutrients);
        assertTrue(nutrients.isEmpty());

        // Scalars default
        assertNull(fo.getType());
        assertNull(fo.getMaxReadyTime());
        assertNull(fo.getMinServings());
        assertNull(fo.getMaxServings());
        assertNull(fo.getSort());
        assertNull(fo.getSortDirection());
        assertNull(fo.getOffset());
        assertNull(fo.getNumber());
    }

    @Test
    public void settersAndGetters_lists() {
        FilterOptions fo = new FilterOptions();

        fo.setCuisines(Arrays.asList("Indian", "Mexican"));
        fo.setExcludeCuisines(Collections.singletonList("Nordic"));
        fo.setDiets(Collections.singletonList("Vegan"));
        fo.setIntolerances(Arrays.asList("Dairy", "Gluten"));
        fo.setIncludeIngredients(Arrays.asList("tomato", "basil"));
        fo.setExcludeIngredients(Collections.singletonList("peanut"));

        fo.setType("dessert");
        fo.setMaxReadyTime(30);
        fo.setMinServings(2);
        fo.setMaxServings(6);

        fo.setSort("popularity");
        fo.setSortDirection("desc");

        fo.setOffset(20);
        fo.setNumber(10);

        assertEquals(Arrays.asList("Indian", "Mexican"), fo.getCuisines());
        assertEquals(Collections.singletonList("Nordic"), fo.getExcludeCuisines());
        assertEquals(Collections.singletonList("Vegan"), fo.getDiets());
        assertEquals(Arrays.asList("Dairy", "Gluten"), fo.getIntolerances());
        assertEquals(Arrays.asList("tomato", "basil"), fo.getIncludeIngredients());
        assertEquals(Collections.singletonList("peanut"), fo.getExcludeIngredients());

        assertEquals("dessert", fo.getType());
        assertEquals(Integer.valueOf(30), fo.getMaxReadyTime());
        assertEquals(Integer.valueOf(2),  fo.getMinServings());
        assertEquals(Integer.valueOf(6),  fo.getMaxServings());

        assertEquals("popularity", fo.getSort());
        assertEquals("desc", fo.getSortDirection());

        assertEquals(Integer.valueOf(20), fo.getOffset());
        assertEquals(Integer.valueOf(10), fo.getNumber());
    }

    @Test
    public void settersAndGetters_toggleFlags() {
        FilterOptions fo = new FilterOptions();

        // Flip each flag from its default and verify
        fo.setInstructionsRequired(false);
        fo.setFillIngredients(true);
        fo.setAddRecipeInformation(false);
        fo.setAddRecipeInstructions(false);
        fo.setAddRecipeNutrition(false);

        assertEquals(Boolean.FALSE, fo.getInstructionsRequired());
        assertEquals(Boolean.TRUE,  fo.getFillIngredients());
        assertEquals(Boolean.FALSE, fo.getAddRecipeInformation());
        assertEquals(Boolean.FALSE, fo.getAddRecipeInstructions());
        assertEquals(Boolean.FALSE, fo.getAddRecipeNutrition());
    }

    @Test
    public void nutrientRanges_setAndGetRange() {
        FilterOptions fo = new FilterOptions();

        // Set one nutrient’s min/max range
        fo.setNutrientRange(Nutrients.PROTEIN, 10.0, 50.0);

        Range<Double> protein = fo.getNutrientRange(Nutrients.PROTEIN);
        assertNotNull(protein);
        assertEquals(Double.valueOf(10.0), protein.getMin());
        assertEquals(Double.valueOf(50.0), protein.getMax());

        // Others remain unset
        assertNull(fo.getNutrientRange(Nutrients.CALORIES));

        // Map reflects only what we set
        Map<Nutrients, Range<Double>> all = fo.getAllNutrientRanges();
        assertTrue(all.containsKey(Nutrients.PROTEIN));
        assertEquals(1, all.size());
    }

    @Test
    public void nutrientRanges_overwriteExistingRange() {
        FilterOptions fo = new FilterOptions();

        fo.setNutrientRange(Nutrients.CALORIES, 100.0, 300.0);
        // overwrite
        fo.setNutrientRange(Nutrients.CALORIES, 200.0, 400.0);

        Range<Double> calories = fo.getNutrientRange(Nutrients.CALORIES);
        assertNotNull(calories);
        assertEquals(Double.valueOf(200.0), calories.getMin());
        assertEquals(Double.valueOf(400.0), calories.getMax());
        assertEquals(1, fo.getAllNutrientRanges().size());
    }
}
