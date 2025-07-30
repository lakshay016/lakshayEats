package use_case.review;

import entity.Review;

public interface ReviewDataAccessInterface {
    boolean save(Review review);
    Review fetchByRecipeId(String recipeId);
}