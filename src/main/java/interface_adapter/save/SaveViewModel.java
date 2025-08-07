package interface_adapter.save;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SaveViewModel {
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private SaveState saveState = new SaveState();
    public SaveState getSaveState() {
        return saveState;
    }
    public void setSaveState(SaveState saveState) {
        this.saveState = saveState;
        propertyChangeSupport.firePropertyChange("saveState", null, this.saveState);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
