package entity;

import org.junit.Test;
import util.PasswordHasher;

import static org.junit.Assert.*;

public class CommonUserFactoryTest {
    @Test
    public void factoryCreatesCommonUser() {
        CommonUserFactory factory = new CommonUserFactory();
        User user = factory.createUser("charlie", "secret");
        assertTrue(user instanceof CommonUser);
        assertEquals("charlie", user.getUsername());
        assertEquals(PasswordHasher.hash("secret"), user.getPassword());
    }

    @Test
    public void factoryCreatesUserWithHashedPassword() {
        CommonUserFactory factory = new CommonUserFactory();
        String hashed = PasswordHasher.hash("secret");
        User user = factory.createUserWithHashedPassword("charlie", hashed);
        assertTrue(user instanceof CommonUser);
        assertEquals("charlie", user.getUsername());
        assertEquals(hashed, user.getPassword());
    }
}
