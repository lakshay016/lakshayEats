package use_case.friendRequest;

import entity.Recipe;

public class FriendRequestInputData {
    private final String requesterUsername;
    private final String targetUsername;
    private final String messageContent;
    private final Recipe recipe;

    public FriendRequestInputData(String requesterUsername, String targetUsername) {
        this.requesterUsername = requesterUsername;
        this.targetUsername = targetUsername;
        this.messageContent = null;
        this.recipe = null;
    }

    public FriendRequestInputData(String requesterUsername, String targetUsername, String messageContent) {
        this.requesterUsername = requesterUsername;
        this.targetUsername = targetUsername;
        this.messageContent = messageContent;
        this.recipe = null;
    }

    public FriendRequestInputData(String requesterUsername, String targetUsername, Recipe recipe) {
        this.requesterUsername = requesterUsername;
        this.targetUsername = targetUsername;
        this.messageContent = null;
        this.recipe = recipe;
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