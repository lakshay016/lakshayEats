package entity;

public class Range<T extends Comparable<T>> {
    private final T min;
    private final T max;

    public Range(T min, T max) {
        this.min = min;
        this.max = max;
    }
    public T getMin() { return min; }
    public T getMax() { return max; }
}
