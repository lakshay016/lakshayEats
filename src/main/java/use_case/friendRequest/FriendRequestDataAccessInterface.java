package use_case.friendRequest;

import entity.User;

import java.io.IOException;
import java.util.List;

public interface FriendRequestDataAccessInterface {

    User getUser(String username);

    void save(User user) throws IOException;

    boolean isFriend(String username1, String username2);

    boolean hasFriendRequest(String requesterUsername, String targetUsername);

    void sendFriendRequest(String requesterUsername, String targetUsername) throws IOException;

    void acceptFriendRequest(String requesterUsername, String targetUsername) throws IOException;

    void rejectFriendRequest(String requesterUsername, String targetUsername) throws IOException;

    void removeFriend(String username1, String username2) throws IOException;

    List<String> getFriendRequests(String username);

    List<String> getFriends(String username);
}