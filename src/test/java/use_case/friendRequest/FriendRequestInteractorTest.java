package use_case.friendRequest;

import entity.Recipe;
import entity.User;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class FriendRequestInteractorTest {
    private static class SimpleUser implements User {
        private final String name; private final String pwd;
        SimpleUser(String name, String pwd) { this.name = name; this.pwd = pwd; }
        public String getUsername() { return name; }
        public String getPassword() { return pwd; }
    }
    private static class InMemoryDAO implements FriendRequestDataAccessInterface {
        Map<String, User> users = new HashMap<>();
        Map<String, Set<String>> friends = new HashMap<>();
        Set<String> requests = new HashSet<>();
        Set<String> blocks = new HashSet<>();
        List<String> messages = new ArrayList<>();
        @Override public User getUser(String username) { return users.get(username); }
        @Override public void saveUser(User user) { users.put(user.getUsername(), user); }
        @Override public boolean areFriends(String u1, String u2) { return friends.getOrDefault(u1, new HashSet<>()).contains(u2); }
        @Override public boolean hasFriendRequest(String r, String t) { return requests.contains(r+"->"+t); }
        @Override public void sendFriendRequest(String r, String t) { requests.add(r+"->"+t); }
        @Override public void acceptFriendRequest(String a, String r) { requests.remove(r+"->"+a); friends.computeIfAbsent(a,k->new HashSet<>()).add(r); friends.computeIfAbsent(r,k->new HashSet<>()).add(a); }
        @Override public void rejectFriendRequest(String r, String rej) { requests.remove(r+"->"+rej); }
        @Override public void removeFriendship(String u1, String u2) { friends.getOrDefault(u1,new HashSet<>()).remove(u2); friends.getOrDefault(u2,new HashSet<>()).remove(u1); }
        @Override public boolean isBlocked(String b, String bl) { return blocks.contains(b+"->"+bl); }
        @Override public void blockUser(String b, String bl) { blocks.add(b+"->"+bl); }
        @Override public void unblockUser(String b, String bl) { blocks.remove(b+"->"+bl); }
        @Override public void saveMessage(String s, String r, String content, LocalDateTime at) { messages.add(s+":"+r+":"+content); }
        @Override public void saveRecipe(String s, String r, Recipe recipe, LocalDateTime at) { messages.add(s+":"+r+":"+recipe.name); }
    }
    private static class TestPresenter implements FriendRequestOutputBoundary {
        FriendRequestOutputData data;
        @Override public void presentFriendRequestResult(FriendRequestOutputData outputData) { data = outputData; }
    }

    @Test
    public void sendFriendRequestSucceeds() {
        InMemoryDAO dao = new InMemoryDAO();
        dao.users.put("alice", new SimpleUser("alice", "a"));
        dao.users.put("bob", new SimpleUser("bob", "b"));
        TestPresenter presenter = new TestPresenter();
        FriendRequestInteractor interactor = new FriendRequestInteractor(presenter, dao);
        interactor.sendFriendRequest(new FriendRequestInputData("alice", "bob"));
        assertTrue(dao.hasFriendRequest("alice", "bob"));
        assertTrue(presenter.data.isSuccess());
    }

    @Test
    public void sendMessageFailsWhenNotFriends() {
        InMemoryDAO dao = new InMemoryDAO();
        dao.users.put("alice", new SimpleUser("alice", "a"));
        dao.users.put("bob", new SimpleUser("bob", "b"));
        TestPresenter presenter = new TestPresenter();
        FriendRequestInteractor interactor = new FriendRequestInteractor(presenter, dao);
        interactor.sendMessage(new FriendRequestInputData("alice", "bob", "hi"));
        assertEquals("You can only send messages to your friends", presenter.data.getMessage());
    }
}
