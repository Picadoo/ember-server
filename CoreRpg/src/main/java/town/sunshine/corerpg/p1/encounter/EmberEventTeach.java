package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberRunMaps;

/**
 * D296 房间事件「本局必感」教学：账户种首次短闪（镜像 D283 firstFlash UX，不共用破绽计数器）。
 * Ledger：{@code p1_evteach_<kind>} period {@code all}。
 */
public final class EmberEventTeach {

    /** period-count prefix; + known event kind */
    public static final String C_TEACH = "p1_evteach_";

    private EmberEventTeach() {}

    /**
     * §3.4 短闪动词（≤8 字面）；未知 kind → ""（开房仍走长 chat）。
     */
    public static String teachFlash(String kind) {
        if (kind == null || kind.isEmpty()) return "";
        switch (kind) {
            case "timed":     return "§e限时清房！";
            case "crystal":   return "§e砸余烬晶！";
            case "escort":    return "§e护住宝兔！";
            case "hold":      return "§e占住光圈！";
            case "beacon":    return "§e护住灯柱！";
            case "relay":     return "§e按序传火！";
            case "breach":    return "§e裂隙·圈内杀！";
            case "chain":     return "§e连斩别断！";
            case "unscathed": return "§e少挨打！";
            default:          return "";
        }
    }

    /** W1c 开房 HUD：ActionBar 短名（复用 {@link EmberRunMaps.Variety#eventLabel}）。 */
    public static String openHud(String kind) {
        if (kind == null || kind.isEmpty()) kind = "timed";
        if (!EmberRunMaps.Variety.EVENTS.contains(kind) && !"timed".equals(kind)) return "";
        return "§b本房事件：§f" + EmberRunMaps.Variety.eventLabel(kind);
    }

    /**
     * 账户生涯首次该 kind：返回 true 并记 1；已教过 / null / 未知 kind → false。
     * 同局同 kind 不重复（ledger 已写）。
     */
    public static boolean tryMarkTeach(PlayerData d, String kind) {
        if (d == null || kind == null || kind.isEmpty()) return false;
        if (!EmberRunMaps.Variety.EVENTS.contains(kind)) return false;
        String key = C_TEACH + kind;
        if (d.periodCount(key, "all") > 0) return false;
        d.addPeriodCount(key, "all", 1);
        return true;
    }
}
