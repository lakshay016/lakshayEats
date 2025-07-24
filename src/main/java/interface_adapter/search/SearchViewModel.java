package interface_adapter.search;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// public class SearchViewModel extends ViewModel<SearchState> {
//    public SearchViewModel() {
//        super("search");
//        setState(new SearchState());
//    }
//}

/**
 * Temporary standalone ViewModel for Search, independent of framework ViewModel.
 */
public class SearchViewModel {
    private SearchState state = new SearchState();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public SearchState getState() {
        return state;
    }

    public void setState(SearchState newState) {
        SearchState old = this.state;
        this.state = newState;
        pcs.firePropertyChange("state", old, newState);
    }

    public void publishState(SearchState newState) {
        setState(newState);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
