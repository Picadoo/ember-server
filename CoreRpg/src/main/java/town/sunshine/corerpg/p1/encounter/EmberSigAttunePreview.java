package town.sunshine.corerpg.p1.encounter;

import java.util.ArrayList;
import java.util.List;

/**
 * D297 方案 M：进本前「本局生效」只读文案（无 Bukkit）。
 * 不改 ALTS / 烙印价 / 掉率。
 */
public final class EmberSigAttunePreview {

    public static final class Slot {
        public final String slotLabel; // 刃 / 护符
        public final String edition;   // 原版 / 调律
        public final String costNow;
        public final String costOther; // may be null

        public Slot(String slotLabel, String edition, String costNow, String costOther) {
            this.slotLabel = slotLabel;
            this.edition = edition;
            this.costNow = costNow;
            this.costOther = costOther;
        }
    }

    private EmberSigAttunePreview() {}

    /** §7本局生效：§f刃·调律 §8/ §f护符·原版 — empty if no slots */
    public static String runLine(List<Slot> slots) {
        if (slots == null || slots.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("§7本局生效：");
        for (int i = 0; i < slots.size(); i++) {
            if (i > 0) sb.append(" §8/ ");
            Slot s = slots.get(i);
            sb.append("§f").append(s.slotLabel).append("·").append(s.edition);
        }
        return sb.toString();
    }

    /** §8代价：§7… · §7… — empty if no slots */
    public static String costLine(List<Slot> slots) {
        if (slots == null || slots.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("§8代价：");
        for (int i = 0; i < slots.size(); i++) {
            if (i > 0) sb.append(" §8· ");
            Slot s = slots.get(i);
            sb.append("§7").append(s.costNow);
            if ("调律".equals(s.edition)) sb.append(" §8（调）");
            else sb.append(" §8（原）");
        }
        return sb.toString();
    }

    public static List<Slot> listOf(Slot a, Slot b) {
        List<Slot> out = new ArrayList<Slot>(2);
        if (a != null) out.add(a);
        if (b != null) out.add(b);
        return out;
    }
}
