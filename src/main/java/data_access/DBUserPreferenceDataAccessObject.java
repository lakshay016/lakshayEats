package data_access;

import entity.Preferences;
import org.json.JSONObject;
import use_case.preferences.PreferencesDataAccessInterface;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DBUserPreferenceDataAccessObject implements PreferencesDataAccessInterface {
    // Keep these constants in the DAO
    private static final String[] DIETS = {
            "Gluten Free", "Ketogenic", "Vegetarian", "Lacto-Vegetarian",
            "Ovo-vegetarian", "Vegan", "Pescetarian", "Paleo", "Primal",
            "Low FODMAP", "Whole30"
    };
    private static final String[] INTOLERANCES = {
            "Dairy", "Egg", "Gluten", "Grain", "Peanut",
            "Seafood", "Sesame", "Shellfish", "Soy", "Sulfite",
            "Tree Nut", "Wheat"
    };


    private final String supabaseUrl = "https://heaxbbjincrnnimobhdc.supabase.co";
    private final String apiKey = System.getenv("SUPABASE_API_KEY");
    private final String tableName = "user_preferences";


    @Override
    public void savePreferences(String username, Preferences preferences) {
        JSONObject restrictionsJson = new JSONObject();
        for (Map.Entry<String, Integer> entry : preferences.getDiets().entrySet()) {
            restrictionsJson.put(entry.getKey(), entry.getValue());
        }

        JSONObject intolerancesJson = new JSONObject();
        for (Map.Entry<String, Integer> entry : preferences.getIntolerances().entrySet()) {
            intolerancesJson.put(entry.getKey(), entry.getValue());
        }

        savePreferencesToDB(username, restrictionsJson, intolerancesJson);
    }


    public void savePreferencesToDB(String username, JSONObject restrictions, JSONObject intolerances) {
        try {
            String urlStr = supabaseUrl + "/rest/v1/" + tableName;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("apikey", apiKey);
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "resolution=merge-duplicates,return=representation");
            conn.setDoOutput(true);

            JSONObject payload = new JSONObject();
            payload.put("username", username);
            payload.put("preferences", restrictions);
            payload.put("intolerances", intolerances);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200 && responseCode != 201) {
                throw new IOException("Failed to save restrictions. HTTP code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Preferences getPreferences(String username) {
        JSONObject combined = fetchRestrictionsAndIntolerances(username);
        if (combined == null) {
            return new Preferences(new HashMap<>(), new HashMap<>());
        }

        Map<String, Integer> dietsMap = new HashMap<>();
        Map<String, Integer> intolerancesMap = new HashMap<>();

        // Fill diets map
        for (String diet : DIETS) {
            int value = combined.optInt(diet, 0);
            dietsMap.put(diet, value);
        }

        // Fill intolerances map
        for (String intolerance : INTOLERANCES) {
            int value = combined.optInt(intolerance, 0);
            intolerancesMap.put(intolerance, value);
        }

        return new Preferences(dietsMap, intolerancesMap);
    }


    public JSONObject fetchRestrictionsAndIntolerances(String username) {
        try {
            String urlStr = supabaseUrl + "/rest/v1/" + tableName + "?username=eq." + username + "&select=*";
            URL url = new URL(urlStr);
            System.out.println("Fetching for username: " + username);
            System.out.println("Request URL: " + urlStr);
            System.out.println("API key: " + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", apiKey);
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Accept", "application/json");
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) return null;
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//            reader.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            System.out.println("Raw Supabase response: " + sb);

            return new org.json.JSONArray(sb.toString()).getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
