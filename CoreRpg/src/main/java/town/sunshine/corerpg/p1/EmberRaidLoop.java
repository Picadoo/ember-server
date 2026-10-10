package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D657: raid weekly-clear acquisition feel after raid_mark lands.
 * Zero weeklyCap / stamina / loot math change. Not a PAPI meter twin.
 */
public final class EmberRaidLoop {

    private EmberRaidLoop() {}

    /**
     * @param used weekly clears after this clear
     * @param cap  weeklyCap
     * @param firstClear true when this was the raid key's first-ever clear
     */
    public static void afterWeeklyClear(Player p, EmberRunMaps.MapDef m, int used, int cap, boolean firstClear) {
        if (p == null || m == null || cap <= 0 || used <= 0) return;
        String P = EmberRunService.P;
        String name = m.name == null ? m.key : m.name;
        PlayerData d = null;
        try {
            org.bukkit.plugin.Plugin pl = org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
            if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin)
                d = ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().get(p.getUniqueId());
        } catch (Throwable ignored) { }
        if (used >= cap) {
            p.sendMessage(P + "§d本周团本额度满 · §f" + used + "/" + cap + " §7· " + name
                    + " §8（周一 0 点重置）");
            try { p.sendActionBar("§d团本周额度满 · §f" + used + "/" + cap); } catch (Throwable ignored) { }
            p.sendMessage(P + EmberRaidPath.glance(EmberRaidPath.get(d), true)); // D758
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[冒险页]", "/ember_p1_adventure", "主线/挑战/深渊", "GREEN"},
                    new String[]{"[工坊]", "/corerpg p1 forge", "花掉落材料", "GOLD"},
                    new String[]{"[换团本路径]", "/corerpg p1 raidpath", "下周焦点", "AQUA"});
        } else {
            String line = "§d团本入账 · §f" + name + " §7本周 §e" + used + "/" + cap
                    + (firstClear ? " §a· 首通" : "")
                    + (used == cap - 1 ? " §e· 下周额度前最后 1 次" : ""); // D810
            try { p.sendActionBar(line); } catch (Throwable ignored) { }
            p.sendMessage(P + line + " §8（通关才扣周次数）");
            // D807 next-loop while week cap remains
            int path = EmberRaidPath.get(d);
            if (EmberRaidPath.valid(path)) {
                ConfirmTokens.sendButtons(p, P + "§7团本下一环：",
                        new String[]{EmberRaidPath.enterLabel(path), EmberRaidPath.enterCmd(path),
                                "再开路径团本 · 剩 " + (cap - used) + " 次", "GREEN"},
                        new String[]{"[招募]", "/corerpg p1 recruit " + EmberRaidPath.mapKey(path), "凑人", "GOLD"},
                        new String[]{"[换路径]", "/corerpg p1 raidpath", "改焦点", "GRAY"});
            } else {
                ConfirmTokens.sendButtons(p, P + "§7团本下一环：",
                        new String[]{"[选团本路径]", "/corerpg p1 raidpath", "定本周焦点", "AQUA"},
                        new String[]{"[冒险页]", "/ember_p1_adventure", "换内容", "GREEN"});
            }
        }
        EmberRaidPath.afterClearHit(p, d, m.key); // D752
    }

    public static String chatLine(int used, int cap) {
        if (cap <= 0) return "";
        return used >= cap
                ? "§d本周团本额度满 · §f" + used + "/" + cap
                : "§d团本入账 · 本周 §e" + used + "/" + cap;
    }
}
