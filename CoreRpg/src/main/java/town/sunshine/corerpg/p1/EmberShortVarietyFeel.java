package town.sunshine.corerpg.p1;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * TIP-CP D13321: short light variety — room HUD/喊话（词缀/事件可感）.
 * Settle 零发 event_core/affix_shard（玩法可感 · 零抬经济）；零改 yaml rate / Cast / Aid / todayRot cue.
 * W1e todayRot 偏置本窗不做.
 */
public final class EmberShortVarietyFeel {

    public static final String AFFIX_TELL = "§6【短征花样】§f词缀精英 · §7先集火！";
    public static final String EVENT_TELL = "§6【短征花样】§f房间事件 · §7按提示做";
    public static final String AFFIX_AB_PREFIX = "§6短征花样 · §f";
    public static final String EVENT_AB_PREFIX = "§6短征花样 · §f";

    private EmberShortVarietyFeel() {}

    /**
     * D138/D13321: variety rolls on plain main OR short expedition (call site still gates firstCleared).
     * Never challenge / abyss / raid / rush. {@code shortExpedition} is intentionally allowed.
     */
    public static boolean varietyEligible(boolean challenge, int abyss, boolean raid, boolean rush,
                                         boolean shortExpedition, boolean varietyOn) {
        if (!varietyOn) return false;
        if (challenge || abyss > 0 || raid || rush) return false;
        // plain main (short=false) or short light variety (short=true) — both OK
        return true;
    }

    /** Affix elite ActionBar: 词缀中文名. */
    public static String affixAb(String affixLabel) {
        String tag = affixLabel == null || affixLabel.isEmpty() ? "词缀" : affixLabel;
        return AFFIX_AB_PREFIX + tag;
    }

    /** Event room ActionBar: 事件种短名. */
    public static String eventAb(String eventLabel) {
        String tag = eventLabel == null || eventLabel.isEmpty() ? "事件" : eventLabel;
        return EVENT_AB_PREFIX + tag;
    }

    /**
     * Affix promote chat for short: how-to only — <b>no</b> affix_shard reward promise.
     */
    public static String affixHowTell(String tag, String how) {
        String t = tag == null || tag.isEmpty() ? "?" : tag;
        String h = how == null ? "" : how;
        return "§6词缀精英「" + t + "」§7出现：" + h;
    }

    /**
     * True when a grant key is a variety economic row (event_core / affix_shard family).
     * Short settle must never emit these (W1d).
     */
    public static boolean isVarietyEconomyKey(String key) {
        if (key == null || key.isEmpty()) return false;
        return key.startsWith("var_affix") || key.startsWith("var_event");
    }

    /** Flash short-variety affix cue (AB + tell + soft sound). */
    public static void affixCue(Player p, String affixLabel) {
        if (p == null) return;
        try { p.sendMessage(EmberRunService.P + AFFIX_TELL); } catch (Throwable ignored) { }
        try { p.sendActionBar(affixAb(affixLabel)); } catch (Throwable ignored) { }
        try {
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4f, 1.2f);
        } catch (Throwable ignored) { }
    }

    /** Flash short-variety event cue (AB + tell + soft sound). */
    public static void eventCue(Player p, String eventLabel) {
        if (p == null) return;
        try { p.sendMessage(EmberRunService.P + EVENT_TELL); } catch (Throwable ignored) { }
        try { p.sendActionBar(eventAb(eventLabel)); } catch (Throwable ignored) { }
        try {
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4f, 1.15f);
        } catch (Throwable ignored) { }
    }
}
