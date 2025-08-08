package data_access;

import entity.User;
import entity.UserFactory;
import okhttp3.*;
import org.json.JSONException;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import okhttp3.RequestBody;

import org.json.JSONArray;
import org.json.JSONObject;
import use_case.signup.SignupUserDataAccessInterface;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class DBUserDataAccessObject implements SignupUserDataAccessInterface,
        LoginUserDataAccessInterface, ChangePasswordUserDataAccessInterface {
    private static final int SUCCESS_CODE = 200;
    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String STATUS_CODE_LABEL = "status_code";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String MESSAGE = "message";
    private final UserFactory factory;
    private final String supabaseUrl = "https://heaxbbjincrnnimobhdc.supabase.co";
    private final String apiKey = System.getenv("SUPABASE_API_KEY");
    private final String tableName = "users";
    private String currentUsername = null;
    public DBUserDataAccessObject(UserFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean existsByName(String username) {
        try {
            return fetchUser(username) != null;
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        try {
            String urlStr = supabaseUrl + "/rest/v1/" + tableName + "?select=username";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", apiKey);
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) return users;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONArray array = new JSONArray(sb.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject user = array.getJSONObject(i);
                String username = user.getString("username");
                if (!username.equals(currentUsername)) { // Don't show current user
                    users.add(username);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void save(User user) {
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
            json.put("username", user.getUsername());
            json.put("password", user.getPassword());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != 201) {
                throw new IOException("Failed to save user. HTTP code: " + conn.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User get(String username) {
        try {
            JSONObject userJson = fetchUser(username);
            if (userJson == null) return null;
            String password = userJson.getString("password");
            return factory.createUser(username, password);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject fetchUser(String username) throws IOException {
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

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public void changePassword(User user) {
        final OkHttpClient client = new OkHttpClient().newBuilder().build();

        final MediaType mediaType = MediaType.parse("application/json");

        final JSONObject requestBody = new JSONObject();
        requestBody.put("password", user.getPassword());

        final RequestBody body = RequestBody.create(mediaType, requestBody.toString());

        final HttpUrl url = HttpUrl.parse(supabaseUrl + "/rest/v1/" + "/users")
                .newBuilder()
                .addQueryParameter("username", "eq." + user.getUsername())
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .method("PATCH", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try {
            final Response response = client.newCall(request).execute();
            final String responseBodyStr = response.body().string();

            if (response.isSuccessful()) {
                System.out.println("Password changed successfully!");
            } else {
                System.err.println("Failed: " + responseBodyStr);
                throw new RuntimeException("Error: " + responseBodyStr);
            }
        } catch (IOException | JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
}
