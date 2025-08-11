import interface_adapter.review.ReviewController;
import org.junit.Test;
import use_case.review.ReviewInputBoundary;
import use_case.review.ReviewInputData;
import static org.junit.Assert.*;

public class ReviewControllerTest {
    private static class StubInteractor implements ReviewInputBoundary {
        ReviewInputData received;
        @Override public void execute(ReviewInputData inputData) { this.received = inputData; }
    }

    @Test
    public void testAddReviewDelegates() {
        StubInteractor interactor = new StubInteractor();
        ReviewController controller = new ReviewController(interactor);
        controller.addReview(1, 5, "me", "great");
        assertEquals(1, interactor.received.getRecipeId());
        assertEquals(5, interactor.received.getRating());
        assertEquals("me", interactor.received.getAuthor());
        assertEquals("great", interactor.received.getText());
    }
}
