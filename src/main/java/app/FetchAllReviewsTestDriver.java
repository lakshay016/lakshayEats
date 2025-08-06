package app;

import data_access.DBReviewDataAccessObject;
import entity.Review;

import java.util.List;

public class FetchAllReviewsTestDriver {
    public static void main(String[] args) {
        DBReviewDataAccessObject db = new DBReviewDataAccessObject();
        List<Review> reviews = db.fetchAll();

        System.out.println("📋 All Reviews in Database:");
        if (reviews.isEmpty()) {
            System.out.println("No reviews found.");
        } else {
            for (Review r : reviews) {
                System.out.println("─ Recipe: " + r.getRecipeId());
                System.out.println("  Author: " + r.getAuthor());
                System.out.println("  Rating: " + r.getRating());
                System.out.println("  Text: " + r.getText());
                System.out.println("  Reviewed At: " + r.getLastReviewedAt());
                System.out.println();
            }
        }
    }
}
