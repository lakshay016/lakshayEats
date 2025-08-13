package entity;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class PreferencesTest {

    @Test
    public void gettersReturnProvidedMaps() {
        Map<String, Integer> diets = new HashMap<>();
        diets.put("vegan", 1);
        Map<String, Integer> intolerances = new HashMap<>();
        intolerances.put("gluten", 1);

        Preferences prefs = new Preferences(diets, intolerances);

        assertSame(diets, prefs.getDiets());
        assertSame(intolerances, prefs.getIntolerances());

        String repr = prefs.toString();
        assertTrue(repr.contains("vegan"));
        assertTrue(repr.contains("gluten"));
    }

    @Test
    public void isDietEnabled_trueWhenNonZero_falseWhenZeroNullOrMissing() {
        Map<String, Integer> diets = new HashMap<>();
        diets.put("vegan", 1);
        diets.put("keto", 0);
        diets.put("paleo", null);
        // "vegetarian" intentionally missing

        Preferences prefs = new Preferences(diets, new HashMap<>());

        assertTrue(prefs.isDietEnabled("vegan"));
        assertFalse(prefs.isDietEnabled("keto"));
        assertFalse(prefs.isDietEnabled("paleo"));
        assertFalse(prefs.isDietEnabled("vegetarian"));
    }

    @Test
    public void isIntoleranceEnabled_treatsAnyNonZeroAsEnabled() {
        Map<String, Integer> intolerances = new HashMap<>();
        intolerances.put("gluten", 2);   // non-zero
        intolerances.put("dairy", -1);   // negative still counts (non-zero)
        intolerances.put("soy", 0);
        intolerances.put("egg", null);

        Preferences prefs = new Preferences(new HashMap<>(), intolerances);

        assertTrue(prefs.isIntoleranceEnabled("gluten"));
        assertTrue(prefs.isIntoleranceEnabled("dairy"));
        assertFalse(prefs.isIntoleranceEnabled("soy"));
        assertFalse(prefs.isIntoleranceEnabled("egg"));
        assertFalse(prefs.isIntoleranceEnabled("peanut")); // missing
    }

    @Test
    public void enabledDiets_returnsOnlyNonZeroKeys() {
        Map<String, Integer> diets = new HashMap<>();
        diets.put("vegan", 1);
        diets.put("vegetarian", 0);
        diets.put("pescetarian", 3);
        diets.put("keto", null);

        Preferences prefs = new Preferences(diets, new HashMap<>());

        Set<String> enabled = prefs.enabledDiets();
        assertEquals(new HashSet<>(Arrays.asList("vegan", "pescetarian")), enabled);
        assertFalse(enabled.contains("vegetarian"));
        assertFalse(enabled.contains("keto"));
    }

    @Test
    public void enabledIntolerances_returnsOnlyNonZeroKeys() {
        Map<String, Integer> ints = new HashMap<>();
        ints.put("gluten", 1);
        ints.put("dairy", 0);
        ints.put("peanut", null);
        ints.put("sesame", 5);

        Preferences prefs = new Preferences(new HashMap<>(), ints);

        Set<String> enabled = prefs.enabledIntolerances();
        assertEquals(new HashSet<>(Arrays.asList("gluten", "sesame")), enabled);
        assertFalse(enabled.contains("dairy"));
        assertFalse(enabled.contains("peanut"));
    }

    @Test
    public void enabledLists_preserveInsertionOrderViaLinkedHashSet() {
        // Use LinkedHashMap so stream encounter order is deterministic
        Map<String, Integer> diets = new LinkedHashMap<>();
        diets.put("vegan", 1);
        diets.put("pescetarian", 1);
        diets.put("vegetarian", 0);   // filtered out
        diets.put("keto", 1);

        Map<String, Integer> ints = new LinkedHashMap<>();
        ints.put("gluten", 0);        // filtered out
        ints.put("dairy", 1);
        ints.put("sesame", 1);

        Preferences prefs = new Preferences(diets, ints);

        assertEquals(Arrays.asList("vegan", "pescetarian", "keto"), prefs.enabledDietsList());
        assertEquals(Arrays.asList("dairy", "sesame"), prefs.enabledIntolerancesList());
    }

    @Test
    public void enabledSets_matchEnabledLists() {
        Map<String, Integer> diets = new LinkedHashMap<>();
        diets.put("A", 1);
        diets.put("B", 0);
        diets.put("C", 2);

        Map<String, Integer> ints = new LinkedHashMap<>();
        ints.put("X", 0);
        ints.put("Y", 9);
        ints.put("Z", -3);

        Preferences prefs = new Preferences(diets, ints);

        assertEquals(new LinkedHashSet<>(Arrays.asList("A", "C")), prefs.enabledDiets());
        assertEquals(new LinkedHashSet<>(Arrays.asList("Y", "Z")), prefs.enabledIntolerances());
        assertEquals(new ArrayList<>(prefs.enabledDiets()), prefs.enabledDietsList());
        assertEquals(new ArrayList<>(prefs.enabledIntolerances()), prefs.enabledIntolerancesList());
    }
}
