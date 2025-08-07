package data_access;

import entity.User;
import entity.Recipe;
import use_case.friendRequest.FriendRequestDataAccessInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBFriendRequestDataAccessObject implements FriendRequestDataAccessInterface {
    private final String supabaseUrl = "https://heaxbbjincrnnimobhdc.supabase.co";
    private final String apiKey = System.getenv("SUPABASE_API_KEY");
    private final String tableName = "friends";
    private final DBUserDataAccessObject userDataAccess;

    public DBFriendRequestDataAccessObject(DBUserDataAccessObject userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    @Override
    public User getUser(String username) {
        return userDataAccess.get(username);
    }

    @Override
    public void saveUser(User user) throws IOException {
        userDataAccess.save(user);
    }

    @Override
    public boolean areFriends(String username1, String username2) {
        try {
            JSONObject data = fetch(username1);
            if (data != null) {
                Set<String> friends = toSet(data.getJSONArray("friends"));
                return friends.contains(username2);
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean hasFriendRequest(String requesterUsername, String targetUsername) {
        try {
            JSONObject data = fetch(targetUsername);
            if (data != null) {
                Set<String> requests = toSet(data.getJSONArray("requests"));
                return requests.contains(requesterUsername);
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void sendFriendRequest(String requesterUsername, String targetUsername) throws IOException {
        JSONObject targetData = fetch(targetUsername);
        Set<String> requests = new HashSet<>();
        Set<String> friends = new HashSet<>();
        Set<String> blocked = new HashSet<>();
        if (targetData != null) {
            requests = toSet(targetData.getJSONArray("requests"));
            friends = toSet(targetData.getJSONArray("friends"));
            blocked = toSet(targetData.getJSONArray("blocked"));
        }
        requests.add(requesterUsername);
        save(targetUsername, friends, requests, blocked);
    }

    @Override
    public void acceptFriendRequest(String rUsername, String tUsername) throws IOException {
        JSONObject rdata = fetch(rUsername);
        JSONObject tdata = fetch(tUsername);

        Set<String> rFriends = new HashSet<>();
        Set<String> rRequests = new HashSet<>();
        Set<String> rBlocked = new HashSet<>();
        Set<String> tFriends = new HashSet<>();
        Set<String> tRequests = new HashSet<>();
        Set<String> tBlocked = new HashSet<>();

        if (rdata != null) {
            rFriends = toSet(rdata.getJSONArray("friends"));
            rRequests = toSet(rdata.getJSONArray("requests"));
            rBlocked = toSet(rdata.getJSONArray("blocked"));
        }
        if (tdata != null) {
            tFriends = toSet(tdata.getJSONArray("friends"));
            tRequests = toSet(tdata.getJSONArray("requests"));
            tBlocked = toSet(tdata.getJSONArray("blocked"));
        }

        tRequests.remove(rUsername);
        tFriends.add(rUsername);
        rFriends.add(tUsername);
        save(tUsername, tFriends, tRequests, tBlocked);
        save(rUsername, rFriends, rRequests, rBlocked);
    }

    @Override
    public void rejectFriendRequest(String rUsername, String tUsername) throws IOException {
        JSONObject data = fetch(tUsername);
        Set<String> requests = new HashSet<>();
        Set<String> friends = new HashSet<>();
        Set<String> blocked = new HashSet<>();
        if (data != null) {
            requests = toSet(data.getJSONArray("requests"));
            friends = toSet(data.getJSONArray("friends"));
            blocked = toSet(data.getJSONArray("blocked"));
        }
        requests.remove(rUsername);
        save(tUsername, friends, requests, blocked);
    }

    @Override
    public void removeFriendship(String username1, String username2) throws IOException {
        JSONObject user1Data = fetch(username1);
        JSONObject user2Data = fetch(username2);
        Set<String> user1Friends = new HashSet<>();
        Set<String> user1Requests = new HashSet<>();
        Set<String> user1Blocked = new HashSet<>();
        Set<String> user2Friends = new HashSet<>();
        Set<String> user2Requests = new HashSet<>();
        Set<String> user2Blocked = new HashSet<>();

        if (user1Data != null) {
            user1Friends = toSet(user1Data.getJSONArray("friends"));
            user1Requests = toSet(user1Data.getJSONArray("requests"));
            user1Blocked = toSet(user1Data.getJSONArray("blocked"));
        }
        if (user2Data != null) {
            user2Friends = toSet(user2Data.getJSONArray("friends"));
            user2Requests = toSet(user2Data.getJSONArray("requests"));
            user2Blocked = toSet(user2Data.getJSONArray("blocked"));
        }

        user1Friends.remove(username2);
        user2Friends.remove(username1);
        save(username1, user1Friends, user1Requests, user1Blocked);
        save(username2, user2Friends, user2Requests, user2Blocked);
    }

    @Override
    public boolean isBlocked(String blockerUsername, String blockedUsername) {
        try {
            JSONObject data = fetch(blockerUsername);
            if (data != null) {
                Set<String> blocked = toSet(data.getJSONArray("blocked"));
                return blocked.contains(blockedUsername);
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void blockUser(String blockerUsername, String blockedUsername) throws IOException {
        JSONObject blockerData = fetch(blockerUsername);
        Set<String> blockerFriends = new HashSet<>();
        Set<String> blockerRequests = new HashSet<>();
        Set<String> blockerBlocked = new HashSet<>();

        if (blockerData != null) {
            blockerFriends = toSet(blockerData.getJSONArray("friends"));
            blockerRequests = toSet(blockerData.getJSONArray("requests"));
            blockerBlocked = toSet(blockerData.getJSONArray("blocked"));
        }

        blockerBlocked.add(blockedUsername);
        blockerFriends.remove(blockedUsername);
        blockerRequests.remove(blockedUsername);

        save(blockerUsername, blockerFriends, blockerRequests, blockerBlocked);
    }

    @Override
    public void unblockUser(String unblockerUsername, String unblockedUsername) throws IOException {
        JSONObject unblockerData = fetch(unblockerUsername);
        Set<String> unblockerFriends = new HashSet<>();
        Set<String> unblockerRequests = new HashSet<>();
        Set<String> unblockerBlocked = new HashSet<>();

        if (unblockerData != null) {
            unblockerFriends = toSet(unblockerData.getJSONArray("friends"));
            unblockerRequests = toSet(unblockerData.getJSONArray("requests"));
            unblockerBlocked = toSet(unblockerData.getJSONArray("blocked"));
        }

        unblockerBlocked.remove(unblockedUsername);

        save(unblockerUsername, unblockerFriends, unblockerRequests, unblockerBlocked);
    }

    @Override
    public void saveMessage(String sender, String receiver, String content, LocalDateTime sentAt) throws IOException {
        try {
            URL url = new URL(supabaseUrl + "/rest/v1/messages");
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
                throw new IOException("Failed to save message. HTTP code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveRecipe(String sender, String receiver, Recipe recipe, LocalDateTime sentAt) throws IOException {
        try {
            URL url = new URL(supabaseUrl + "/rest/v1/messages");
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
            json.put("content", "RECIPE: " + recipe.name + " (ID: " + recipe.id + ")");
            json.put("sentAt", sentAt.toString());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 201) {
                throw new IOException("Failed to save recipe. HTTP code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save(String username, Set<String> friends, Set<String> requests, Set<String> blocked) throws IOException {
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

    private JSONObject fetch(String username) throws IOException {
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

    private Set<String> toSet(JSONArray array) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < array.length(); i++) {
            result.add(array.getString(i));
        }
        return result;
    }
}