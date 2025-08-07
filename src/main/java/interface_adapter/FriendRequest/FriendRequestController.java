package interface_adapter.FriendRequest;

import use_case.friendRequest.FriendRequestInputBoundary;
import use_case.friendRequest.FriendRequestInputData;

/**
 * Controller for friend request functionality.
 * Handles user input and delegates to the appropriate use case.
 */
public class FriendRequestController {

    private final FriendRequestInputBoundary interactor;

    public FriendRequestController(FriendRequestInputBoundary friendRequestInteractor) {
        this.interactor = friendRequestInteractor;
    }

    /**
     * Sends a friend request from one user to another.
     *
     * @param requesterUsername The username of the person sending the request
     * @param targetUsername The username of the person receiving the request
     */
    public void sendFriendRequest(String requesterUsername, String targetUsername) {
        FriendRequestInputData inputData = new FriendRequestInputData(requesterUsername, targetUsername);
        interactor.sendFriendRequest(inputData);
    }
}