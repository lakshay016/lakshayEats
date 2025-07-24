package use_case.RecipeEdit;

public interface RecipeEditOutputBoundary {

    void prepareSuccessView(String message);
    void prepareFailView(String errorMessage);
}
