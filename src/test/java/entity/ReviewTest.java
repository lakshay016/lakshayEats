package entity;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ReviewTest {
    @Test
    public void gettersReturnConstructorValues() {
        LocalDateTime now = LocalDateTime.now();
        Review review = new Review(1, 5, "alice", "tasty", now);
        assertEquals(1, review.getRecipeId());
        assertEquals(5, review.getRating());
        assertEquals("alice", review.getAuthor());
        assertEquals("tasty", review.getText());
        assertEquals(now, review.getLastReviewedAt());
    }
}
