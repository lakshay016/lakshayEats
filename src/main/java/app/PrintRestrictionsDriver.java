package app;

import data_access.DBUserPreferenceDataAccessObject;
import org.json.JSONObject;

public class PrintRestrictionsDriver {
    public static void main(String[] args) {
        DBUserPreferenceDataAccessObject db = new DBUserPreferenceDataAccessObject();

        String username = "mia";
        JSONObject data = db.fetchRestrictionsAndIntolerances(username);

        if (data == null) {
            System.out.println("No data found for user: " + username);
            return;
        }

        JSONObject restrictions = data.getJSONObject("preferences");
        JSONObject intolerances = data.getJSONObject("intolerances");

        System.out.println("Dietary Restrictions for " + username + ":");
        for (String key : restrictions.keySet()) {
            System.out.println("  " + key + ": " + (restrictions.getInt(key) == 1 ? "✅" : "❌"));
        }

        System.out.println("\nIntolerances:");
        for (String key : intolerances.keySet()) {
            System.out.println("  " + key + ": " + (intolerances.getInt(key) == 1 ? "✅" : "❌"));
        }
    }
}
