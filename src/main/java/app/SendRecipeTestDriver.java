package app;

import data_access.DBUserDataAccessObject;
import data_access.DBFriendDataAccessObject;
import entity.CommonUserFactory;
import entity.CommonUser;
import entity.Recipe;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// TODO: DELETE
public class SendRecipeTestDriver {
    public static void main(String[] args) throws IOException {
        CommonUserFactory factory = new CommonUserFactory();
        DBUserDataAccessObject userDataAccess = new DBUserDataAccessObject(factory);
        DBFriendDataAccessObject friendDataAccess = new DBFriendDataAccessObject();

        CommonUser batman = (CommonUser) factory.createUser("Batman", "password123");
        CommonUser joker = (CommonUser) factory.createUser("Joker", "password123");

        try {
            userDataAccess.save(batman);
        } catch (Exception e_) {
        }

        try {
            userDataAccess.save(joker);
        } catch (Exception e_) {
        }

        Set<String> emptySet = new HashSet<>();
        try {
            friendDataAccess.save("Batman", emptySet, emptySet, emptySet);
            friendDataAccess.save("Joker", emptySet, emptySet, emptySet);
        } catch (Exception e_) {
        }

        System.out.println("Testing Simple Message and Recipe Sharing");
        System.out.println("*******************");

        System.out.println("Send friend request");
        batman.requestFriend(joker);

        System.out.println("Accept friend request");
        joker.acceptFriend(batman);

        System.out.println("Send message to friend");
        batman.sendMessage(joker, "Hello! Check out this recipe!");

        System.out.println("Send recipe to friend");
        Recipe recipe = new Recipe();
        recipe.id = 123;
        recipe.name = "Chocolate Chip Cookies";
        batman.sendRecipe(joker, recipe);

        System.out.println("View inbox");
        joker.viewInbox();

        System.out.println("Try to send message to non-friend (should fail)");
        CommonUser user3 = (CommonUser) factory.createUser("user3", "password123");
        batman.sendMessage(user3, "This should fail");

        System.out.println("Try to send recipe to non-friend (should fail)");
        batman.sendRecipe(user3, recipe);
    }
}