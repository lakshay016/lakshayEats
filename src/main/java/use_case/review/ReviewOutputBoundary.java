package use_case.review;

public interface ReviewOutputBoundary {
    void presentSuccess(ReviewOutputData outputData);
    void presentFailure(String errorMessage);
}
