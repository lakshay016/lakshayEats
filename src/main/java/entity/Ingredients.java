package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an ingredient with its metric amount.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredients {
    private String name;
    private double amount;
    private String unit;

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
}