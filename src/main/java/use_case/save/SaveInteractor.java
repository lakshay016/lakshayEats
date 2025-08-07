package use_case.save;

import entity.SearchResult;

public class SaveInteractor implements SaveInputBoundary {
    private SaveDataAccessInterface saveDataAccessInterface;
    private SaveOutputBoundary saveOutputBoundary;
    public SaveInteractor(SaveDataAccessInterface saveDataAccessInterface, SaveOutputBoundary saveOutputBoundary) {
        this.saveDataAccessInterface = saveDataAccessInterface;
        this.saveOutputBoundary = saveOutputBoundary;
    }

    public void execute(SaveInputData inputData) {
        SearchResult recipe = inputData.getRecipe();
        boolean success = saveDataAccessInterface.save(inputData.getUsername(), recipe);
        if (success) {
            SaveOutputData outputBoundary = new SaveOutputData("Recipe saved successfully.", recipe);
            saveOutputBoundary.prepareSuccessView(outputBoundary);
        } else {
            saveOutputBoundary.prepareErrorView("Failed to save recipe.");
        }
    }
}
