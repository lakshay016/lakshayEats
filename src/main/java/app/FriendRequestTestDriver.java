package app;

import data_access.DBUserDataAccessObject;
import data_access.DBFriendDataAccessObject;
import data_access.DBFriendRequestDataAccessObject;
import entity.CommonUserFactory;
import entity.User;
import interface_adapter.FriendRequest.FriendRequestController;
import use_case.friendRequest.FriendRequestInteractor;
import use_case.friendRequest.FriendRequestOutputBoundary;
import use_case.friendRequest.FriendRequestOutputData;

import java.util.HashSet;
import java.util.Set;

public class FriendRequestTestDriver {
    public static void main(String[] args) {
        CommonUserFactory factory = new CommonUserFactory();
        DBUserDataAccessObject userDataAccess = new DBUserDataAccessObject(factory);
        DBFriendDataAccessObject friendDataAccess = new DBFriendDataAccessObject();
        DBFriendRequestDataAccessObject friendRequestDataAccess = new DBFriendRequestDataAccessObject(userDataAccess);
        User user1 = factory.createUser("user1", "password123");
        User user2 = factory.createUser("user2", "password123");
        try {
            userDataAccess.save(user1);
        } catch (Exception e) {
        }

        try {
            userDataAccess.save(user2);
        } catch (Exception e) {
        }

        Set<String> emptySet = new HashSet<>();
        try {
            friendDataAccess.save("user1", emptySet, emptySet, emptySet);
            friendDataAccess.save("user2", emptySet, emptySet, emptySet);
        } catch (Exception e) {
        }

        FriendRequestController controller = getFriendRequestController(friendRequestDataAccess);

        System.out.println("Testing Friend Request Use Case");
        System.out.println("==================================");

        System.out.println("Normal friend request");
        controller.sendFriendRequest("user1", "user2");

        System.out.println("Self-request (should fail due to usage error)");
        controller.sendFriendRequest("user1", "user1");

        System.out.println("Accept friend request");
        controller.acceptFriendRequest("user2", "user1");

        System.out.println("Try to send friend request to existing friend (should fail)");
        controller.sendFriendRequest("user1", "user2");

        System.out.println("Remove friendship");
        controller.removeFriend("user1", "user2");

        System.out.println("Send new friend request after removal");
        controller.sendFriendRequest("user1", "user2");

        System.out.println("Deny friend request");
        controller.denyFriendRequest("user2", "user1");

        System.out.println("Block user");
        controller.blockUser("user1", "user2");

        System.out.println("Try to send friend request to blocked user (should fail)");
        controller.sendFriendRequest("user1", "user2");

        System.out.println("Unblock user");
        controller.unblockUser("user1", "user2");

        System.out.println("Test 11: Try to send friend request after unblock");
        controller.sendFriendRequest("user1", "user2");
    }

    private static FriendRequestController getFriendRequestController(DBFriendRequestDataAccessObject friendRequestDataAccess) {
        FriendRequestOutputBoundary presenter = new FriendRequestOutputBoundary() {
            @Override
            public void presentFriendRequestResult(FriendRequestOutputData outputData) {
                if (outputData.isSuccess()) {
                    System.out.println("SUCCESS: " + outputData.getMessage());
                } else {
                    System.out.println("ERROR: " + outputData.getMessage());
                }
                System.out.println("Friend request from '" + outputData.getRequesterUsername() +
                        "' to '" + outputData.getTargetUsername() + "'");
                System.out.println("***********");
            }};
        FriendRequestInteractor interactor = new FriendRequestInteractor(presenter, friendRequestDataAccess);
        FriendRequestController x = new FriendRequestController(interactor);
        return x;
    }
}