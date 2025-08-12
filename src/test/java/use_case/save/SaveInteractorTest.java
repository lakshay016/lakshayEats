package use_case.save;

import entity.SearchResult;
import org.junit.Test;

import static org.junit.Assert.*;

public class SaveInteractorTest {
    private static class InMemoryDAO implements SaveDataAccessInterface {
        boolean shouldSucceed = true;
        SearchResult saved;
        @Override
        public boolean save(String username, SearchResult recipe) {
            if (shouldSucceed) {
                this.saved = recipe;
                return true;
            }
            return false;
        }
    }
    private static class TestPresenter implements SaveOutputBoundary {
        SaveOutputData success; String error;
        @Override public void prepareSuccessView(SaveOutputData data) { success = data; }
        @Override public void prepareErrorView(String errorMessage) { error = errorMessage; }
    }

    @Test
    public void successSavesRecipe() {
        InMemoryDAO dao = new InMemoryDAO();
        TestPresenter presenter = new TestPresenter();
        SaveInteractor interactor = new SaveInteractor(dao, presenter);
        SearchResult result = new SearchResult();
        result.setId(1);
        interactor.execute(new SaveInputData("alice", result));
        assertEquals(result, dao.saved);
        assertEquals("Recipe saved successfully.", presenter.success.getMessage());
    }

    @Test
    public void failureCallsErrorView() {
        InMemoryDAO dao = new InMemoryDAO();
        dao.shouldSucceed = false;
        TestPresenter presenter = new TestPresenter();
        SaveInteractor interactor = new SaveInteractor(dao, presenter);
        interactor.execute(new SaveInputData("alice", new SearchResult()));
        assertEquals("Failed to save recipe.", presenter.error);
    }
}
