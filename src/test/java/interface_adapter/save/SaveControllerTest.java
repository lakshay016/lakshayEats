import entity.SearchResult;
import interface_adapter.save.SaveController;
import org.junit.Test;
import use_case.save.SaveInputBoundary;
import use_case.save.SaveInputData;
import static org.junit.Assert.*;

public class SaveControllerTest {
    private static class StubInteractor implements SaveInputBoundary {
        SaveInputData received;
        @Override public void execute(SaveInputData inputData) { this.received = inputData; }
    }

    @Test
    public void testSaveDelegates() {
        StubInteractor interactor = new StubInteractor();
        SaveController controller = new SaveController(interactor);
        SearchResult result = new SearchResult();
        controller.save("user1", result);
        assertEquals("user1", interactor.received.getUsername());
        assertEquals(result, interactor.received.getRecipe());
    }
}
