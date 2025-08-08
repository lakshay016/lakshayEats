package interface_adapter.preferences;

import entity.Preferences;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PreferencesViewModel {
    private Preferences preferences;
    private String username;
    private String message;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
        firePropertyChanged();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        firePropertyChanged();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        firePropertyChanged();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void firePropertyChanged() {
        support.firePropertyChange(null, null, null);
    }
}
