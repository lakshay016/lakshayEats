package data_access;

import entity.Recipe;

import org.json.JSONArray;
import org.json.JSONObject;
import use_case.RecipeEdit.RecipeDataAccessInterface;

import java.io.*;
import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
import java.util.Arrays;

public class DBRecipeDataAccessObject implements RecipeDataAccessInterface {
    private final String supabaseUrl = "https://heaxbbjincrnnimobhdc.supabase.co";
    private final String apiKey = System.getenv("SUPABASE_API_KEY");
    private final String tableName = "recipes";

    @Override
    public void save(Recipe recipe) {
        try {
            URL url = new URL(supabaseUrl + "/rest/v1/" + tableName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("apikey", apiKey);
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "return=representation");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();

            json.put("id", recipe.getId());
            json.put("title", recipe.getName());
            json.put("ingredients", recipe.getIngredients());
            json.put("instructions", recipe.getSteps());
            json.put("created_by", recipe.getAuthorId());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            if (conn.getResponseCode() != 201) {
                throw new IOException("Failed to save recipe. HTTP code: " + conn.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void edit(Recipe recipe) {
        try {
            URL url = new URL(supabaseUrl + "/rest/v1/" + tableName + "?id=eq." + recipe.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("apikey", apiKey);
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "return=representation");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("title", recipe.getName());
            json.put("ingredients", recipe.getIngredients());
            json.put("instructions", recipe.getSteps());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            if (conn.getResponseCode() != 200) {
                throw new IOException("Failed to edit recipe. HTTP code: " + conn.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Recipe getRecipe(int id) throws IOException {
        URL url = new URL(supabaseUrl + "/rest/v1/" + tableName + "?id=eq." + id);
        // maybe I should add more filter to url
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", apiKey);
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) return null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        JSONArray array = new JSONArray(sb.toString());
        if (array.length() == 0) return null;

        JSONObject obj = array.getJSONObject(0);
        //null?
        String name = obj.getString("name");
        JSONArray ingredientsJson = obj.getJSONArray("ingredients");
        String steps = obj.getString("steps");
        String authorId = obj.getString("authorId");
        String s = String.valueOf(id);

        return new Recipe(
                s,
                name,
                // why is there a checkstyle warning here
                Arrays.asList(ingredientsJson.toList().toArray(new String[0])),
                steps,
                authorId
        );

    }
}
