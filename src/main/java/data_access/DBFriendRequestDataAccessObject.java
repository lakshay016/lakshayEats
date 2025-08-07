package data_access;

import entity.User;
import use_case.friendRequest.FriendRequestDataAccessInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBFriendRequestDataAccessObject implements FriendRequestDataAccessInterface {
    private final String supabaseUrl = "https://heaxbbjincrnnimobhdc.supabase.co";
    private final String apiKey = System.getenv("SUPABASE_API_KEY");
    private final String tableName = "friends";
    private final DBUserDataAccessObject userDataAccess;
    private final DBFriendDataAccessObject friendDataAccess;

    public DBFriendRequestDataAccessObject(DBUserDataAccessObject userDataAccess) {
        this.userDataAccess = userDataAccess;
        this.friendDataAccess = new DBFriendDataAccessObject();
    }


    public User getUser(String username) {
        return userDataAccess.get(username);
    }


    public void save(User user) throws IOException {
        userDataAccess.save(user);
    }


    public boolean isFriend(String username1, String username2) {
        try {
            JSONObject data = friendDataAccess.fetch(username1);
            if (data != null) {
                Set<String> friends = friendDataAccess.toSet(data.getJSONArray("friends"));
                return friends.contains(username2);
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean hasFriendRequest(String requesterUsername, String targetUsername) {
        try {
            JSONObject data = friendDataAccess.fetch(targetUsername);
            if (data != null) {
                Set<String> requests = friendDataAccess.toSet(data.getJSONArray("requests"));
                return requests.contains(requesterUsername);
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
    public void sendFriendRequest(String requesterUsername, String targetUsername) throws IOException {
        JSONObject targetData = friendDataAccess.fetch(targetUsername);
        Set<String> requests = new HashSet<>();
        Set<String> friends = new HashSet<>();
        Set<String> blocked = new HashSet<>();
        if (targetData != null) {
            requests = friendDataAccess.toSet(targetData.getJSONArray("requests"));
            friends = friendDataAccess.toSet(targetData.getJSONArray("friends"));
            blocked = friendDataAccess.toSet(targetData.getJSONArray("blocked"));}
        requests.add(requesterUsername);
        friendDataAccess.save(targetUsername, friends, requests, blocked);
    }

    public void acceptFriendRequest(String rUsername, String tUsername) throws IOException {
        JSONObject rdata = friendDataAccess.fetch(rUsername);
        JSONObject tdata = friendDataAccess.fetch(tUsername);

        Set<String> rFriends = new HashSet<>();
        Set<String> rRequests = new HashSet<>();
        Set<String> rBlocked = new HashSet<>();
        Set<String> tFriends = new HashSet<>();
        Set<String> tRequests = new HashSet<>();
        Set<String> tBlocked = new HashSet<>();
        //Check for un-existing data
        if (rdata != null) {
            rFriends = friendDataAccess.toSet(rdata.getJSONArray("friends"));
            rRequests = friendDataAccess.toSet(rdata.getJSONArray("requests"));
            rBlocked = friendDataAccess.toSet(rdata.getJSONArray("blocked"));
        }
        if (tdata != null) {
            tFriends = friendDataAccess.toSet(tdata.getJSONArray("friends"));
            tRequests = friendDataAccess.toSet(tdata.getJSONArray("requests"));
            tBlocked = friendDataAccess.toSet(tdata.getJSONArray("blocked"));
        }
        // Process the acceptance
        tRequests.remove(rUsername);
        tFriends.add(rUsername);
        rFriends.add(tUsername);
        friendDataAccess.save(tUsername, tFriends, tRequests, tBlocked);
        friendDataAccess.save(rUsername, rFriends, rRequests, rBlocked);
    }

    public void rejectFriendRequest(String rUsername, String tUsername) throws IOException {
        JSONObject data = friendDataAccess.fetch(tUsername);
        Set<String> requests = new HashSet<>();
        Set<String> friends = new HashSet<>();
        Set<String> blocked = new HashSet<>();
        if (data != null) {
            requests = friendDataAccess.toSet(data.getJSONArray("requests"));
            friends = friendDataAccess.toSet(data.getJSONArray("friends"));
            blocked = friendDataAccess.toSet(data.getJSONArray("blocked"));}
        requests.remove(rUsername);
        friendDataAccess.save(tUsername, friends, requests, blocked);
    }

    public void removeFriend(String rUsername, String tUsername) throws IOException {
        JSONObject user1Data = friendDataAccess.fetch(rUsername);
        JSONObject user2Data = friendDataAccess.fetch(tUsername);
        Set<String> user1Friends = new HashSet<>();
        Set<String> user1Requests = new HashSet<>();
        Set<String> user1Blocked = new HashSet<>();
        Set<String> user2Friends = new HashSet<>();
        Set<String> user2Requests = new HashSet<>();
        Set<String> user2Blocked = new HashSet<>();
        //Check for non existent data
        if (user1Data != null) {
            user1Friends = friendDataAccess.toSet(user1Data.getJSONArray("friends"));
            user1Requests = friendDataAccess.toSet(user1Data.getJSONArray("requests"));
            user1Blocked = friendDataAccess.toSet(user1Data.getJSONArray("blocked"));
        }
        if (user2Data != null) {
            user2Friends = friendDataAccess.toSet(user2Data.getJSONArray("friends"));
            user2Requests = friendDataAccess.toSet(user2Data.getJSONArray("requests"));
            user2Blocked = friendDataAccess.toSet(user2Data.getJSONArray("blocked"));
        }
        // remove users from each others friends list
        user1Friends.remove(tUsername);
        user2Friends.remove(rUsername);
        friendDataAccess.save(rUsername, user1Friends, user1Requests, user1Blocked);
        friendDataAccess.save(tUsername, user2Friends, user2Requests, user2Blocked);
    }

    public List<String> getFriendRequests(String username) {
        try {
            JSONObject data = friendDataAccess.fetch(username);
            if (data != null) {
                Set<String> requests = friendDataAccess.toSet(data.getJSONArray("requests"));
                return new ArrayList<>(requests);
            }
            return new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    public List<String> getFriends(String username) {
        try {
            JSONObject data = friendDataAccess.fetch(username);
            if (data != null) {
                Set<String> friends = friendDataAccess.toSet(data.getJSONArray("friends"));
                return new ArrayList<>(friends);
            }
            return new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}