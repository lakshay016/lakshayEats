package entity.search;


import entity.Nutrients;
import org.junit.Test;

import static org.junit.Assert.*;

public class NutrientsTest {

    @Test
    public void minKeyAndMaxKey_calories_simpleLowercase() {
        assertEquals("minCalories", Nutrients.CALORIES.minKey());
        assertEquals("maxCalories", Nutrients.CALORIES.maxKey());
    }

    @Test
    public void minKeyAndMaxKey_totalFat_camelCase() {
        assertEquals("minTotalFat", Nutrients.TOTAL_FAT.minKey());
        assertEquals("maxTotalFat", Nutrients.TOTAL_FAT.maxKey());
    }

    @Test
    public void minKeyAndMaxKey_saturatedFat_camelCase() {
        assertEquals("minSaturatedFat", Nutrients.SATURATED_FAT.minKey());
        assertEquals("maxSaturatedFat", Nutrients.SATURATED_FAT.maxKey());
    }

    @Test
    public void minKeyAndMaxKey_vitaminB12_mixedAlphaNumeric() {
        assertEquals("minVitaminB12", Nutrients.VITAMIN_B12.minKey());
        assertEquals("maxVitaminB12", Nutrients.VITAMIN_B12.maxKey());
    }

    @Test
    public void minKeyAndMaxKey_protein_simpleLowercase() {
        assertEquals("minProtein", Nutrients.PROTEIN.minKey());
        assertEquals("maxProtein", Nutrients.PROTEIN.maxKey());
    }

    @Test
    public void minKeyAndMaxKey_caffeine_simpleLowercase() {
        assertEquals("minCaffeine", Nutrients.CAFFEINE.minKey());
        assertEquals("maxCaffeine", Nutrients.CAFFEINE.maxKey());
    }

    @Test
    public void minKeyAndMaxKey_alwaysPrefixedAndNonEmpty() {
        for (Nutrients n : Nutrients.values()) {
            String min = n.minKey();
            String max = n.maxKey();

            assertTrue("minKey should start with 'min'", min.startsWith("min"));
            assertTrue("maxKey should start with 'max'", max.startsWith("max"));

            assertTrue("minKey should be longer than prefix", min.length() > 3);
            assertTrue("maxKey should be longer than prefix", max.length() > 3);

            // First character after the prefix should be uppercase
            assertTrue(Character.isUpperCase(min.charAt(3)));
            assertTrue(Character.isUpperCase(max.charAt(3)));
        }
    }

    @Test
    public void minKeyAndMaxKey_areDistinctForEachEnum() {
        for (Nutrients n : Nutrients.values()) {
            assertNotEquals("minKey and maxKey must differ", n.minKey(), n.maxKey());
        }
    }
}

