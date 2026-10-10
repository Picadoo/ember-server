package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D462: advanced-mode first-path pick on Q04/Q05/Q06 first clear.
 * Real route choice into 残响 / 前哨 / 自选誓约 — zero power / stamp / AFK change.
 */
public final class EmberModePath {

    public static final String C_OFFERED = "p1_mode_path_offered_";

    private EmberModePath() {}

    public static boolean isModeUnlockMap(String mapKey) {
        return "q04".equals(mapKey) || "q05".equals(mapKey) || "q06".equals(mapKey);
    }

    public static void scheduleOffer(Player p, String mapKey) {
        if (p == null || !isModeUnlockMap(mapKey)) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) { offer(p, mapKey); return; }
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (p.isOnline()) offer(p, mapKey);
        }, 45L);
    }

    /** Force re-offer (admin / smoke). */
    public static void forceOffer(Player p, String mapKey) {
        if (p == null || !isModeUnlockMap(mapKey)) return;
        PlayerData d = dataOf(p);
        if (d != null) {
            int cur = d.periodCount(C_OFFERED + mapKey, "all");
            if (cur > 0) d.addPeriodCount(C_OFFERED + mapKey, "all", -cur);
            flush(p);
        }
        offer(p, mapKey);
    }

    public static void offer(Player p, String mapKey) {
        if (p == null || !isModeUnlockMap(mapKey)) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        if (d.periodCount(C_OFFERED + mapKey, "all") > 0) return;
        d.addPeriodCount(C_OFFERED + mapKey, "all", 1);
        flush(p);
        String P = EmberRunService.P;
        if ("q04".equals(mapKey)) {
            p.sendMessage(P + "§e选一条进阶路径（首领残响 · 每周 3 次徽记）：");
            ConfirmTokens.sendButtons(p, P,
                    new String[]{"[去残响·Q01]", "/corerpg p1 rush echo_q01 go", "单独再打 Q01 强化首领 · 2 徽记", "AQUA"},
                    new String[]{"[打开进阶模式]", "/corerpg p1 modes", "七图残响厅", "GOLD"},
                    new String[]{"[稍后再说]", null, "冒险页 → 进阶模式", "GRAY"});
            return;
        }
        if ("q05".equals(mapKey)) {
            p.sendMessage(P + "§e选一条进阶路径（连战·前哨 · Q01→Q03）：");
            ConfirmTokens.sendButtons(p, P,
                    new String[]{"[去前哨连战]", "/corerpg p1 rush outpost go", "三首领连战 · 周领徽记+印记", "LIGHT_PURPLE"},
                    new String[]{"[打开进阶模式]", "/corerpg p1 modes", "看状态再进", "GOLD"},
                    new String[]{"[稍后再说]", null, "冒险页 → 进阶模式", "GRAY"});
            return;
        }
        // q06
        p.sendMessage(P + "§e挂哪条自选誓约？（队长重打已首通图时生效 · 每条 +1 徽记）");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[限药]", "/corerpg p1 pledge toggle lean", "本局最多 3 瓶回复药", "YELLOW"},
                new String[]{"[逆行]", "/corerpg p1 pledge toggle reverse", "开局最重的一间", "RED"},
                new String[]{"[两个都挂]", "/corerpg p1 pledge pick both", "限药+逆行", "GOLD"},
                new String[]{"[打开誓约页]", "/corerpg p1 pledge", "细调规则", "AQUA"},
                new String[]{"[稍后再说]", null, "随时可改", "GRAY"});
    }

    /** Apply both starter pledges (idempotent on). */
    public static void pickBoth(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (!(pl instanceof town.sunshine.corerpg.CoreRpgPlugin)) return;
        town.sunshine.corerpg.CoreRpgPlugin core = (town.sunshine.corerpg.CoreRpgPlugin) pl;
        EmberRunService runs = core.getEmberRuns();
        if (runs == null) return;
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (d == null) return;
        if (!runs.progressFlag(d, EmberPledgeService.UNLOCK)) {
            p.sendMessage(EmberRunService.P + "§c自选誓约需本人首通 Q06");
            return;
        }
        for (String id : new String[]{"lean", "reverse"}) {
            if (!EmberPledgeService.isOn(d, id)) EmberPledgeService.applyToggle(d, id);
        }
        core.getDataStore().flushMutation(p.getUniqueId());
        p.sendMessage(EmberRunService.P + "§a已挂：§f限药 + 逆行 §7· 你当队长重打已首通普通版时生效");
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
