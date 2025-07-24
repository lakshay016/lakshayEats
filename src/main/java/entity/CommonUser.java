package entity;
import java.util.ArrayList;

public class CommonUser implements User{
    private final String userName;
    private final String password;
    public ArrayList <Object>inbox;
    public final ArrayList<User> friends;
    public final ArrayList<User> blockedList;
    public ArrayList<User> friendRequests;

    public CommonUser(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.inbox = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.blockedList = new ArrayList<>();
        this.friendRequests = new ArrayList<>();
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
    public void sendMessage(CommonUser receiver, String message) {
        if (this.friends.contains(receiver)){
        User.inbox.add(this.getUsername() + ": " + message);
        }
        else{
            System.out.println("Add as a friend first!");
        }
    }

    @Override
    public void sendRecipe(CommonUser receiver, Recipe recipe) {
        if (this.friends.contains(receiver)){
            receiver.inbox.add(this.getUsername() + ": " + recipe);
        }
        else{
            System.out.println("Add as a friend first!");
        }
    }

    @Override
    public void addFriend(CommonUser friend) {
        if (friend.blockedList.contains(this)){
            System.out.println("Friend Request Sent!");
        if (friend.friendRequests.contains(this)){
            System.out.println("Friend Request Has Already Been Sent!");
        }
        if (this.friends.contains(friend)){
            System.out.println("This User Has already Requested to Friend you! Please Accept" +
                    "The Friend Request In Your Requests Tab!");
        }
        if (this.friends.contains(friend)){
            System.out.println("Already Friends with User!");
        }
        else{
            friend.inbox.add(this);
        }
    }
    }

    public void removeFriend(CommonUser friend) {
        this.friends.remove(friend);
    }

    @Override
    public void blockUser(CommonUser user) {
        this.blockedList.add(user);
        this.friends.remove(user);
    }

    @Override
    public void unblockUser(CommonUser friend) {
        this.blockedList.remove(friend);
    }

    @Override
    public void acceptFriend(CommonUser friend) {
        if (this.friendRequests.contains(friend)){
            this.friends.add(friend);
            friend.friends.add(this);
        }

    }

    @Override
    public void rejectFriend(CommonUser friend) {
        this.friendRequests.remove(friend);

    }

}


