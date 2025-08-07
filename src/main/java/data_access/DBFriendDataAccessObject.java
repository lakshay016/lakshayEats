package data_access;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class DBFriendDataAccessObject {
    private final String supabaseUrl = "https://heaxbbjincrnnimobhdc.supabase.co";
    private final String apiKey = System.getenv("SUPABASE_API_KEY");
    private final String tableName = "friends";
    public void save(String username, Set<String> friends, Set<String> requests, Set<String> blocked) {
        try {
            URL url = new URL(supabaseUrl + "/rest/v1/" + tableName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("apikey", apiKey);
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");

            conn.setRequestProperty("Prefer", "resolution=merge-duplicates,return=representation");

            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("friends", new JSONArray(friends));
            json.put("requests", new JSONArray(requests));
            json.put("blocked", new JSONArray(blocked));

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 201 && responseCode != 200) {
                throw new IOException("Failed to upsert friend data. HTTP code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public JSONObject fetch(String username) throws IOException {
        String urlStr = supabaseUrl + "/rest/v1/" + tableName + "?username=eq." + username + "&select=*";
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", apiKey);
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) return null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        JSONArray array = new JSONArray(sb.toString());
        return array.length() > 0 ? array.getJSONObject(0) : null;
    }

    public Set<String> toSet(JSONArray array) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < array.length(); i++) {
            result.add(array.getString(i));
        }
        return result;
    }
}
