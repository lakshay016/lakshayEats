package use_case.friendRequest;
public interface FriendRequestInputBoundary {
    void sendFriendRequest(FriendRequestInputData inputData);
    void acceptFriendRequest(FriendRequestInputData inputData);
    void denyFriendRequest(FriendRequestInputData inputData);
    void removeFriend(FriendRequestInputData inputData);
    void blockUser(FriendRequestInputData inputData);
    void unblockUser(FriendRequestInputData inputData);
    void sendMessage(FriendRequestInputData inputData);
    void sendRecipe(FriendRequestInputData inputData);
}