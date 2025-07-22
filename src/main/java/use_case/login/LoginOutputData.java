package use_case.login;

public class LoginOutputData {
    private final String username;
    private final Boolean useCaseFailed;

    public LoginOutputData(String username, boolean useCaseFailed) {
        this.username = username;
        this.useCaseFailed = useCaseFailed;
    }
    public String getUsername() {
        return username;
    }
}
