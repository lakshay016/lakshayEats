package use_case.review;

import entity.Review;

import java.util.List;

public interface ReviewDataAccessInterface {
    boolean save(Review review);
    List<Review> fetchByRecipeId(String recipeId);
}