package use_case.friendRequest;

import entity.Recipe;

public class FriendRequestOutputData {
    private final boolean success;
    private final String message;
    private final String requesterUsername;
    private final String targetUsername;
    private final String messageContent;
    private final Recipe recipe;

    public FriendRequestOutputData(boolean success, String message, String requesterUsername, String targetUsername) {
        this.success = success;
        this.message = message;
        this.requesterUsername = requesterUsername;
        this.targetUsername = targetUsername;
        this.messageContent = null;
        this.recipe = null;
    }

    public FriendRequestOutputData(boolean success, String message, String requesterUsername,
                                   String targetUsername, String messageContent) {
        this.success = success;
        this.message = message;
        this.requesterUsername = requesterUsername;
        this.targetUsername = targetUsername;
        this.messageContent = messageContent;
        this.recipe = null;
    }

    public FriendRequestOutputData(boolean success, String message, String requesterUsername,
                                   String targetUsername, Recipe recipe) {
        this.success = success;
        this.message = message;
        this.requesterUsername = requesterUsername;
        this.targetUsername = targetUsername;
        this.messageContent = null;
        this.recipe = recipe;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public Recipe getRecipe() {
        return recipe;
    }
}