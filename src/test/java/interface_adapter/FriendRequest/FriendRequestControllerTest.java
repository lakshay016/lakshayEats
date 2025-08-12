import entity.Recipe;
import interface_adapter.FriendRequest.FriendRequestController;
import org.junit.Test;
import use_case.friendRequest.FriendRequestInputBoundary;
import use_case.friendRequest.FriendRequestInputData;
import static org.junit.Assert.*;

public class FriendRequestControllerTest {
    private static class StubInteractor implements FriendRequestInputBoundary {
        String lastMethod;
        FriendRequestInputData data;
        @Override public void sendFriendRequest(FriendRequestInputData inputData){lastMethod="send";data=inputData;}
        @Override public void acceptFriendRequest(FriendRequestInputData inputData){lastMethod="accept";data=inputData;}
        @Override public void denyFriendRequest(FriendRequestInputData inputData){lastMethod="deny";data=inputData;}
        @Override public void removeFriend(FriendRequestInputData inputData){lastMethod="remove";data=inputData;}
        @Override public void blockUser(FriendRequestInputData inputData){lastMethod="block";data=inputData;}
        @Override public void unblockUser(FriendRequestInputData inputData){lastMethod="unblock";data=inputData;}
        @Override public void sendMessage(FriendRequestInputData inputData){lastMethod="message";data=inputData;}
        @Override public void sendRecipe(FriendRequestInputData inputData){lastMethod="recipe";data=inputData;}
    }

    @Test
    public void testSendFriendRequest() {
        StubInteractor interactor = new StubInteractor();
        FriendRequestController controller = new FriendRequestController(interactor);
        controller.sendFriendRequest("a","b");
        assertEquals("send", interactor.lastMethod);
        assertEquals("a", interactor.data.getRequesterUsername());
        assertEquals("b", interactor.data.getTargetUsername());
    }

    @Test
    public void testBlockUser() {
        StubInteractor interactor = new StubInteractor();
        FriendRequestController controller = new FriendRequestController(interactor);
        controller.blockUser("u1","u2");
        assertEquals("block", interactor.lastMethod);
    }

    @Test
    public void testSendMessage() {
        StubInteractor interactor = new StubInteractor();
        FriendRequestController controller = new FriendRequestController(interactor);
        controller.sendMessage("a","b","hi");
        assertEquals("message", interactor.lastMethod);
        assertEquals("hi", interactor.data.getMessageContent());
    }

    @Test
    public void testSendRecipe() {
        StubInteractor interactor = new StubInteractor();
        FriendRequestController controller = new FriendRequestController(interactor);
        Recipe recipe = new Recipe();
        controller.sendRecipe("a","b", recipe);
        assertEquals("recipe", interactor.lastMethod);
        assertEquals(recipe, interactor.data.getRecipe());
    }

    @Test
    public void testRemoveFriend() {
        StubInteractor interactor = new StubInteractor();
        FriendRequestController controller = new FriendRequestController(interactor);
        controller.removeFriend("a","b");
        assertEquals("remove", interactor.lastMethod);
    }

    @Test
    public void testAcceptFriendRequest() {
        StubInteractor interactor = new StubInteractor();
        FriendRequestController controller = new FriendRequestController(interactor);
        controller.acceptFriendRequest("a","b");
        assertEquals("accept", interactor.lastMethod);
    }

    @Test
    public void testDenyFriendRequest() {
        StubInteractor interactor = new StubInteractor();
        FriendRequestController controller = new FriendRequestController(interactor);
        controller.denyFriendRequest("a","b");
        assertEquals("deny", interactor.lastMethod);
    }

    @Test
    public void testUnblockUser() {
        StubInteractor interactor = new StubInteractor();
        FriendRequestController controller = new FriendRequestController(interactor);
        controller.unblockUser("a","b");
        assertEquals("unblock", interactor.lastMethod);
    }
}
