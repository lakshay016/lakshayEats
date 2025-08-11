package entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommonUserFactoryTest {
    @Test
    public void factoryCreatesCommonUser() {
        CommonUserFactory factory = new CommonUserFactory();
        User user = factory.createUser("charlie", "secret");
        assertTrue(user instanceof CommonUser);
        assertEquals("charlie", user.getUsername());
        assertEquals("secret", user.getPassword());
    }
}
