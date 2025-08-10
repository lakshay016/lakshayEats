package entity.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Nutrition;
import org.junit.Test;

import static org.junit.Assert.*;


public class NutritionTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void ctorAndAccessorsWork() {
        Nutrition n = new Nutrition();
        n.setName("Calories");
        n.setAmount(482.1);
        n.setUnit("kcal");
        n.setPercentOfDailyNeeds(24.1);

        assertEquals("Calories", n.getName());
        assertEquals(482.1, n.getAmount(), 1e-9);
        assertEquals("kcal", n.getUnit());
        assertEquals(24.1, n.getPercentOfDailyNeeds(), 1e-9);
    }

    @Test
    public void equalsAndHashCodeUseAllFields() {
        Nutrition a = new Nutrition("Fat", 6.74, "g", 10.4);
        Nutrition b = new Nutrition("Fat", 6.74, "g", 10.4);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        // Flip each field and ensure inequality
        assertNotEquals(new Nutrition("Protein", 6.74, "g", 10.4), a);    // name
        assertNotEquals(new Nutrition("Fat", 7.00, "g", 10.4), a);        // amount
        assertNotEquals(new Nutrition("Fat", 6.74, "mg", 10.4), a);       // unit
        assertNotEquals(new Nutrition("Fat", 6.74, "g", 9.9), a);         // percentOfDailyNeeds
    }

    @Test
    public void jsonDeserializesStandardFields() throws Exception {
        String json = "{"
                + "\"name\":\"Calcium\","
                + "\"amount\":130.0,"
                + "\"unit\":\"mg\","
                + "\"percentOfDailyNeeds\":10.0"
                + "}";

        Nutrition n = MAPPER.readValue(json, Nutrition.class);

        assertEquals("Calcium", n.getName());
        assertEquals(130.0, n.getAmount(), 1e-9);
        assertEquals("mg", n.getUnit());
        assertEquals(10.0, n.getPercentOfDailyNeeds(), 1e-9);
    }

    @Test
    public void jsonIgnoresUnknownFields() throws Exception {
        // allow null; mapper should default to 0.0 for double
        String json = "{"
                + "\"name\":\"Caffeine\","
                + "\"amount\":95.0,"
                + "\"unit\":\"mg\","
                + "\"percentOfDailyNeeds\":null,"
                + "\"someRandomField\":\"ignored\""
                + "}";

        Nutrition n = MAPPER.readValue(json, Nutrition.class);

        assertEquals("Caffeine", n.getName());
        assertEquals(95.0, n.getAmount(), 1e-9);
        assertEquals("mg", n.getUnit());
        // null becomes 0.0 on deserialize.
        assertEquals(0.0, n.getPercentOfDailyNeeds(), 1e-9);
    }

}
