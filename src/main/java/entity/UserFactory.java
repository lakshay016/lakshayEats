package entity;

/**
 * user of applicaition (not sure if needed yet)
 */
public interface UserFactory {

    User createUser(String name, String password);

    /**
     * Creates a user when the provided password is already hashed.
     */
    User createUserWithHashedPassword(String name, String hashedPassword);
}
