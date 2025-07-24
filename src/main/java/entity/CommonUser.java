package entity;
import java.util.ArrayList;

public class CommonUser implements User{
    private final String userName;
    private final String password;
    public final ArrayList<Recipe> inbox;
    public final ArrayList<User> friends;

    public CommonUser(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.inbox = new ArrayList<>();
        this.friends = new ArrayList<>();
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Object sendMessage(User receiver, String message) {
        if (this.friends.contains(receiver)){
        User.inbox.add(this.getUsername() + ": " + message);
        return null;
        }
        else{
            return ("Add as a friend first!");
        }

    }

    @Override
    public Object sendRecipe(User receiver, Recipe recipe) {
        if (this.friends.contains(receiver)){
            User.inbox.add(this.getUsername() + ": " + recipe);
            return null;
        }
        else{
            return ("Add as a friend first!");
        }

    }

    }


