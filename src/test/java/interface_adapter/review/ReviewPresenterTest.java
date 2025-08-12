import interface_adapter.review.ReviewPresenter;
import org.junit.Test;
import use_case.review.ReviewOutputData;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class ReviewPresenterTest {
    @Test
    public void testPresentSuccess() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        ReviewPresenter presenter = new ReviewPresenter();
        presenter.presentSuccess(new ReviewOutputData(1,5,"a","t", LocalDateTime.of(2024,1,1,0,0)));
        System.setOut(original);
        assertTrue(out.toString().contains("Review for recipe 1 saved"));
    }

    @Test
    public void testPresentFailure() {
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(err));
        ReviewPresenter presenter = new ReviewPresenter();
        presenter.presentFailure("oops");
        System.setErr(originalErr);
        assertTrue(err.toString().contains("oops"));
    }
}
