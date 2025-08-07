package interface_adapter.save;

import entity.FilterOptions;
import entity.SearchResult;
import use_case.save.SaveInputBoundary;
import use_case.save.SaveInputData;

public class SaveController {

    private final SaveInputBoundary interactor;

    public SaveController(SaveInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void save(String userId, SearchResult recipe) {
        SaveInputData inputData = new SaveInputData(userId, recipe);
        interactor.execute(inputData);
    }
}
