package view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import data_access.DBFriendRequestDataAccessObject;
import data_access.DBMessageDataAccessObject;
import data_access.DBUserDataAccessObject;
import interface_adapter.FriendRequest.FriendRequestController;
import use_case.friendRequest.FriendRequestInteractor;
import use_case.friendRequest.FriendRequestOutputBoundary;
import use_case.friendRequest.FriendRequestOutputData;

public class FriendsPage extends JPanel {
    private final String currentUser;
    private DBFriendRequestDataAccessObject friendDAO;
    private final DBMessageDataAccessObject messageDAO = new DBMessageDataAccessObject();
    private final DBUserDataAccessObject userDataAccessObject;

    public FriendsPage(String currentUser, DBUserDataAccessObject userDataAccessObject,
                       DBFriendRequestDataAccessObject friendDAO) {
        this.currentUser = currentUser;
        this.userDataAccessObject = userDataAccessObject;
        this.friendDAO = new DBFriendRequestDataAccessObject(userDataAccessObject);
        setLayout(new BorderLayout());

        FriendsView fv = new FriendsView();
        add(fv, BorderLayout.CENTER);

        DBFriendRequestDataAccessObject friendRequestDAO = new DBFriendRequestDataAccessObject(userDataAccessObject);
        FriendRequestController friendRequestController = new FriendRequestController(
                new FriendRequestInteractor(
                        new FriendRequestOutputBoundary() {
                            @Override
                            public void presentFriendRequestResult(FriendRequestOutputData outputData) {
                                if (outputData.isSuccess()) {
                                    JOptionPane.showMessageDialog(FriendsPage.this,
                                            outputData.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(FriendsPage.this,
                                            outputData.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        },
                        friendRequestDAO
                )
        );

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
        fv.onBlockFriend(friendId -> {
            System.out.println("Block friend handler called for: " + friendId);
            friendRequestController.blockUser(currentUser, friendId);
            refreshFriends(fv);
        });
        fv.onUnblockUser(blockedUsername -> {
            friendRequestController.unblockUser(currentUser, blockedUsername);
            refreshFriends(fv);
        });
        fv.onRemoveFriend(friendId -> {
            System.out.println("Remove friend handler called for: " + friendId);
            friendRequestController.removeFriend(currentUser, friendId);
            refreshFriends(fv);
        });

        fv.onAcceptFriendRequest(requesterUsername -> {
            friendRequestController.acceptFriendRequest(currentUser, requesterUsername);
            refreshFriends(fv);
        });

        fv.onRejectFriendRequest(requesterUsername -> {
            friendRequestController.denyFriendRequest(currentUser, requesterUsername);
            refreshFriends(fv);
        });

        fv.onSendFriendRequest(targetUsername -> {
            friendRequestController.sendFriendRequest(currentUser, targetUsername);
            refreshFriends(fv);
        });
        refreshFriends(fv);

    }

    private void refreshFriends(FriendsView fv) {
        new SwingWorker<List<FriendsView.FriendItem>, Void>() {
            @Override
            protected List<FriendsView.FriendItem> doInBackground() throws Exception {
                List<FriendsView.FriendItem> out = new ArrayList<>();
                List<String> allUsers = userDataAccessObject.getAllUsers();
                List<String> blockedUsers = new ArrayList<>();

                for (String user : allUsers) {
                    if (user.equals(currentUser)) {
                        continue;
                    }

                    boolean isFriend = friendDAO.areFriends(currentUser, user);
                    boolean isBlocked = friendDAO.isBlocked(currentUser, user);

                    if (isFriend) {
                        out.add(new FriendsView.FriendItem(user, user, isFriend));
                    }
                    if (isBlocked){
                        blockedUsers.add(user);
                    }
                }
                fv.setFriends(out);
                fv.setBlockedUsers(blockedUsers);
                return out;
            }

            @Override
            protected void done() {
                try {
                    fv.setFriends(get());
                    checkForIncomingRequests(fv);
                } catch (Exception ignore) {
                }
            }
        }.execute();
    }



    private void checkForIncomingRequests(FriendsView fv) {
        AtomicInteger a = new AtomicInteger();
        try {
            List<String> allUsers = userDataAccessObject.getAllUsers();
            for (String user : allUsers) {
                if (!user.equals(currentUser) && friendDAO.hasFriendRequest(user, currentUser)&& a.get() ==0) {
                    SwingUtilities.invokeLater(() -> {
                        fv.showFriendRequestPopup(user);
                        a.addAndGet(1);
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}