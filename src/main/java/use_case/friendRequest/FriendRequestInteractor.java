package use_case.friendRequest;

import entity.User;
import entity.Recipe;
import use_case.friendRequest.FriendRequestDataAccessInterface;
import java.io.IOException;
import java.time.LocalDateTime;

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
            if (userDataAccess.areFriends(requesterUsername, targetUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "Users are already friends",
                                requesterUsername, targetUsername)
                );
                return;}
            if (userDataAccess.isBlocked(requesterUsername, targetUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "Cannot send friend request to blocked user",
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

    public void removeFriend(FriendRequestInputData inputData) {
        String username1 = inputData.getRequesterUsername();
        String username2 = inputData.getTargetUsername();

        try {
            User user1 = userDataAccess.getUser(username1);
            User user2 = userDataAccess.getUser(username2);

            if (user1 == null || user2 == null) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "One or both users not found",
                                username1, username2)
                );
                return;
            }

            if (!userDataAccess.areFriends(username1, username2)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "Users are not friends",
                                username1, username2)
                );
                return;
            }

            userDataAccess.removeFriendship(username1, username2);

            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(true, "Friendship removed successfully",
                            username1, username2)
            );

        } catch (IOException e) {
            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(false, "Error removing friendship: " + e.getMessage(),
                            username1, username2)
            );
        }
    }

    public void acceptFriendRequest(FriendRequestInputData inputData) {
        String accepterUsername = inputData.getRequesterUsername();
        String requesterUsername = inputData.getTargetUsername();

        try {
            User accepter = userDataAccess.getUser(accepterUsername);
            User requester = userDataAccess.getUser(requesterUsername);

            if (accepter == null || requester == null) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "One or both users not found",
                                accepterUsername, requesterUsername)
                );
                return;
            }

            if (!userDataAccess.hasFriendRequest(requesterUsername, accepterUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "No friend request found",
                                accepterUsername, requesterUsername)
                );
                return;
            }

            userDataAccess.acceptFriendRequest(requesterUsername, accepterUsername);

            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(true, "Friend request accepted successfully",
                            accepterUsername, requesterUsername)
            );

        } catch (IOException e) {
            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(false, "Error accepting friend request: " + e.getMessage(),
                            accepterUsername, requesterUsername)
            );
        }
    }

    public void denyFriendRequest(FriendRequestInputData inputData) {
        String denierUsername = inputData.getRequesterUsername();
        String requesterUsername = inputData.getTargetUsername();

        try {
            User denier = userDataAccess.getUser(denierUsername);
            User requester = userDataAccess.getUser(requesterUsername);

            if (denier == null || requester == null) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "One or both users not found",
                                denierUsername, requesterUsername)
                );
                return;
            }
            if (!userDataAccess.hasFriendRequest(requesterUsername, denierUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "No friend request found",
                                denierUsername, requesterUsername)
                );
                return;
            }
            userDataAccess.rejectFriendRequest(requesterUsername, denierUsername);

            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(true, "Friend request denied successfully",
                            denierUsername, requesterUsername)
            );
        } catch (IOException e) {
            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(false, "Error denying friend request: " + e.getMessage(),
                            denierUsername, requesterUsername)
            );
        }
    }

    public void blockUser(FriendRequestInputData inputData) {
        String blockerUsername = inputData.getRequesterUsername();
        String blockedUsername = inputData.getTargetUsername();

        try {
            User blocker = userDataAccess.getUser(blockerUsername);
            User blocked = userDataAccess.getUser(blockedUsername);

            if (blocker == null || blocked == null) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "One or both users not found",
                                blockerUsername, blockedUsername)
                );
                return;
            }

            if (blockerUsername.equals(blockedUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "Cannot block yourself",
                                blockerUsername, blockedUsername)
                );
                return;
            }

            if (userDataAccess.isBlocked(blockerUsername, blockedUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "User is already blocked",
                                blockerUsername, blockedUsername)
                );
                return;
            }

            userDataAccess.blockUser(blockerUsername, blockedUsername);

            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(true, "User blocked successfully",
                            blockerUsername, blockedUsername)
            );

        } catch (IOException e) {
            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(false, "Error blocking user: " + e.getMessage(),
                            blockerUsername, blockedUsername)
            );
        }
    }

    public void unblockUser(FriendRequestInputData inputData) {
        String unblockerUsername = inputData.getRequesterUsername();
        String unblockedUsername = inputData.getTargetUsername();
        try {
            User unblocker = userDataAccess.getUser(unblockerUsername);
            User unblocked = userDataAccess.getUser(unblockedUsername);

            if (unblocker == null || unblocked == null) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "One or both users not found",
                                unblockerUsername, unblockedUsername)
                );
                return;
            }
            if (unblockerUsername.equals(unblockedUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "Cannot unblock yourself",
                                unblockerUsername, unblockedUsername)
                );
                return;
            }
            if (!userDataAccess.isBlocked(unblockerUsername, unblockedUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "User is not blocked",
                                unblockerUsername, unblockedUsername)
                );
                return;
            }
            userDataAccess.unblockUser(unblockerUsername, unblockedUsername);

            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(true, "User unblocked successfully",
                            unblockerUsername, unblockedUsername)
            );
        } catch (IOException e) {
            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(false, "Error unblocking user: " + e.getMessage(),
                            unblockerUsername, unblockedUsername)
            );
        }
    }

    @Override
    public void sendMessage(FriendRequestInputData inputData) {
        String senderUsername = inputData.getRequesterUsername();
        String receiverUsername = inputData.getTargetUsername();
        String messageContent = inputData.getMessageContent();

        try {
            User sender = userDataAccess.getUser(senderUsername);
            User receiver = userDataAccess.getUser(receiverUsername);

            if (sender == null || receiver == null) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "One or both users not found",
                                senderUsername, receiverUsername, messageContent)
                );
                return;
            }

            if (senderUsername.equals(receiverUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "Cannot send message to yourself",
                                senderUsername, receiverUsername, messageContent)
                );
                return;
            }

            if (!userDataAccess.areFriends(senderUsername, receiverUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "You can only send messages to your friends",
                                senderUsername, receiverUsername, messageContent)
                );
                return;
            }

            userDataAccess.saveMessage(senderUsername, receiverUsername, messageContent, LocalDateTime.now());

            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(true, "Message sent successfully",
                            senderUsername, receiverUsername, messageContent)
            );
        } catch (IOException e) {
            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(false, "Error sending message: " + e.getMessage(),
                            senderUsername, receiverUsername, messageContent)
            );
        }
    }

    @Override
    public void sendRecipe(FriendRequestInputData inputData) {
        String senderUsername = inputData.getRequesterUsername();
        String receiverUsername = inputData.getTargetUsername();
        Recipe recipe = inputData.getRecipe();

        try {
            User sender = userDataAccess.getUser(senderUsername);
            User receiver = userDataAccess.getUser(receiverUsername);

            if (sender == null || receiver == null) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "One or both users not found",
                                senderUsername, receiverUsername, recipe)
                );
                return;
            }

            if (senderUsername.equals(receiverUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "Cannot send recipe to yourself",
                                senderUsername, receiverUsername, recipe)
                );
                return;
            }

            if (!userDataAccess.areFriends(senderUsername, receiverUsername)) {
                outputBoundary.presentFriendRequestResult(
                        new FriendRequestOutputData(false, "You can only send recipes to your friends",
                                senderUsername, receiverUsername, recipe)
                );
                return;
            }

            userDataAccess.saveRecipe(senderUsername, receiverUsername, recipe, LocalDateTime.now());

            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(true, "Recipe sent successfully",
                            senderUsername, receiverUsername, recipe)
            );
        } catch (IOException e) {
            outputBoundary.presentFriendRequestResult(
                    new FriendRequestOutputData(false, "Error sending recipe: " + e.getMessage(),
                            senderUsername, receiverUsername, recipe)
            );
        }
    }
}