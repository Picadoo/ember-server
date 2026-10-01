package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * {@code /corerpg p1 ...} — ember-v1.0-P1 admin/test commands.
 * Everything except {@code status} needs corerpg.admin.
 */
public final class EmberCommand {

    private final EmberMode mode;
    private final EmberDamageTrace trace;

    public EmberCommand(EmberMode mode, EmberDamageTrace trace) {
        this.mode = mode;
        this.trace = trace;
    }

    private static final String P = ChatColor.GOLD + "[P1] " + ChatColor.GRAY;

    public boolean cmd(CommandSender s, String[] args) {
        String sub = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "status";
        if ("status".equals(sub)) return status(s);
        if (!s.hasPermission("corerpg.admin")) {
            s.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        if ("enable".equals(sub) || "on".equals(sub)) {
            mode.setRuntimeEnabled(Boolean.TRUE);
            s.sendMessage(P + "运行期开启 " + EmberMode.MODE_ID + "（仅 scope 内世界生效，重启后恢复配置值）"
                    + (mode.isBlocked() ? ChatColor.RED + " 但已被阻止: " + mode.getBlockedReason() : ""));
            return true;
        }
        if ("disable".equals(sub) || "off".equals(sub)) {
            mode.setRuntimeEnabled(Boolean.FALSE);
            s.sendMessage(P + "运行期关闭 " + EmberMode.MODE_ID + "：所有世界回到旧模式（下一秒刷新属性）");
            return true;
        }
        if ("follow".equals(sub)) {
            mode.setRuntimeEnabled(null);
            s.sendMessage(P + "取消运行期覆盖，跟随 ember-v1.yml enabled=" + mode.isConfigEnabled());
            return true;
        }
        if ("world".equals(sub)) return world(s, args);
        if ("debug".equals(sub)) return debug(s, args);
        if ("calc".equals(sub)) return calc(s, args);
        return help(s);
    }

    public boolean help(CommandSender s) {
        s.sendMessage(P + "/corerpg p1 status | enable | disable | follow");
        s.sendMessage(P + "/corerpg p1 world add|remove [世界] | world list");
        s.sendMessage(P + "/corerpg p1 debug [all|console|off]  — 每击伤害来源日志");
        s.sendMessage(P + "/corerpg p1 calc <武阶> <武成色> <武精工> <武强化> <符阶> <符成色> <符精工> <符强化> <等级> [sustain]");
        return true;
    }

    private boolean status(CommandSender s) {
        s.sendMessage(P + EmberMode.MODE_ID + " active=" + mode.isActive() + " (config=" + mode.isConfigEnabled()
                + ", runtime=" + (mode.getRuntimeOverride() == null ? "follow" : mode.getRuntimeOverride()) + ")"
                + (mode.isBlocked() ? ChatColor.RED + " BLOCKED: " + mode.getBlockedReason() : ""));
        for (String line : mode.describeScope()) s.sendMessage(P + line);
        if (s instanceof Player) {
            Player p = (Player) s;
            s.sendMessage(P + "当前世界 " + p.getWorld().getName() + " → " + (EmberMode.isP1(p) ? ChatColor.GOLD + "P1 新模式" : "旧模式"));
            if (trace != null) s.sendMessage(P + "debug " + trace.describe(p.getUniqueId()));
        }
        if (s.hasPermission("corerpg.admin")) s.sendMessage(P + "tables " + EmberMode.tables());
        return true;
    }

    private boolean world(CommandSender s, String[] args) {
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "list";
        if ("list".equals(op)) {
            for (String line : mode.describeScope()) s.sendMessage(P + line);
            for (World w : Bukkit.getWorlds()) {
                if (mode.worldInScope(w.getName())) s.sendMessage(P + "  ✔ " + w.getName() + " (" + w.getPlayers().size() + " 人)");
            }
            return true;
        }
        String name = args.length >= 4 ? args[3] : (s instanceof Player ? ((Player) s).getWorld().getName() : null);
        if (name == null) { s.sendMessage(P + "控制台需指定世界名"); return true; }
        if ("add".equals(op)) {
            mode.addWorld(name);
            s.sendMessage(P + "运行期加入 P1 世界: " + name + (mode.isActive() ? "" : ChatColor.YELLOW + "（总开关未开启：/corerpg p1 enable）"));
            return true;
        }
        if ("remove".equals(op) || "del".equals(op)) {
            mode.removeWorld(name);
            s.sendMessage(P + "运行期移出 P1 世界: " + name);
            return true;
        }
        return help(s);
    }

    private boolean debug(CommandSender s, String[] args) {
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "self";
        if ("console".equals(op)) {
            trace.setConsole(!trace.isConsole());
            s.sendMessage(P + "控制台伤害日志: " + trace.isConsole() + "（仅 P1 世界）");
            return true;
        }
        if (!(s instanceof Player)) { s.sendMessage(P + "控制台只能用 debug console"); return true; }
        Player p = (Player) s;
        if ("off".equals(op)) { trace.off(p.getUniqueId()); s.sendMessage(P + "伤害日志: 关"); return true; }
        if ("all".equals(op)) {
            boolean on = trace.toggleAll(p.getUniqueId());
            s.sendMessage(P + "P1 世界全部命中日志: " + (on ? "开" : "关"));
            return true;
        }
        boolean on = trace.toggleSelf(p.getUniqueId());
        s.sendMessage(P + "与你相关的每击伤害日志: " + (on ? "开" : "关") + "（raw → 各修正 → final）");
        return true;
    }

    private boolean calc(CommandSender s, String[] args) {
        if (args.length < 11) { return help(s); }
        try {
            int wt = Integer.parseInt(args[2]), wq = Integer.parseInt(args[3]), wf = Integer.parseInt(args[4]), we = Integer.parseInt(args[5]);
            int ct = Integer.parseInt(args[6]), cq = Integer.parseInt(args[7]), cf = Integer.parseInt(args[8]), ce = Integer.parseInt(args[9]);
            int lv = Integer.parseInt(args[10]);
            boolean sustain = args.length >= 12 && ("sustain".equalsIgnoreCase(args[11]) || "true".equalsIgnoreCase(args[11]));
            EmberTables t = EmberMode.tables();
            double b = EmberFormula.baseAttack(t, wt, wq, wf, we, lv);
            double h0 = EmberFormula.baseHp(t, ct, cq, cf, ce, lv);
            double h = EmberFormula.maxHp(t, h0, sustain);
            double d = EmberFormula.defense(t, ct);
            double m = EmberFormula.mitigation(t, d);
            s.sendMessage(P + String.format(Locale.ROOT, "B=%.4f  H0=%.4f  H=%.4f  D=%.1f  M=%.4f  EHP=%.4f",
                    b, h0, h, d, m, EmberFormula.ehp(h, m)));
            s.sendMessage(P + String.format(Locale.ROOT, "满蓄力 %.2f / 暴击 %.2f / 期望 %.2f / 烬斩 %.2f",
                    EmberFormula.melee(t, b, 1, false), EmberFormula.melee(t, b, 1, true),
                    EmberFormula.expectedFullSwing(t, b), EmberFormula.skill(t, b)));
        } catch (NumberFormatException ex) {
            s.sendMessage(P + "参数需为整数");
        }
        return true;
    }
}
