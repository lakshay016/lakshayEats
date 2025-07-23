package app;

import data_access.DBUserDataAccessObject;
import entity.CommonUserFactory;
import entity.User;
import entity.UserFactory;
import use_case.login.LoginUserDataAccessInterface;

public class LoginTestDriver {
    public static void main(String[] args) {
        UserFactory factory = new CommonUserFactory();

        LoginUserDataAccessInterface db = new DBUserDataAccessObject(factory);

        String testUsername = "Dylan";
        User user = db.get(testUsername);

        if (user == null) {
            System.out.println("No user found with username: " + testUsername);
        } else {
            System.out.println("User found!");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
        }
    }
}
