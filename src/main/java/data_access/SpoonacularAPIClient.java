package data_access;

import entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import use_case.search.SearchDataAccessInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpoonacularAPIClient implements SearchDataAccessInterface {
    private static final String COMPLEX_SEARCH_URL = "https://api.spoonacular.com/recipes/complexSearch";
    private final String apiKey;
    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public SpoonacularAPIClient(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public List<SearchResult> searchByDish(String query, FilterOptions opts) throws IOException {
        // 1) Build URL with all non-null params
        HttpUrl.Builder ub = HttpUrl.parse(COMPLEX_SEARCH_URL).newBuilder()
                .addQueryParameter("apiKey", apiKey)
                .addQueryParameter("query", query);

        // a) primitive filters
        if (opts.getCuisines() != null) ub.addQueryParameter("cuisine", String.join(",", opts.getCuisines()));
        if (opts.getExcludeCuisines() != null)
            ub.addQueryParameter("excludeCuisine", String.join(",", opts.getExcludeCuisines()));
        if (opts.getDiets() != null) ub.addQueryParameter("diet", String.join(",", opts.getDiets()));
        if (opts.getIntolerances() != null)
            ub.addQueryParameter("intolerances", String.join(",", opts.getIntolerances()));
        if (opts.getIncludeIngredients() != null)
            ub.addQueryParameter("includeIngredients", String.join(",", opts.getIncludeIngredients()));
        if (opts.getExcludeIngredients() != null)
            ub.addQueryParameter("excludeIngredients", String.join(",", opts.getExcludeIngredients()));
        if (opts.getType() != null) ub.addQueryParameter("type", opts.getType());
        if (opts.getMaxReadyTime() != null) ub.addQueryParameter("maxReadyTime", opts.getMaxReadyTime().toString());
        if (opts.getMinServings() != null) ub.addQueryParameter("minServings", opts.getMinServings().toString());
        if (opts.getMaxServings() != null) ub.addQueryParameter("maxServings", opts.getMaxServings().toString());
        if (opts.getSort() != null) ub.addQueryParameter("sort", opts.getSort());
        if (opts.getSortDirection() != null) ub.addQueryParameter("sortDirection", opts.getSortDirection());
        // b) flags
        ub.addQueryParameter("instructionsRequired", opts.getInstructionsRequired().toString());
        ub.addQueryParameter("fillIngredients", opts.getFillIngredients().toString());
        ub.addQueryParameter("addRecipeInformation", opts.getAddRecipeInformation().toString());
        ub.addQueryParameter("addRecipeInstructions", opts.getAddRecipeInstructions().toString());
        ub.addQueryParameter("addRecipeNutrition", opts.getAddRecipeNutrition().toString());
        // c) pagination
        if (opts.getOffset() != null) ub.addQueryParameter("offset", opts.getOffset().toString());
        if (opts.getNumber() != null) ub.addQueryParameter("number", opts.getNumber().toString());
        // d) nutrient ranges
        for (Map.Entry<Nutrients, Range<Double>> e : opts.getAllNutrientRanges().entrySet()) {
            Range<Double> r = e.getValue();
            if (r.getMin() != null) ub.addQueryParameter(e.getKey().minKey(), String.valueOf(r.getMin()));
            if (r.getMax() != null) ub.addQueryParameter(e.getKey().maxKey(), String.valueOf(r.getMax()));
        }
        Request req = new Request.Builder()
                .url(ub.build())
                .get()
                .build();

        // 2) Execute
        try (Response resp = http.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                throw new IOException("Unexpected code " + resp);
            }

            // 3) Parse the JSON “results” array
            JsonNode root = mapper.readTree(resp.body().string());
            JsonNode arr = root.get("results");
            List<SearchResult> results = new ArrayList<>();
            for (JsonNode node : arr) {
                // ignore unknown props thanks to @JsonIgnoreProperties
                SearchResult sr = mapper.treeToValue(node, SearchResult.class);

                // 3a) Instructions
                sr.setInstructions(extractInstructions(node.path("analyzedInstructions")));

                // 3b) Nutrition & weight‐per‐serving
                JsonNode nut = node.path("nutrition");
                sr.setNutrition(extractNutrition(nut.path("nutrients")));
                sr.setWeightPerServing(nut.path("weightPerServing").path("amount").asInt());

                // 3c) Ingredient metrics
                sr.setIngredients(extractIngredients(nut.path("ingredients")));

                results.add(sr);
            }
            return results;
        }
    }
    
    private List<Instructions> extractInstructions(JsonNode arr) throws IOException {
        List<Instructions> out = new ArrayList<>();
        for (JsonNode group : arr) {
            // Spoonacular nests steps under each instruction-group
            for (JsonNode stepNode : group.path("steps")) {
                Instructions step = mapper.treeToValue(stepNode, Instructions.class);
                out.add(step);
            }
        }
        return out;
    }

    private List<Nutrition> extractNutrition(JsonNode nutrientsArr) {
        List<Nutrition> out = new ArrayList<>();
        for (JsonNode n : nutrientsArr) {
            Nutrition item = new Nutrition();
            item.setName(n.path("name").asText());
            item.setAmount(n.path("amount").asDouble());
            item.setUnit(n.path("unit").asText());
            item.setPercentOfDailyNeeds(n.path("percentOfDailyNeeds").asDouble());
            out.add(item);
        }
        return out;
    }

    private List<Ingredients> extractIngredients(JsonNode ingrArr) {
        List<Ingredients> out = new ArrayList<>();
        for (JsonNode i : ingrArr) {
            Ingredients im = new Ingredients();
            im.setName(i.path("name").asText());
            JsonNode m = i.path("amount").path("metric");
            im.setMetricValue(m.path("value").asDouble());
            im.setMetricUnit (m.path("unit").asText());
            out.add(im);
        }
        return out;
    }
}
