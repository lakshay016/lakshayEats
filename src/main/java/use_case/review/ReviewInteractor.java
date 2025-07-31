package use_case.review;

import entity.Review;
import java.time.LocalDateTime;

public class ReviewInteractor implements ReviewInputBoundary {
    private final ReviewDataAccessInterface reviewDao;
    private final ReviewOutputBoundary presenter;

    public ReviewInteractor(ReviewDataAccessInterface reviewDao, ReviewOutputBoundary presenter) {
        this.reviewDao = reviewDao;
        this.presenter = presenter;
    }

    @Override
    public void execute(ReviewInputData inputData) {
        LocalDateTime now = LocalDateTime.now();
        Review review = new Review(
                inputData.getRecipeId(),
                inputData.getRating(),
                inputData.getAuthor(),
                inputData.getText(),
                now
        );

        boolean success = reviewDao.save(review);
        if (success) {
            presenter.presentSuccess(new ReviewOutputData(
                    inputData.getRecipeId(),
                    inputData.getRating(),
                    inputData.getAuthor(),
                    inputData.getText(),
                    now
            ));
        } else {
            presenter.presentFailure("Unable to save review");
        }
    }
}