package entity;
import java.util.ArrayList;

/**
 * registered user
 */
public interface User {
    ArrayList<String> inbox = new ArrayList<>();




    String getUsername();
    String getPassword();
    Object sendMessage(User receiver, String message);
    Object sendRecipe(User receiver, Recipe recipe);

}
