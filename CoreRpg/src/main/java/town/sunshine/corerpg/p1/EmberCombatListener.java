package town.sunshine.corerpg.p1;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.SkillService;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The single P1 damage/heal pipeline (source table A01–A04, A20, B01–B12, D01/D02/D09/D10, E01/E02, F03).
 * Every handler first checks {@link EmberMode#isP1}; outside P1 worlds (and with the mode off) nothing here acts.
 *
 * <ul>
 *   <li>LOWEST: player melee → BASE replaced by B × (0.2+0.8c²) × (1.5 on the fixed 10% crit, only c ≥ 0.9);
 *       c from the damage-ratio estimator. Sweep, player projectiles, fire-aspect ignition cancelled.</li>
 *   <li>HIGHEST, player victim: enemy hits/DoT → BASE × M once; environment raw; every vanilla modifier
 *       (armor, resistance, protection, absorption, shield) zeroed. PvP inside P1 worlds cancelled.</li>
 *   <li>HIGHEST, mob victim of a player: vanilla modifiers zeroed so the mob takes exactly the P1 value
 *       (MythicMobs DamageModifiers still act on BASE and show up in the trace).</li>
 *   <li>Regain: every natural/potion/effect heal on a P1 player is cancelled; P1 heals go through
 *       {@link EmberHeal} (setHealth, logged). Totem resurrection and golden apples refused.</li>
 * </ul>
 */
public final class EmberCombatListener implements Listener {

    /** Causes treated as enemy damage over time (×M) when there is no damager entity (B11). */
    private static final Set<DamageCause> ENEMY_DOT = EnumSet.of(DamageCause.WITHER, DamageCause.POISON,
            DamageCause.MAGIC, DamageCause.CUSTOM, DamageCause.THORNS, DamageCause.DRAGON_BREATH);
    private static final DamageModifier[] VANILLA_MODS = {DamageModifier.HARD_HAT, DamageModifier.BLOCKING,
            DamageModifier.ARMOR, DamageModifier.RESISTANCE, DamageModifier.MAGIC, DamageModifier.ABSORPTION};

    private final CoreRpgPlugin plugin;
    private final EmberLoadoutService loadouts;

    /** Last accepted P1 swing per player, for the G02 set runtime (root id, charge, crit, valid ≥0.9). */
    public static final class Swing {
        public final long rootId; public final double charge; public final boolean crit; public final boolean valid; public final long at;
        Swing(long rootId, double charge, boolean crit, boolean valid, long at) {
            this.rootId = rootId; this.charge = charge; this.crit = crit; this.valid = valid; this.at = at;
        }
    }

    private long rootSeq;
    private final Map<UUID, Swing> lastSwing = new HashMap<UUID, Swing>();
    /** victim → [P1 damage of the hit that opened its i-frames, tick-ish ms] for vanilla's "only the excess" rule */
    private final Map<UUID, double[]> iframeLast = new HashMap<UUID, double[]>();
    /** B05: registered entry-grace window (ms epoch) — replaces the legacy Resistance V */
    private final Map<UUID, Long> graceUntil = new HashMap<UUID, Long>();

    /** Label for P1-internal damage currently being dealt (e.g. "烬斩"), shown in the trace. */
    public static String internalTag;

    public EmberCombatListener(CoreRpgPlugin plugin, EmberLoadoutService loadouts) {
        this.plugin = plugin;
        this.loadouts = loadouts;
    }

    public Swing lastSwing(UUID id) { return lastSwing.get(id); }

    public void grantGrace(Player p, long ms) { graceUntil.put(p.getUniqueId(), System.currentTimeMillis() + ms); }

    // ================================================================== outgoing (player → mob)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMelee(EntityDamageByEntityEvent e) {
        Entity victim = e.getEntity();
        if (!EmberMode.isP1(victim)) return;
        Entity damager = e.getDamager();
        if (damager instanceof Player && e.getCause() == DamageCause.ENTITY_SWEEP_ATTACK) { // A04
            e.setCancelled(true);
            EmberDamageTrace.note(e, e.getDamage(), "A04 横扫 → 取消");
            return;
        }
        if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player
                && !(victim instanceof Player)) { // A20
            e.setCancelled(true);
            EmberDamageTrace.note(e, e.getDamage(), "A20 玩家投射物 → 取消");
            return;
        }
        if (!(damager instanceof Player) || !(victim instanceof LivingEntity) || victim instanceof Player
                || victim instanceof ArmorStand) return;
        if (e.getCause() != DamageCause.ENTITY_ATTACK) return;
        if (SkillService.internalDamage) {
            EmberDamageTrace.note(e, e.getDamage(), "内部伤害 " + (internalTag == null ? "(旧技能/被动)" : internalTag) + "，不按普攻结算");
            return;
        }
        Player p = (Player) damager;
        LivingEntity le = (LivingEntity) victim;
        EmberTables t = EmberMode.tables();
        double raw = e.getDamage();

        // ---- F03 charge from the damage ratio
        double attr = 1.0;
        AttributeInstance ai = p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (ai != null) attr = ai.getValue();
        ItemStack hand = p.getInventory().getItemInMainHand();
        int sharp = hand == null ? 0 : hand.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        boolean unknownEnch = hand != null && (hand.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD) > 0
                || hand.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) > 0);
        boolean jump = p.getFallDistance() > 0 && !p.isOnGround() && !p.isSprinting() && !p.isInsideVehicle()
                && !p.hasPotionEffect(PotionEffectType.BLINDNESS) && !inWaterOrClimbing(p);
        boolean iframe = le.getNoDamageTicks() > le.getMaximumNoDamageTicks() / 2.0f;
        double rawFull = iframe ? raw + le.getLastDamage() : raw; // vanilla passed only the excess over lastDamage
        ChargeEstimator.Result est = ChargeEstimator.estimate(rawFull, attr, ChargeEstimator.sharpnessBonus(sharp), jump, unknownEnch);
        double c = est.charge;

        // ---- A01/E01: replace, never add
        EmberLoadout l = loadouts.get(p);
        boolean crit = !est.unreliable && EmberFormula.rollCrit(t, c, ThreadLocalRandom.current().nextDouble());
        double dmg = EmberFormula.melee(t, l.b, c, crit);
        double full = dmg;
        String ifNote = "";
        long now = System.currentTimeMillis();
        if (iframe) {
            double[] prev = iframeLast.get(le.getUniqueId());
            double prevDmg = prev != null && now - (long) prev[1] < 600L ? prev[0] : 0.0;
            dmg = Math.max(0.0, full - prevDmg);
            ifNote = String.format(Locale.ROOT, " · i-frame 只计超出 %.2f", prevDmg);
            if (prev == null || full > prev[0]) iframeLast.put(le.getUniqueId(), new double[]{full, prev == null ? now : prev[1]});
        } else {
            iframeLast.put(le.getUniqueId(), new double[]{full, now});
        }
        if (iframeLast.size() > 4096) iframeLast.clear();
        if (dmg <= 0) {
            e.setCancelled(true);
            EmberDamageTrace.note(e, raw, "P1 普攻 i-frame 内未超出 → 取消");
            return;
        }
        e.setDamage(dmg);
        boolean valid = !est.unreliable && c >= t.critMinCharge;
        lastSwing.put(p.getUniqueId(), new Swing(++rootSeq, c, crit, valid, now));
        EmberDamageTrace.note(e, raw, String.format(Locale.ROOT,
                "A01 P1 普攻替换: 原版 %.2f (attr %.2f, 锋利 %d) → %s · B=%.2f × 蓄力系数 %.3f%s%s = %.2f",
                rawFull, attr, sharp, est, l.b, EmberFormula.chargeFactor(c),
                crit ? " × 暴击 " + t.critMult : (est.vanillaCrit ? " (原版跳劈已剥离)" : ""), ifNote, dmg));
    }

    private static boolean inWaterOrClimbing(Player p) {
        Block b = p.getLocation().getBlock();
        Material m = b.getType();
        return m == Material.WATER || m == Material.STATIONARY_WATER || m == Material.LADDER || m == Material.VINE;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCombust(EntityCombustByEntityEvent e) {
        if (!EmberMode.isP1(e.getEntity())) return;
        Entity c = e.getCombuster();
        if (c instanceof Projectile && ((Projectile) c).getShooter() instanceof Entity) c = (Entity) ((Projectile) c).getShooter();
        if (c instanceof Player) e.setCancelled(true); // A03 fire aspect / flame
    }

    // ================================================================== final pass (HIGHEST)

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFinal(EntityDamageEvent e) {
        Entity victim = e.getEntity();
        if (!EmberMode.isP1(victim)) return;
        Entity src = EmberDamageTrace.attacker(e);
        if (victim instanceof Player) {
            playerVictim((Player) victim, src, e);
        } else if (src instanceof Player && victim instanceof LivingEntity) {
            double before = e.getDamage();
            String z = zeroVanilla(e);
            if (!z.isEmpty()) EmberDamageTrace.note(e, before, "怪物侧原版修正清零: " + z);
        }
    }

    private void playerVictim(Player p, Entity src, EntityDamageEvent e) {
        double before = e.getDamage();
        Long g = graceUntil.get(p.getUniqueId());
        if (g != null) {
            if (System.currentTimeMillis() < g) {
                e.setCancelled(true);
                EmberDamageTrace.note(e, before, "B05 入场保护窗口 → 取消");
                return;
            }
            graceUntil.remove(p.getUniqueId());
        }
        if (src instanceof Player) { // B13: no PvP inside P1 worlds
            e.setCancelled(true);
            EmberDamageTrace.note(e, before, "B13 P1 世界内 PvP → 取消");
            return;
        }
        EmberLoadout l = loadouts.get(p);
        String pipe;
        double base = before;
        if (src != null || ENEMY_DOT.contains(e.getCause())) {
            base = before * l.m;
            pipe = String.format(Locale.ROOT, "B01/B10/B11 敌方伤害 × M %.4f (D=%.0f)", l.m, l.d);
        } else {
            pipe = "B12 环境管道（原值，不乘 M）";
        }
        e.setDamage(base);
        String z = zeroVanilla(e);
        EmberDamageTrace.note(e, before, String.format(Locale.ROOT, "%s: %.2f → %.2f%s", pipe, before, base,
                z.isEmpty() ? "" : " · 原版修正清零: " + z));
    }

    /** Zero every applicable vanilla reduction (armor/toughness, resistance, protection, absorption, shield, helmet). */
    private static String zeroVanilla(EntityDamageEvent e) {
        StringBuilder sb = new StringBuilder();
        for (DamageModifier m : VANILLA_MODS) {
            if (!e.isApplicable(m)) continue;
            double v = e.getDamage(m);
            if (Math.abs(v) > 1e-9) {
                sb.append(m.name().toLowerCase(Locale.ROOT)).append(' ').append(EmberDamageTrace.fmt(v)).append(' ');
                e.setDamage(m, 0.0);
            }
        }
        return sb.toString().trim();
    }

    // ================================================================== heals / survival

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRegain(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player) || !EmberMode.isP1(e.getEntity())) return;
        e.setCancelled(true); // D01 SATIATED, D02 REGEN, D09/D10 MAGIC/MAGIC_REGEN/CUSTOM — P1 heals use EmberHeal
        EmberDamageTrace.heal((Player) e.getEntity(), "regain " + e.getRegainReason().name().toLowerCase(Locale.ROOT)
                + " +" + EmberDamageTrace.fmt(e.getAmount()) + " → 取消（未登记回复）");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onResurrect(EntityResurrectEvent e) {
        if (e.getEntity() instanceof Player && EmberMode.isP1(e.getEntity())) {
            e.setCancelled(true); // B08
            EmberDamageTrace.heal((Player) e.getEntity(), "图腾复活 → 取消 (B08)");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent e) {
        if (!EmberMode.isP1(e.getPlayer())) return;
        if (e.getItem() != null && e.getItem().getType() == Material.GOLDEN_APPLE) { // B06
            e.setCancelled(true);
            e.getPlayer().sendMessage("§6[余烬] §7新模式副本内金苹果不可用（不叠吸收盾与再生）。");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorld(PlayerChangedWorldEvent e) {
        final Player p = e.getPlayer();
        loadouts.invalidate(p.getUniqueId());
        if (!EmberMode.active()) return;
        if (EmberMode.isP1(p)) loadouts.ensureLoaded(p);
        // next tick: StatService switches the max-HP/attack-speed layer between legacy and P1
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (p.isOnline() && plugin.getStatService() != null) plugin.getStatService().refresh(p);
        });
    }

    public void onQuit(UUID id) {
        lastSwing.remove(id);
        graceUntil.remove(id);
    }
}
