package app;

import data_access.DBMessageDataAccessObject;

import java.time.LocalDateTime;

// TODO: DELETE
public class AddMessageTestDriver {
    public static void main(String[] args) {
        DBMessageDataAccessObject db = new DBMessageDataAccessObject();

        String sender = "dylan";
        String receiver = "mia";
        String content = "yes! I like food";
        LocalDateTime sentAt = LocalDateTime.now();

        db.saveMessage(sender, receiver, content, sentAt);
        System.out.println("Message sent from " + sender + " to " + receiver);
    }
}
