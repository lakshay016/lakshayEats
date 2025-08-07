package use_case.friendRequest;

/**
 * Output boundary for friend request use case.
 * Defines the contract for presenting friend request results.
 */
public interface FriendRequestOutputBoundary {

    /**
     * Prepares and presents the result of a friend request operation.
     *
     * @param outputData Contains the result information
     */
    void presentFriendRequestResult(FriendRequestOutputData outputData);
}