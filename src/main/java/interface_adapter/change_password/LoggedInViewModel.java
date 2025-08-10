package interface_adapter.change_password;

import interface_adapter.ViewModel;
import interface_adapter.change_password.LoggedInState;

/**
 * The View Model for the Logged In View.
 */
public class LoggedInViewModel extends ViewModel<LoggedInState> {

    public LoggedInViewModel() {
        super("logged in");
        setState(new LoggedInState());
    }

}
