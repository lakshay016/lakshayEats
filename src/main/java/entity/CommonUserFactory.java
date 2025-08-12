package entity;

import util.PasswordHasher;

/** Factory for creating {@link CommonUser} instances with hashed passwords. */
public class CommonUserFactory implements UserFactory {

    @Override
    public User createUser(String name, String password) {
        return new CommonUser(name, PasswordHasher.hash(password));
    }
}
