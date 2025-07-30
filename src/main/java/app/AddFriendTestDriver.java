package app;

import data_access.DBFriendDataAccessObject;
import org.json.JSONObject;


import java.util.Set;

public class AddFriendTestDriver {
    public static void main(String[] args) {
        DBFriendDataAccessObject db = new DBFriendDataAccessObject();

        String user = "lakshay";
        String newFriend = "dylan";

        try {
            JSONObject json = db.fetch(user);

            if (json == null) {
                System.out.println("No friend data found for " + user);
                return;
            }

            Set<String> friends = db.toSet(json.getJSONArray("friends"));
            Set<String> requests = db.toSet(json.getJSONArray("requests"));
            Set<String> blocked = db.toSet(json.getJSONArray("blocked"));

            if (friends.contains(newFriend)) {
                System.out.println(newFriend + " is already a friend of " + user);
            } else {
                friends.add(newFriend);
                db.save(user, friends, requests, blocked);
                System.out.println("Added " + newFriend + " to " + user + "'s friends");
            }

        } catch (Exception e) {
            System.err.println("Error updating friend data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
