package app;

import data_access.DBMessageDataAccessObject;
import org.json.JSONObject;

import java.util.List;

public class ViewAllMessagesTestDriver {
    public static void main(String[] args) {
        DBMessageDataAccessObject db = new DBMessageDataAccessObject();
        List<JSONObject> messages = db.fetchAllMessages();

        System.out.println("All Messages:");
        for (JSONObject msg : messages) {
            System.out.println("From: " + msg.getString("sender"));
            System.out.println("To: " + msg.getString("receiver"));
            System.out.println("At: " + msg.getString("sentAt"));
            System.out.println("Message: " + msg.getString("content"));
            System.out.println("─────────────────────────────");
        }
    }
}
