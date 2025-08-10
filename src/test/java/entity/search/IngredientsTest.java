package entity.search;


import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Ingredients;
import org.junit.Test;

import static org.junit.Assert.*;

public class IngredientsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void ctorAndAccessorsWork() {
        Ingredients ing = new Ingredients();
        ing.setName("Olive Oil");
        ing.setAmount(2.0);
        ing.setUnit("tbsp");

        assertEquals("Olive Oil", ing.getName());
        assertEquals(2.0, ing.getAmount(), 1e-9);
        assertEquals("tbsp", ing.getUnit());
    }

    @Test
    public void equalsAndHashCodeMatchForSameValues() {
        Ingredients a = new Ingredients();
        a.setName("Salt");
        a.setAmount(1.0);
        a.setUnit("tsp");

        Ingredients b = new Ingredients();
        b.setName("Salt");
        b.setAmount(1.0);
        b.setUnit("tsp");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void equalsReturnsFalseForDifferentValues() {
        Ingredients base = new Ingredients();
        base.setName("Sugar");
        base.setAmount(1.0);
        base.setUnit("cup");

        Ingredients diffName = new Ingredients();
        diffName.setName("Salt");
        diffName.setAmount(1.0);
        diffName.setUnit("cup");
        assertNotEquals(base, diffName);

        Ingredients diffAmount = new Ingredients();
        diffAmount.setName("Sugar");
        diffAmount.setAmount(2.0);
        diffAmount.setUnit("cup");
        assertNotEquals(base, diffAmount);

        Ingredients diffUnit = new Ingredients();
        diffUnit.setName("Sugar");
        diffUnit.setAmount(1.0);
        diffUnit.setUnit("tbsp");
        assertNotEquals(base, diffUnit);
    }

    @Test
    public void jsonDeserializesMetricFields() throws Exception {
        String json = "{"
                + "\"name\":\"Flour\","
                + "\"metricValue\":500.0,"
                + "\"metricUnit\":\"g\""
                + "}";

        Ingredients ing = MAPPER.readValue(json, Ingredients.class);

        assertEquals("Flour", ing.getName());
        assertEquals(500.0, ing.getAmount(), 1e-9);
        assertEquals("g", ing.getUnit());
    }
}
