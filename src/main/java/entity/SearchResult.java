package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;

/**
 * What we need from /recipes/complexSearch.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {
    private int    id;
    private String title;
    private String image;
    private int    readyInMinutes;
    private int    servings;
    private double spoonacularScore;
    private List<Instructions> instructions;
    @JsonIgnore
    private List<Nutrition> nutrition;
    private List<Ingredients> ingredients;
    @JsonIgnore
    private int weightPerServing;

    public SearchResult() {
    }

    public SearchResult(int id, String title, String image, int readyInMinutes, int servings,
                        double spoonacularScore, List<Instructions> instructions, List<Nutrition> nutrition,
                        List<Ingredients> ingredients, int weightPerServing) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.readyInMinutes = readyInMinutes;
        this.servings = servings;
        this.spoonacularScore = spoonacularScore;
        this.instructions = instructions;
        this.nutrition = nutrition;
        this.ingredients = ingredients;
        this.weightPerServing = weightPerServing;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(int readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public double getSpoonacularScore() {
        return spoonacularScore;
    }

    public void setSpoonacularScore(double spoonacularScore) {
        this.spoonacularScore = spoonacularScore;
    }

    public List<Instructions> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instructions> instructions) {
        this.instructions = instructions;
    }

    public List<Nutrition> getNutrition() {
        return nutrition;
    }

    public void setNutrition(List<Nutrition> nutrition) {
        this.nutrition = nutrition;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    public int getWeightPerServing() {
        return weightPerServing;
    }

    public void setWeightPerServing(int weightPerServing) {
        this.weightPerServing = weightPerServing;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", readyInMinutes=" + readyInMinutes +
                ", servings=" + servings +
                ", spoonacularScore=" + spoonacularScore +
                // if these lists have nice toString(), they’ll print sensibly:
                ", instructions=" + instructions +
                ", nutrition=" + nutrition +
                ", ingredients=" + ingredients +
                ", weightPerServing=" + weightPerServing +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchResult)) return false;
        SearchResult that = (SearchResult) o;
        return id == that.id
                && readyInMinutes == that.readyInMinutes
                && servings == that.servings
                && Double.compare(that.spoonacularScore, spoonacularScore) == 0
                && weightPerServing == that.weightPerServing
                && Objects.equals(title, that.title)
                && Objects.equals(image, that.image)
                && Objects.equals(instructions, that.instructions)
                && Objects.equals(nutrition, that.nutrition)
                && Objects.equals(ingredients, that.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, title, image, readyInMinutes, servings,
                spoonacularScore, instructions, nutrition, ingredients,
                weightPerServing
        );
    }

}

