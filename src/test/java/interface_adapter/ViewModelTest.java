import interface_adapter.ViewModel;
import org.junit.Test;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static org.junit.Assert.*;

public class ViewModelTest {
    @Test
    public void testFirePropertyChanged() {
        ViewModel<String> vm = new ViewModel<>("test");
        final String[] observed = {null};
        vm.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                observed[0] = (String) evt.getNewValue();
            }
        });
        vm.setState("hello");
        vm.firePropertyChanged();
        assertEquals("hello", observed[0]);
    }
}
