package entity;

import util.PasswordHasher;

/** Factory for creating {@link CommonUser} instances with hashed passwords. */
public class CommonUserFactory implements UserFactory {

    @Override
    public User createUser(String name, String password) {
        return new CommonUser(name, PasswordHasher.hash(password));
    }

    @Override
    public User createUserWithHashedPassword(String name, String hashedPassword) {
        return new CommonUser(name, hashedPassword);
    }
}
