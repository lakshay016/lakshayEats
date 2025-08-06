package interface_adapter.review;

import use_case.review.ReviewOutputBoundary;
import use_case.review.ReviewOutputData;

public class ReviewPresenter implements ReviewOutputBoundary {
    @Override
    public void presentSuccess(ReviewOutputData outputData) {
        System.out.println("Review for recipe " + outputData.getRecipeId() + " saved at " + outputData.getLastReviewedAt());
    }

    @Override
    public void presentFailure(String errorMessage) {
        System.err.println("Review error: " + errorMessage);
    }
}