package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/** D172: pure parts of the durable payment path (undo / mark redeem / reroll) */
public class EmberPayTest {

    private static final String UID = "0123456789abcdef0123456789abcdef"; // cr_p1_item.item_uid is char(32)

    @Test
    public void requestIdsFitTheLedgerAndAreDistinct_D172() {
        UUID o = UUID.randomUUID();
        // cr_p1_txn.request_id is varchar(64); the refund hold adds "refund:" (cr_p1_delivery.request_id varchar(96))
        for (String rid : new String[]{EmberPayRules.undoRid(UID, 123456), EmberPayRules.rerollRid(UID, 99999),
                EmberPayRules.redeemRid(o, Long.MAX_VALUE, Integer.MIN_VALUE)}) {
            assertTrue(rid, rid.length() <= 64);
            assertEquals("refund:" + rid, EmberItemStore.holdId(rid));
        }
        // the undo id is the pre-1.65.5 one (a replay of an old undo still hits the same ledger row)
        assertEquals("undo:" + UID + ":7", EmberPayRules.undoRid(UID, 7));
        assertNotEquals(EmberPayRules.rerollRid(UID, 1), EmberPayRules.rerollRid(UID, 2));
        assertNotEquals(EmberPayRules.undoRid(UID, 1), EmberPayRules.undoRid(UID, 2));
        assertNotEquals(EmberPayRules.redeemRid(o, 1000L, 5), EmberPayRules.redeemRid(o, 1001L, 5));
        assertNotEquals(EmberPayRules.redeemRid(o, 1000L, 5), EmberPayRules.redeemRid(o, 1000L, 6));
        // paid markers per request
        assertNotEquals(EmberDelivery.paidMarker(EmberPayRules.undoRid(UID, 1)), EmberDelivery.paidMarker(EmberPayRules.undoRid(UID, 2)));
    }

    @Test
    public void priceOwesOneRefundLinePerPart_D172() {
        EmberPay.Price blanks = EmberPay.Price.of(new Cost(0, 0, 3, 0, 0));
        List<EmberItemStore.Owed> l = blanks.owed("n");
        assertEquals(1, l.size());
        assertEquals("mat", l.get(0).kind);
        assertEquals(EmberUpgradeRules.MAT_BLANK, l.get(0).item);
        assertEquals(3, l.get(0).amount);

        EmberPay.Price reroll = EmberPay.Price.of(new Cost(40, 0, 0, 0, 600));
        l = reroll.owed("n");
        assertEquals(2, l.size());
        assertEquals("coin", l.get(1).kind);
        assertEquals(600, l.get(1).amount);

        EmberPay.Price marks = EmberPay.Price.marks(2, 8);
        l = marks.owed("n");
        assertEquals(1, l.size());
        assertEquals("mark", l.get(0).kind);
        assertEquals("t2", l.get(0).item);
        assertEquals(8, l.get(0).amount);
        assertTrue(marks.json().contains("\"mark_t2\":8"));
        assertTrue(marks.label().contains("T2 印记×8"));

        assertTrue(EmberPay.Price.of(Cost.NONE).free());
        assertTrue(EmberPay.Price.marks(0, 8).owed("n").isEmpty()); // tier out of range owes nothing (and takes nothing)
        assertFalse(blanks.free());
    }

    @Test
    public void batchPriceIsTheSumOfItsPieces_D172() {
        EmberPay.Price a = EmberPay.Price.of(new Cost(0, 0, 1, 0, 0)), b = EmberPay.Price.of(new Cost(0, 0, 2, 0, 0));
        EmberPay.Price s = EmberPay.Price.of(Cost.NONE).plus(a).plus(b).plus(a);
        assertEquals(4, s.cost.blanks);
        assertEquals(4, s.owed("n").get(0).amount);
        EmberPay.Price m = EmberPay.Price.marks(1, 8).plus(EmberPay.Price.of(new Cost(0, 0, 0, 0, 100)));
        assertEquals(8, m.marks);
        assertEquals(1, m.markTier);
        assertEquals(100, m.cost.coins);
        try { EmberPay.Price.marks(1, 8).plus(EmberPay.Price.marks(2, 8)); fail("mixed tiers"); } catch (IllegalArgumentException expected) { }
    }

    @Test
    public void markDeliveryOnlyTouchesForgeMarkCounters_D172() {
        assertTrue(EmberPayRules.markItem("t1"));
        assertTrue(EmberPayRules.markItem("t3"));
        assertFalse(EmberPayRules.markItem("t4"));
        assertFalse(EmberPayRules.markItem("coin"));
        assertFalse(EmberPayRules.markItem("t1@all"));
        assertFalse(EmberPayRules.markItem(null));
        assertEquals(EmberRunService.C_MARK + "2", EmberPayRules.markCounter("t2"));
    }

    @Test
    public void rollOwedEncodesSequenceAndLock_D172() {
        for (int n = 1; n < 50; n++) for (boolean lock : new boolean[]{false, true}) {
            int v = EmberPayRules.owedValue(n, lock);
            assertTrue(v > 0); // absent == 0 == "nothing owed"
            assertEquals(n, EmberPayRules.owedSeq(v));
            assertEquals(lock, EmberPayRules.owedLock(v));
        }
        assertFalse(EmberPayRules.C_RRN.equals(EmberPayRules.C_RRO));
        // addPeriodCount drops other periods of the SAME name: the two counters must not be prefixes of each other
        assertFalse(EmberPayRules.C_RRN.startsWith(EmberPayRules.C_RRO) || EmberPayRules.C_RRO.startsWith(EmberPayRules.C_RRN));
    }

    @Test
    public void recoveryRollsOnlyWhenCommitted_D172() {
        assertEquals(EmberPayRules.Recovery.APPLY, EmberPayRules.recovery("committed"));
        assertEquals(EmberPayRules.Recovery.WAIT, EmberPayRules.recovery("hold"));
        assertEquals(EmberPayRules.Recovery.WAIT, EmberPayRules.recovery(null)); // unreadable → later, never a guess
        assertEquals(EmberPayRules.Recovery.CLEAR, EmberPayRules.recovery("none"));
    }

    @Test
    public void seededRollIsTheSameEveryTime_D172() {
        Map<?, ?> root = (Map<?, ?>) new Yaml().load(new InputStreamReader(EmberPayTest.class.getResourceAsStream("/" + EmberGrowth.FILE), StandardCharsets.UTF_8));
        EmberAffix.Rules r = EmberAffix.parse(root);
        String owner = UUID.randomUUID().toString();
        int same = 0, differ = 0;
        for (int n = 1; n <= 200; n++) {
            String rid = EmberPayRules.rerollRid(UID, n);
            long s1 = EmberPayRules.seed(42L, owner, rid), s2 = EmberPayRules.seed(42L, owner, rid);
            assertEquals(s1, s2);
            EmberAffix.Roll a = EmberAffix.roll(r, "blade", 3, 0, new Random(s1)), b = EmberAffix.roll(r, "blade", 3, 0, new Random(s2));
            assertEquals(a.id, b.id);
            assertEquals(a.tier, b.tier);
            assertEquals(a.pityAfter, b.pityAfter);
            if (EmberPayRules.seed(43L, owner, rid) == s1) same++; else differ++;
        }
        assertEquals(0, same); // another server salt → other seeds
        assertNotEquals(EmberPayRules.seed(42L, owner, EmberPayRules.rerollRid(UID, 1)), EmberPayRules.seed(42L, owner, EmberPayRules.rerollRid(UID, 2)));
        assertNotEquals(EmberPayRules.seed(42L, owner, "x"), EmberPayRules.seed(42L, UUID.randomUUID().toString(), "x"));
        assertEquals(200, differ);
    }

    @Test
    public void afterPayFaultIsInertOutsideTheTestEnv_D172() {
        if (EmberFaults.enabled()) return;
        UUID u = UUID.randomUUID();
        assertFalse(EmberFaults.arm(u, EmberFaults.AFTER_PAY));
        assertFalse(EmberFaults.fire(u, EmberFaults.AFTER_PAY));
    }
}
