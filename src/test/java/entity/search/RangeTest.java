package entity.search;

import entity.Range;
import org.junit.Test;

import static org.junit.Assert.*;


public class RangeTest {

    @Test
    public void createsRangeOfIntegers_gettersReturnValues() {
        Range<Integer> r = new Range<>(1, 10);
        assertEquals(Integer.valueOf(1), r.getMin());
        assertEquals(Integer.valueOf(10), r.getMax());
    }

    @Test
    public void createsRangeOfDoubles_gettersReturnValues() {
        Range<Double> r = new Range<>(-3.5, 7.25);
        assertEquals(Double.valueOf(-3.5), r.getMin());
        assertEquals(Double.valueOf(7.25), r.getMax());
    }

    @Test
    public void createsRangeOfStrings_lexicographicOrderIsPreserved() {
        Range<String> r = new Range<>("apple", "banana");
        assertEquals("apple", r.getMin());
        assertEquals("banana", r.getMax());
        // sanity: "apple" < "banana" lexicographically
        assertTrue(r.getMin().compareTo(r.getMax()) < 0);
    }

    @Test
    public void isImmutable_noSettersPresent() {
        // This test is more of an API guard: it just asserts current behavior.
        // If someone adds setters in the future, this test should be updated/removed.
        Range<Integer> r = new Range<>(5, 6);
        assertEquals(Integer.valueOf(5), r.getMin());
        assertEquals(Integer.valueOf(6), r.getMax());
    }

    @Test
    public void worksWithCustomComparableType() {
        Name a = new Name("Ann");
        Name b = new Name("Bob");
        Range<Name> r = new Range<>(a, b);

        assertSame(a, r.getMin());
        assertSame(b, r.getMax());
        // Comparable contract: Ann < Bob
        assertTrue(r.getMin().compareTo(r.getMax()) < 0);
    }

    /** Minimal custom Comparable to verify generic constraint works as expected. */
    private static final class Name implements Comparable<Name> {
        private final String value;
        private Name(String value) { this.value = value; }
        @Override public int compareTo(Name o) { return this.value.compareTo(o.value); }
        @Override public String toString() { return value; }
    }
}
