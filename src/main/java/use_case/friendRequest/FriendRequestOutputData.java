package use_case.friendRequest;

/**
 * Output data for friend request use case.
 * Contains the result information after processing a friend request.
 */
public class FriendRequestOutputData {

    private final boolean success;
    private final String message;
    private final String requesterUsername;
    private final String targetUsername;

    public FriendRequestOutputData(boolean success, String message,
                                   String requesterUsername, String targetUsername) {
        this.success = success;
        this.message = message;
        this.requesterUsername = requesterUsername;
        this.targetUsername = targetUsername;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }
}