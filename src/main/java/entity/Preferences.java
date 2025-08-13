package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;

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

    @Override
    public String toString() {
        return "Preferences{" +
                "diets=" + diets +
                ", intolerances=" + intolerances +
                '}';
    }
    /** True if the diet key maps to a non-zero value. */
    public boolean isDietEnabled(String key) {
        Integer v = diets.get(key);
        return v != null && v != 0;
    }

    /** True if the intolerance key maps to a non-zero value. */
    public boolean isIntoleranceEnabled(String key) {
        Integer v = intolerances.get(key);
        return v != null && v != 0;
    }

    /** Enabled diet names as a Set for O(1) membership checks (great for checkboxes). */
    public Set<String> enabledDiets() {
        return diets.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue() != 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** Enabled intolerance names as a Set. */
    public Set<String> enabledIntolerances() {
        return intolerances.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue() != 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** Enabled diet names as a List  */
    public List<String> enabledDietsList() {
        return new ArrayList<>(enabledDiets());
    }

    /** Enabled intolerance names as a List. */
    public List<String> enabledIntolerancesList() {
        return new ArrayList<>(enabledIntolerances());
    }

}
