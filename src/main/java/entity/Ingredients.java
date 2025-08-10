package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents an ingredient with its metric amount.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredients {
    private String name;
    private double amount;
    private String unit;

    public Ingredients() {
    }

    public Ingredients(String name, double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    @JsonProperty("metricValue")
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    @JsonProperty("metricUnit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", unit='" + unit + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredients)) return false;
        Ingredients that = (Ingredients) o;
        return Double.compare(that.amount, amount) == 0
                && Objects.equals(name, that.name)
                && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount, unit);
    }
}