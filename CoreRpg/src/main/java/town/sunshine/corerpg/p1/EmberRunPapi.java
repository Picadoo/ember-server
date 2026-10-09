package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.StaminaService;

import java.util.List;
import java.util.Locale;

/**
 * D240 / ARCH S3-11: PlaceholderAPI {@code %corerpg_p1_*%} split out of {@link EmberRunService} into named sections,
 * with <b>no behaviour change</b> (same keys, same values, same first-match order).
 * <p>{@link #route} is a Bukkit-free, ordered copy of the old if-chain: it returns the section that the old code would
 * have entered first. A section returns {@code null} only where the old chain <i>fell through</i> (rush_ entry not in the
 * rush table, season / cosmetics / festival absent or declining) and then — exactly like before — the per-map
 * {@code <map>_<field>} tail ({@link Section#MAP}) answers. Every other section always answers non-null.
 * <p>{@link EmberRunService#placeholder} is a one-line delegate; {@code CoreRpgExpansion} still strips {@code p1_}.
 */
public final class EmberRunPapi {

    /** Section of the {@code p1_} namespace (declaration order = old if-chain order of first match). */
    public enum Section {
        /** pass_ / passd_ / target / marks_t / pending / active / next / failrefund / recruits */
        ENTRY,
        /** vbounty / rush / pledge_ / rush_&lt;entry&gt; (falls through when not a rush entry) */
        RUSH,
        /** sign_ / online_ / afk_ / sig_ / reroll_ / honor_ / spec_ (delegated services) */
        GROWTH,
        /** forge_t2 / forge_t3 / challenge / q07done / q0Ndone */
        GATE,
        /** featured / featured_key / modifier / rule_ / bounty / loot_ */
        ROTATION,
        /** top_abyss_N / top_featured_N */
        BOARD,
        /** goal* / season* / sboard_ / srank_ / badges (falls through when season absent / declines) */
        SEASON,
        /** shop* / shop_ / title / honors (shop keys fall through when cosmetics absent / declines) */
        COSMETIC,
        /** fest_ (falls through when festival absent) */
        FESTIVAL,
        /** abyss_best / abyss_state / abyss_tN */
        ABYSS,
        /** raid_&lt;raid&gt; */
        RAID,
        /** awaken_route / awaken / awaken_next / set_progress / stats / ehp / blade / charm / D299 next_* / held_next_* / D307 held_*_cost|lack / D333 held_is_armor */
        LOADOUT,
        /** codex* */
        CODEX,
        /** D318 armor / armor_* (六槽护甲页, {@link EmberSixPapi}) */
        ARMOR,
        /** tail: &lt;map&gt;_&lt;state|open|cleared|name|cost|tier|purpose|fc&gt;, else "" */
        MAP
    }

    private final EmberRunService runs;

    EmberRunPapi(EmberRunService runs) {
        this.runs = runs;
    }

    // ------------------------------------------------------------------ Bukkit-free routing / texts

    /** Ordered copy of the pre-D240 if-chain (first match wins). Never null. */
    public static Section route(String key) {
        if (key == null) return Section.MAP;
        if (key.startsWith("pass_")) return Section.ENTRY;
        if ("target".equals(key)) return Section.ENTRY;
        if (key.startsWith("marks_t")) return Section.ENTRY;
        if ("pending".equals(key)) return Section.ENTRY;
        if ("active".equals(key)) return Section.ENTRY;
        if ("vbounty".equals(key)) return Section.RUSH;
        if ("rush".equals(key)) return Section.RUSH;
        if (key.startsWith("pledge_")) return Section.RUSH;
        if (key.startsWith("rush_")) return Section.RUSH;
        if (key.startsWith("passd_")) return Section.ENTRY;
        if (key.startsWith("sign_") || key.startsWith("online_") || key.startsWith("afk_") || key.startsWith("sig_")
                || key.startsWith("reroll_") || key.startsWith("honor_") || key.startsWith("spec_")) return Section.GROWTH;
        if ("forge_t2".equals(key) || "forge_t3".equals(key) || "challenge".equals(key) || "q07done".equals(key)) return Section.GATE;
        if (isQDone(key)) return Section.GATE;
        if ("featured".equals(key) || "modifier".equals(key) || key.startsWith("rule_")) return Section.ROTATION;
        if (key.startsWith("top_abyss_") || key.startsWith("top_featured_")) return Section.BOARD;
        if (key.startsWith("goal") || key.startsWith("season") || key.startsWith("sboard_") || key.startsWith("srank_")
                || "badges".equals(key)) return Section.SEASON;
        if (key.startsWith("shop")) return Section.COSMETIC; // shop* and shop_* (old: two checks, both cosmetics)
        if (key.startsWith("fest_")) return Section.FESTIVAL;
        if ("title".equals(key) || "honors".equals(key)) return Section.COSMETIC;
        if (key.startsWith("loot_")) return Section.ROTATION;
        if ("abyss_best".equals(key)) return Section.ABYSS;
        if ("bounty".equals(key)) return Section.ROTATION;
        if ("route_pri".equals(key) || "route_sec".equals(key) || "featured_left".equals(key)) return Section.ENTRY; // D306
        if ("next".equals(key)) return Section.ENTRY;
        if (key.startsWith("raid_")) return Section.RAID;
        if ("abyss_state".equals(key) || key.startsWith("abyss_t")) return Section.ABYSS;
        if ("recruits".equals(key) || "failrefund".equals(key)) return Section.ENTRY;
        if ("featured_key".equals(key)) return Section.ROTATION;
        if ("awaken_route".equals(key) || isLoadoutKey(key)) return Section.LOADOUT;
        if (key.startsWith("codex")) return Section.CODEX;
        if (key.startsWith("armor_")) return Section.ARMOR; // D318 (no earlier prefix matches "armor")
        return Section.MAP;
    }

    /** D174 stage 2b: q04done / q05done / q06done (7 chars, q0?done). */
    static boolean isQDone(String key) {
        return key.length() == 7 && key.startsWith("q0") && key.endsWith("done");
    }

    static boolean isLoadoutKey(String key) {
        return "awaken".equals(key) || "awaken_next".equals(key) || "set_progress".equals(key) || "stats".equals(key)
                || "ehp".equals(key) || "blade".equals(key) || "charm".equals(key)
                // D299 再刷短反馈：装备页近档 + 工坊手持近档
                || "blade_next_q".equals(key) || "blade_next_c".equals(key)
                || "charm_next_q".equals(key) || "charm_next_c".equals(key)
                || "held_next_q".equals(key) || "held_next_c".equals(key)
                // D307 工坊菜单诚实：本次费用 / 缺料 / 互换免费 / 分解得胚
                || "held_enhance_cost".equals(key) || "held_enhance_lack".equals(key)
                || "held_upgrade_cost".equals(key) || "held_upgrade_lack".equals(key)
                || "held_refine_lack".equals(key) || "held_quality_lack".equals(key)
                || "held_swap_cost".equals(key) || "held_dismantle_yield".equals(key)
                // D333 工坊手持甲菜单闸：主手可信 P1 甲 → 1，否则 0
                || "held_is_armor".equals(key);
    }

    /** D144 余烬连战 menu line (weekly reward still open vs. practice only). */
    static String rushLine(boolean pays) {
        return pays ? "§a本周奖励未领 · 失败可无限重试" : "§7本周奖励已领 · 可练习（无奖励）";
    }

    /** D174 stage 2b %corerpg_p1_rush_&lt;entry&gt;% once unlocked. */
    static String rushEntryLine(boolean pays, int used, int weekly) {
        return pays ? "§a本周已领 " + used + "/" + weekly + " · 失败可无限重试" : "§7本周 " + weekly + " 次已领完 · 可练习（无奖励）";
    }

    static String needFirstClear(String mapKey) {
        return "§8需本人首通 " + mapKey.toUpperCase(Locale.ROOT);
    }

    /** P2-10 legacy weekly board row (no season). */
    static String topRowText(String name, int value, boolean abyss) {
        return name + " · " + (abyss ? "第 " + value + " 层" : value + " 次");
    }

    /** top_abyss_N / top_featured_N → N in 1..10, else -1. */
    static int topRank(String key) {
        boolean ab = key.startsWith("top_abyss_");
        int i;
        try { i = Integer.parseInt(key.substring(ab ? 10 : 13)); } catch (NumberFormatException e) { return -1; }
        return i < 1 || i > 10 ? -1 : i;
    }

    static String abyssStateLine(boolean configured, boolean open, String requires, int best, int maxStart) {
        if (!configured) return "未配置";
        if (!open) return "需本人首通 " + requires.toUpperCase(Locale.ROOT);
        return "最高第 " + best + " 层 · 可开 1～" + maxStart + " 层";
    }

    /** B2.174 §19.1 装备页 actual B / H / D line. */
    static String statsLine(double b, double h, double d, double m, int level) {
        return String.format(Locale.ROOT, "攻击 %.1f · 生命 %.0f · 防御 %.0f（承伤 ×%.2f）· Lv%d", b, h, d, m, level);
    }

    static String awakenRouteLine(List<String> r) {
        return r.isEmpty() ? "" : "§7路线：" + r.get(0) + (r.size() > 1 ? " §8（还有 " + (r.size() - 1) + " 条，点开看）" : "");
    }

    // ------------------------------------------------------------------ dispatcher

    /** %corerpg_p1_&lt;key&gt;% (key already without {@code p1_}). */
    public String placeholder(Player p, String key) {
        EmberRunMaps maps = runs.maps();
        if (p == null || maps == null) return "";
        PlayerData d = runs.plugin().getDataStore().get(p.getUniqueId());
        String v;
        switch (route(key)) {
            case ENTRY: v = entry(p, d, key, maps); break;
            case RUSH: v = rush(p, d, key, maps); break;
            case GROWTH: v = growth(p, d, key); break;
            case GATE: v = gate(d, key); break;
            case ROTATION: v = rotation(d, key, maps); break;
            case BOARD: v = board(key); break;
            case SEASON: v = season(p, d, key); break;
            case COSMETIC: v = cosmetic(p, d, key); break;
            case FESTIVAL: v = festival(p, d, key); break;
            case ABYSS: v = abyss(d, key, maps); break;
            case RAID: v = runs.raid().papi(d, key.substring(5)); break; // P2-5 %corerpg_p1_raid_r01% (D233 → EmberRaidService)
            case LOADOUT: v = loadout(p, key); break;
            case CODEX: v = codex(p, d, key); break;
            case ARMOR: v = armor(p, key); break;
            default: v = null;
        }
        return v != null ? v : map(d, key, maps);
    }

    // ------------------------------------------------------------------ sections

    /** D318 %corerpg_p1_armor_*% (switch gear.six_slot.enabled off → "" / armor_on 0; 待领 keys stay live, D320 ④) */
    private String armor(Player p, String key) {
        EmberSixSlotService s = EmberSixSlotService.get();
        boolean on = EmberSixSlot.enabled() && s != null;
        if (!on) return EmberSixPapi.text(false, null, s == null || p == null || !key.startsWith("armor_stash") ? 0 : s.stashCount(p.getUniqueId()), key);
        return EmberSixPapi.text(true, s.cachedView(p), s.stashCount(p.getUniqueId()), key);
    }


    /** D306: hub 「今天该打哪」 primary / secondary from real stamina / afk_full / featured / q07. */
    private String routeLine(Player p, PlayerData d, String key) {
        StaminaService st = runs.plugin().getStaminaService();
        int stamina = st == null ? 0 : st.getStamina(d);
        int staminaMax = st == null ? 90 : st.getMax(d);
        int daily = st == null ? 30 : st.costOf("daily");
        int abyss = st == null ? 30 : st.costOf("abyss");
        int raid = st == null ? 50 : st.costOf("raid");
        boolean q07 = runs.progressFlag(d, "q07");
        int featuredLeft = runs.featuredLeft(d);
        String featuredShort = runs.featuredShort();
        String next = runs.nextStep(d, p == null ? null : p.getUniqueId());
        boolean afkFull = false;
        EmberAfkService afk = EmberAfkService.get();
        if (afk != null && p != null) {
            String f = afk.papi(p, d, "full");
            afkFull = "1".equals(f);
        }
        if ("route_sec".equals(key)) {
            return EmberHubRoute.secondary(stamina, daily, abyss, raid, afkFull, q07, featuredLeft);
        }
        return EmberHubRoute.primary(next, stamina, staminaMax, daily, abyss, raid, afkFull, q07, featuredLeft, featuredShort);
    }

    private String entry(Player p, PlayerData d, String key, EmberRunMaps maps) {
        if (key.startsWith("pass_")) return runs.hasPass(p.getUniqueId(), key.substring(5)) ? "yes" : "no";
        if ("target".equals(key)) { String t = runs.target(d); return t == null ? "未选择" : EmberItemData.familyName(t); }
        if (key.startsWith("marks_t")) {
            try { return String.valueOf(runs.marks(d, Integer.parseInt(key.substring(7)))); } catch (NumberFormatException e) { return "0"; }
        }
        if ("pending".equals(key)) { // D93: a first-clear choice waiting for a click is not "stuck in storage"
            int n = 0;
            for (EmberRunRules.Row r : runs.store().ledger(p.getUniqueId()).open()) if (!EmberRunRules.ST_AWAIT.equals(r.status)) n++;
            return String.valueOf(n);
        }
        if ("active".equals(key)) return EmberMode.active() ? "yes" : "no";
        if (key.startsWith("passd_")) { // D174 stage 2b: entries sharing one DP hall dungeon check the pass by dungeon
            Object[] ps = runs.passOf(p.getUniqueId());
            EmberRunMaps.MapDef pm = ps == null || System.currentTimeMillis() > (Long) ps[1] ? null : maps.byKey((String) ps[0]);
            return pm != null && pm.dungeon.equalsIgnoreCase(key.substring(6)) ? "yes" : "no";
        }
        if ("featured_left".equals(key)) return String.valueOf(runs.featuredLeft(d)); // D306
        if ("route_pri".equals(key) || "route_sec".equals(key)) return routeLine(p, d, key); // D306 hub daily routing
        if ("next".equals(key)) return runs.nextStep(d, p.getUniqueId()); // new-player polish %corerpg_p1_next%
        if ("recruits".equals(key)) return runs.recruitsLabel(); // E-review #5
        return runs.failRefundLabel(p.getUniqueId()); // failrefund · D128
    }

    private String rush(Player p, PlayerData d, String key, EmberRunMaps maps) {
        if ("vbounty".equals(key)) { String l = runs.varietyBountyLine(d); return l.isEmpty() ? "—" : l; } // D144 花样委托
        if ("rush".equals(key)) { // D144 余烬连战 menu line
            if (maps.rush.isEmpty()) return "未配置";
            if (!runs.progressFlag(d, "q07")) return "§8需本人首通 Q07";
            int used = runs.rushWeek(d, p.getUniqueId()); // D160
            return rushLine(EmberRunRules.rushPaysReward(used, EmberRunService.RUSH_WEEKLY));
        }
        if (key.startsWith("pledge_")) return runs.pledgePapi(d, key.substring(7)); // D174 stage 2b 自选誓约
        EmberRunMaps.MapDef rm = maps.rush.get(key.substring(5)); // rush_<entry>
        if (rm == null) return null; // old chain: not a rush entry → per-map tail
        if (!runs.progressFlag(d, rm.requires)) return needFirstClear(rm.requires);
        for (String ck : rm.chainKeys) if (!runs.progressFlag(d, ck)) return needFirstClear(ck);
        int used = runs.rushWeek(d, p.getUniqueId(), rm);
        return rushEntryLine(EmberRunRules.rushPaysReward(used, rm.rushWeekly), used, rm.rushWeekly);
    }

    private static String growth(Player p, PlayerData d, String key) {
        if (key.startsWith("sign_")) { EmberSignService g = EmberSignService.get(); return g == null ? "" : g.signPapi(p, d, key.substring(5)); } // D180
        if (key.startsWith("online_")) { EmberSignService g = EmberSignService.get(); return g == null ? "" : g.onlinePapi(p, d, key.substring(7)); } // D180
        if (key.startsWith("afk_")) { EmberAfkService a = EmberAfkService.get(); return a == null ? "" : a.papi(p, d, key.substring(4)); } // D177
        EmberGrowthService g = EmberGrowthService.get();
        if (key.startsWith("sig_")) return g == null ? "" : g.sigPapi(p, d, key.substring(4)); // D174 stage 1.5
        if (key.startsWith("reroll_")) return g == null ? "" : g.rerollPapi(p, d, key.substring(7)); // D143
        if (key.startsWith("honor_")) return g == null ? "" : g.honorPapi(p, d, key.substring(6)); // D142
        return g == null ? "" : g.papi(p, d, key.substring(5)); // spec_ · D141
    }

    private String gate(PlayerData d, String key) {
        if ("forge_t2".equals(key)) return runs.progressFlag(d, "q04") ? "已开放" : "需本人首通 Q04";
        if ("forge_t3".equals(key)) return runs.progressFlag(d, "q07") ? "已开放" : "需本人首通 Q07";
        if ("challenge".equals(key)) return runs.challengeOpen(d) ? "已开放" : "需本人首通 Q07";
        if ("q07done".equals(key)) return runs.progressFlag(d, "q07") ? "1" : "0"; // D99 menu condition (post-Q07 icons)
        return runs.progressFlag(d, key.substring(0, 3)) ? "1" : "0"; // D174 stage 2b: q04done / q05done / q06done
    }

    private String rotation(PlayerData d, String key, EmberRunMaps maps) {
        if ("featured".equals(key)) return runs.featuredLabel(d); // P2-1
        if ("modifier".equals(key)) return runs.modifierLabel(); // P2-8
        if (key.startsWith("rule_")) return runs.ruleLine(d, key.substring(5)); // D94
        if (key.startsWith("loot_")) {
            EmberRunMaps.MapDef lm = maps.byKey(key.substring(5));
            if (lm == null) lm = maps.raids.get(key.substring(5));
            return EmberRunMaps.lootLabel(lm) + runs.lootOdds(d, lm);
        }
        if ("bounty".equals(key)) return runs.bountyLabel(d); // P2-7 %corerpg_p1_bounty%
        return String.valueOf(runs.featured(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()))); // featured_key
    }

    private String board(String key) { // P2-10 %corerpg_p1_top_abyss_1%
        boolean ab = key.startsWith("top_abyss_");
        int i = topRank(key);
        if (i < 0) return "";
        EmberSeason season = runs.season();
        if (season != null) { // F-review #2: the season week board (same rows as the season page)
            List<EmberSeason.Row> sr = season.weekTop(ab ? "abyss" : "featured", i);
            return sr.size() < i ? "—" : sr.get(i - 1).name + " · " + EmberSeason.rowText(ab ? "abyss" : "featured", sr.get(i - 1));
        }
        EmberLeaderboard top = runs.top();
        if (top == null) return "";
        List<EmberLeaderboard.Row> rows = top.top(ab, EmberRunRules.rotationWeekKey(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone())), i);
        if (rows.size() < i) return "—";
        EmberLeaderboard.Row r = rows.get(i - 1);
        return topRowText(r.name, r.value, ab);
    }

    private String season(Player p, PlayerData d, String key) { // D116 / D117
        EmberSeason season = runs.season();
        return season == null ? null : season.papi(p.getUniqueId(), d, key);
    }

    private String cosmetic(Player p, PlayerData d, String key) {
        EmberCosmetics cosmetics = runs.cosmetics();
        if ("title".equals(key)) return cosmetics == null ? "" : cosmetics.titleMenuText(d); // P2-9 %corerpg_p1_title% (D103: never empty)
        if ("honors".equals(key)) return cosmetics == null ? "0/0" : cosmetics.earnedCount(d) + "/" + EmberCosmetics.ALL.size();
        if (cosmetics == null) return null; // old chain: shop keys without cosmetics → per-map tail
        if (key.startsWith("shop_")) return cosmetics.shopLabel(p.getUniqueId(), d, key.substring(5)); // D119
        return cosmetics.papi(p.getUniqueId(), d, key); // F-review #2 (D121); null → per-map tail as before
    }

    private String festival(Player p, PlayerData d, String key) { // D139
        EmberFestival festival = runs.festival();
        if (festival == null) return null;
        String v = festival.papi(p, d, key.substring(5));
        return v == null ? "" : v;
    }

    private String abyss(PlayerData d, String key, EmberRunMaps maps) {
        if ("abyss_best".equals(key)) return String.valueOf(runs.abyssBest(d)); // P2-2
        if ("abyss_state".equals(key)) {
            boolean configured = !maps.abyss.isEmpty();
            boolean open = configured && runs.abyssOpen(d);
            return abyssStateLine(configured, open, maps.abyssRequires, open ? runs.abyssBest(d) : 0, open ? runs.abyssMaxStart(d) : 0);
        }
        try {
            EmberRunMaps.AbyssTier t = maps.abyssTier(Integer.parseInt(key.substring(7)));
            return t == null ? "" : runs.abyssLine(d, t);
        } catch (NumberFormatException e) { return ""; }
    }

    private String loadout(Player p, String key) {
        if ("awaken_route".equals(key)) { // D120: cheapest real route (first line)
            List<String> r;
            if (Bukkit.isPrimaryThread()) r = runs.breakthroughRoutes(p, null);
            else try { final Player fp = p; r = Bukkit.getScheduler().callSyncMethod(runs.plugin(), () -> runs.breakthroughRoutes(fp, null)).get(750, java.util.concurrent.TimeUnit.MILLISECONDS); }
            catch (Exception e) { return ""; }
            return awakenRouteLine(r);
        }
        EmberLoadoutService ls = runs.plugin().getEmberLoadouts();
        EmberLoadout l = ls == null ? null : ls.get(p);
        if (l == null && ls != null) l = ls.refresh(p);
        if (l == null) return "";
        switch (key) {
            case "awaken": return l.setLabel();
            case "set_progress": return l.setProgress();
            // B2.174 §19.1 装备页: actual B / H / D (formula output, §19.2), main hand + selected charm
            case "stats": return statsLine(l.b, l.h, l.d, l.m, l.level);
            case "ehp": return String.format(Locale.ROOT, "%.0f", l.ehp());
            case "blade": return l.blade == null ? "主手没拿余烬刃" : l.blade.shortLabel();
            case "charm": return l.charm == null ? "未选定护符" : l.charm.shortLabel();
            // D299 W1a：穿着刃/护符成色·精工近档
            case "blade_next_q": return l.blade == null ? "§8主手无刃" : EmberGearNextHint.qualityLine(l.blade);
            case "blade_next_c": return l.blade == null ? "§8主手无刃" : EmberGearNextHint.craftLine(l.blade);
            case "charm_next_q": return l.charm == null ? "§8未选护符" : EmberGearNextHint.qualityLine(l.charm);
            case "charm_next_c": return l.charm == null ? "§8未选护符" : EmberGearNextHint.craftLine(l.charm);
            // D299 W1b：工坊手持件近档（无手持 / 非 P1 → 提示）
            case "held_next_q": return heldNext(p, true);
            case "held_next_c": return heldNext(p, false);
            // D307 W1a/b：工坊本次费用 / 缺料 / 互换免费 / 分解得胚
            case "held_enhance_cost": return heldForgeLine(p, "enhance");
            case "held_enhance_lack": return heldForgeLack(p, "enhance");
            case "held_upgrade_cost": return heldForgeLine(p, "upgrade");
            case "held_upgrade_lack": return heldForgeLack(p, "upgrade");
            case "held_refine_lack": return heldForgeLack(p, "refine");
            case "held_quality_lack": return heldForgeLack(p, "quality");
            case "held_swap_cost": return EmberGearNextHint.swapLine();
            case "held_dismantle_yield": return heldForgeLine(p, "dismantle");
            case "held_is_armor": return EmberSixPapi.heldIsArmor(heldTrusted(p)); // D333
            default: return l.nextAwakeningHint();
        }
    }

    /** D299 W1b：主手 P1 件的成色/精工近档；非 P1 或空手则短提示。 */
    private String heldNext(Player p, boolean quality) {
        EmberItemData d = heldTrusted(p);
        if (d == null) return "§8手持刃或护符看近档";
        if (d.isArmor()) return EmberSixPapi.heldArmorLine(d, "next"); // D318 K0
        return quality ? EmberGearNextHint.qualityLine(d) : EmberGearNextHint.craftLine(d);
    }

    /** D307：主手可信 P1 件；空手/非 P1/非本人 → null。 */
    private EmberItemData heldTrusted(Player p) {
        org.bukkit.inventory.ItemStack it = p.getInventory().getItemInMainHand();
        EmberLoadoutService ls = runs.plugin().getEmberLoadouts();
        if (ls == null || it == null || !ls.items().hasData(it)) return null;
        EmberItems.Read r = ls.items().read(it);
        if (r == null || r.data == null) return null;
        if (ls.trust(p, r.data) != null) return null;
        return r.data;
    }

    /** D307 W1a：费用/闸/得胚行（与 UpgradeRules / Forge 预览同源）。 */
    private String heldForgeLine(Player p, String kind) {
        EmberItemData d = heldTrusted(p);
        if (d == null) return "§8手持刃或护符";
        if (d.isArmor()) return EmberSixPapi.heldArmorLine(d, kind); // D318 K0 / 分解 ×0.1
        if ("enhance".equals(kind)) return EmberGearNextHint.enhanceLine(d);
        if ("upgrade".equals(kind)) {
            String flag = EmberUpgradeRules.upgradeFlag(d.tier);
            boolean gate = flag != null && runs.progressFlag(runs.plugin().getDataStore().get(p.getUniqueId()), flag);
            return EmberGearNextHint.upgradeLine(d, gate);
        }
        if ("dismantle".equals(kind)) return EmberGearNextHint.dismantleYieldLine(d);
        return "";
    }

    /** D307 W1b：缺料半行；材料够 / 无费用 → 空。 */
    private String heldForgeLack(Player p, String kind) {
        EmberItemData d = heldTrusted(p);
        if (d == null || d.isArmor()) return ""; // D318: armor pays nothing (K0)
        EmberUpgradeRules.Cost cost = null;
        if ("enhance".equals(kind)) {
            EmberUpgradeRules.Plan c = EmberUpgradeRules.enhanceCheck(d);
            if (!c.ok()) return "";
            cost = c.cost;
        } else if ("upgrade".equals(kind)) {
            cost = EmberUpgradeRules.upgradeCost(d.tier);
            if (cost == null) return "";
            String flag = EmberUpgradeRules.upgradeFlag(d.tier);
            boolean gate = flag != null && runs.progressFlag(runs.plugin().getDataStore().get(p.getUniqueId()), flag);
            if (!gate) return ""; // gate message already on cost line
        } else if ("refine".equals(kind)) {
            cost = EmberUpgradeRules.refineCost(d.craft);
        } else if ("quality".equals(kind)) {
            cost = EmberUpgradeRules.qualityCost(d.quality);
        }
        if (cost == null) return "";
        EmberForgeService forge = runs.plugin().getEmberForge();
        if (forge == null) return "";
        return EmberGearNextHint.lackHalf(forge.lackingFor(p, cost));
    }

    private String codex(Player p, PlayerData d, String key) { // B2.180 图录 · 装备 (display only, §19.5)
        EmberLoadoutService ls = runs.plugin().getEmberLoadouts();
        if (ls != null) ls.backfillCodex(p);
        if ("codex_count".equals(key)) return EmberCodex.count(d) + "/" + EmberCodex.ENTRIES.size();
        if (key.startsWith("codex_stage_")) {
            try {
                int i = Integer.parseInt(key.substring(12));
                return i >= 0 && i < EmberCodex.STAGE_AT.length ? EmberCodex.stageLabel(d, i) : "";
            } catch (NumberFormatException e) { return ""; }
        }
        if (key.startsWith("codex_")) return EmberCodex.has(d, key.substring(6)) ? "§a已登记" : "§8未获得";
        return "";
    }

    /** Tail: %corerpg_p1_&lt;map&gt;_&lt;field&gt;% (also where the fall-through keys end up). */
    private String map(PlayerData d, String key, EmberRunMaps maps) {
        int us = key.indexOf('_');
        if (us <= 0) return "";
        EmberRunMaps.MapDef m = maps.byKey(key.substring(0, us));
        if (m == null) return "";
        return mapField(m, key.substring(us + 1), d, maps);
    }

    private String mapField(EmberRunMaps.MapDef m, String f, PlayerData d, EmberRunMaps maps) {
        switch (f) {
            case "state": return runs.stateLabel(d, m);
            case "open": return runs.unlocked(d, m) ? "yes" : "no";
            case "cleared": return runs.firstClearDone(d, m) ? "yes" : "no";
            case "name": return m.name;
            case "cost": return maps.cost(m) + " 体力";
            case "tier": return m.dropLabel;
            case "purpose": return m.purpose;
            case "fc": return runs.firstCleared(d, m) ? "已领取" : m.firstClearLabel();
            default: return "";
        }
    }
}
