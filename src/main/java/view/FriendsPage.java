package view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import data_access.*;
import entity.SearchResult;
import interface_adapter.FriendRequest.FriendRequestController;
import interface_adapter.save.SaveController;
import interface_adapter.save.SavePresenter;
import interface_adapter.save.SaveViewModel;
import use_case.friendRequest.FriendRequestInteractor;
import use_case.friendRequest.FriendRequestOutputBoundary;
import use_case.friendRequest.FriendRequestOutputData;
import use_case.save.SaveDataAccessInterface;
import use_case.save.SaveInteractor;
import view.search.RecipeDetailDialog;

public class FriendsPage extends JPanel {
    private final String currentUser;
    private final SpoonacularAPIClient apiClient;
    private final SaveController saveController;
    private DBFriendRequestDataAccessObject friendDAO;
    private final DBMessageDataAccessObject messageDAO = new DBMessageDataAccessObject();
    private final DBUserDataAccessObject userDataAccessObject;

    public FriendsPage(String currentUser, DBUserDataAccessObject userDataAccessObject,
                       DBFriendRequestDataAccessObject friendDAO) {
        this.currentUser = currentUser;
        this.userDataAccessObject = userDataAccessObject;
        this.friendDAO = new DBFriendRequestDataAccessObject(userDataAccessObject);

        // Initialize API client and save controller
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null) {
            throw new RuntimeException("Missing SPOONACULAR_API_KEY");
        }
        this.apiClient = new SpoonacularAPIClient(apiKey);

        // Setup save controller for recipe interactions
        SaveViewModel saveVm = new SaveViewModel();
        SaveDataAccessInterface saveDao = new DBRecipeDataAccessObject();
        SavePresenter savePresenter = new SavePresenter(saveVm);
        this.saveController = new SaveController(new SaveInteractor(saveDao, savePresenter));
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

        // Add recipe opening handler
        fv.onOpenRecipe(recipeId -> {
            openRecipe(recipeId, fv);
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

    private void openRecipe(int recipeId, FriendsView fv) {
        new SwingWorker<SearchResult, Void>() {
            @Override
            protected SearchResult doInBackground() throws Exception {
                // Load recipe details from Spoonacular API
                List<Integer> recipeIds = new ArrayList<>();
                recipeIds.add(recipeId);
                List<SearchResult> recipes = apiClient.loadIDs(recipeIds);
                return recipes.isEmpty() ? null : recipes.get(0);
            }

            @Override
            protected void done() {
                try {
                    SearchResult recipe = get();
                    if (recipe != null) {
                        // Open recipe detail dialog
                        SwingUtilities.invokeLater(() -> {
                            Window owner = SwingUtilities.getWindowAncestor(FriendsPage.this);
                            RecipeDetailDialog dialog = new RecipeDetailDialog(
                                    owner, recipe, saveController, currentUser, null);
                            dialog.setVisible(true);
                        });
                    } else {
                        JOptionPane.showMessageDialog(FriendsPage.this,
                                "Could not load recipe details. The recipe may have been removed from Spoonacular.",
                                "Recipe Not Found", JOptionPane.WARNING_MESSAGE);}
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(FriendsPage.this,
                            "Error loading recipe: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
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