package entity;

import java.util.Map;

public class Preferences {
    private final Map<String, Integer> diets;
    private final Map<String, Integer> intolerances;

    public Preferences(Map<String, Integer> diets, Map<String, Integer> intolerances) {
        this.diets = diets;
        this.intolerances = intolerances;
    }

    public Map<String, Integer> getDiets() {
        return diets;
    }

    public Map<String, Integer> getIntolerances() {
        return intolerances;
    }

}
