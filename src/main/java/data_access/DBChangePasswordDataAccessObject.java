package data_access;

import entity.User;
import use_case.change_password.ChangePasswordUserDataAccessInterface;

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DBChangePasswordDataAccessObject implements ChangePasswordUserDataAccessInterface {
    private final String supabaseUrl = "https://heaxbbjincrnnimobhdc.supabase.co";
    private final String apiKey = System.getenv("SUPABASE_API_KEY");
    private final String tableName = "users";

    @Override
    public void changePassword(User user) {
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
            json.put("username", user.getUsername()); // primary key or unique constraint
            json.put("password", user.getPassword());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200 && responseCode != 201) {
                throw new RuntimeException("Failed to update password. HTTP code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}