package entity;

public class CommonUser implements User{
    private final String userName;
    private final String password;

    public CommonUser(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

}

