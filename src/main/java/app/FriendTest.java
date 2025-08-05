package app;

import entity.CommonUser;
import data_access.DBFriendDataAccessObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendTest {
    public static void main(String[] args) {
        DBFriendDataAccessObject db = new DBFriendDataAccessObject();

        try {
            String username1 = "Tiana";
            String username2 = "Dylan";
            String username3 = "Bobby";
            String username4 = "Jenny";
            String username5 = "Patrick";
            String username6 = "Mario";

            // Step 1: Create in DB if not already there
            db.save(username1, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
            db.save(username2,Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
            db.save(username3,Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
            db.save(username4,Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
            db.save(username5,Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
            db.save(username6,Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

            // Step 2: Fetch DB data
            JSONObject data1 = db.fetch(username1);
            JSONObject data2 = db.fetch(username2);
            JSONObject data3 = db.fetch(username3);
            JSONObject data4 = db.fetch(username4);
            JSONObject data5 = db.fetch(username5);
            JSONObject data6 = db.fetch(username6);

            // Step 3: Create users
            CommonUser user1 = new CommonUser(username1, "1234");
            CommonUser user2 = new CommonUser(username2, "1234");
            CommonUser user3 = new CommonUser(username3, "1234");
            CommonUser user4 = new CommonUser(username4, "1234");
            CommonUser user5 = new CommonUser(username5, "1234");
            CommonUser user6 = new CommonUser(username6, "1234");

            // Step 4: Inject DB data into user objects
            user1.setFriends(db.toSet(data1.getJSONArray("friends")));
            user1.setRequests(db.toSet(data1.getJSONArray("requests")));
            user1.setBlocked(db.toSet(data1.getJSONArray("blocked")));

            user2.setFriends(db.toSet(data2.getJSONArray("friends")));
            user2.setRequests(db.toSet(data2.getJSONArray("requests")));
            user2.setBlocked(db.toSet(data2.getJSONArray("blocked")));

            user3.setFriends(db.toSet(data3.getJSONArray("friends")));
            user3.setRequests(db.toSet(data3.getJSONArray("requests")));
            user3.setBlocked(db.toSet(data3.getJSONArray("blocked")));

            user4.setFriends(db.toSet(data4.getJSONArray("friends")));
            user4.setRequests(db.toSet(data4.getJSONArray("requests")));
            user4.setBlocked(db.toSet(data4.getJSONArray("blocked")));

            user5.setFriends(db.toSet(data5.getJSONArray("friends")));
            user5.setRequests(db.toSet(data5.getJSONArray("requests")));
            user5.setBlocked(db.toSet(data5.getJSONArray("blocked")));

            user6.setFriends(db.toSet(data6.getJSONArray("friends")));
            user6.setRequests(db.toSet(data6.getJSONArray("requests")));
            user6.setBlocked(db.toSet(data6.getJSONArray("blocked")));


            // Step 5: Friend operations
            user1.requestFriend(user2);
            user2.acceptFriend(user1);
            user3.requestFriend(user1);
            user1.requestFriend(user3);
            // Tiana and Dylan are friends
            // User Tiana and Bobby are friends via auto-accept

            user4.requestFriend(user1);
            user1.rejectFriend(user4);
            // Tiana rejected Jenny's friend request

            user5.requestFriend(user2);
            // Patricks request for Dylan is pending

            user1.removeFriend(user3);
            //Tiana removed Bobby

            user6.requestFriend(user2);
            user2.acceptFriend(user6);

            //Dylan and Mario are friends

            user2.blockUser(user6);
            // Dylan blocked Mario

            user2.unblockUser(user6);
            // Dylan unblocked Mario
            user2.sendMessage(user1, "Hi Tiana");


            // Step 6: Output
            System.out.println("Username: " + user1.getUsername());
            System.out.println("Friends: " + user1.getFriends());
            System.out.println("Requests: " + user1.getRequests());
            System.out.println("Blocked: " + user1.getBlocked());
            user1.viewInbox();
            System.out.println("*******");

            System.out.println("Username: " + user2.getUsername());
            System.out.println("Friends: " + user2.getFriends());
            System.out.println("Requests: " + user2.getRequests());
            System.out.println("Blocked: " + user2.getBlocked());
            System.out.println("*******");

            System.out.println("Username: " + user3.getUsername());
            System.out.println("Friends: " + user3.getFriends());
            System.out.println("Requests: " + user3.getRequests());
            System.out.println("Blocked: " + user3.getBlocked());
            System.out.println("*******");

            System.out.println("Username: " + user4.getUsername());
            System.out.println("Friends: " + user4.getFriends());
            System.out.println("Requests: " + user4.getRequests());
            System.out.println("Blocked: " + user4.getBlocked());
            System.out.println("*******");

            System.out.println("Username: " + user5.getUsername());
            System.out.println("Friends: " + user5.getFriends());
            System.out.println("Requests: " + user5.getRequests());
            System.out.println("Blocked: " + user5.getBlocked());
            System.out.println("*******");

            System.out.println("Username: " + user6.getUsername());
            System.out.println("Friends: " + user6.getFriends());
            System.out.println("Requests: " + user6.getRequests());
            System.out.println("Blocked: " + user6.getBlocked());
            System.out.println("*******");

        } catch (IOException e) {
            System.err.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
