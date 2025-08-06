package interface_adapter.save;

import entity.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class SaveState {
    private String Message;
    private List<SearchResult> recipes = new ArrayList<>();



    public void setRecipes(List<SearchResult> recipes) {}
    public String getMessage() {
        return Message;
    }
    public void setMessage(String Message) {
        this.Message = Message;
    }

    public List<SearchResult> getRecipes() {
        return recipes;
    }
    public void setResults(List<SearchResult> recipes) {
        this.recipes = recipes;
    }

}

