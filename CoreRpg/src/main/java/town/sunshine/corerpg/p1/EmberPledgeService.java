package town.sunshine.corerpg.p1;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * D232 / ARCH S3 step 3: 自选誓约 (Q06 pledge) logic extracted from {@link EmberRunService}
 * with <b>no behaviour change</b>. Owns {@code p1_pledge_*} counters, toggle/off/list menu,
 * session modifier encode, settle count, and PAPI text helpers. {@link EmberRunService} keeps
 * thin delegates so enter/settle/PAPI stay stable; weekly-rule selection still lives there and
 * only falls through to {@link #sessionKey} when the featured weekly mod is off.
 * <p>{@link #isOn}, {@link #applyToggle}, {@link #applyOff}, {@link #countInModifier},
 * {@link #encodeKey}, {@link #activeIds}, {@link #settleGrant} are Bukkit-free so unit tests
 * can pin counter / modifier / S09 grant shapes against the bundled {@code ember-v1-runs.yml}
 * (bv58 unchanged).
 */
public final class EmberPledgeService {

    /** + rule id, period all: 1 = the player pledges this rule on the repeat normal runs they lead */
    static final String C_PLEDGE = "p1_pledge_";
    static final String UNLOCK = "q06";

    private final EmberRunService runs;

    EmberPledgeService(EmberRunService runs) {
        this.runs = runs;
    }

    private static String P() { return EmberRunService.P; }

    // ------------------------------------------------------------------ Bukkit-free counter / modifier

    public static boolean isOn(PlayerData d, String ruleId) {
        return d != null && ruleId != null && d.periodCount(C_PLEDGE + ruleId, "all") > 0;
    }

    /**
     * Toggle one pledged rule. Returns true when the rule is <b>on</b> after the call.
     * Mutates {@code pd} counters only (no flush).
     */
    public static boolean applyToggle(PlayerData pd, String ruleId) {
        if (pd == null || ruleId == null) return false;
        int cur = pd.periodCount(C_PLEDGE + ruleId, "all");
        pd.addPeriodCount(C_PLEDGE + ruleId, "all", cur > 0 ? -cur : 1);
        return cur <= 0;
    }

    /** Clear every pledged rule in {@code poolIds}. Returns how many were cleared. */
    public static int applyOff(PlayerData pd, Collection<String> poolIds) {
        if (pd == null || poolIds == null) return 0;
        int n = 0;
        for (String id : poolIds) {
            int cur = pd.periodCount(C_PLEDGE + id, "all");
            if (cur > 0) {
                pd.addPeriodCount(C_PLEDGE + id, "all", -cur);
                n++;
            }
        }
        return n;
    }

    /** Config-order ids still in the pool that this character has pledged. */
    public static List<String> activeIds(PlayerData d, List<EmberRunMaps.Modifier> pool) {
        List<String> out = new ArrayList<String>();
        if (d == null || pool == null) return out;
        for (EmberRunMaps.Modifier m : pool) if (isOn(d, m.id)) out.add(m.id);
        return out;
    }

    /** Session modifier string {@code pledge:id[+id…]} from ordered rule ids (empty list → null). */
    public static String encodeKey(List<String> ids) {
        if (ids == null || ids.isEmpty()) return null;
        StringBuilder b = new StringBuilder(EmberRunMaps.PLEDGE);
        for (int i = 0; i < ids.size(); i++) b.append(i == 0 ? "" : "+").append(ids.get(i));
        return b.toString();
    }

    /**
     * Settlement: how many pledged rules the session carried (pool members only —
     * a rule dropped from the pool pays nothing).
     */
    public static int countInModifier(String modifier, Collection<String> poolIds) {
        int n = 0;
        if (poolIds == null || poolIds.isEmpty()) return 0;
        for (String id : EmberRunMaps.pledgeIds(modifier)) {
            for (String p : poolIds) if (p.equals(id)) { n++; break; }
        }
        return n;
    }

    /**
     * S09 settle grant: {@code S09.per_rule} (ember-v1-economy.yml, = 1) insignia per pledged rule on a signature-map
     * repeat clear. Null when pledged≤0. D243 (G8): the per-rule amount moved from code to the economy yml (same value).
     */
    public static EmberRunRules.Grant settleGrant(String mapKey, int pledged) {
        if (pledged <= 0 || mapKey == null) return null;
        return new EmberRunRules.Grant("pledge_sigmark", EmberRunRules.Kind.SIGMARK, mapKey, pledged * EmberEconomy.amount("S09", "per_rule"), null);
    }

    // ------------------------------------------------------------------ live wrappers

    /** the pledged rules that count: still in the pool (normal: true), config order */
    List<EmberRunMaps.Modifier> pledged(PlayerData d) {
        List<EmberRunMaps.Modifier> out = new ArrayList<EmberRunMaps.Modifier>();
        EmberRunMaps maps = runs.maps();
        if (d == null || maps == null) return out;
        for (EmberRunMaps.Modifier m : maps.pledgePool()) if (isOn(d, m.id)) out.add(m);
        return out;
    }

    int count(String modifier) {
        EmberRunMaps maps = runs.maps();
        if (maps == null) return 0;
        List<String> pool = new ArrayList<String>();
        for (EmberRunMaps.Modifier m : maps.pledgePool()) pool.add(m.id);
        return countInModifier(modifier, pool);
    }

    /**
     * the session modifier for the leader's pledge, or null: leader's own Q06 first clear, a repeat NORMAL run of a
     * signature map (Q01–Q07 — the pledge pays that map's insignia), every member already first-cleared it.
     */
    String sessionKey(Player leader, EmberRunMaps.MapDef m, List<Player> party) {
        PlayerData d = runs.dataOf(leader.getUniqueId());
        if (d == null || !runs.progressFlag(d, UNLOCK) || !EmberSignature.hasMap(m.key)) return null;
        List<EmberRunMaps.Modifier> l = pledged(d);
        if (l.isEmpty()) return null;
        for (Player p : party) if (!runs.firstCleared(runs.dataOf(p.getUniqueId()), m)) {
            leader.sendMessage(P() + "§7自选誓约这局不生效：" + p.getName() + " 还没首通 " + m.key.toUpperCase(Locale.ROOT) + "（首通保持原样）");
            return null;
        }
        List<String> ids = new ArrayList<String>();
        for (EmberRunMaps.Modifier x : l) ids.add(x.id);
        return encodeKey(ids);
    }

    /** /corerpg p1 pledge [toggle &lt;id&gt; | off | list] — bare = the menu */
    boolean cmd(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        PlayerData d = runs.dataOf(p.getUniqueId());
        EmberRunMaps maps = runs.maps();
        String op = args.length > 2 ? args[2].toLowerCase(Locale.ROOT) : "";
        if (op.isEmpty()) { runs.openMenuFor(p, "ember_p1_pledge"); return true; }
        if (d == null) return true;
        if ("list".equals(op)) {
            p.sendMessage(P() + "§d自选誓约 §7— " + head(d));
            for (EmberRunMaps.Modifier m : maps.pledgePool())
                p.sendMessage(P() + (isOn(d, m.id) ? "§a● " : "§8○ ") + "§f" + m.name + " §7" + m.text);
            return true;
        }
        if (!runs.progressFlag(d, UNLOCK)) { p.sendMessage(P() + ChatColor.RED + "自选誓约需本人首通 Q06 霜封哨所。"); return true; }
        if ("off".equals(op)) {
            List<String> pool = new ArrayList<String>();
            for (EmberRunMaps.Modifier m : maps.pledgePool()) pool.add(m.id);
            applyOff(d, pool);
            runs.flushData(p.getUniqueId());
            p.sendMessage(P() + "§7自选誓约已全部取消。");
            return true;
        }
        if ("toggle".equals(op) && args.length > 3) {
            String id = args[3].toLowerCase(Locale.ROOT);
            EmberRunMaps.Modifier pick = null;
            for (EmberRunMaps.Modifier m : maps.pledgePool()) if (m.id.equals(id)) pick = m;
            if (pick == null) { p.sendMessage(P() + ChatColor.RED + "没有这条可挂的规则：" + id); return true; }
            boolean nowOn = applyToggle(d, id);
            runs.flushData(p.getUniqueId());
            p.sendMessage(P() + (nowOn ? "§d已挂上誓约「" + pick.name + "」§7" + pick.text : "§7已取消誓约「" + pick.name + "」") + " · " + head(d));
            return true;
        }
        if ("pick".equals(op) && args.length > 3 && "both".equalsIgnoreCase(args[3])) {
            EmberModePath.pickBoth(p);
            return true;
        }
        p.sendMessage(P() + "用法：/corerpg p1 pledge（打开誓约页）· toggle <规则> · off · list · pick both");
        return true;
    }

    /** %corerpg_p1_pledge_head|ok|s_&lt;id&gt;|n_&lt;id&gt;|t_&lt;id&gt;% for ember_p1_pledge / ember_p1_modes */
    String papi(PlayerData d, String k) {
        if (d == null) return "";
        EmberRunMaps maps = runs.maps();
        if ("head".equals(k)) return head(d);
        if ("ok".equals(k)) return runs.progressFlag(d, UNLOCK) ? "1" : "0";
        if (k.length() > 2 && k.charAt(1) == '_') {
            EmberRunMaps.Modifier m = null;
            for (EmberRunMaps.Modifier x : maps.pledgePool()) if (x.id.equals(k.substring(2))) m = x;
            if (m == null) return "";
            boolean on = isOn(d, m.id);
            switch (k.charAt(0)) {
                case 's': return on ? "§a● 已挂上（左键取消）" : "§8○ 未挂（左键挂上）";
                case 'n': return m.name;
                case 't': return m.text;
                default: return "";
            }
        }
        return "";
    }

    String head(PlayerData d) {
        List<EmberRunMaps.Modifier> l = pledged(d);
        if (!runs.progressFlag(d, UNLOCK)) return "§8首通 Q06 后开放";
        if (l.isEmpty()) return "§7现在没挂规则（重打按原样）";
        StringBuilder b = new StringBuilder();
        for (EmberRunMaps.Modifier m : l) b.append(b.length() == 0 ? "" : "+").append(m.name);
        return "§d已挂：" + b + " §7· 你当队长重打已首通的 Q01–Q07 普通版时生效，每条 +1 本图徽记（本周精选图的周规则那天优先）";
    }
}
