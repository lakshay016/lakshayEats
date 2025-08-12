import interface_adapter.FriendRequest.FriendRequestPresenter;
import org.junit.Test;
import use_case.friendRequest.FriendRequestOutputData;
import static org.junit.Assert.*;

public class FriendRequestPresenterTest {
    private static class StubView implements FriendRequestPresenter.FriendRequestView {
        String successMsg; String errorMsg; String requester; String target; Boolean status;
        @Override public void showSuccessMessage(String message){successMsg=message;}
        @Override public void showErrorMessage(String message){errorMsg=message;}
        @Override public void updateFriendRequestStatus(String r,String t,boolean s){requester=r;target=t;status=s;}
    }

    @Test
    public void testPresentSuccess() {
        StubView view = new StubView();
        FriendRequestPresenter presenter = new FriendRequestPresenter(view);
        presenter.presentFriendRequestResult(new FriendRequestOutputData(true, "ok", "a", "b"));
        assertEquals("ok", view.successMsg);
        assertEquals("a", view.requester);
        assertTrue(view.status);
    }

    @Test
    public void testPresentFailure() {
        StubView view = new StubView();
        FriendRequestPresenter presenter = new FriendRequestPresenter(view);
        presenter.presentFriendRequestResult(new FriendRequestOutputData(false, "bad", "a", "b"));
        assertEquals("bad", view.errorMsg);
        assertEquals("a", view.requester);
        assertFalse(view.status);
    }
}
