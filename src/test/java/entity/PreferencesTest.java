package entity;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PreferencesTest {
    @Test
    public void gettersReturnProvidedMaps() {
        Map<String, Integer> diets = new HashMap<>();
        diets.put("vegan", 1);
        Map<String, Integer> intolerances = new HashMap<>();
        intolerances.put("gluten", 1);
        Preferences prefs = new Preferences(diets, intolerances);
        assertEquals(diets, prefs.getDiets());
        assertEquals(intolerances, prefs.getIntolerances());
        String repr = prefs.toString();
        assertTrue(repr.contains("vegan"));
        assertTrue(repr.contains("gluten"));
    }
}
