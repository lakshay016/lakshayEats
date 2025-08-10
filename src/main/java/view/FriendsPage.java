package view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import data_access.DBFriendDataAccessObject;
import data_access.DBMessageDataAccessObject;
import data_access.DBUserDataAccessObject;
import org.json.JSONObject;

public class FriendsPage extends JPanel {
    private final String currentUser;
    private final DBFriendDataAccessObject friendDAO = new DBFriendDataAccessObject();
    private final DBMessageDataAccessObject messageDAO = new DBMessageDataAccessObject();
    private final DBUserDataAccessObject userDataAccessObject;

    public FriendsPage(String currentUser, DBUserDataAccessObject userDataAccessObject) {
        this.currentUser = currentUser;
        this.userDataAccessObject = userDataAccessObject;
        setLayout(new BorderLayout());

        FriendsView fv = new FriendsView();
        add(fv, BorderLayout.CENTER);

        // Wire up the handlers
        fv.onFollow(friendId -> {
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    updateFriendRelationship(friendId, true);
                    return null;
                }
                @Override protected void done() { refreshFriends(fv); }
            }.execute();
        });

        fv.onUnfollow(friendId -> {
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    updateFriendRelationship(friendId, false);
                    return null;
                }
                @Override protected void done() { refreshFriends(fv); }
            }.execute();
        });

        fv.onLoadMessages(friendId -> {
            new SwingWorker<List<String>, Void>() {
                @Override protected List<String> doInBackground() {
                    List<String> out = new ArrayList<>();
                    for (org.json.JSONObject obj : messageDAO.fetchAllMessages()) {
                        String sender = obj.optString("sender", "");
                        String receiver = obj.optString("receiver", "");
                        if ((sender.equals(currentUser) && receiver.equals(friendId)) ||
                                (sender.equals(friendId) && receiver.equals(currentUser))) {
                            String content = obj.optString("content", "");
                            out.add(sender.equals(currentUser) ? "You: " + content : friendId + ": " + content);
                        }
                    }
                    return out;
                }
                @Override protected void done() {
                    try { fv.setMessages(get()); } catch (Exception ignore) {}
                }
            }.execute();
        });

        fv.onSendMessage((friendId, text) -> {
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() {
                    messageDAO.saveMessage(currentUser, friendId, text, LocalDateTime.now());
                    return null;
                }
            }.execute();
        });

        refreshFriends(fv);
    }

    private void refreshFriends(FriendsView fv) {
        new SwingWorker<List<FriendsView.FriendItem>, Void>() {
            @Override
            protected List<FriendsView.FriendItem> doInBackground() throws Exception {
                List<FriendsView.FriendItem> out = new ArrayList<>();
                List<String> allUsers = userDataAccessObject.getAllUsers();
                // Get all users
                //List<String> allUsers = userDataAccessObject.getAllUsers();

                // Get current user's friends and requests
                JSONObject json = friendDAO.fetch(currentUser);
                Set<String> friends = new HashSet<>();
                Set<String> requests = new HashSet<>();
                Set<String> sentRequests = new HashSet<>();

                if (json != null) {
                    if (json.has("friends")) friends.addAll(friendDAO.toSet(json.getJSONArray("friends")));
                    if (json.has("requests")) requests.addAll(friendDAO.toSet(json.getJSONArray("requests")));
                }

                // Check for sent requests (requests from current user to others)
                for (String user : allUsers) {
                    JSONObject userJson = friendDAO.fetch(user);
                    if (userJson != null && userJson.has("requests")) {
                        Set<String> userRequests = friendDAO.toSet(userJson.getJSONArray("requests"));
                        if (userRequests.contains(currentUser)) {
                            sentRequests.add(user);
                        }
                    }
                }

                // Create FriendItems for all users
                for (String user : allUsers) {
                    boolean isFriend = friends.contains(user);
                    boolean hasRequestFromMe = sentRequests.contains(user);
                    boolean hasRequestToMe = requests.contains(user);
                    out.add(new FriendsView.FriendItem(user, user, isFriend));


                }

                return out;
            }
            @Override
            protected void done() {
                try { fv.setFriends(get()); } catch (Exception ignore) {}
            }
        }.execute();
    }



    private void updateFriendRelationship(String friendId, boolean follow) throws Exception {
        JSONObject json = friendDAO.fetch(currentUser);
        java.util.Set<String> friends = new java.util.HashSet<>();
        java.util.Set<String> requests = new java.util.HashSet<>();
        java.util.Set<String> blocked = new java.util.HashSet<>();

        if (json != null) {
            if (json.has("friends")) friends.addAll(friendDAO.toSet(json.getJSONArray("friends")));
            if (json.has("requests")) requests.addAll(friendDAO.toSet(json.getJSONArray("requests")));
            if (json.has("blocked")) blocked.addAll(friendDAO.toSet(json.getJSONArray("blocked")));
        }
        if (follow) friends.add(friendId); else friends.remove(friendId);
        friendDAO.save(currentUser, friends, requests, blocked);
    }
}