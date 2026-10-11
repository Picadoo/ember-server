package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D4641: chase imprint auto when marks ready and worn piece has empty signature.
 * Zero STAMP_RATE / insignia yield / ConfirmTokens (overwrite still manual). Not MarksBreak twin.
 */
public final class EmberImprintBreak {

    /** Latch: p1_imprint_auto_<code>@all — one auto per chase def */
    public static final String C_AUTO = "p1_imprint_auto_";

    private EmberImprintBreak() {}

    public static boolean canAuto(PlayerData d, EmberLoadout lo, EmberSignature.Def def, EmberRunService runs) {
        if (d == null || lo == null || def == null || runs == null) return false;
        EmberItemData t = "blade".equals(def.slot) ? lo.blade : lo.charm;
        if (t == null) return false;
        EmberGrowthService g = EmberGrowthService.get();
        if (g == null) return false;
        int cur = g.sigOf(d, t);
        if (cur != 0) return false;
        int marks = d.periodCount(EmberSignature.C_MARK + def.map, "all");
        String why = EmberSignature.imprintCheck(def, t,
                runs.progressFlag(d, EmberSignature.IMPRINT_UNLOCK),
                runs.progressFlag(d, def.map), marks, d.getCoin(), cur);
        return why == null;
    }

    /**
     * Rising-edge auto imprint for current chase when empty-sig piece worn.
     * @return true when auto started
     */
    public static boolean maybeAuto(Player p, PlayerData d, EmberLoadout lo, EmberRunService runs) {
        if (p == null || d == null) return false;
        EmberSignature.Def def = EmberSigChase.get(d);
        if (def == null) return false;
        String key = C_AUTO + def.code;
        if (d.periodCount(key, "all") > 0) return false;
        if (lo == null && runs != null && runs.loadouts() != null)
            lo = runs.loadouts().refresh(p);
        if (!canAuto(d, lo, def, runs)) return false;
        EmberGrowthService g = EmberGrowthService.get();
        if (g == null) return false;
        d.addPeriodCount(key, "all", 1);
        EmberSigChase.flush(p);
        boolean ok = g.autoImprintEmpty(p, def);
        if (!ok) {
            d.addPeriodCount(key, "all", -1);
            EmberSigChase.flush(p);
            return false;
        }
        return true;
    }
}
