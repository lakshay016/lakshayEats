package interface_adapter.FriendRequest;

import use_case.friendRequest.FriendRequestInputBoundary;
import use_case.friendRequest.FriendRequestInputData;
import entity.Recipe;

public class FriendRequestController {

    private final FriendRequestInputBoundary interactor;

    public FriendRequestController(FriendRequestInputBoundary friendRequestInteractor) {
        this.interactor = friendRequestInteractor;
    }
    public void sendFriendRequest(String requesterUsername, String targetUsername) {
        FriendRequestInputData inputData = new FriendRequestInputData(requesterUsername, targetUsername);
        interactor.sendFriendRequest(inputData);
    }
    public void removeFriend(String username1, String username2) {
        FriendRequestInputData inputData = new FriendRequestInputData(username1, username2);
        interactor.removeFriend(inputData);
    }
    public void acceptFriendRequest(String username1, String username2) {
        FriendRequestInputData inputData = new FriendRequestInputData(username1, username2);
        interactor.acceptFriendRequest(inputData);
    }
    public void denyFriendRequest(String username1, String username2) {
        FriendRequestInputData inputData = new FriendRequestInputData(username1, username2);
        interactor.denyFriendRequest(inputData);
    }
    public void blockUser(String username1, String username2) {
        FriendRequestInputData inputData = new FriendRequestInputData(username1, username2);
        interactor.blockUser(inputData);
    }
    public void unblockUser(String username1, String username2) {
        FriendRequestInputData inputData = new FriendRequestInputData(username1, username2);
        interactor.unblockUser(inputData);
    }
    public void sendMessage(String senderUsername, String receiverUsername, String messageContent) {
        FriendRequestInputData inputData = new FriendRequestInputData(senderUsername, receiverUsername, messageContent);
        interactor.sendMessage(inputData);
    }
    public void sendRecipe(String senderUsername, String receiverUsername, Recipe recipe) {
        FriendRequestInputData inputData = new FriendRequestInputData(senderUsername, receiverUsername, recipe);
        interactor.sendRecipe(inputData);
    }
}