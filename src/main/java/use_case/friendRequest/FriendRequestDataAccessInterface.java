package use_case.friendRequest;

import entity.User;
import entity.Recipe;
import java.io.IOException;
import java.time.LocalDateTime;

public interface FriendRequestDataAccessInterface {
    User getUser(String username);
    void saveUser(User user) throws IOException;
    boolean areFriends(String username1, String username2);
    boolean hasFriendRequest(String requesterUsername, String targetUsername);
    void sendFriendRequest(String requesterUsername, String targetUsername) throws IOException;
    void acceptFriendRequest(String accepterUsername, String requesterUsername) throws IOException;
    void rejectFriendRequest(String rejecterUsername, String requesterUsername) throws IOException;
    void removeFriendship(String username1, String username2) throws IOException;
    boolean isBlocked(String blockerUsername, String blockedUsername);
    void blockUser(String blockerUsername, String blockedUsername) throws IOException;
    void unblockUser(String unblockerUsername, String unblockedUsername) throws IOException;
    void saveMessage(String sender, String receiver, String content, LocalDateTime sentAt) throws IOException;
    void saveRecipe(String sender, String receiver, Recipe recipe, LocalDateTime sentAt) throws IOException;
}