package entity;

import java.util.Map;

public class Preferences {
    private final Map<String, Integer> dietaryPreferences;
    private final Map<String, Integer> intolerances;

    public Preferences(Map<String, Integer> dietaryPreferences, Map<String, Integer> intolerances) {
        this.dietaryPreferences = dietaryPreferences;
        this.intolerances = intolerances;
    }

    public Map<String, Integer> getDietaryPreferences() {
        return dietaryPreferences;
    }

    public Map<String, Integer> getIntolerances() {
        return intolerances;
    }
}
