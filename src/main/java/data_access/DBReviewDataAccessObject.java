package data_access;

import entity.Review;
import use_case.review.ReviewDataAccessInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBReviewDataAccessObject implements ReviewDataAccessInterface {
    private final String supabaseUrl = "https://heaxbbjincrnnimobhdc.supabase.co";
    private final String apiKey = System.getenv("SUPABASE_API_KEY");
    private final String tableName = "reviews";

    @Override
    public boolean save(Review review) {
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
            json.put("recipeId", review.getRecipeId());
            json.put("rating", review.getRating());
            json.put("author", review.getAuthor());
            json.put("text", review.getText());
            json.put("lastReviewedAt", review.getLastReviewedAt().toString());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 201) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                throw new IOException("Failed to save review. HTTP code: " + responseCode + "\n" + sb);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Review> fetchByRecipeId(String recipeId) {
        List<Review> reviews = new ArrayList<>();
        try {
            String urlStr = supabaseUrl + "/rest/v1/" + tableName + "?recipeId=eq." + recipeId + "&select=*";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", apiKey);
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Failed to fetch reviews. HTTP code: " + responseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONArray array = new JSONArray(sb.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int recipe = obj.getInt("recipeId");
                int rating = obj.getInt("rating");
                String author = obj.getString("author");
                String text = obj.getString("text");
                LocalDateTime lastReviewedAt = LocalDateTime.parse(obj.getString("lastReviewedAt"));

                reviews.add(new Review(recipe, rating, author, text, lastReviewedAt));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> fetchAll() {
        List<Review> reviews = new ArrayList<>();
        try {
            String urlStr = supabaseUrl + "/rest/v1/" + tableName + "?select=*";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", apiKey);
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Failed to fetch all reviews. HTTP code: " + responseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONArray array = new JSONArray(sb.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int recipe = obj.getInt("recipeId");
                int rating = obj.getInt("rating");
                String text = obj.getString("text");
                String author = obj.getString("author");
                LocalDateTime lastReviewedAt = LocalDateTime.parse(obj.getString("lastReviewedAt"));

                reviews.add(new Review(recipe, rating,author, text, lastReviewedAt ));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }

}
