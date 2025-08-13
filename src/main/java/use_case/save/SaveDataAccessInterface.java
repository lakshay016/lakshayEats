package use_case.save;

import entity.SearchResult;

public interface SaveDataAccessInterface {
    boolean save(String username, SearchResult recipe);
}
