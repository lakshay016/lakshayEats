package use_case.review;

import entity.Review;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ReviewInteractorTest {
    private static class InMemoryDAO implements ReviewDataAccessInterface {
        boolean saveReturn = true;
        Review lastSaved;
        @Override public boolean save(Review review) { lastSaved = review; return saveReturn; }
        @Override public List<Review> fetchByRecipeId(String recipeId) { return new ArrayList<>(); }
    }
    private static class TestPresenter implements ReviewOutputBoundary {
        ReviewOutputData success; String failure;
        @Override public void presentSuccess(ReviewOutputData outputData) { success = outputData; }
        @Override public void presentFailure(String errorMessage) { failure = errorMessage; }
    }

    @Test
    public void successFlow() {
        InMemoryDAO dao = new InMemoryDAO();
        TestPresenter presenter = new TestPresenter();
        ReviewInteractor interactor = new ReviewInteractor(dao, presenter);
        ReviewInputData input = new ReviewInputData(1, 5, "alice", "great");
        interactor.execute(input);
        assertEquals(1, presenter.success.getRecipeId());
        assertEquals(5, presenter.success.getRating());
    }

    @Test
    public void failureFlow() {
        InMemoryDAO dao = new InMemoryDAO();
        dao.saveReturn = false;
        TestPresenter presenter = new TestPresenter();
        ReviewInteractor interactor = new ReviewInteractor(dao, presenter);
        interactor.execute(new ReviewInputData(1, 5, "alice", "great"));
        assertEquals("Unable to save review", presenter.failure);
    }
}
