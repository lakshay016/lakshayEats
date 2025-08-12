package use_case.login;

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
}
