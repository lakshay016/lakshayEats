package use_case.login;

import entity.CommonUser;
import entity.CommonUserFactory;
import entity.User;
import org.junit.Test;
import util.PasswordHasher;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class LoginInteractorTest {
    private static class InMemoryUserDAO implements LoginUserDataAccessInterface {
        Map<String, User> users = new HashMap<>();
        String current;
        @Override public boolean existsByName(String username) { return users.containsKey(username); }
        @Override public void save(User user) { users.put(user.getUsername(), user); }
        @Override public User get(String username) { return users.get(username); }
        @Override public String getCurrentUsername() { return current; }
        @Override public void setCurrentUsername(String username) { current = username; }
    }
    private static class TestPresenter implements LoginOutputBoundary {
        LoginOutputData successData; String failMessage;
        @Override public void prepareSuccessView(LoginOutputData data) { successData = data; }
        @Override public void prepareFailView(String errorMessage) { failMessage = errorMessage; }
    }
    private static class SimpleUser implements User {
        String name, pwd;
        SimpleUser(String n, String p) { name = n; pwd = p; }
        @Override public String getUsername() { return name; }
        @Override public String getPassword() { return pwd; }
    }

    /**
     * A {@link CommonUserFactory} that assumes the provided password is already hashed.
     * This mimics the production flow where {@link entity.UserFactory} is used to
     * reconstruct users from stored credentials without rehashing the password.
     */
    private static class PassThroughFactory extends CommonUserFactory {
        @Override
        public User createUser(String name, String password) {
            return new CommonUser(name, password);
        }
    }

    /**
     * An in-memory DAO that stores hashed passwords and uses a {@link CommonUserFactory}
     * to recreate user objects when retrieved, mirroring the real data access layer.
     */
    private static class FactoryUserDAO implements LoginUserDataAccessInterface {
        private final Map<String, String> users = new HashMap<>();
        private final CommonUserFactory factory;
        private String current;

        FactoryUserDAO(CommonUserFactory factory) { this.factory = factory; }

        @Override public boolean existsByName(String username) { return users.containsKey(username); }
        @Override public void save(User user) { users.put(user.getUsername(), user.getPassword()); }
        @Override public User get(String username) {
            String pwd = users.get(username);
            return pwd != null ? factory.createUser(username, pwd) : null;
        }
        @Override public String getCurrentUsername() { return current; }
        @Override public void setCurrentUsername(String username) { current = username; }
    }

    @Test
    public void failWhenUserMissing() {
        InMemoryUserDAO dao = new InMemoryUserDAO();
        TestPresenter presenter = new TestPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);
        interactor.execute(new LoginInputData("alice", "pw"));
        assertEquals("Account not found: alice", presenter.failMessage);
    }

    @Test
    public void failWhenPasswordIncorrect() {
        InMemoryUserDAO dao = new InMemoryUserDAO();
        dao.save(new SimpleUser("bob", PasswordHasher.hash("123")));
        TestPresenter presenter = new TestPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);
        interactor.execute(new LoginInputData("bob", "wrong"));
        assertEquals("Incorrect password for bob", presenter.failMessage);
    }

    @Test
    public void successWhenCredentialsMatch() {
        InMemoryUserDAO dao = new InMemoryUserDAO();
        dao.save(new SimpleUser("bob", PasswordHasher.hash("123")));
        TestPresenter presenter = new TestPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);
        interactor.execute(new LoginInputData("bob", "123"));
        assertEquals("bob", presenter.successData.getUsername());
        assertEquals("bob", dao.getCurrentUsername());
    }

    /**
     * Ensure that when a user is created and retrieved via {@link CommonUserFactory},
     * login succeeds with the correct credentials and fails otherwise.
     */
    @Test
    public void loginWithFactoryCreatedHashedUser() {
        PassThroughFactory factory = new PassThroughFactory();
        FactoryUserDAO dao = new FactoryUserDAO(factory);

        // Create and store a user whose password has already been hashed
        String hashed = PasswordHasher.hash("secret");
        dao.save(factory.createUser("dave", hashed));

        TestPresenter presenter = new TestPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        // Successful login with correct password
        interactor.execute(new LoginInputData("dave", "secret"));
        assertEquals("dave", presenter.successData.getUsername());

        // Failed login with incorrect password
        presenter.successData = null; presenter.failMessage = null;
        interactor.execute(new LoginInputData("dave", "wrong"));
        assertEquals("Incorrect password for dave", presenter.failMessage);
    }
}
