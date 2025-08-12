package entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class RecipeTest {
    @Test
    public void fieldsCanBeAssigned() {
        Recipe recipe = new Recipe();
        recipe.name = "Soup";
        recipe.id = 42;
        assertEquals("Soup", recipe.name);
        assertEquals(42, recipe.id);
    }
}
