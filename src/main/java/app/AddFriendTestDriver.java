package app;

import data_access.DBFriendDataAccessObject;
import org.json.JSONObject;
import entity.CommonUser;
import entity.CommonUserFactory;


import java.util.Set;

// TODO: DELETE
public class AddFriendTestDriver {
    public static void main(String[] args) {
        DBFriendDataAccessObject db = new DBFriendDataAccessObject();

        String user = "lakshay";
        String newFriend = "dylan";
        String blockedFriend = "Bobby";
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
            }
            if (blocked.contains(blockedFriend)) {
                System.out.println(blockedFriend + " is already blocked by " + user);
            }
            else {
                CommonUserFactory factory = new CommonUserFactory();
                CommonUser Lakshay = (CommonUser) factory.createUser("Lakshay", "1234");
                CommonUser Dylan = (CommonUser) factory.createUser("Dylan", "1234");
                CommonUser Bobby = (CommonUser) factory.createUser("Bobby", "1234");
                db.save("Lakshay",friends,requests,blocked);
                db.save("Dylan",friends,requests,blocked);
                db.save("Bobby",friends,requests,blocked);
                Lakshay.requestFriend(Dylan);
                Dylan.acceptFriend(Lakshay);
                db.save("Lakshay",friends,requests,blocked);
                db.save("Dylan",friends,requests,blocked);
                System.out.println(Lakshay.getFriends());
                System.out.println(Dylan.getFriends());
                db.save(user, friends, requests, blocked);
                System.out.println(blockedFriend + " added to " + user + " 's blocked list");
                System.out.println("Added " + newFriend + " to " + user + "'s friends");
            }

        } catch (Exception e) {
            System.err.println("Error updating friend data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
