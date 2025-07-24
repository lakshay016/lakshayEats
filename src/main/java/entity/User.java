package entity;
import java.util.ArrayList;

/**
 * registered user
 */
public interface User {
    ArrayList<String> inbox = new ArrayList<>();
    ArrayList<String> friendList = new ArrayList<>();
    ArrayList<String> blockedList = new ArrayList<>();




    String getUsername();
    String getPassword();
    void sendMessage(CommonUser receiver, String message);
    void sendRecipe(CommonUser receiver, Recipe recipe);
    void addFriend(CommonUser friend);
    void removeFriend(CommonUser friend);
    void blockUser(CommonUser friend);
    void unblockUser(CommonUser friend);
    void acceptFriend(CommonUser friend);
    void rejectFriend(CommonUser friend);

}
