package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Per-hit damage-source log (G01 item 4). Read-only probes at every priority record how the BASE and
 * final damage change, P1 code adds named steps via {@link #note}, and MONITOR prints
 * raw → each modifier → final to the viewers. Nothing here ever changes an event, so the probes are
 * safe in the legacy mode too (useful for before/after comparisons). Toggle: {@code /corerpg p1 debug}.
 */
public final class EmberDamageTrace implements Listener {

    private static volatile EmberDamageTrace instance;

    private final JavaPlugin plugin;
    /** players who see hits they are part of (any world) */
    private final Set<UUID> selfViewers = Collections.synchronizedSet(new HashSet<UUID>());
    /** players who see every hit inside P1 worlds */
    private final Set<UUID> allViewers = Collections.synchronizedSet(new HashSet<UUID>());
    private volatile boolean console;
    private final Map<EntityDamageEvent, Trace> live = new IdentityHashMap<EntityDamageEvent, Trace>();

    private static final class Trace {
        final double raw;
        double lastBase, lastFinal;
        final List<String> steps = new ArrayList<String>();
        final boolean p1;
        boolean cancelNoted;
        Trace(double raw, double fin, boolean p1) { this.raw = raw; this.lastBase = raw; this.lastFinal = fin; this.p1 = p1; }
    }

    public EmberDamageTrace(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static EmberDamageTrace get() { return instance; }

    public void setConsole(boolean v) { console = v; }
    public boolean isConsole() { return console; }

    /** @return new state for self-view */
    public boolean toggleSelf(UUID id) {
        if (selfViewers.remove(id)) return false;
        selfViewers.add(id);
        return true;
    }

    public boolean toggleAll(UUID id) {
        if (allViewers.remove(id)) return false;
        allViewers.add(id);
        return true;
    }

    public void off(UUID id) { selfViewers.remove(id); allViewers.remove(id); }

    public boolean isViewer(UUID id) { return selfViewers.contains(id) || allViewers.contains(id); }

    public String describe(UUID id) {
        return "self=" + selfViewers.contains(id) + " all=" + allViewers.contains(id) + " console=" + console;
    }

    private boolean anyone() { return console || !selfViewers.isEmpty() || !allViewers.isEmpty(); }

    // ------------------------------------------------------------------ API for P1 code

    /**
     * Adds a named step to the current trace of this event (no-op when nobody is watching).
     * {@code baseBefore} is the BASE value the caller saw before changing it; a mismatch with the last
     * recorded value is logged first as an unattributed change by another listener.
     */
    public static void note(EntityDamageEvent e, double baseBefore, String step) {
        EmberDamageTrace t = instance;
        if (t == null || !t.anyone()) return;
        Trace tr = t.live.get(e);
        if (tr == null) return;
        if (Math.abs(baseBefore - tr.lastBase) > 1e-9) {
            tr.steps.add("Δ(same priority) base " + fmt(tr.lastBase) + "→" + fmt(baseBefore)
                    + (tr.p1 ? ChatColor.RED + " (未登记来源)" + ChatColor.GRAY : ""));
        }
        tr.steps.add(step);
        if (e.isCancelled()) tr.cancelNoted = true;
        tr.lastBase = e.getDamage();
        tr.lastFinal = safeFinal(e);
    }

    /** Heal / regen-gate line, shown to viewers that watch this player. */
    public static void heal(Player p, String line) {
        EmberDamageTrace t = instance;
        if (t == null || !t.anyone()) return;
        String msg = ChatColor.DARK_AQUA + "[P1回复] " + ChatColor.GRAY + p.getName() + " " + line;
        t.emit(msg, p, null, EmberMode.isP1(p));
    }

    // ------------------------------------------------------------------ probes

    @EventHandler(priority = EventPriority.LOWEST)
    public void pLowest(EntityDamageEvent e) {
        if (!anyone()) return;
        if (!relevant(e)) return;
        live.put(e, new Trace(e.getDamage(), safeFinal(e), EmberMode.isP1(e.getEntity())));
    }

    @EventHandler(priority = EventPriority.LOW) public void pLow(EntityDamageEvent e) { probe(e, "LOW"); }
    @EventHandler(priority = EventPriority.NORMAL) public void pNormal(EntityDamageEvent e) { probe(e, "NORMAL"); }
    @EventHandler(priority = EventPriority.HIGH) public void pHigh(EntityDamageEvent e) { probe(e, "HIGH"); }
    @EventHandler(priority = EventPriority.HIGHEST) public void pHighest(EntityDamageEvent e) { probe(e, "HIGHEST"); }

    @EventHandler(priority = EventPriority.MONITOR)
    public void pMonitor(EntityDamageEvent e) {
        if (live.isEmpty()) return;
        Trace tr = live.remove(e);
        if (tr == null) return;
        probeTrace(e, tr, "≤HIGHEST(after)");
        Entity victim = e.getEntity();
        Entity attacker = attacker(e);
        StringBuilder sb = new StringBuilder();
        sb.append(tr.p1 ? ChatColor.GOLD + "[P1伤害] " : ChatColor.GRAY + "[旧伤害] ");
        sb.append(ChatColor.WHITE).append(name(attacker)).append(ChatColor.GRAY).append(" → ")
          .append(ChatColor.WHITE).append(name(victim)).append(ChatColor.GRAY).append(" ")
          .append(e.getCause().name().toLowerCase(Locale.ROOT));
        sb.append("\n").append(ChatColor.GRAY).append("  raw ").append(ChatColor.WHITE).append(fmt(tr.raw));
        for (String s : tr.steps) sb.append("\n").append(ChatColor.GRAY).append("  · ").append(s);
        String mods = vanillaMods(e);
        if (!mods.isEmpty()) sb.append("\n").append(ChatColor.GRAY).append("  vanilla ").append(mods);
        sb.append("\n").append(ChatColor.GRAY).append("  final ").append(e.isCancelled() ? ChatColor.RED + "CANCELLED" : ChatColor.YELLOW + fmt(safeFinal(e)));
        emit(sb.toString(), victim, attacker, tr.p1);
    }

    private void probe(EntityDamageEvent e, String prio) {
        if (live.isEmpty()) return;
        Trace tr = live.get(e);
        if (tr == null) return;
        probeTrace(e, tr, prio);
    }

    private static void probeTrace(EntityDamageEvent e, Trace tr, String prio) {
        double b = e.getDamage(), f = safeFinal(e);
        if (Math.abs(b - tr.lastBase) > 1e-9 || Math.abs(f - tr.lastFinal) > 1e-9) {
            tr.steps.add("Δ@" + prio + " base " + fmt(tr.lastBase) + "→" + fmt(b) + " final " + fmt(tr.lastFinal) + "→" + fmt(f)
                    + (tr.p1 ? ChatColor.RED + " (未登记来源)" + ChatColor.GRAY : ""));
            tr.lastBase = b;
            tr.lastFinal = f;
        }
        if (e.isCancelled() && !tr.cancelNoted) {
            tr.cancelNoted = true;
            tr.steps.add(ChatColor.RED + "cancelled by @" + prio + ChatColor.GRAY);
        }
    }

    private boolean relevant(EntityDamageEvent e) {
        Entity v = e.getEntity();
        Entity a = attacker(e);
        if (!allViewers.isEmpty() && EmberMode.isP1(v)) return true;
        if (console && EmberMode.isP1(v)) return true;
        if (v instanceof Player && selfViewers.contains(v.getUniqueId())) return true;
        return a instanceof Player && selfViewers.contains(a.getUniqueId());
    }

    private void emit(String msg, Entity victim, Entity attacker, boolean p1) {
        Set<UUID> to = new HashSet<UUID>();
        if (victim instanceof Player && selfViewers.contains(victim.getUniqueId())) to.add(victim.getUniqueId());
        if (attacker instanceof Player && selfViewers.contains(attacker.getUniqueId())) to.add(attacker.getUniqueId());
        if (p1) synchronized (allViewers) { to.addAll(allViewers); }
        for (UUID id : to) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) for (String line : msg.split("\n")) p.sendMessage(line);
        }
        if (console && p1) plugin.getLogger().info(ChatColor.stripColor(msg).replace("\n", " | "));
    }

    static Entity attacker(EntityDamageEvent e) {
        if (!(e instanceof EntityDamageByEntityEvent)) return null;
        Entity d = ((EntityDamageByEntityEvent) e).getDamager();
        if (d instanceof Projectile) {
            ProjectileSource s = ((Projectile) d).getShooter();
            if (s instanceof Entity) return (Entity) s;
        }
        return d;
    }

    private static String vanillaMods(EntityDamageEvent e) {
        StringBuilder sb = new StringBuilder();
        for (EntityDamageEvent.DamageModifier m : EntityDamageEvent.DamageModifier.values()) {
            if (m == EntityDamageEvent.DamageModifier.BASE) continue;
            if (!e.isApplicable(m)) continue;
            double v = e.getDamage(m);
            if (Math.abs(v) < 1e-9) continue;
            sb.append(m.name().toLowerCase(Locale.ROOT)).append('=').append(fmt(v)).append(' ');
        }
        return sb.toString().trim();
    }

    static double safeFinal(EntityDamageEvent e) {
        try { return e.getFinalDamage(); } catch (Throwable t) { return e.getDamage(); }
    }

    private static String name(Entity e) {
        if (e == null) return "-";
        if (e instanceof Player) return e.getName();
        String n = e.getCustomName();
        return (n != null ? ChatColor.stripColor(n) + "/" : "") + e.getType().name().toLowerCase(Locale.ROOT);
    }

    public static String fmt(double v) {
        if (Double.isNaN(v)) return "NaN";
        return String.format(Locale.ROOT, "%.2f", v);
    }
}
