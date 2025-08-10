package data_access;

import entity.*;
import use_case.login.LoginUserDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

// TODO: DELETE if not used in testing
public class InMemoryUserDataAccessObject implements LoginUserDataAccessInterface {
    private final Map<String, User> users = new HashMap<>();
    private String currentUsername = null;
    private final UserFactory factory;

    public InMemoryUserDataAccessObject(UserFactory factory) {
        this.factory = factory;
    }


    @Override
    public boolean existsByName(String username) {
        return users.containsKey(username);
    }

    @Override
    public void save(User user) {
        users.put(user.getUsername(), user);
    }

    @Override
    public User get(String username) {
        return users.get(username);
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }
}
