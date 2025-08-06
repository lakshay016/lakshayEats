package interface_adapter.review;

public class ReviewState {
    private final boolean isLoading;
    private final String message;

    public ReviewState(boolean isLoading, String message) {
        this.isLoading = isLoading;
        this.message = message;
    }

    public boolean isLoading() { return isLoading; }
    public String getMessage() { return message; }
}
