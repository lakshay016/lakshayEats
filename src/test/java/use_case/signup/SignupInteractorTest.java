package use_case.signup;

import entity.User;
import entity.UserFactory;
import org.junit.Test;
import use_case.login.LoginUserDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SignupInteractorTest {
    private static class InMemoryUserDAO implements LoginUserDataAccessInterface {
        Map<String, User> users = new HashMap<>();
        String current;
        @Override public boolean existsByName(String username) { return users.containsKey(username); }
        @Override public void save(User user) { users.put(user.getUsername(), user); }
        @Override public User get(String username) { return users.get(username); }
        @Override public String getCurrentUsername() { return current; }
        @Override public void setCurrentUsername(String username) { current = username; }
    }
    private static class TestPresenter implements SignupOutputBoundary {
        SignupOutputData successData; String failMessage; boolean switched;
        @Override public void prepareSuccessView(SignupOutputData data) { successData = data; }
        @Override public void prepareFailView(String error) { failMessage = error; }
        @Override public void switchToLoginView() { switched = true; }
    }
    private static class SimpleUser implements User {
        String name, pwd;
        SimpleUser(String n, String p) { name = n; pwd = p; }
        @Override public String getUsername() { return name; }
        @Override public String getPassword() { return pwd; }
    }
    private static class SimpleFactory implements UserFactory {
        @Override public User createUser(String name, String password) { return new SimpleUser(name, password); }
    }

    @Test
    public void failWhenUsernameExists() {
        InMemoryUserDAO dao = new InMemoryUserDAO();
        dao.save(new SimpleUser("alice", "pw"));
        TestPresenter presenter = new TestPresenter();
        SignupInteractor interactor = new SignupInteractor(dao, presenter, new SimpleFactory());
        interactor.execute(new SignupInputData("alice", "pw", "pw"));
        assertEquals("Username already exists.", presenter.failMessage);
    }

    @Test
    public void failWhenPasswordsDoNotMatch() {
        InMemoryUserDAO dao = new InMemoryUserDAO();
        TestPresenter presenter = new TestPresenter();
        SignupInteractor interactor = new SignupInteractor(dao, presenter, new SimpleFactory());
        interactor.execute(new SignupInputData("bob", "pw1", "pw2"));
        assertEquals("Passwords don't match.", presenter.failMessage);
    }

    @Test
    public void successCreatesUser() {
        InMemoryUserDAO dao = new InMemoryUserDAO();
        TestPresenter presenter = new TestPresenter();
        SignupInteractor interactor = new SignupInteractor(dao, presenter, new SimpleFactory());
        interactor.execute(new SignupInputData("charlie", "pw", "pw"));
        assertNotNull(dao.get("charlie"));
        assertEquals("Signup successful!", presenter.successData.getMessage());
    }
}
