package app;

import data_access.DBReviewDataAccessObject;
import entity.Review;

import java.time.LocalDateTime;

public class CreateReviewTestDriver {
    public static void main(String[] args) {
        DBReviewDataAccessObject db = new DBReviewDataAccessObject();

        int recipeId = 62343;
        int rating = 4;
        String text = "rahhhh";
        String author = "dylan";
        LocalDateTime now = LocalDateTime.now();

        Review review = new Review(recipeId, rating, author, text, now);
        db.save(review);

        System.out.println("Review saved for recipe: " + recipeId);
    }
}
