package interface_adapter.review;

import use_case.review.ReviewInputBoundary;
import use_case.review.ReviewInputData;

public class ReviewController {
    private final ReviewInputBoundary interactor;

    public ReviewController(ReviewInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void addReview(String recipeId, int rating, String text) {
        interactor.execute(new ReviewInputData(recipeId, rating, text));
    }
}