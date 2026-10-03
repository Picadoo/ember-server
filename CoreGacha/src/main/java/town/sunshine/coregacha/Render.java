package town.sunshine.coregacha;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import town.sunshine.coregacha.engine.Item;

/**
 * Display of the gacha's own cosmetics (no stats anywhere): chat badge / tag, particle aura, hub-only pet (a marker
 * armour stand: no hitbox, no AI, invulnerable, tagged and removed on quit / unload / restart), /gacha fx show.
 */
public final class Render implements Listener {
    static final String PET_TAG = "coregacha_pet";
    private final CoreGachaPlugin pl;
    private final Map<UUID, ArmorStand> pets = new HashMap<UUID, ArmorStand>();
    private final Map<UUID, Long> showCd = new HashMap<UUID, Long>();
    private long tick;

    Render(CoreGachaPlugin pl) { this.pl = pl; }

    private Item worn(UUID u, String kind) {
        PCache pc = pl.service().get(u);
        String id = pc == null ? null : pc.wear.get(kind);
        Item it = id == null ? null : pl.service().cfg.items.get(id);
        return it != null && pc.owned.contains(it.id) ? it : null;
    }

    String badge(UUID u) { Item i = worn(u, "badge"); return i == null ? "" : i.prop("symbol", ""); }

    String tag(UUID u) { Item i = worn(u, "tag"); return i == null ? "" : i.prop("label", i.name); }

    // ------------------------------------------------------------------ chat

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        try {
            UUID u = e.getPlayer().getUniqueId();
            String b = badge(u), t = tag(u);
            if (b.isEmpty() && t.isEmpty()) return;
            String f = e.getFormat();
            int i = f.indexOf("%1$s");
            if (i < 0) { e.setFormat(b + (b.isEmpty() ? "" : "§r ") + f); return; }
            e.setFormat(f.substring(0, i) + (b.isEmpty() ? "" : b + "§r") + "%1$s" + (t.isEmpty() ? "" : " §8「" + t + "§8」§r") + f.substring(i + 4));
        } catch (RuntimeException ignored) { /* plain chat this once */ }
    }

    // ------------------------------------------------------------------ lifecycle

    @EventHandler
    public void onJoin(PlayerJoinEvent e) { pl.service().load(e.getPlayer()); }

    void onLoaded(Player p) { /* pets / auras pick the cache up on the next tick */ }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID u = e.getPlayer().getUniqueId();
        removePet(u);
        showCd.remove(u);
        PCache pc = pl.service().cache.remove(u);
        if (pc != null && pc.onlineDirty) pl.service().saveOnline(u, pc.onlineMin);
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent e) { removePet(e.getPlayer().getUniqueId()); }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for (Entity en : e.getChunk().getEntities()) if (en.getScoreboardTags().contains(PET_TAG) && !pets.containsValue(en)) en.remove();
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        for (Entity en : e.getChunk().getEntities()) if (en.getScoreboardTags().contains(PET_TAG)) { en.remove(); pets.values().remove(en); }
    }

    void cleanAll() {
        for (ArmorStand a : pets.values()) a.remove();
        pets.clear();
        for (World w : Bukkit.getWorlds()) for (Entity en : w.getEntitiesByClass(ArmorStand.class)) if (en.getScoreboardTags().contains(PET_TAG)) en.remove();
    }

    void onWearChanged(Player p, String kind) { if ("pet".equals(kind)) removePet(p.getUniqueId()); }

    void afterGain(Player p, Item it) {
        if (it.external()) {
            net.md_5.bungee.api.chat.TextComponent t = new net.md_5.bungee.api.chat.TextComponent(GachaService.P + "CoreRpg 外观到外观商店里换上 ");
            net.md_5.bungee.api.chat.TextComponent b = new net.md_5.bungee.api.chat.TextComponent("§a[外观商店]");
            b.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/corerpg p1 cosmetic"));
            t.addExtra(b);
            p.spigot().sendMessage(t);
        } else {
            PCache pc = pl.service().get(p.getUniqueId());
            if (pc != null && !pc.wear.containsKey(it.kind)) pl.service().wear(p, it, false, null); // first of its kind: put it on
        }
    }

    private void removePet(UUID u) { ArmorStand a = pets.remove(u); if (a != null) a.remove(); }

    // ------------------------------------------------------------------ ticking (every 2 ticks)

    void tick() {
        tick++;
        java.util.List<String> petWorlds = pl.getConfig().getStringList("display.pet_worlds");
        String noFx = pl.getConfig().getString("display.no_fx_world_prefix", "dungeon_");
        int auraEvery = Math.max(2, pl.getConfig().getInt("display.aura_period_ticks", 10)) / 2;
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID u = p.getUniqueId();
            boolean hidden = p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);
            Item pet = hidden ? null : worn(u, "pet");
            if (pet != null && petWorlds.contains(p.getWorld().getName())) follow(p, pet); else removePet(u);
            if (hidden || tick % auraEvery != 0 || p.getWorld().getName().startsWith(noFx)) continue;
            Item aura = worn(u, "aura");
            if (aura != null) aura(p, aura);
        }
    }

    private void follow(Player p, Item pet) {
        ArmorStand a = pets.get(p.getUniqueId());
        double yaw = Math.toRadians(p.getLocation().getYaw());
        double bob = Math.sin(tick / 6.0) * 0.12;
        // behind-right of the player, about head height
        Location to = p.getLocation().clone().add(-Math.cos(yaw) * 0.8 + Math.sin(yaw) * 0.9, 0.9 + bob, -Math.sin(yaw) * 0.8 - Math.cos(yaw) * 0.9);
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0);
        if (a == null || a.isDead() || a.getWorld() != p.getWorld()) {
            if (a != null) a.remove();
            a = (ArmorStand) p.getWorld().spawnEntity(to, EntityType.ARMOR_STAND);
            a.setVisible(false); a.setSmall(true); a.setMarker(true); a.setGravity(false); a.setInvulnerable(true); a.setBasePlate(false);
            a.setSilent(true); a.setCollidable(false); a.setAI(false);
            a.addScoreboardTag(PET_TAG);
            a.setCustomName("§e" + Item.stripColor(pet.name) + " §7· " + p.getName());
            a.setCustomNameVisible(true);
            a.setHelmet(head(pet.prop("head", "PUMPKIN")));
            pets.put(p.getUniqueId(), a);
        } else if (a.getLocation().distanceSquared(to) > 0.0025) {
            Location cur = a.getLocation();
            double k = cur.distanceSquared(to) > 25 ? 1.0 : 0.35; // ease in, snap when far
            Location nx = cur.clone().add(to.clone().subtract(cur).toVector().multiply(k));
            nx.setYaw(to.getYaw());
            a.teleport(nx);
        }
    }

    static ItemStack head(String spec) {
        String[] s = spec.split(":");
        Material m = Material.matchMaterial(s[0]);
        if (m == null) m = Material.PUMPKIN;
        short data = s.length > 1 ? Short.parseShort(s[1]) : 0;
        return new ItemStack(m, 1, data);
    }

    static Particle particle(String n) { try { return Particle.valueOf(n); } catch (Exception e) { return Particle.CRIT; } }

    private void aura(Player p, Item it) {
        Location base = p.getLocation();
        World w = p.getWorld();
        Particle pt = particle(it.prop("particle", "CRIT"));
        String style = it.prop("style", "ring");
        double ph = tick / 5.0;
        try {
            if ("spiral".equals(style)) {
                for (int i = 0; i < 3; i++) {
                    double a = ph + i * 2.094, y = ((tick + i * 7) % 20) / 10.0;
                    w.spawnParticle(pt, base.clone().add(Math.cos(a) * 0.7, y, Math.sin(a) * 0.7), 1, 0, 0, 0, 0);
                }
                if (tick % 20 == 0 && it.props.containsKey("extra")) w.spawnParticle(particle(it.prop("extra", "LAVA")), base.clone().add(0, 1, 0), 1, 0.3, 0.3, 0.3, 0);
            } else if ("burst".equals(style)) {
                if (tick % 10 == 0) w.spawnParticle(pt, base.clone().add(Math.cos(ph) * 0.8, 2.3, Math.sin(ph) * 0.8), 6, 0.1, 0.1, 0.1, 0.03);
                double hue = (tick % 30) / 30.0;
                Color c = Color.fromRGB(java.awt.Color.HSBtoRGB((float) hue, 0.9f, 1f) & 0xFFFFFF);
                for (int i = 0; i < 2; i++) {
                    double a = ph + i * Math.PI;
                    // REDSTONE dust: offsets = rgb, extra 1 = coloured (1.12)
                    w.spawnParticle(Particle.REDSTONE, base.clone().add(Math.cos(a) * 0.7, 1.0, Math.sin(a) * 0.7), 0,
                            Math.max(0.001, c.getRed() / 255.0), c.getGreen() / 255.0, c.getBlue() / 255.0, 1);
                }
            } else { // ring
                for (int i = 0; i < 4; i++) {
                    double a = ph + i * Math.PI / 2;
                    w.spawnParticle(pt, base.clone().add(Math.cos(a) * 0.8, 0.15, Math.sin(a) * 0.8), 1, 0, 0, 0, 0);
                }
            }
        } catch (IllegalArgumentException ignored) { /* particle needs data on this version */ }
    }

    /** /gacha fx */
    void show(Player p) {
        Item it = worn(p.getUniqueId(), "show");
        if (it == null) { p.sendMessage(GachaService.P + "§c没有装上烟火特效（史诗「星落」、国庆「满天星火」）· /gacha wear"); return; }
        if (p.getWorld().getName().startsWith(pl.getConfig().getString("display.no_fx_world_prefix", "dungeon_"))) { p.sendMessage(GachaService.P + "§c副本里不放烟火。"); return; }
        long now = System.currentTimeMillis(), cd = pl.getConfig().getInt("display.show_cooldown_seconds", 30) * 1000L;
        Long last = showCd.get(p.getUniqueId());
        if (last != null && now - last < cd) { p.sendMessage(GachaService.P + "§c冷却中：还要 " + ((cd - (now - last)) / 1000 + 1) + " 秒"); return; }
        showCd.put(p.getUniqueId(), now);
        p.sendMessage(GachaService.P + "§7放了一场「" + it.name + "§7」（冷却 " + cd / 1000 + " 秒）");
        Particle pt = particle(it.prop("particle", "FIREWORKS_SPARK"));
        Sound snd;
        try { snd = Sound.valueOf(it.prop("sound", "ENTITY_FIREWORK_BLAST")); } catch (Exception e) { snd = Sound.ENTITY_FIREWORK_BLAST; }
        final Sound s = snd;
        final Location at = p.getLocation().clone();
        java.util.Random r = new java.util.Random();
        for (int k = 0; k < 4; k++) {
            final int kk = k;
            Bukkit.getScheduler().runTaskLater(pl, () -> {
                Location l = at.clone().add(r.nextDouble() * 4 - 2, 3 + r.nextDouble() * 2, r.nextDouble() * 4 - 2);
                at.getWorld().spawnParticle(pt, l, 40, 0.3, 0.3, 0.3, 0.12);
                at.getWorld().playSound(l, s, 1.0f, 0.8f + kk * 0.15f);
            }, k * 8L);
        }
    }
}
