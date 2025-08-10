package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a single nutrient (e.g. Calories, Fat) with amount, unit, and daily percentage.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nutrition {
    private String name;
    private double amount;
    private String unit;
    private double percentOfDailyNeeds;

    public Nutrition() {
    }

    public Nutrition(String name, double amount, String unit, double dailyvalue) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.percentOfDailyNeeds = dailyvalue;
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

    @JsonProperty("amount")
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    @JsonProperty("unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPercentOfDailyNeeds() {
        return percentOfDailyNeeds;
    }

    @JsonProperty("percentOfDailyNeeds")
    public void setPercentOfDailyNeeds(double percentOfDailyNeeds) {
        this.percentOfDailyNeeds = percentOfDailyNeeds;
    }

    @Override
    public String toString() {
        return "Nutrition{" +
                "names=" + name +
                ", amount=" + amount +
                ", unit=" + unit +
                ", percentOfDailyNeed=" + percentOfDailyNeeds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nutrition)) return false;
        Nutrition that = (Nutrition) o;
        return Double.compare(that.amount, amount) == 0
                && Double.compare(that.percentOfDailyNeeds, percentOfDailyNeeds) == 0
                && Objects.equals(name, that.name)
                && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount, unit, percentOfDailyNeeds);
    }
}
