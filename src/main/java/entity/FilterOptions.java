package entity;

import java.util.List;
import java.util.EnumMap;
import java.util.Map;

/**
 * Encapsulates all the query parameters we'll send to /recipes/complexSearch.
 */
public class FilterOptions {
    // 1) Cuisine filters
    // Valid choices are:
    //      [African, Asian, American, British, Cajun, Caribbean, Chinese, Eastern European, European,
    //      French, German, Greek, Indian, Irish, Italian, Japanese, Jewish, Korean, Latin American,
    //      Mediterranean, Mexican, Middle Eastern, Nordic, Southern, Spanish, Thai, Vietnamese]
    private List<String> cuisines;
    private List<String> excludeCuisines;

    // 2) Dietary restrictions
    private List<String> diets;
    // [Gluten Free, Ketogenic, Vegetarian, Lacto-Vegetarian, Ovo-vegetarian, Vegan, Pescetarian,
    // Paleo, Primal, Low FODMAP, Whole30]
    private List<String> intolerances;
    // [Dairy, Egg, Gluten, Grain, Peanut, Seafood, Sesame, Shellfish, Soy, Sulfite, Tree Nut, Wheat]

    // 3) Ingredient inclusion/exclusion (used by both complexSearch & findByIngredients)
    private List<String> includeIngredients;
    private List<String> excludeIngredients;

    // 4) Meal type
    // [main course, side dish, dessert, appetizer, salad, bread, breakfast, soup, beverage, sauce,
    // marinade, fingerfood, snack, drink]
    private String type;

    // 5) Time (mins) & servings
    private Integer maxReadyTime;
    private Integer minServings;
    private Integer maxServings;

    // 6) Sorting
    private String sort;
    /**
     * [(empty), meta-score, popularity, healthiness, price, time, random, max-used-ingredients,
     * min-missing-ingredients, alcohol, caffeine, copper, energy, calories, calcium, carbohydrates,
     * carbs, choline, cholesterol, total-fat, fluoride, trans-fat, saturated-fat, mono-unsaturated-fat,
     *  poly-unsaturated-fat, fiber, folate, folic-acid, iodine, iron, magnesium, manganese, vitamin-b3,
     * niacin, vitamin-b5, pantothenic-acid, phosphorus, potassium, protein, vitamin-b2, riboflavin, selenium,
     * sodium, vitamin-b1, thiamin, vitamin-a, vitamin-b6, vitamin-b12, vitamin-c, vitamin-d, vitamin-e, vitamin-k,
     * sugar, zinc]
     */
    private String sortDirection;
    // "asc" or "desc"

    // 7) Flags
    private Boolean instructionsRequired = true;
    private Boolean fillIngredients = false;
    private Boolean addRecipeInformation = true;
    private Boolean addRecipeInstructions = true;
    private Boolean addRecipeNutrition = true;

    // 8) Pagination
    // skip this many
    private Integer offset;
    // return up to this many
    private Integer number;

    // 9) Key word (title match) <-- tba

    // Nutrient‐ranges map, keyed by Nutrient enum
    private final Map<Nutrients, Range<Double>> nutrientRanges =
            new EnumMap<>(Nutrients.class);

    public Map<Nutrients, Range<Double>> getAllNutrientRanges() {
        return nutrientRanges;
    }

    public Range<Double> getNutrientRange(Nutrients nut) {
        return nutrientRanges.get(nut);
    }

    public void setNutrientRange(Nutrients nut, Double min, Double max) {
        nutrientRanges.put(nut, new Range<>(min, max));
    }

    public List<String> getCuisines() { return cuisines; }
    public void setCuisines(List<String> cuisines) { this.cuisines = cuisines; }

    public List<String> getExcludeCuisines() { return excludeCuisines; }
    public void setExcludeCuisines(List<String> excludeCuisines) { this.excludeCuisines = excludeCuisines; }

    public List<String> getDiets() { return diets; }
    public void setDiets(List<String> diets) { this.diets = diets; }

    public List<String> getIntolerances() { return intolerances; }
    public void setIntolerances(List<String> intolerances) { this.intolerances = intolerances; }

    public List<String> getIncludeIngredients() { return includeIngredients; }
    public void setIncludeIngredients(List<String> includeIngredients) { this.includeIngredients = includeIngredients; }

    public List<String> getExcludeIngredients() { return excludeIngredients; }
    public void setExcludeIngredients(List<String> excludeIngredients) { this.excludeIngredients = excludeIngredients; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getMaxReadyTime() { return maxReadyTime; }
    public void setMaxReadyTime(Integer maxReadyTime) { this.maxReadyTime = maxReadyTime; }

    public Integer getMinServings() { return minServings; }
    public void setMinServings(Integer minServings) { this.minServings = minServings; }

    public Integer getMaxServings() { return maxServings; }
    public void setMaxServings(Integer maxServings) { this.maxServings = maxServings; }

    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    public Boolean getInstructionsRequired() { return instructionsRequired; }
    public void setInstructionsRequired(Boolean instructionsRequired) {
        this.instructionsRequired = instructionsRequired;
    }

    public Boolean getFillIngredients() { return fillIngredients; }
    public void setFillIngredients(Boolean fillIngredients) { this.fillIngredients = fillIngredients; }

    public Boolean getAddRecipeInformation() { return addRecipeInformation; }
    public void setAddRecipeInformation(Boolean addRecipeInformation) {
        this.addRecipeInformation = addRecipeInformation;
    }

    public Boolean getAddRecipeInstructions() { return addRecipeInstructions; }
    public void setAddRecipeInstructions(Boolean addRecipeInstructions) {
        this.addRecipeInstructions = addRecipeInstructions;
    }

    public Boolean getAddRecipeNutrition() { return addRecipeNutrition; }
    public void setAddRecipeNutrition(Boolean addRecipeNutrition) {
        this.addRecipeNutrition = addRecipeNutrition;
    }

    public Integer getOffset() { return offset; }
    public void setOffset(Integer offset) { this.offset = offset; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

}

