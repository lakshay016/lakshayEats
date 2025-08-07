package use_case.friendRequest;

import entity.User;
import use_case.friendRequest.FriendRequestDataAccessInterface;

import java.io.IOException;

public class FriendRequestInteractor implements FriendRequestInputBoundary {

    private final FriendRequestOutputBoundary outputBoundary;
    private final FriendRequestDataAccessInterface userDataAccess;

    public FriendRequestInteractor(FriendRequestOutputBoundary outputBoundary,
                                   FriendRequestDataAccessInterface userDataAccess) {
        this.outputBoundary = outputBoundary;
        this.userDataAccess = userDataAccess;
    }

    @Override
    public void sendFriendRequest(FriendRequestInputData inputData) {
        String requesterUsername = inputData.getRequesterUsername();
        String targetUsername = inputData.getTargetUsername();

        try {
            User requester = userDataAccess.getUser(requesterUsername);
            User target = userDataAccess.getUser(targetUsername);

            if (requester == null || target == null) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "One or both users not found",
                                requesterUsername, targetUsername)
                );
                return;}
            if (requesterUsername.equals(targetUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "Cannot send friend request to yourself",
                                requesterUsername, targetUsername)
                );
                return;}
            if (userDataAccess.isFriend(requesterUsername, targetUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "Users are already friends",
                                requesterUsername, targetUsername)
                );
                return;}
            if (userDataAccess.hasFriendRequest(requesterUsername, targetUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "Friend request already sent",
                                requesterUsername, targetUsername)
                );
                return;}
            userDataAccess.sendFriendRequest(requesterUsername, targetUsername);

            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(true, "Friend request sent successfully",
                            requesterUsername, targetUsername)
            );} catch (IOException e) {
            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(false, "Error processing friend request!: " + e.getMessage(),
                            requesterUsername, targetUsername)
            );
        }
    }
}