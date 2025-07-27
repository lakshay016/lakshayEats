package interface_adapter.edit;

import use_case.RecipeEdit.RecipeEditOutputBoundary;

public class EditPresenter implements RecipeEditOutputBoundary {

    @Override
    public void prepareSuccessView(String message) {
        System.out.println("SUCCESS");
    }

    @Override
    public void prepareFailView(String error) {
        System.err.println("ERROR");
    }
}
