package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an ingredient with its metric amount.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredients {
    private String name;
    private double metricValue;
    private String metricUnit;

    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public double getMetricValue() {
        return metricValue;
    }

    @JsonProperty("metricValue")
    public void setMetricValue(double metricValue) {
        this.metricValue = metricValue;
    }

    public String getMetricUnit() {
        return metricUnit;
    }

    @JsonProperty("metricUnit")
    public void setMetricUnit(String metricUnit) {
        this.metricUnit = metricUnit;
    }
}