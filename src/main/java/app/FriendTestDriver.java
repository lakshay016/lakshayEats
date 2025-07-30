package app;

import data_access.DBFriendDataAccessObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

public class FriendTestDriver {
    public static void main(String[] args) {
        DBFriendDataAccessObject db = new DBFriendDataAccessObject();

        String testUsername = "lakshay";
        try {
            JSONObject json = db.fetch(testUsername);

            if (json == null) {
                System.out.println("No friend data found for username: " + testUsername);
            } else {
                System.out.println("Friend data found!");
                Set<String> friends = db.toSet(json.getJSONArray("friends"));
                Set<String> requests = db.toSet(json.getJSONArray("requests"));
                Set<String> blocked = db.toSet(json.getJSONArray("blocked"));

                System.out.println("Friends: " + friends);
                System.out.println("Requests: " + requests);
                System.out.println("Blocked: " + blocked);
            }
        } catch (Exception e) {
            System.err.println("Error retrieving friend data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
