package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

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
}

