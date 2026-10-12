package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D1341: map gear-identity remain (图鉴 have/need) — display only for remain lines.
 * D2206: soft SetFocus when identity completes (non-clear-gate unlock · zero ConfirmTokens).
 * D12249: menuLine short display for TrMenu Q lore.
 * Never gates clears; never changes drop rates. Not a hope PAPI twin.
 */
public final class EmberMapIdentity {

    /** Per-family latch: soft-focus already applied once for this loot family. */
    public static final String C_SOFT = "p1_map_id_soft_";

    private EmberMapIdentity() {}

    public static int[] haveNeed(EmberRunMaps.MapDef lm, PlayerData pd) {
        if (lm == null || lm.lootFamily == null || lm.lootFamily.isEmpty()) return new int[]{0, 0};
        int tier = 1;
        String dl = lm.dropLabel == null ? "" : lm.dropLabel.trim().toUpperCase(java.util.Locale.ROOT);
        if (dl.startsWith("T") && dl.length() >= 2) {
            try { tier = Math.max(1, Math.min(3, Integer.parseInt(dl.substring(1, 2)))); }
            catch (NumberFormatException ignored) {}
        }
        int have = 0, need = 0;
        for (int t = 1; t <= tier; t++) {
            for (String slot : new String[] {"blade", "charm"}) {
                need++;
                if (pd != null && EmberCodex.has(pd, EmberCodex.key(lm.lootFamily, slot, t))) have++;
            }
        }
        return new int[]{have, need};
    }

    /**
     * D12249 / D12309: short menu line for Q lore L2 — one line.
     * Incomplete appends compressed nextMissing (strip 「下一缺 · 」+ spaces, drop family already in label).
     * Shape: {@code 焚烬 · 图鉴 1/2 · 还差1 · 下一缺T1刃} / {@code 焚烬 · 图鉴齐}.
     */
    public static String menuLine(EmberRunMaps.MapDef lm, PlayerData pd) {
        if (lm == null) return "本图身份未登记";
        if (lm.lootFamily == null || lm.lootFamily.isEmpty())
            return "本图无装备偏向";
        int[] hn = haveNeed(lm, pd);
        int have = hn[0], need = hn[1];
        String label = EmberRunMaps.lootLabel(lm);
        int gap = Math.max(0, need - have);
        if (gap == 0)
            return label + " · 图鉴齐";
        String base = label + " · 图鉴 " + have + "/" + need + " · 还差" + gap;
        String tail = compressNextMissing(lm, pd);
        if (tail.isEmpty()) return base;
        return base + " · " + tail;
    }

    /**
     * D12309: compress {@link #nextMissing} for menuLine tail.
     * {@code 下一缺 · T1 焚烬刃} → {@code 下一缺T1刃} (drop family; already in label).
     */
    static String compressNextMissing(EmberRunMaps.MapDef lm, PlayerData pd) {
        String nm = nextMissing(lm, pd);
        if (nm == null || nm.isEmpty() || nm.contains("已齐")) return "";
        String rest = nm;
        if (rest.startsWith("下一缺 · ")) rest = rest.substring("下一缺 · ".length());
        else if (rest.startsWith("下一缺·")) rest = rest.substring("下一缺·".length());
        else if (rest.startsWith("下一缺")) {
            rest = rest.substring("下一缺".length()).trim();
            if (rest.startsWith("·")) rest = rest.substring(1).trim();
        } else {
            return nm.replace(" ", "");
        }
        if (lm != null && lm.lootFamily != null) {
            String fam = EmberItemData.familyName(lm.lootFamily);
            if (fam != null && !fam.isEmpty()) rest = rest.replace(fam, "");
        }
        rest = rest.replace(" ", "");
        return "下一缺" + rest;
    }

    /** Plain remain: {@code 图身份 焚烬·T1 · 图鉴 1/2 · 还差1件 · 进过即计 · 通关不锁掉落}. */
    public static String remainLine(EmberRunMaps.MapDef lm, PlayerData pd) {
        if (lm == null) return "本图身份未登记 · 每局仍必掉1装 · 通关不锁掉落";
        if (lm.lootFamily == null || lm.lootFamily.isEmpty())
            return "本图无装备偏向 · 每局1装·差装可分解 · 通关不锁掉落";
        int[] hn = haveNeed(lm, pd);
        int have = hn[0], need = hn[1];
        String label = EmberRunMaps.lootLabel(lm);
        int gap = Math.max(0, need - have);
        if (gap == 0)
            return "图身份 " + label + " · 图鉴 " + have + "/" + need + " · 齐 · 进过即计 · 通关不锁掉落";
        return "图身份 " + label + " · 图鉴 " + have + "/" + need + " · 还差" + gap + "件 · 进过即计 · 通关不锁掉落";
    }

    /** First missing codex piece for this map identity (blade then charm, low tier first). */
    public static String nextMissing(EmberRunMaps.MapDef lm, PlayerData pd) {
        if (lm == null || lm.lootFamily == null || lm.lootFamily.isEmpty()) return "";
        int tier = 1;
        String dl = lm.dropLabel == null ? "" : lm.dropLabel.trim().toUpperCase(java.util.Locale.ROOT);
        if (dl.startsWith("T") && dl.length() >= 2) {
            try { tier = Math.max(1, Math.min(3, Integer.parseInt(dl.substring(1, 2)))); }
            catch (NumberFormatException ignored) {}
        }
        String fam = lm.lootFamily;
        for (int t = 1; t <= tier; t++) {
            for (String slot : new String[] {"blade", "charm"}) {
                if (pd == null || !EmberCodex.has(pd, EmberCodex.key(fam, slot, t)))
                    return "下一缺 · T" + t + " " + EmberItemData.familyName(fam) + EmberItemData.slotName(slot);
            }
        }
        return "本图身份图鉴已齐";
    }

    /** D1815 EM1-A: compose existing acquisition paths for this map (display only). */
    public static String methodLine(EmberRunMaps.MapDef lm, PlayerData pd) {
        if (lm == null) return "本图方法 · 未登记 · 每局1装不锁通关";
        StringBuilder sb = new StringBuilder("本图方法");
        String miss = nextMissing(lm, pd);
        if (miss != null && !miss.isEmpty() && !miss.contains("已齐")) {
            sb.append(" · ").append(miss.replace("下一缺 · ", "刷"));
        } else if (lm.lootFamily != null && !lm.lootFamily.isEmpty()) {
            sb.append(" · 身份齐·刷分解/成套");
        } else {
            sb.append(" · 无装偏向·刷分解料");
        }
        if (pd != null && lm.lootFamily != null && EmberRunRules.validFamily(lm.lootFamily)) {
            int focus = EmberSetFocus.get(pd);
            String fk = EmberSetFocus.familyKey(focus);
            if (lm.lootFamily.equals(fk)) sb.append(" · 对焦点");
        }
        if (mainlineQ(lm) && pd != null) {
            String gem = EmberGemAcquire.remainLine(pd);
            if (gem != null && !gem.isEmpty())
                sb.append(" · ").append(gem.replace("通关保底 ", "宝石"));
        }
        sb.append(" · 不锁通关");
        return sb.toString();
    }

    /** Mainline Q01–Q07 key (gem pity maps); ignores run session. */
    public static boolean mainlineQ(EmberRunMaps.MapDef m) {
        if (m == null || m.raid || m.event || m.rush || m.shortExpedition) return false;
        String k = m.key == null ? "" : m.key.toLowerCase(java.util.Locale.ROOT);
        return k.startsWith("q0") && k.length() == 3;
    }

    /** First mainline Q-map whose lootFamily matches (for focus farm tip). */
    public static String suggestMapForFamily(EmberRunMaps maps, String family) {
        if (maps == null || family == null || family.isEmpty()) return null;
        for (EmberRunMaps.MapDef md : maps.maps.values()) {
            if (md == null || md.raid || md.event || md.rush || md.shortExpedition) continue;
            String k = md.key == null ? "" : md.key.toLowerCase(java.util.Locale.ROOT);
            if (!k.startsWith("q0") || k.length() != 3) continue;
            if (family.equals(md.lootFamily)) return md.key;
        }
        return null;
    }

    /**
     * D2206: after a new codex register — if some mainline Q identity for {@code family}
     * is now complete and SetFocus is unset, soft-set focus to that family.
     * Zero clear-gate / drop / ConfirmTokens. Returns true when soft-set applied.
     */
    public static boolean maybeSoftFocusAfterCodex(Player p, PlayerData d, String family, EmberRunMaps maps) {
        if (p == null || d == null || family == null || family.isEmpty()) return false;
        if (!EmberRunRules.validFamily(family)) return false;
        if (EmberSetFocus.valid(EmberSetFocus.get(d))) return false;
        String latch = C_SOFT + family;
        if (d.periodCount(latch, "all") > 0) return false;
        if (maps == null || maps.maps == null) return false;
        EmberRunMaps.MapDef done = null;
        for (EmberRunMaps.MapDef md : maps.maps.values()) {
            if (!mainlineQ(md)) continue;
            if (!family.equals(md.lootFamily)) continue;
            int[] hn = haveNeed(md, d);
            if (hn[1] > 0 && hn[0] >= hn[1]) {
                done = md;
                break;
            }
        }
        if (done == null) return false;
        int focusId = EmberSetFocus.parse(family);
        if (!EmberSetFocus.valid(focusId)) return false;
        if (!EmberSetFocus.set(d, focusId)) return false;
        d.addPeriodCount(latch, "all", 1);
        // D2220: soft loot target only when unset (FamilyPath mirror · no ConfirmTokens)
        boolean lootSoft = false;
        if (d.periodCount(EmberRunService.C_TARGET, "all") == 0) {
            EmberFamilyPath.setLootTarget(d, family);
            lootSoft = d.periodCount(EmberRunService.C_TARGET, "all") > 0;
        }
        String famName = EmberItemData.familyName(family);
        String mapName = done.name == null ? done.key : done.name;
        String line = "§a图身份齐 · §f" + mapName + " §7→ 软定套装焦点 §f" + famName
                + (lootSoft ? " §7+ 掉落目标" : "")
                + " §8（可随时 /corerpg p1 setfocus 改 · 不锁通关）";
        p.sendMessage(EmberRunService.P + line);
        try { p.sendActionBar("§a图鉴齐 · 焦点 §f" + famName); } catch (Throwable ignored) { }
        try { EmberMapIdentityBone.maybeGift(p, d); } catch (Throwable ignored) { } // D11985
        return true;
    }

    /** Pure: whether identity for map is complete. */
    public static boolean identityComplete(EmberRunMaps.MapDef lm, PlayerData pd) {
        int[] hn = haveNeed(lm, pd);
        return hn[1] > 0 && hn[0] >= hn[1];
    }
}
