package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.QuestService;
import town.sunshine.corerpg.SkillService;
import town.sunshine.corerpg.p1.EmberSetEngine.Hit;
import town.sunshine.corerpg.p1.EmberSetEngine.Kind;
import town.sunshine.corerpg.p1.EmberSetEngine.Outcome;
import town.sunshine.corerpg.p1.EmberSetEngine.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * G02 SetRuntime (策划书 §4.2–4.4, §8) on Bukkit: feeds {@link EmberSetEngine} from the damage MONITOR and executes
 * 焚烬 / 烬爆 / 炽愈. Engine time is monotonic (nanoTime ms); 烬爆/炽愈 cooldowns survive gear switches and are
 * persisted as remaining durations across reconnects ({@link EmberPlayerState#burstCdRemainMs}).
 * Only acts in P1 worlds while the mode is active.
 */
public final class EmberSetService implements Listener {

    private final CoreRpgPlugin plugin;
    private final EmberLoadoutService loadouts;
    private final EmberCombatListener combat;
    private final Map<UUID, Session> sessions = new HashMap<UUID, Session>();
    private BukkitTask task;
    private long ticks;

    private static final class Session {
        final EmberSetEngine engine = new EmberSetEngine();
        final Map<String, LivingEntity> burnTargets = new HashMap<String, LivingEntity>();
        long spreadCdUntil; // D141 燎原 icd
        boolean restored;
        boolean hudShown;
        /** D439: monotonic ms until proc ActionBar flash yields to normal hud */
        long procFlashUntil;
        /** D444: after flash ends, force idle 0/every set line for a short handback */
        long forceHudUntil;
        /** D444: almost-ready debounce — last counter we flashed for */
        int lastAlmostCounter = -1;
        /** D444: almost-ready debounce — when we last flashed */
        long lastAlmostAt;
        /** D445: skill confirm flash owns ActionBar briefly (below proc) */
        long skillFeelUntil;
    }

    /** D444: almost-ready debounce window (ms); pure helper for unit tests. */
    static final long ALMOST_DEBOUNCE_MS = 2500L;
    /** D444: post-proc forced HUD handback (ms). */
    static final long FORCE_HUD_MS = 2000L;

    /**
     * D444: whether to flash almost-ready for this counter approach.
     * Flash once per approach to {@code every-1}; skip same counter within {@link #ALMOST_DEBOUNCE_MS}.
     */
    static boolean shouldFlashAlmost(int counter, int every, int lastAlmostCounter, long lastAlmostAt, long now) {
        if (every <= 1 || counter != every - 1) return false;
        if (lastAlmostCounter == counter && now - lastAlmostAt < ALMOST_DEBOUNCE_MS) return false;
        return true;
    }

    /** D445: skill hit-confirm ActionBar window (ms); ≤1.2s, ActionBar-only. */
    public static final long SKILL_FLASH_MS = 1200L;

    /** D445: true while a set proc / almost-ready flash owns the ActionBar. */
    public boolean isProcFlashActive(Player p) {
        if (p == null) return false;
        Session s = sessions.get(p.getUniqueId());
        if (s == null) return false;
        return now() < s.procFlashUntil;
    }

    /**
     * D445: short ActionBar skill-confirm flash (烬斩命中 / 烬突落地).
     * Yields when set proc flash owns the bar — does not steal true-proc readability.
     * @return true if the flash was shown
     */
    /**
     * D457: run-start set / four-piece playstyle cue. Zero power / set_bonus change.
     */
    public void announceSetFeel(Player p) {
        if (p == null) return;
        EmberLoadout l = loadouts == null ? null : loadouts.get(p);
        if (l == null || l.activeSet == null || "none".equals(l.activeSet)) return;
        int armorN = 0;
        boolean armorOn = false;
        EmberItemData[] worn = l.armorCopy();
        if (worn == null) worn = new EmberItemData[4];
        Object[] sp = EmberSixRank.setProgress(l.blade, l.charm, worn);
        if (sp != null) {
            armorN = sp[1] == null ? 0 : ((Integer) sp[1]).intValue();
            armorOn = Boolean.TRUE.equals(sp[3]);
        }
        String line = EmberSetFeel.runLine(l.activeSet, l.awakening, armorN, armorOn);
        if (line == null || line.isEmpty()) return;
        p.sendMessage(ChatColor.GOLD + "[余烬] " + line);
        flashSkillConfirm(p, line);
    }

    public boolean flashSkillConfirm(Player p, String msg) {
        if (p == null || msg == null || msg.isEmpty()) return false;
        Session s = session(p);
        long t = now();
        // yield to set true-proc / almost-ready / forced handback (D445 priority below set)
        if (t < s.procFlashUntil || t < s.forceHudUntil) return false;
        long until = t + SKILL_FLASH_MS;
        s.skillFeelUntil = until;
        try { p.sendActionBar(msg); } catch (Throwable ignored) { }
        try {
            p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(msg));
        } catch (Throwable ignored) { }
        // ActionBar-only — no subtitle (noise budget vs D444 true-proc)
        s.hudShown = true;
        final UUID id = p.getUniqueId();
        final long token = until;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Session ss = sessions.get(id);
            Player pp = Bukkit.getPlayer(id);
            if (ss == null || pp == null || !pp.isOnline()) return;
            if (ss.skillFeelUntil != token) return; // newer skill/set flash won
            ss.skillFeelUntil = 0L;
            showHud(pp, ss, now());
        }, Math.max(1L, SKILL_FLASH_MS / 50L));
        return true;
    }

    public EmberSetService(CoreRpgPlugin plugin, EmberLoadoutService loadouts, EmberCombatListener combat) {
        this.plugin = plugin;
        this.loadouts = loadouts;
        this.combat = combat;
    }

    static long now() { return System.nanoTime() / 1_000_000L; }

    public void start() {
        if (task == null) task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    public void stop() {
        if (task != null) { task.cancel(); task = null; }
    }

    private Session session(Player p) {
        Session s = sessions.get(p.getUniqueId());
        if (s == null) { s = new Session(); sessions.put(p.getUniqueId(), s); }
        if (!s.restored) {
            EmberPlayerState st = loadouts.state(p.getUniqueId());
            if (st.loaded) {
                s.engine.restoreCooldowns(st.burstCdRemainMs, st.sustainCdRemainMs, now());
                s.restored = true;
            }
        }
        EmberLoadout l = loadouts.get(p);
        if (s.engine.setLoadout(l.activeSet, l.awakening)) {
            s.burnTargets.clear();
            EmberDamageTrace.set(p, "套装变化 → " + l.setLabel() + "：计数与燃烧清空，内置冷却保留");
        }
        EmberGrowthService g = EmberGrowthService.get(); // D141 set-family talent (连爆 / 燎原 / 扛核)
        EmberSetEngine.Tune t = g == null ? EmberSetEngine.Tune.NONE : g.setTune(p);
        if (!t.equals(s.engine.tune())) s.engine.setTune(t);
        return s;
    }

    // ------------------------------------------------------------------ damage MONITOR

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e) {
        EmberCombatListener.Swing sw = combat.takeSwing(e);
        if (!EmberMode.active() || !EmberMode.isP1(e.getEntity())) return;
        Entity victim = e.getEntity();
        if (victim instanceof Player) {
            // enemy damage taken keeps the 8 s combat window open
            Session s = sessions.get(victim.getUniqueId());
            if (s != null && !e.isCancelled() && e.getFinalDamage() > 0 && !(attacker(e) instanceof Player)) s.engine.touch(now());
            return;
        }
        Player p = attacker(e);
        if (p == null || !(victim instanceof LivingEntity) || victim instanceof ArmorStand) return;
        LivingEntity le = (LivingEntity) victim;
        Session s = session(p);
        long now = now();
        Hit h = new Hit();
        h.kind = classify(e, sw);
        h.rootId = sw == null ? 0 : sw.rootId;
        h.charge = sw == null ? 0 : (sw.valid ? sw.charge : Double.NaN);
        h.finalDamage = e.getFinalDamage();
        h.cancelled = e.isCancelled();
        h.targetAlive = !le.isDead() && le.getHealth() > 0;
        h.targetEnemy = isEnemy(le);
        h.targetInvulnerable = le.isInvulnerable();
        h.targetId = le.getUniqueId().toString();
        double mh = EmberHeal.maxHp(p);
        h.hpFrac = mh > 0 ? Math.max(0, Math.min(1, p.getHealth() / mh)) : 1.0; // D174 L12 低血回涌
        EmberLoadout l = loadouts.get(p);
        Outcome o = s.engine.onHit(h, now, l.b, EmberHeal.maxHp(p));
        if (!h.cancelled && h.finalDamage > 0) s.engine.touch(now);
        if (o.counted) {
            showHud(p, s, now);
            // D439: one-away-from-proc readable (no table change)
            if (o.trigger == Trigger.NONE && "count".equals(o.reason)) maybeFlashAlmost(p, s, o);
        }
        if (o.trigger == Trigger.NONE) return;
        execute(p, le, o, l.b);
    }

    private static Player attacker(EntityDamageByEntityEvent e) {
        Entity d = e.getDamager();
        if (d instanceof Player) return (Player) d;
        if (d instanceof Projectile && ((Projectile) d).getShooter() instanceof Player) return (Player) ((Projectile) d).getShooter();
        return null;
    }

    private static Kind classify(EntityDamageByEntityEvent e, EmberCombatListener.Swing sw) {
        if (SkillService.internalDamage) {
            Kind k = EmberCombatListener.internalKind;
            return k == null ? Kind.SKILL : k;
        }
        if (e.getDamager() instanceof Projectile) return Kind.PROJECTILE;
        if (!(e.getDamager() instanceof Player)) return Kind.SUMMON;
        DamageCause c = e.getCause();
        if (c == DamageCause.ENTITY_SWEEP_ATTACK) return Kind.SWEEP;
        if (c == DamageCause.THORNS) return Kind.THORNS;
        if (c == DamageCause.ENTITY_ATTACK) return Kind.MELEE_MAIN; // root id comes only from onMelee (sw)
        return Kind.ENVIRONMENT;
    }

    private boolean isEnemy(LivingEntity le) {
        if (le instanceof Player || le instanceof ArmorStand) return false;
        if (le instanceof Tameable && ((Tameable) le).isTamed()) return false;
        if (le instanceof Monster || le instanceof Slime || le instanceof Ghast || le instanceof Shulker
                || le instanceof ComplexLivingEntity) return true;
        QuestService q = plugin.getQuestService();
        return q != null && q.isMythic(le);
    }

    // ------------------------------------------------------------------ triggers

    private void execute(final Player p, final LivingEntity main, final Outcome o, final double b) {
        final Session s = sessions.get(p.getUniqueId());
        if (s == null) return;
        if (o.trigger == Trigger.HEAL) {
            double got = EmberHeal.heal(p, o.amount, String.format(Locale.ROOT, "炽愈 %.2f%%×H",
                    EmberSetRules.sustainPct(s.engine.awakening()) * 100));
            if (got <= 0) EmberDamageTrace.set(p, String.format(Locale.ROOT, "炽愈触发（满血，+0；6s 冷却照常开始）"));
            flashProc(p, s, ChatColor.GREEN + "炽愈！" + ChatColor.GRAY + (got > 0
                    ? String.format(Locale.ROOT, " +%.1f", got) : "（满血）"));
            p.getWorld().spawnParticle(Particle.HEART, p.getLocation().add(0, 1.2, 0), 6, 0.35, 0.25, 0.35, 0.0);
            return;
        }
        // damage triggers run one tick later, outside the damage event that caused them
        final Location center = main.getLocation();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!p.isOnline() || p.isDead() || !EmberMode.isP1(p) || p.getWorld() != center.getWorld()) return;
            if (o.trigger == Trigger.IGNITE) ignite(p, s, main, o.amount);
            else if (o.trigger == Trigger.EXPLODE) explode(p, s, main, center, o.amount, o.radius);
        });
    }

    private void ignite(Player p, Session s, LivingEntity le, double perTick) {
        if (le.isDead() || !le.isValid() || le.getWorld() != p.getWorld()) {
            EmberDamageTrace.set(p, "焚烬：主目标已死亡/离开 → 不点燃");
            return;
        }
        String id = le.getUniqueId().toString();
        boolean refresh = s.engine.burns().get(id) != null;
        String evicted = s.engine.burns().ignite(id, perTick, now());
        s.burnTargets.put(id, le);
        if (evicted != null) {
            s.burnTargets.remove(evicted);
            EmberDamageTrace.set(p, "焚烬：第 6 个目标 → 最早的燃烧结束 (" + evicted.substring(0, 8) + ")");
        }
        EmberBurnBook.Burn burn = s.engine.burns().get(id);
        EmberDamageTrace.set(p, String.format(Locale.ROOT, "焚烬%s %s：每跳 %.2f（快照），燃烧中 %d 目标",
                refresh ? "续燃(只延长结束时间)" : "点燃", le.getName(), burn == null ? perTick : burn.perTick, s.engine.burns().size()));
        le.getWorld().spawnParticle(Particle.FLAME, le.getLocation().add(0, 1, 0), 12, 0.3, 0.5, 0.3, 0.01);
        // D439 readable: 燃层 ignite / refresh
        flashProc(p, s, ChatColor.GOLD + (refresh ? "焚烬续燃" : "焚烬点燃")
                + ChatColor.GRAY + " · 燃烧 " + s.engine.burns().size() + " 目标");
    }

    private void explode(Player p, Session s, LivingEntity main, Location center, double amount, double r) {
        List<EmberSetEngine.Candidate> cand = new ArrayList<EmberSetEngine.Candidate>();
        Map<String, LivingEntity> byId = new HashMap<String, LivingEntity>();
        for (Entity en : center.getWorld().getNearbyEntities(center, r, r, r)) {
            if (!(en instanceof LivingEntity)) continue;
            LivingEntity le = (LivingEntity) en;
            if (le.isDead() || !le.isValid() || le.isInvulnerable() || !isEnemy(le)) continue;
            double d = le.getLocation().distance(center);
            String id = le.getUniqueId().toString();
            cand.add(new EmberSetEngine.Candidate(id, le.getEntityId(), le == main ? 0.0 : d));
            byId.put(id, le);
        }
        List<EmberSetEngine.Candidate> pick = EmberSetEngine.pickTargets(cand, r);
        center.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, center.clone().add(0, 1, 0), 1);
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.4f);
        String tag = String.format(Locale.ROOT, "烬爆 %.2f×B(%.2f)=%.2f", EmberSetRules.burstCoef(s.engine.awakening()), b(p), amount);
        StringBuilder names = new StringBuilder();
        for (EmberSetEngine.Candidate c : pick) {
            LivingEntity le = byId.get(c.id);
            names.append(le.getName()).append(String.format(Locale.ROOT, "@%.1f ", c.distance));
            EmberCombatListener.dealP1(p, le, amount, Kind.EXPLOSION, tag);
        }
        EmberDamageTrace.set(p, String.format(Locale.ROOT, "%s 半径 %.1f，%d/%d 目标：%s", tag, r, pick.size(), cand.size(), names.toString().trim()));
        flashProc(p, s, ChatColor.RED + "烬爆！" + ChatColor.GRAY + " · 命中 " + pick.size());
    }

    private double b(Player p) { return loadouts.get(p).b; }

    /**
     * D174 L08 潮蚀护符: a 烬斩 target is ignited with the 焚烬 burn × {@code mult} — only while the scorch set is active
     * (same snapshot / refresh / 5-target rules as the set's own ignite; p1sim skill_variant skill_ignite).
     */
    public boolean skillIgnite(Player p, LivingEntity le, double mult) {
        Session s = session(p);
        if (s == null || !"scorch".equals(s.engine.family()) || le == null) return false;
        double perTick = EmberSetRules.burnCoef(s.engine.awakening()) * s.engine.tune().burnMult * b(p) * mult;
        if (!(perTick > 0)) return false;
        ignite(p, s, le, perTick);
        return true;
    }

    // ------------------------------------------------------------------ ticker

    private void tick() {
        ticks++;
        if (!EmberMode.active()) {
            if (!sessions.isEmpty()) {
                for (Session s : sessions.values()) { s.engine.clearCombat(); s.burnTargets.clear(); }
            }
            return;
        }
        long now = now();
        for (Iterator<Map.Entry<UUID, Session>> it = sessions.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, Session> en = it.next();
            Player p = Bukkit.getPlayer(en.getKey());
            Session s = en.getValue();
            if (p == null) continue; // quit handler persists and removes
            if (!s.engine.burns().isEmpty()) burnTick(p, s, now);
        }
        if (ticks % 20 != 0) return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!EmberMode.isP1(p)) continue;
            Session s = session(p); // also applies set changes
            if (s.engine.expire(now)) EmberDamageTrace.set(p, "脱战 8s → 计数清空");
            showHud(p, s, now);
        }
    }

    private void burnTick(Player p, Session s, long now) {
        if (p.isDead() || !EmberMode.isP1(p)) {
            s.engine.clearCombat();
            s.burnTargets.clear();
            return;
        }
        for (EmberBurnBook.Tick t : s.engine.burns().due(now)) {
            LivingEntity le = s.burnTargets.get(t.target);
            if (le == null || le.isDead() || !le.isValid() || le.getWorld() != p.getWorld()) {
                s.engine.burns().remove(t.target);
                s.burnTargets.remove(t.target);
                continue;
            }
            EmberCombatListener.dealP1(p, le, t.amount, Kind.BURN,
                    String.format(Locale.ROOT, "焚烬 燃烧跳 %.2f（点燃时快照）", t.amount));
            le.getWorld().spawnParticle(Particle.FLAME, le.getLocation().add(0, 1, 0), 6, 0.25, 0.4, 0.25, 0.01);
        }
        // forget entity refs of burns that ended
        if (s.burnTargets.size() != s.engine.burns().size()) s.burnTargets.keySet().retainAll(s.engine.burns().view().keySet());
    }

    private void showHud(Player p, Session s, long now) {
        if (now < s.procFlashUntil) return; // D439 proc flash owns ActionBar briefly
        if (now < s.skillFeelUntil) return; // D445 skill confirm owns ActionBar briefly
        EmberSupplyService sup = plugin.getEmberSupplies();
        String hint = sup == null ? null : sup.lowHpHint(p); // new-player polish: low HP → how to drink (wins over the set line)
        boolean force = now < s.forceHudUntil;
        String hud = hint != null ? hint : s.engine.hud(now, force); // D444: force idle 0/every after flash
        if (hud != null) {
            p.sendActionBar(hint != null ? hud : ChatColor.GOLD + hud);
            s.hudShown = true;
        } else if (s.hudShown) {
            p.sendActionBar("");
            s.hudShown = false;
        }
    }

    /** D439/D444: short ActionBar flash on set proc (subtitle) / almost-ready (ActionBar only). */
    private void flashProc(Player p, Session s, String msg) {
        flashProc(p, s, msg, true);
    }

    private void flashProc(Player p, Session s, String msg, boolean withSubtitle) {
        if (p == null || s == null || msg == null || msg.isEmpty()) return;
        long until = now() + 1500L;
        s.procFlashUntil = until;
        try { p.sendActionBar(msg); } catch (Throwable ignored) { }
        try {
            p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(msg));
        } catch (Throwable ignored) { }
        // D444: subtitle for real proc only — almost-ready stays ActionBar-only to cut spam
        if (withSubtitle) {
            try { p.sendTitle("", msg, 5, 25, 8); } catch (Throwable ignored) { }
        }
        s.hudShown = true;
        final UUID id = p.getUniqueId();
        final long token = until;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Session ss = sessions.get(id);
            Player pp = Bukkit.getPlayer(id);
            if (ss == null || pp == null || !pp.isOnline()) return;
            if (ss.procFlashUntil != token) return; // newer flash won
            ss.procFlashUntil = 0L;
            // D444: hand back to set line even when idle 0/every (else ActionBar goes blank)
            ss.forceHudUntil = now() + FORCE_HUD_MS;
            showHud(pp, ss, now());
        }, 30L);
    }

    /** D439/D444: ActionBar when counter is one hit from trigger; debounced once per approach. */
    private void maybeFlashAlmost(Player p, Session s, Outcome o) {
        String fam = s.engine.family();
        int ev = s.engine.every();
        int c = o.counter;
        long now = now();
        // left almost-ready → clear debounce so the next approach can flash
        if (c != ev - 1) {
            if (s.lastAlmostCounter == ev - 1) s.lastAlmostCounter = -1;
            return;
        }
        if (!shouldFlashAlmost(c, ev, s.lastAlmostCounter, s.lastAlmostAt, now)) return;
        s.lastAlmostCounter = c;
        s.lastAlmostAt = now;
        String msg = null;
        if ("burst".equals(fam)) {
            msg = ChatColor.YELLOW + "烬爆将满 " + c + "/" + ev;
        } else if ("sustain".equals(fam)) {
            msg = ChatColor.YELLOW + "炽愈将满 " + c + "/" + ev;
        } else if ("scorch".equals(fam)) {
            msg = ChatColor.YELLOW + "焚烬将满 " + c + "/" + ev;
        }
        if (msg != null) flashProc(p, s, msg, false); // D444: no subtitle for almost-ready
    }

    // ------------------------------------------------------------------ clears / persistence

    /** D141 反震: move the burst counter forward after a boss telegraph (hit_burst / dodge_burst). */
    public boolean primeBurst(Player p, int n) {
        Session s = sessions.get(p.getUniqueId());
        if (s == null) s = session(p);
        boolean ok = s != null && s.engine.primeBurst(n);
        if (ok) EmberDamageTrace.set(p, "反震：预警招 → 烬爆计数 +" + n + "（" + s.engine.counter() + "/" + s.engine.every() + "）");
        return ok;
    }

    /** D141 燎原: when a burning enemy dies, its burn moves to the nearest enemy within 4 blocks that is not burning. */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMobDeath(org.bukkit.event.entity.EntityDeathEvent e) {
        if (e.getEntity() instanceof Player || !EmberMode.active() || !EmberMode.isP1(e.getEntity())) return;
        String id = e.getEntity().getUniqueId().toString();
        for (Map.Entry<UUID, Session> en : sessions.entrySet()) {
            Session s = en.getValue();
            if (s.engine.tune().burnSpread <= 0 || s.engine.burns().get(id) == null) continue;
            if (now() < s.spreadCdUntil) continue;
            Player p = Bukkit.getPlayer(en.getKey());
            if (p == null || p.getWorld() != e.getEntity().getWorld()) continue;
            LivingEntity best = null;
            double bd = 16.0;
            for (Entity x : e.getEntity().getNearbyEntities(4, 3, 4)) {
                if (!(x instanceof LivingEntity) || x.isDead() || !isEnemy((LivingEntity) x)) continue;
                if (s.engine.burns().get(x.getUniqueId().toString()) != null) continue;
                double d = x.getLocation().distanceSquared(e.getEntity().getLocation());
                if (d < bd) { bd = d; best = (LivingEntity) x; }
            }
            if (best != null && s.engine.burns().transfer(id, best.getUniqueId().toString(), now(), (long) (s.engine.tune().burnSpread * 1000))) {
                s.burnTargets.remove(id);
                s.burnTargets.put(best.getUniqueId().toString(), best);
                s.spreadCdUntil = now() + (long) (s.engine.tune().spreadIcd * 1000);
                EmberDamageTrace.set(p, "燎原：燃烧转移到 " + best.getName());
                best.getWorld().spawnParticle(Particle.FLAME, best.getLocation().add(0, 1, 0), 10, 0.3, 0.5, 0.3, 0.01);
                EmberGrowthService g = EmberGrowthService.get(); // D455 sig feel if L01 owns burn_spread
                if (g != null) g.flashSigOwned(p, "burn_spread", "燃烧传火");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        Session s = sessions.get(e.getEntity().getUniqueId());
        if (s != null) { s.engine.clearCombat(); s.burnTargets.clear(); }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorld(PlayerChangedWorldEvent e) {
        Session s = sessions.get(e.getPlayer().getUniqueId());
        if (s != null) {
            s.engine.clearCombat();
            s.burnTargets.clear();
            if (s.hudShown) { e.getPlayer().sendActionBar(""); s.hudShown = false; }
        }
    }

    /** Before EmberLoadoutService's MONITOR quit save: write the monotonic remaining cooldowns into the state. */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        persist(e.getPlayer().getUniqueId(), true);
    }

    private void persist(UUID id, boolean drop) {
        Session s = drop ? sessions.remove(id) : sessions.get(id);
        if (s == null || !s.restored) return; // never restored → the loaded state values stay as they were
        EmberPlayerState st = loadouts.state(id);
        long now = now();
        st.burstCdRemainMs = s.engine.burstCdRemaining(now);
        st.sustainCdRemainMs = s.engine.sustainCdRemaining(now);
    }

    /** onDisable: copy every session's remaining cooldowns into the states before they are saved. */

    /** D445: set proc / almost-ready / forceHud currently owns ActionBar — skill feel must yield. */
    public boolean isSetHudOwned(Player p, long nowMs) {
        if (p == null) return false;
        Session s = sessions.get(p.getUniqueId());
        if (s == null) return false;
        long now = nowMs > 0 ? nowMs : now();
        return now < s.procFlashUntil || now < s.forceHudUntil;
    }

    public void flushAll() {
        for (UUID id : new ArrayList<UUID>(sessions.keySet())) persist(id, false);
        stop();
    }

    /** for /corerpg p1 inspect */
    public String describe(Player p) {
        Session s = sessions.get(p.getUniqueId());
        if (s == null) return "套装运行时：无会话";
        long now = now();
        return String.format(Locale.ROOT, "套装运行时：%s 觉醒%d 计数 %d/%d · 烬爆冷却 %.1fs · 炽愈冷却 %.1fs · 燃烧 %d 目标",
                s.engine.family(), s.engine.awakening(), s.engine.counter(), s.engine.every(),
                s.engine.burstCdRemaining(now) / 1000.0, s.engine.sustainCdRemaining(now) / 1000.0, s.engine.burns().size());
    }
}
