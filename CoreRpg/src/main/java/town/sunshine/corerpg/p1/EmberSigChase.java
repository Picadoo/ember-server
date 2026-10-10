package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D464: signature imprint-chase path pick — commit which Lxx you farm marks to imprint.
 * D470: rising-edge imprint-ready cue when chase marks hit threshold.
 * D472: imprint-complete feel + chase-close offer (acquisition loop close).
 * D604: hope-meter style via EmberHopeMeter.sigChase.
 * Zero STAMP_RATE / imprint price / ALTS / clear-gate change.
 */
public final class EmberSigChase {

    public static final String C_CHASE = "p1_sig_chase";
    public static final String C_OFFERED = "p1_sig_chase_offered";
    /** Def.code latched after ready cue; cleared when marks drop below need or chase clears. */
    public static final String C_READY = "p1_sig_chase_ready";

    private EmberSigChase() {}

    public static EmberSignature.Def get(PlayerData d) {
        if (d == null) return null;
        int c = d.periodCount(C_CHASE, "all");
        return c > 0 ? EmberSignature.byCode(c) : null;
    }

    public static boolean set(PlayerData d, EmberSignature.Def def) {
        if (d == null || def == null) return false;
        int cur = d.periodCount(C_CHASE, "all");
        if (cur == def.code) return false;
        d.addPeriodCount(C_CHASE, "all", def.code - cur);
        return true;
    }

    public static boolean clear(PlayerData d) {
        if (d == null) return false;
        int cur = d.periodCount(C_CHASE, "all");
        if (cur <= 0) return false;
        d.addPeriodCount(C_CHASE, "all", -cur);
        clearReadyLatch(d);
        return true;
    }

    static void clearReadyLatch(PlayerData d) {
        if (d == null) return;
        int r = d.periodCount(C_READY, "all");
        if (r > 0) d.addPeriodCount(C_READY, "all", -r);
    }

    public static EmberSignature.Def parse(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        String s = raw.trim();
        if ("clear".equalsIgnoreCase(s) || "none".equalsIgnoreCase(s) || "off".equalsIgnoreCase(s) || "取消".equals(s))
            return null; // sentinel handled by caller via clear word
        EmberSignature.Def byId = EmberSignature.byId(s);
        if (byId != null) return byId;
        try {
            int n = Integer.parseInt(s);
            return EmberSignature.byCode(n);
        } catch (NumberFormatException ignored) {}
        for (EmberSignature.Def d : EmberSignature.DEFS) {
            if (d.name.equals(s) || d.name.contains(s)) return d;
        }
        return null;
    }

    public static String label(EmberSignature.Def def) {
        if (def == null) return "未选";
        return def.id + " " + def.name;
    }

    public static String tip(EmberSignature.Def def) {
        if (def == null) return "点选一条签名当追烙目标";
        return "回 " + def.map.toUpperCase(Locale.ROOT) + " 刷徽记 · " + EmberSignature.imprintMarks() + " 枚可烙";
    }

    /** Progress / ready line — D604 hope-meter style (still imprint-ready when full). */
    public static String progressLine(EmberSignature.Def def, int marks) {
        return EmberHopeMeter.sigChase(def, marks);
    }

    /** D1355: non-hope remain toward imprint（聊天可并行 hope glance）. */
    public static String remainLine(EmberSignature.Def def, int marks) {
        if (def == null) return "未选追烙目标 · /corerpg p1 sig chase";
        int need = EmberSignature.imprintMarks();
        int m = Math.max(0, marks);
        int gap = Math.max(0, need - m);
        if (gap == 0) return "追烙 " + def.id + " · 徽记够 · 可烙";
        return "追烙 " + def.id + " · 徽记 " + m + "/" + need + " · 还差" + gap;
    }

    /** D1997: status/settle/enter glance — empty when no chase (caller skips). */
    public static String statusGlance(PlayerData d) {
        EmberSignature.Def def = get(d);
        if (def == null) return "";
        int marks = d == null ? 0 : d.periodCount(EmberSignature.C_MARK + def.map, "all");
        return remainLine(def, marks);
    }

    /** True when chase targets this map key (q01…). */
    public static boolean chaseMap(PlayerData d, String mapKey) {
        EmberSignature.Def def = get(d);
        if (def == null || mapKey == null) return false;
        return mapKey.equalsIgnoreCase(def.map);
    }

    public static String glance(PlayerData d) {
        EmberSignature.Def def = get(d);
        if (def == null) return EmberHopeMeter.sigChase(null, 0);
        int marks = d.periodCount(EmberSignature.C_MARK + def.map, "all");
        return progressLine(def, marks);
    }

    /** Append to D456 miss line when settle map matches chase. */
    public static String missAppend(PlayerData d, String mapKey) {
        EmberSignature.Def def = get(d);
        if (def == null || mapKey == null || !def.map.equalsIgnoreCase(mapKey)) return "";
        int marks = d.periodCount(EmberSignature.C_MARK + def.map, "all");
        int need = EmberSignature.imprintMarks();
        if (marks >= need) return " §a· 追烙 " + def.id + " 已够烙";
        return String.format(Locale.ROOT, " §8· 追烙 %s %d/%d", def.id, marks, need);
    }

    public static void scheduleImprintOffer(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) { offerImprintUnlock(p); return; }
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (p.isOnline()) offerImprintUnlock(p);
        }, 50L);
    }

    /** First-pick after Q02 imprint unlock: chase L01 or L02 (Q01 farm path). */
    public static void offerImprintUnlock(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        if (d.periodCount(C_OFFERED, "all") > 0) return;
        d.addPeriodCount(C_OFFERED, "all", 1);
        flush(p);
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选一条签名当追烙目标（刷对应图徽记 · " + EmberSignature.imprintMarks() + " 枚可烙）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[L01 残门焚斧]", "/corerpg p1 sig chase L01", tip(EmberSignature.byId("L01")), "GOLD"},
                new String[]{"[L02 门楼余烬]", "/corerpg p1 sig chase L02", tip(EmberSignature.byId("L02")), "AQUA"},
                new String[]{"[打开签名页]", "/corerpg p1 sig menu", "图鉴 / 烙印", "YELLOW"},
                new String[]{"[稍后再说]", null, "随时 /corerpg p1 sig chase", "GRAY"});
    }

    /** Offer all defs for maps the player has first-cleared (or all if none). */
    public static void offerPick(Player p, PlayerData d, EmberRunService runs) {
        if (p == null || d == null) return;
        String P = EmberRunService.P;
        List<EmberSignature.Def> opts = new ArrayList<EmberSignature.Def>();
        for (EmberSignature.Def def : EmberSignature.DEFS) {
            if (runs == null || runs.progressFlag(d, def.map)) opts.add(def);
        }
        if (opts.isEmpty()) opts.addAll(EmberSignature.DEFS);
        p.sendMessage(P + "§e追烙目标（当前：" + label(get(d)) + "）· 点一条：");
        // chat can fit ~5 buttons; offer first 5 cleared + open menu
        int n = Math.min(5, opts.size());
        String[][] btns = new String[n + 2][];
        for (int i = 0; i < n; i++) {
            EmberSignature.Def def = opts.get(i);
            btns[i] = new String[]{"[" + def.id + "]", "/corerpg p1 sig chase " + def.id, tip(def), "GOLD"};
        }
        btns[n] = new String[]{"[取消追烙]", "/corerpg p1 sig chase clear", "清空目标", "GRAY"};
        btns[n + 1] = new String[]{"[签名页]", "/corerpg p1 sig menu", "图鉴烙印", "AQUA"};
        ConfirmTokens.sendButtons(p, P, btns);
        if (opts.size() > 5)
            p.sendMessage(P + "§8更多：/corerpg p1 sig chase L03…L15");
    }

    
    /**
     * D470: if chase target has enough marks and we have not yet told for this Def.code,
     * announce ready + [去烙印] button. Clears latch when under threshold.
     * @return true if a rising-edge cue was shown
     */

    /**
     * D472: after successful imprint — ActionBar feel; if chase matched, clear chase and offer next.
     * @return true if chase was closed as complete
     */
    public static boolean afterImprint(Player p, PlayerData d, EmberSignature.Def sd) {
        if (p == null || sd == null) return false;
        String feel = "§d签名烙印 · §f" + sd.name + " §a完成";
        try { p.sendActionBar(feel); } catch (Throwable ignored) { }
        EmberSignature.Def chase = get(d);
        if (chase == null || chase.code != sd.code) return false;
        clear(d); // closes chase after successful imprint of target
        clearReadyLatch(d);
        if (p != null) flush(p);
        String P = EmberRunService.P;
        p.sendMessage(P + "§a追烙完成：§f" + label(sd) + " §7· 已烙上，可换下一条");
        return true;
    }

    /**
     * D647: imprint spend complete UX — always after successful pay (chase may or may not close).
     * Zero imprint price / mark table change.
     * @param worn true if the imprinted piece is currently equipped in its slot
     * @param sigOff true if that slot's signature toggle is off
     * @param chaseClosed whether {@link #afterImprint} closed the chase
     */
    public static void afterSpendComplete(Player p, PlayerData d, EmberSignature.Def sd,
                                          boolean worn, boolean sigOff, boolean chaseClosed) {
        if (p == null || sd == null) return;
        String P = EmberRunService.P;
        int left = d == null ? 0 : d.periodCount(EmberSignature.C_MARK + sd.map, "all");
        p.sendMessage(P + "§7烙印花费闭环 · 剩 §f" + left + " §7枚 " + sd.map.toUpperCase(java.util.Locale.ROOT) + " 徽记"
                + (left >= EmberSignature.imprintMarks() ? " §a· 还可再烙" : ""));
        java.util.List<String[]> btns = new java.util.ArrayList<String[]>();
        if (!worn) {
            btns.add(new String[]{"[去装备页]", "/corerpg p1 gear", "穿上/选定后签名才生效", "GOLD"});
        } else if (sigOff) {
            btns.add(new String[]{"[打开签名]", "/corerpg p1 sig toggle " + sd.slot, "槽位签名已关 · 点开", "YELLOW"});
        } else {
            // D2115: worn+on → piece forge commit (not sig-dry twin)
            btns.add(EmberForgeCommitNext.enhance());
        }
        if (chaseClosed) {
            btns.add(new String[]{"[换追烙]", "/corerpg p1 sig chase offer", "点下一条签名", "AQUA"});
        } else {
            btns.add(new String[]{"[追烙进度]", "/corerpg p1 sig chase", "看徽记与下一条", "AQUA"});
        }
        btns.add(new String[]{"[签名页]", "/corerpg p1 sig menu", "图鉴 / 开关", "GRAY"});
        ConfirmTokens.sendButtons(p, P + "§7下一步：", btns.toArray(new String[0][]));
    }


    /**
     * D653: stamp drop hit chase — acquisition loop close without waiting for imprint marks.
     * Zero STAMP_RATE / imprint price change.
     */
    public static boolean afterStampHit(Player p, PlayerData d, EmberSignature.Def sd) {
        if (p == null || sd == null || d == null) return false;
        if (!closesChase(get(d), sd)) return false;
        String P = EmberRunService.P;
        p.sendMessage(P + "§6追烙掉落命中：§f" + label(sd) + " §a· 签名件已到账");
        try { p.sendActionBar("§6追烙命中 · §f" + sd.name); } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P + "§7下一步：", // D915
                new String[]{"[去烙印同款]", "/corerpg p1 sig pick " + sd.id, "徽记够可烙到穿着件", "GOLD"},
                new String[]{"[工坊]", "/corerpg p1 forge", "养成到账件", "GREEN"},
                new String[]{"[成套追]", "/corerpg p1 setchase", "换装", "AQUA"});
        return true;
    }

    /** Pure: whether imprint of {@code sd} should close chase {@code chase}. */
    public static boolean closesChase(EmberSignature.Def chase, EmberSignature.Def sd) {
        return chase != null && sd != null && chase.code == sd.code;
    }

    public static boolean maybeReadyCue(Player p, PlayerData d) {
        if (p == null || d == null) return false;
        EmberSignature.Def def = get(d);
        if (def == null) {
            clearReadyLatch(d);
            return false;
        }
        int marks = d.periodCount(EmberSignature.C_MARK + def.map, "all");
        int need = EmberSignature.imprintMarks();
        int latched = d.periodCount(C_READY, "all");
        if (marks < need) {
            if (latched > 0) clearReadyLatch(d);
            return false;
        }
        if (latched == def.code) return false; // already told for this chase
        // rising edge (or chase switched onto an already-ready target)
        int delta = def.code - latched;
        d.addPeriodCount(C_READY, "all", delta);
        flush(p);
        String P = EmberRunService.P;
        String line = progressLine(def, marks);
        p.sendMessage(P + line);
        String rem = remainLine(def, marks);
        p.sendMessage(P + "§7" + rem);
        try { p.sendActionBar("§e" + rem); } catch (Throwable ignored) { }
        ConfirmTokens.sendButton(p, P + "§7下一步：", // D753 one-click when no overwrite needed
                "[一键烙印·" + def.id + "]", "/corerpg p1 sig imprint " + def.id + " go",
                EmberSignature.imprintMarks() + " 枚徽记 · 烙到穿着" + EmberItemData.slotName(def.slot) + "（无覆盖则直接花）");
        return true;
    }

    public static void forceOffer(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d != null) {
            int cur = d.periodCount(C_OFFERED, "all");
            if (cur > 0) d.addPeriodCount(C_OFFERED, "all", -cur);
            flush(p);
        }
        offerImprintUnlock(p);
    }

    private static PlayerData dataOf(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (!(pl instanceof town.sunshine.corerpg.CoreRpgPlugin)) return null;
        return ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().get(p.getUniqueId());
    }

    private static void flush(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin)
            ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().flushMutation(p.getUniqueId());
    }
}
