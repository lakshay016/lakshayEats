package data_access;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBMessageDataAccessObject {
    private final String supabaseUrl = "https://heaxbbjincrnnimobhdc.supabase.co";
    private final String apiKey = System.getenv("SUPABASE_API_KEY");
    private final String tableName = "messages";

    public void saveMessage(String sender, String receiver, String content, LocalDateTime sentAt) {
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
            json.put("sender", sender);
            json.put("receiver", receiver);
            json.put("content", content);
            json.put("sentAt", sentAt.toString());

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
                throw new IOException("Failed to save message. HTTP code: " + responseCode + "\n" + sb);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<JSONObject> fetchAllMessages() {
        List<JSONObject> messages = new ArrayList<>();
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
                throw new IOException("Failed to fetch messages. HTTP code: " + responseCode);
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
                messages.add(array.getJSONObject(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
