package interface_adapter.FriendRequest;

import use_case.friendRequest.FriendRequestOutputBoundary;
import use_case.friendRequest.FriendRequestOutputData;
public class FriendRequestPresenter implements FriendRequestOutputBoundary {

    private final FriendRequestView friendRequestView;

    public FriendRequestPresenter(FriendRequestView friendRequestView) {
        this.friendRequestView = friendRequestView;
    }

    @Override
    public void presentFriendRequestResult(FriendRequestOutputData outputData) {
        if (outputData.isSuccess()) {friendRequestView.showSuccessMessage(outputData.getMessage());}
        else {friendRequestView.showErrorMessage(outputData.getMessage());}
        friendRequestView.updateFriendRequestStatus(
                outputData.getRequesterUsername(),
                outputData.getTargetUsername(),
                outputData.isSuccess());}

    public interface FriendRequestView {
        void showSuccessMessage(String message);
        void showErrorMessage(String message);
        void updateFriendRequestStatus(String requester, String target, boolean success);
    }
}