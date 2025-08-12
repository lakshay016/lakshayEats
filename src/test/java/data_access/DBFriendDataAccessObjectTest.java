package data_access;

import org.json.JSONArray;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class DBFriendDataAccessObjectTest {
    @Test
    public void toSetConvertsJSONArrayToSet() {
        JSONArray array = new JSONArray();
        array.put("alice");
        array.put("bob");
        array.put("alice");
        DBFriendDataAccessObject dao = new DBFriendDataAccessObject();
        Set<String> result = dao.toSet(array);
        assertEquals(2, result.size());
        assertTrue(result.contains("alice"));
        assertTrue(result.contains("bob"));
    }
}
