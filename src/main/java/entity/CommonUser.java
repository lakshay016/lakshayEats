package entity;
import app.LoginTestDriver;
import data_access.DBUserDataAccessObject;
import org.json.JSONException;
import org.json.JSONObject;
import use_case.login.LoginUserDataAccessInterface;
import data_access.DBFriendDataAccessObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class CommonUser implements User{
    private final String userName;
    private final String password;
    public ArrayList <Object>inbox;

    public CommonUser(String userName, String password) {
        this.userName = userName;
        this.password = password;

    }
    DBFriendDataAccessObject db = new DBFriendDataAccessObject();

    public Set<String> getFriends() throws IOException {
        String testUsername = this.userName;
        JSONObject json = db.fetch(testUsername);

        if (json == null) {
            System.out.println("No friend data found for username: " + testUsername);
        } else {
            System.out.println("Friend data found!");
            return db.toSet(json.getJSONArray("friends"));
        }
        return null;
    }
    Set<String> getRequests() throws IOException {
        String testUsername = this.userName;
        JSONObject json = db.fetch(testUsername);

        if (json == null) {
            System.out.println("No friend data found for username: " + testUsername);
        } else {
            System.out.println("Friend data found!");
            return db.toSet(json.getJSONArray("requests"));
        }
        return null;
    }
    Set<String> getBlocked() throws IOException{
        String testUsername = this.userName;
        JSONObject json = db.fetch(testUsername);

        if (json == null) {
            System.out.println("No friend data found for username: " + testUsername);
        } else {
            System.out.println("Friend data found!");
            return db.toSet(json.getJSONArray("blocked"));
        }
        return null;
    }


    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void sendMessage(CommonUser receiver, String message) {

    }

    public void sendRecipe(CommonUser receiver, Recipe recipe) {

    }


    public void addFriend(CommonUser friend) {

    }

    public void RequestFriend(CommonUser x) throws IOException {
        String testUsername = x.userName;
        JSONObject json = db.fetch(testUsername);

        if (json == null) {
            System.out.println("No friend data found for username: " + testUsername);
        } else {
            System.out.println("Friend data found!");
            Set<String> xfriends = db.toSet(json.getJSONArray("friends"));
            Set<String> xrequests = db.toSet(json.getJSONArray("requests"));
            Set<String> xblocked = db.toSet(json.getJSONArray("blocked"));

            if (xblocked.contains(this.userName)) {
                System.out.println("Error Processing Request");}
            else if (xrequests.contains(this.userName)) {
                System.out.println("Friend Request Has Already Been Sent!");}
            else if (xfriends.contains(this.userName)) {
                System.out.println("Already Friends with User!");}
            else {xrequests.add(this.userName);
                System.out.println("Request Sent!");
                db.save(x.userName,xfriends,xrequests,xblocked);
            }
        }
    }

    public void removeFriend(CommonUser y) throws IOException {
        String testUsername = y.userName;
        JSONObject json = db.fetch(testUsername);

        if (json == null) {
            System.out.println("No friend data found for username: " + testUsername);
        } else {
            System.out.println("Friend data found!");
            Set<String> yfriends = db.toSet(json.getJSONArray("friends"));
            Set<String> yrequests = db.toSet(json.getJSONArray("requests"));
            Set<String> yblocked = db.toSet(json.getJSONArray("blocked"));
            yfriends.remove(this.userName);
            Set<String> thisFriends = this.getFriends();
            Set<String> thisRequests = this.getRequests();
            Set<String> thisBlocked = this.getBlocked();
            this.getFriends().remove(y.userName);
            db.save(y.userName, yfriends, yrequests, yblocked);
            db.save(this.userName, thisFriends, thisRequests, thisBlocked);
        }


    }

    public void blockUser(CommonUser user) throws IOException {
        this.getBlocked().add(user.getUsername());
        this.getFriends().remove(user.getUsername());
        db.save(this.userName, this.getFriends(), this.getRequests(), this.getBlocked());

    }

    public void unblockUser(CommonUser friend) throws IOException {
        this.getBlocked().remove(friend.userName);
        db.save(this.userName, this.getFriends(), this.getRequests(), this.getBlocked());
    }

    public void acceptFriend(CommonUser friend) throws IOException {
        String testUsername = friend.userName;
        JSONObject json = db.fetch(testUsername);

        if (json == null) {
            System.out.println("No friend data found for username: " + testUsername);
        } else {
            System.out.println("Friend data found!");
            Set<String> ffriends = db.toSet(json.getJSONArray("friends"));
            Set<String> frequests = db.toSet(json.getJSONArray("requests"));
            Set<String> fblocked = db.toSet(json.getJSONArray("blocked"));
            ffriends.add(this.userName);
            this.getFriends().add(friend.userName);
            this.getRequests().remove(friend.userName);
            frequests.remove(this.userName);
            db.save(this.userName, this.getFriends(), this.getRequests(), this.getBlocked());
            db.save(friend.userName, ffriends, frequests, fblocked);
        }}

    public void rejectFriend(CommonUser friend) throws IOException {
        this.getRequests().remove(friend.userName);
        db.save(this.userName, this.getFriends(), this.getRequests(), this.getBlocked());

    }}

        /*
        TEST THE SHIT
        MAKE A SEARCH FUNCTION FOR USERS
        CLEAN ARCHITECTURE!!!
         */


