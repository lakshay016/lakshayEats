package entity.search;


import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Instructions;
import org.junit.Test;

import static org.junit.Assert.*;

public class InstructionsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void ctorAndAccessorsWork() {
        Instructions inst = new Instructions();
        inst.setNumber(1);
        inst.setStep("Preheat oven to 180C.");

        assertEquals(1, inst.getNumber());
        assertEquals("Preheat oven to 180C.", inst.getStep());
    }

    @Test
    public void equalsTrueWhenStepsMatchRegardlessOfNumber() {
        Instructions a = new Instructions();
        a.setNumber(1);
        a.setStep("Chop onions.");

        Instructions b = new Instructions();
        // different number
        b.setNumber(5);
        // same step
        b.setStep("Chop onions.");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void equalsFalseWhenStepsDiffer() {
        Instructions a = new Instructions();
        a.setNumber(1);
        a.setStep("Chop onions.");

        Instructions b = new Instructions();
        b.setNumber(1);
        // different step
        b.setStep("Fry onions.");

        assertNotEquals(a, b);
    }

    @Test
    public void jsonDeserializesNumberAndStep() throws Exception {
        String json = "{"
                + "\"number\":2,"
                + "\"step\":\"Mix flour and sugar.\""
                + "}";

        Instructions inst = MAPPER.readValue(json, Instructions.class);

        assertEquals(2, inst.getNumber());
        assertEquals("Mix flour and sugar.", inst.getStep());
    }

    @Test
    public void equalsIsReflexiveSymmetricAndConsistent() {
        Instructions inst = new Instructions();
        inst.setNumber(3);
        inst.setStep("Boil water.");

        // Reflexive
        assertEquals(inst, inst);

        // Symmetric
        Instructions inst2 = new Instructions();
        inst2.setNumber(99);
        inst2.setStep("Boil water.");
        assertEquals(inst, inst2);
        assertEquals(inst2, inst);

        // Consistent
        assertEquals(inst, inst2);
        assertEquals(inst, inst2);
    }

    @Test
    public void equalsFalseForNullOrDifferentClass() {
        Instructions inst = new Instructions();
        inst.setNumber(1);
        inst.setStep("Stir mixture.");

        assertNotEquals(inst, null);
        assertNotEquals(inst, "Not an Instructions object");
    }
}

