package use_case.friendRequest;

/**
 * Input boundary for friend request use case.
 * Defines the contract for sending friend requests.
 */
public interface FriendRequestInputBoundary {

    /**
     * Sends a friend request from one user to another.
     *
     * @param inputData Contains the requester and target user information
     */
    void sendFriendRequest(FriendRequestInputData inputData);
}