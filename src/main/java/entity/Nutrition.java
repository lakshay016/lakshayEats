package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single nutrient (e.g. Calories, Fat) with amount, unit, and daily percentage.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nutrition {
    private String name;
    private double amount;
    private String unit;
    private double percentOfDailyNeeds;

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
}
