package use_case.change_password;

import entity.User;
import entity.UserFactory;
import org.junit.Test;
import util.PasswordHasher;

import static org.junit.Assert.*;

public class ChangePasswordInteractorTest {
    private static class InMemoryDAO implements ChangePasswordUserDataAccessInterface {
        User changed;
        @Override public void changePassword(User user) { changed = user; }
    }
    private static class TestPresenter implements ChangePasswordOutputBoundary {
        ChangePasswordOutputData success; String fail;
        @Override public void prepareSuccessView(ChangePasswordOutputData outputData) { success = outputData; }
        @Override public void prepareFailView(String errorMessage) { fail = errorMessage; }
    }
    private static class SimpleFactory implements UserFactory {
        @Override public User createUser(String name, String password) {
            return new SimpleUser(name, PasswordHasher.hash(password));
        }

        @Override public User createUserWithHashedPassword(String name, String hashedPassword) {
            return new SimpleUser(name, hashedPassword);
        }
    }
    private static class SimpleUser implements User {
        String name, pwd;
        SimpleUser(String n, String p) { name = n; pwd = p; }
        public String getUsername() { return name; }
        public String getPassword() { return pwd; }
    }
    @Test
    public void executeChangesPassword() {
        InMemoryDAO dao = new InMemoryDAO();
        TestPresenter presenter = new TestPresenter();
        ChangePasswordInteractor interactor = new ChangePasswordInteractor(dao, presenter, new SimpleFactory());
        interactor.execute(new ChangePasswordInputData("alice", "new"));
        assertEquals("alice", dao.changed.getUsername());
        assertEquals(PasswordHasher.hash("new"), dao.changed.getPassword());
        assertEquals("alice", presenter.success.getUsername());
    }
}
