package entity;

/**
 * user of applicaition (not sure if needed yet)
 */
public interface UserFactory {

    User createUser(String name, String password);
}
