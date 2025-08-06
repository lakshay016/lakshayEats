package use_case.search;

import entity.FilterOptions;

public class SearchInputData {
    private final String query;
    private final FilterOptions filters;

    public SearchInputData(String query, FilterOptions filters) {
        this.query = query;
        this.filters = filters;
    }

    public String getQuery() {
        return query;
    }

    public FilterOptions getFilters() {
        return filters;
    }
}