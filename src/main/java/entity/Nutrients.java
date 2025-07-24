package entity;

public enum Nutrients {
    CARBS("carbs"),
    PROTEIN("protein"),
    CALORIES("calories"),
    FAT("fat"),
    ALCOHOL("alcohol"),
    CAFFEINE("caffeine"),
    COPPER("copper"),
    CALCIUM("calcium"),
    CHOLINE("choline"),
    CHOLESTEROL("cholesterol"),
    FLUORIDE("fluoride"),
    TOTAL_FAT("totalFat"),
    SATURATED_FAT("saturatedFat"),
    VITAMIN_A("vitaminA"),
    VITAMIN_C("vitaminC"),
    VITAMIN_D("vitaminD"),
    VITAMIN_E("vitaminE"),
    VITAMIN_K("vitaminK"),
    VITAMIN_B1("vitaminB1"),
    VITAMIN_B2("vitaminB2"),
    VITAMIN_B5("vitaminB5"),
    VITAMIN_B3("vitaminB3"),
    VITAMIN_B6("vitaminB6"),
    VITAMIN_B12("vitaminB12"),
    FIBER("fiber"),
    FOLATE("folate"),
    FOLIC_ACID("folicAcid"),
    IODINE("iodine"),
    IRON("iron"),
    MAGNESIUM("magnesium"),
    MANGANESE("manganese"),
    PHOSPHORUS("phosphorus"),
    POTASSIUM("potassium"),
    SELENIUM("selenium"),
    SODIUM("sodium"),
    SUGAR("sugar"),
    ZINC("zinc");

    private final String apiBaseName;

    Nutrients(String apiBaseName) {
        this.apiBaseName = apiBaseName;
    }

    public String minKey() {
        return "min" + capitalize(apiBaseName);
    }

    public String maxKey() {
        return "max" + capitalize(apiBaseName);
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
