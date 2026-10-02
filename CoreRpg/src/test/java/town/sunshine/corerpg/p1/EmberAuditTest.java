package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EmberAuditTest {
    @Test
    public void diffFindsMissingRowsLostItemsAndDuplicates() {
        Map<String, Integer> held = new HashMap<String, Integer>();
        held.put("a", 1); held.put("b", 2); held.put("c", 1); held.put("x", 1);
        Map<String, String> rows = new HashMap<String, String>();
        rows.put("a", "active"); rows.put("b", "active"); rows.put("c", "dismantled");
        rows.put("lost", "active"); rows.put("gone", "dismantled");
        assertEquals(Arrays.asList("DUPLICATE b x2", "HELD_NOT_ACTIVE c state=dismantled", "NO_ROW x", "ACTIVE_NOT_HELD lost"),
                EmberAudit.diff(held, rows));
    }

    @Test
    public void cleanInventoryHasNoFindings() {
        Map<String, Integer> held = new HashMap<String, Integer>();
        held.put("a", 1);
        Map<String, String> rows = new HashMap<String, String>();
        rows.put("a", "active"); rows.put("old", "exchanged");
        assertEquals(0, EmberAudit.diff(held, rows).size());
    }
}
