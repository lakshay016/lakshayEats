package use_case.change_password;


public class ChangePasswordInputData {

    private final String password;
    private final String username;

    public ChangePasswordInputData(String username, String password) {
        this.password = password;
        this.username = username;
    }

    String getPassword() {
        return password;
    }

    String getUsername() {
        return username;
    }

}
