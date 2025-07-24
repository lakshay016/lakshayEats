package use_case.search;

import entity.FilterOptions;
import entity.SearchResult;

import java.io.IOException;
import java.util.List;

public interface SearchDataAccessInterface {
    List<SearchResult> searchByDish(String query, FilterOptions filters) throws IOException;
//    List<SearchResult> searchByIngredients(List<String> ingredients, FilterOptions filters) throws IOException;
}

