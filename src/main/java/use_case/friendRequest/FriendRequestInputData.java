package use_case.friendRequest;

/**
 * Input data for friend request use case.
 * Contains the information needed to send a friend request.
 */
public class FriendRequestInputData {

    private final String requesterUsername;
    private final String targetUsername;

    public FriendRequestInputData(String requesterUsername, String targetUsername) {
        this.requesterUsername = requesterUsername;
        this.targetUsername = targetUsername;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }
}