package entity;

import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class CommonUserTest {
    @Test
    public void constructorInitializesFields() throws IOException {
        CommonUser user = new CommonUser("alice", "pw");
        assertEquals("alice", user.getUsername());
        assertEquals("pw", user.getPassword());
        assertTrue(user.getFriends().isEmpty());
        assertTrue(user.getRequests().isEmpty());
        assertTrue(user.getBlocked().isEmpty());
    }

    @Test
    public void settersUpdateFriendCollections() throws IOException {
        CommonUser user = new CommonUser("bob", "pass");
        Set<String> friends = new HashSet<>();
        friends.add("alice");
        user.setFriends(friends);
        assertTrue(user.getFriends().contains("alice"));
    }
}
