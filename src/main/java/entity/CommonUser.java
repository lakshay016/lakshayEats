package entity;
import org.json.JSONObject;
import data_access.DBFriendDataAccessObject;
import data_access.DBMessageDataAccessObject;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

public class CommonUser implements User{
    private final String userName;
    private final String password;
    private Set<String> friends = new HashSet<>();
    private Set<String> requests = new HashSet<>();
    private Set<String> blocked = new HashSet<>();
    public Set <Object> inbox = new HashSet<>();
    DBFriendDataAccessObject db = new DBFriendDataAccessObject();

    public CommonUser(String userName, String password) {
        Set<String> tempFriends = new HashSet<>();
        Set<String> tempRequests = new HashSet<>();
        Set<String> tempBlocked = new HashSet<>();

        try {
            JSONObject data = db.fetch(userName);
            if (data != null) {
                tempFriends = db.toSet(data.getJSONArray("friends"));
                tempRequests = db.toSet(data.getJSONArray("requests"));
                tempBlocked = db.toSet(data.getJSONArray("blocked"));
            } else {
                System.out.println("No friend data found for username: " + userName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.friends = tempFriends;
        this.requests = tempRequests;
        this.blocked = tempBlocked;
        this.userName = userName;
        this.password = password;
    }

    public Set<String> getFriends() throws IOException {
        return Collections.unmodifiableSet(friends);
    }
    public Set<String> getRequests() throws IOException {
        return Collections.unmodifiableSet(requests);
    }
    public Set<String> getBlocked() throws IOException{
        return Collections.unmodifiableSet(blocked);
    }


    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void sendMessage(CommonUser receiver, String message) {
        if (this.friends.contains(receiver.getUsername())) {
            DBMessageDataAccessObject messageDb = new DBMessageDataAccessObject();
            messageDb.saveMessage(this.userName, receiver.getUsername(), message, LocalDateTime.now());
            System.out.println("Message sent to " + receiver.getUsername());
        } else {
            System.out.println("You can only send messages to your friends.");
        }
    }

    public void sendRecipe(CommonUser receiver, Recipe recipe) {
        if (this.friends.contains(receiver.getUsername())) {
            DBMessageDataAccessObject messageDb = new DBMessageDataAccessObject();
            String recipeContent = "RECIPE: " + recipe.name + " (ID: " + recipe.id + ")";
            messageDb.saveMessage(this.userName, receiver.getUsername(), recipeContent, LocalDateTime.now());
            System.out.println("Recipe '" + recipe.name + "' sent to " + receiver.getUsername());
        } else {
            System.out.println("You can only send recipes to your friends.");
        }
    }
    public void viewInbox() {
        DBMessageDataAccessObject messageDb = new DBMessageDataAccessObject();
        java.util.List<org.json.JSONObject> messages = messageDb.fetchAllMessages();
        System.out.println("Inbox for " + this.userName + ":");
        for (org.json.JSONObject message : messages) {
            if (message.getString("receiver").equals(this.userName)) {
                System.out.println("From " + message.getString("sender") + ": " +
                        message.getString("content") + " (Sent at " + message.getString("sentAt") + ")");
                System.out.println("**********");
            }
        }
    }

    public void requestFriend(CommonUser x) throws IOException {
        if (x instanceof CommonUser other) {
            // Do nothing if the other user already sent you a request (mutual)
            if (requests.contains(other.getUsername())) {
                acceptFriend(other);  // You both requested each other — auto-accept!
                return;
            }
            //Check for blocks
            if (this.blocked.contains(other.getUsername())) {
                return;
            }
            if (x.blocked.contains(this.getUsername())){
                return;}

            // Do nothing if already friends
            if (friends.contains(other.getUsername())) return;

            // Add this user to the other user’s request list
            other.requests.add(this.userName);

            // Save updated data for the other user
            db.save(other.userName, other.friends, other.requests, other.blocked);
            db.save(this.userName, this.friends, this.requests, this.blocked);
        }
    }

    public void removeFriend(CommonUser y) throws IOException {
            // If not already friends, do nothing
            if (!(friends.contains(y.getUsername()))) return;

            // Remove this user from the other user’s friend list
            // Remove the other user from this users friend list
            y.friends.remove(this.userName);
            this.friends.remove(y.getUsername());

            // Save updated data for the other user
            db.save(y.userName, y.friends, y.requests, y.blocked);
            db.save(this.userName, this.friends, this.requests, this.blocked);
        }


    public void blockUser(CommonUser user) throws IOException {
        // Remove them from friends list
        this.blocked.add(user.getUsername());
        // Remove friend requests (if any)
        this.removeFriend(user);
        db.save(user.userName, user.friends, user.requests, user.blocked);
        db.save(this.userName, this.friends, this.requests, this.blocked);

    }

    public void unblockUser(CommonUser friend) throws IOException {
        // if they aren't in the blocked list, do nothing
        if (!(blocked.contains(friend.getUsername()))) return;
        this.blocked.remove(friend.getUsername());
        db.save(friend.userName, friend.friends, friend.requests, friend.blocked);
        db.save(this.userName,this.friends,this.requests,this.blocked);
    }

    public void acceptFriend(CommonUser friend) throws IOException {
        if (!requests.contains(friend.userName)) {
            System.out.println(friend.userName + " did not send you a friend request.");
            return;
        }

        // Move from requests to friends
        requests.remove(friend.userName);
        this.friends.add(friend.userName);
        friend.friends.add(this.userName);

        // Update database
        db.save(userName, friends, requests, blocked);
        db.save(friend.userName, friend.friends, friend.requests, friend.blocked);

        System.out.println("You are now friends with " + friend.userName + "!");
    }

    public void rejectFriend(CommonUser friend) throws IOException {
        if (!requests.contains(friend.userName)) {
            System.out.println(friend.userName + " did not send you a friend request.");
            return;
        }

        requests.remove(friend.userName);

        // Update the database
        db.save(userName, friends, requests, blocked);
        db.save(friend.userName, friend.friends, friend.requests, friend.blocked);

    }

    public void setFriends(Set<String> friends) {
        this.friends = friends;
    }

    public void setRequests(Set<String> requests) {
        this.requests = requests;
    }

    public void setBlocked(Set<String> blocked) {
        this.blocked = blocked;
    }
}



