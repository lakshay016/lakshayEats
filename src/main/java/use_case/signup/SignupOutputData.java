package use_case.signup;

public class SignupOutputData {
    private final String message;

    public SignupOutputData(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}
