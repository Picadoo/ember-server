package town.sunshine.coregacha;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import town.sunshine.coregacha.engine.Banner;
import town.sunshine.coregacha.engine.Item;
import town.sunshine.coregacha.engine.Result;
import town.sunshine.coregacha.engine.Tier;

/**
 * ~2 s opening: five ✦ light up white with rising notes, then flip to the colour of the best tier of the batch
 * (Genshin's meteor colour), a tier sound, a burst for 传说 — then the reveal page (TrMenu ember_gacha_reveal) and a chat
 * summary. Results are already committed; this is only display.
 */
final class Anim {
    private final CoreGachaPlugin pl;
    Anim(CoreGachaPlugin pl) { this.pl = pl; }

    void play(Player p, Banner b, List<Result> rs, int tickets, int shards) {
        Tier best = Tier.COMMON;
        for (Result r : rs) if (r.tier.compareTo(best) < 0) best = r.tier;
        final Tier top = best;
        final int frames = 8;
        for (int f = 0; f <= frames; f++) {
            final int ff = f;
            Bukkit.getScheduler().runTaskLater(pl, () -> {
                if (!p.isOnline()) return;
                if (ff < frames) {
                    int lit = Math.min(5, ff + 1);
                    String col = ff >= 5 ? top.color : "§f";
                    StringBuilder s = new StringBuilder();
                    for (int i = 0; i < 5; i++) s.append(i < lit ? col + "✦ " : "§8✧ ");
                    p.sendTitle(s.toString().trim(), ff >= 5 ? top.color + "✧ " + top.label + " ✧" : "§7" + Item.stripColor(b.name) + " · 抽取中…", 0, 10, 4);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.7f, 0.6f + ff * 0.18f);
                } else {
                    p.sendTitle(top.color + "§l" + top.label, rs.size() == 1 ? rs.get(0).item.display() : "§7十连 · 最高 " + top.colored(), 0, 30, 10);
                    if (top == Tier.LEGEND) {
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_LARGE_BLAST, 1f, 1f);
                        p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation().add(0, 1.5, 0), 60, 0.6, 0.6, 0.6, 0.15);
                    } else if (top == Tier.EPIC) {
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.7f);
                        p.getWorld().spawnParticle(Particle.SPELL_WITCH, p.getLocation().add(0, 1.2, 0), 30, 0.4, 0.5, 0.4, 0.05);
                    } else p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open ember_gacha_reveal " + p.getName());
                    summary(p, b, rs, tickets, shards);
                }
            }, 1L + f * 3L);
        }
    }

    void summary(Player p, Banner b, List<Result> rs, int tickets, int shards) {
        p.sendMessage(GachaService.P + "§f" + Item.stripColor(b.name) + " §7" + (rs.size() == 1 ? "单抽" : "十连") + "结果：");
        for (Result r : rs) {
            p.sendMessage("  " + r.tier.colored() + " §f" + Item.stripColor(r.item.name) + " §8(" + r.item.kindLabel() + ")"
                    + (r.dup ? " §7重复 → §b+" + r.shards + " 光屑" : " §a新！"));
        }
        PCache pc = pl.service().get(p.getUniqueId());
        int p5 = pc == null || pc.pity.get(b.pityGroup) == null ? 0 : pc.pity.get(b.pityGroup)[0];
        PCache.BState bs = pc == null ? null : pc.banners.get(b.id);
        p.sendMessage(GachaService.P + "券 §f" + tickets + " §7· 光屑 §b" + shards + " §7· 距上次传说 §f" + p5 + "§7/" + pl.service().cfg.hardPity
                + " 抽 · 火花 §f" + (bs == null ? 0 : bs.spark) + "§7/" + pl.service().cfg.spark);
        TextComponent t = new TextComponent(GachaService.P);
        String[][] btn = {{"§a[再抽一次]", "/gacha pull " + b.id + " 1"}, {" §6[十连]", "/gacha pull " + b.id + " 10"}, {" §e[扭蛋页]", "/gacha"}, {" §d[我的外观]", "/gacha wear"}};
        for (String[] x : btn) { TextComponent c = new TextComponent(x[0]); c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, x[1])); t.addExtra(c); }
        p.spigot().sendMessage(t);
        boolean ext = false;
        for (Result r : rs) if (!r.dup) { if (r.item.external()) { if (ext) continue; ext = true; } pl.render().afterGain(p, r.item); }
    }
}
