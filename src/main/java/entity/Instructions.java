package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a single step in the recipe instructions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Instructions {
    private int number;
    private String step;

    public Instructions() {
    }

    public Instructions(int number, String s) {
        this.number = number;
        this.step = s;
    }

    public int getNumber() {
        return number;
    }

    @JsonProperty("number")
    public void setNumber(int number) {
        this.number = number;
    }

    public String getStep() {
        return step;
    }

    @JsonProperty("step")
    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "Instructions{" +
                "steps=" + step +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instructions)) return false;
        Instructions that = (Instructions) o;
        return Objects.equals(step, that.step);
    }

    @Override
    public int hashCode() {
        return Objects.hash(step);
    }
}