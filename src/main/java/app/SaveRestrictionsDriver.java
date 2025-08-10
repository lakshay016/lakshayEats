package app;

import data_access.DBUserPreferenceDataAccessObject;
import org.json.JSONObject;

// TODO: DELETE
public class SaveRestrictionsDriver {
    public static void main(String[] args) {
        DBUserPreferenceDataAccessObject db = new DBUserPreferenceDataAccessObject();

        String username = "mia";

        JSONObject restrictions = new JSONObject();
        restrictions.put("Vegetarian", 1);

        JSONObject intolerances = new JSONObject();
        intolerances.put("Dairy", 1);

        db.saveRestrictionsAndIntolerances(username, restrictions, intolerances);
        System.out.println("✅ Restrictions and intolerances saved for: " + username);
    }
}
