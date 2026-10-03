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
        EmberLoadout l = loadouts.get(p);
        Outcome o = s.engine.onHit(h, now, l.b, EmberHeal.maxHp(p));
        if (!h.cancelled && h.finalDamage > 0) s.engine.touch(now);
        if (o.counted) showHud(p, s, now);
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
            showHud(p, s, now());
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
    }

    private double b(Player p) { return loadouts.get(p).b; }

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
        EmberSupplyService sup = plugin.getEmberSupplies();
        String hint = sup == null ? null : sup.lowHpHint(p); // new-player polish: low HP → how to drink (wins over the set line)
        String hud = hint != null ? hint : s.engine.hud(now);
        if (hud != null) {
            p.sendActionBar(hint != null ? hud : ChatColor.GOLD + hud);
            s.hudShown = true;
        } else if (s.hudShown) {
            p.sendActionBar("");
            s.hudShown = false;
        }
    }

    // ------------------------------------------------------------------ clears / persistence

    /** D141 借势: fill the burst counter after a dodged boss telegraph. */
    public boolean primeBurst(Player p) {
        Session s = sessions.get(p.getUniqueId());
        if (s == null) s = session(p);
        boolean ok = s != null && s.engine.primeBurst();
        if (ok) EmberDamageTrace.set(p, "借势：躲开预警 → 下一次普攻触发烬爆");
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
