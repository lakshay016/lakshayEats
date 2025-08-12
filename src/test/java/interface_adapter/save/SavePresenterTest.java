import interface_adapter.save.SavePresenter;
import interface_adapter.save.SaveViewModel;
import org.junit.Test;
import use_case.save.SaveOutputData;
import entity.SearchResult;
import static org.junit.Assert.*;

public class SavePresenterTest {
    @Test
    public void testPrepareSuccessView() {
        SaveViewModel viewModel = new SaveViewModel();
        SavePresenter presenter = new SavePresenter(viewModel);
        presenter.prepareSuccessView(new SaveOutputData("msg", new SearchResult()));
        assertEquals("Saved!", viewModel.getSaveState().getMessage());
    }

    @Test
    public void testPrepareErrorView() {
        SaveViewModel viewModel = new SaveViewModel();
        SavePresenter presenter = new SavePresenter(viewModel);
        presenter.prepareErrorView("oops");
        assertEquals("oops", viewModel.getSaveState().getMessage());
    }
}
