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
        // Create dependencies
        CommonUserFactory factory = new CommonUserFactory();
        DBUserDataAccessObject userDataAccess = new DBUserDataAccessObject(factory);
        DBFriendDataAccessObject friendDataAccess = new DBFriendDataAccessObject();
        DBFriendRequestDataAccessObject friendRequestDataAccess = new DBFriendRequestDataAccessObject(userDataAccess);

        // Create test users with friend data
        System.out.println("🧪 Setting up test users with friend data...");

        // Create users
        User user1 = factory.createUser("user1", "password123");
        User user2 = factory.createUser("user2", "password123");
        User alice = factory.createUser("alice", "password123");
        User bob = factory.createUser("bob", "password123");

        try {
            // Save users
            userDataAccess.save(user1);
            userDataAccess.save(user2);
            userDataAccess.save(alice);
            userDataAccess.save(bob);

            // Set up friend data for each user
            Set<String> emptySet = new HashSet<>();

            // user1: no friends, no requests, no blocked
            friendDataAccess.save("user1", emptySet, emptySet, emptySet);

            // user2: no friends, no requests, no blocked
            friendDataAccess.save("user2", emptySet, emptySet, emptySet);

            // alice: no friends, no requests, no blocked
            friendDataAccess.save("alice", emptySet, emptySet, emptySet);

            // bob: no friends, no requests, no blocked
            friendDataAccess.save("bob", emptySet, emptySet, emptySet);

            System.out.println("✅ Test users created with friend data!");

        } catch (Exception e) {
            System.out.println("⚠️ Some users may already exist, continuing with tests...");
        }

        // Create a clean console presenter
        FriendRequestOutputBoundary presenter = new FriendRequestOutputBoundary() {
            @Override
            public void presentFriendRequestResult(FriendRequestOutputData outputData) {
                if (outputData.isSuccess()) {
                    System.out.println("✅ SUCCESS: " + outputData.getMessage());
                } else {
                    System.out.println("❌ ERROR: " + outputData.getMessage());
                }
                System.out.println("Friend request from '" + outputData.getRequesterUsername() +
                        "' to '" + outputData.getTargetUsername() + "'");
                System.out.println("─────────────────────────────────────────────");
            }
        };

        // Create interactor
        FriendRequestInteractor interactor = new FriendRequestInteractor(presenter, friendRequestDataAccess);

        // Create controller
        FriendRequestController controller = new FriendRequestController(interactor);

        // Test scenarios
        System.out.println("\n🧪 Testing Friend Request Use Case");
        System.out.println("==================================");

        // Test 1: Normal friend request
        System.out.println("\n📝 Test 1: Normal friend request");
        controller.sendFriendRequest("user1", "user2");

        // Test 2: Self-request (should fail)
        System.out.println("\n📝 Test 2: Self-request (should fail)");
        controller.sendFriendRequest("user1", "user1");

        // Test 3: Non-existent user (should fail)
        System.out.println("\n📝 Test 3: Non-existent user (should fail)");
        controller.sendFriendRequest("user1", "nonexistentuser");

        // Test 4: Another normal request
        System.out.println("\n📝 Test 4: Another normal request");
        controller.sendFriendRequest("alice", "bob");

        // Test 5: Duplicate request (should fail)
        System.out.println("\n📝 Test 5: Duplicate request (should fail)");
        controller.sendFriendRequest("user1", "user2");

        System.out.println("\n🏁 Testing complete!");
    }
}