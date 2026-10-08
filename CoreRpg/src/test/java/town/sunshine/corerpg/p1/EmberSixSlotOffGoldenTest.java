package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import static org.junit.Assert.assertEquals;

/**
 * D318 六槽 T1-1「开关关 = 逐位不变」: a SHA-256 over the exact bits of B / H0 / H / D / M / EHP (+ active set / awakening)
 * of every 2-slot reference loadout in a 1.8M-cell grid (blade null|fam×T0–3×+0/6/10×q0/3×f0/3; charm null|fam×T0–3×
 * +0..10×q0–3×f0–3; Lv 1/30/60; festival charm none | 50/5). The golden digest was written by this test against the
 * pre-D318 code (CoreRpg 1.65.97, commit f2b74dec) with {@code -Dsix.golden.write=true}; after D318 the 2-slot overloads
 * and the six-slot overload with the switch off (armor == null, which is what EmberLoadoutService passes when
 * {@code gear.six_slot.enabled} is false) must reproduce it bit for bit.
 */
public class EmberSixSlotOffGoldenTest {

    static final Path GOLDEN = Paths.get("src/test/resources/six/t1-1-two-slot-golden.txt");
    static final String[] FAMS = {"scorch", "burst", "sustain"};

    /** mode 0 = compute(t, b, c, lv, fh, fd); mode 1 = the D318 overload with armor == null (switch off) */
    static String digest(int mode) throws Exception {
        EmberTables t = EmberTables.defaults();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(8 * 6 + 8);
        long n = 0;
        java.util.List<EmberItemData> blades = new java.util.ArrayList<EmberItemData>();
        blades.add(null);
        for (String f : FAMS) for (int tier = 0; tier <= 3; tier++) for (int e : new int[]{0, 6, 10}) for (int q : new int[]{0, 3}) for (int c : new int[]{0, 3})
            blades.add(new EmberItemData(String.format("%032x", blades.size()), EmberItemData.templateId(tier == 0 ? "none" : f, "blade", tier),
                    tier == 0 ? "none" : f, "blade", tier, q, c, e, 0, true, "drop", 2, 0));
        java.util.List<EmberItemData> charms = new java.util.ArrayList<EmberItemData>();
        charms.add(null);
        for (String f : FAMS) for (int tier = 0; tier <= 3; tier++) for (int e = 0; e <= 10; e++) for (int q = 0; q <= 3; q++) for (int c = 0; c <= 3; c++)
            charms.add(new EmberItemData(String.format("c%031x", charms.size()), EmberItemData.templateId(tier == 0 ? "none" : f, "charm", tier),
                    tier == 0 ? "none" : f, "charm", tier, q, c, e, 0, true, "drop", 2, 0));
        for (EmberItemData b : blades) for (EmberItemData c : charms) for (int lv : new int[]{1, 30, 60}) for (int fest = 0; fest < 2; fest++) {
            double fh = fest == 0 ? 0 : 50, fd = fest == 0 ? 0 : 5;
            EmberLoadout l = compute(mode, t, b, c, lv, fh, fd);
            buf.clear();
            buf.putLong(Double.doubleToLongBits(l.b)).putLong(Double.doubleToLongBits(l.h0)).putLong(Double.doubleToLongBits(l.h))
               .putLong(Double.doubleToLongBits(l.d)).putLong(Double.doubleToLongBits(l.m)).putLong(Double.doubleToLongBits(l.ehp()))
               .putInt(l.awakening).putInt(l.activeSet.hashCode());
            md.update(buf.array());
            n++;
        }
        StringBuilder sb = new StringBuilder();
        for (byte x : md.digest()) sb.append(String.format("%02x", x & 0xff));
        return n + " " + sb;
    }

    static EmberLoadout compute(int mode, EmberTables t, EmberItemData b, EmberItemData c, int lv, double fh, double fd) {
        if (mode == 1) return EmberLoadout.compute(t, b, c, lv, fh, fd, null);
        return EmberLoadout.compute(t, b, c, lv, fh, fd);
    }

    @Test public void twoSlotPathsBitIdenticalToPreD318Golden() throws Exception {
        if (Boolean.getBoolean("six.golden.write")) {
            Files.write(GOLDEN, (digest(0) + "\n").getBytes(StandardCharsets.UTF_8));
            return;
        }
        String want = new String(Files.readAllBytes(GOLDEN), StandardCharsets.UTF_8).trim();
        assertEquals("2-slot overload vs pre-D318 golden", want, digest(0));
        assertEquals("six-slot overload with switch off (armor null) vs pre-D318 golden", want, digest(1));
    }
}
