package use_case.signup;

public class SignupOutputData {
    private String username;
    private final String message;

    public SignupOutputData(String username, String message) {
        this.username = username;
        this.message = message;
    }
    public String getUsername() {
        return username;
    }


    public String getMessage() { return message; }
}
