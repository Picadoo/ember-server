package town.sunshine.coregacha;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Gameplay ticket sources (design §2.1): active online minutes and CoreRpg's daily commission count (read only), once
 * per day each; plus the physical 扭蛋券 (NeigeItems id, never matched by display name) redeemed into the wallet.
 */
final class Sources implements Listener {
    private final CoreGachaPlugin pl;
    Sources(CoreGachaPlugin pl) { this.pl = pl; }

    /** every minute on the main thread */
    void minute() {
        GachaService s = pl.service();
        String day = GachaService.today();
        List<Integer> mins = pl.getConfig().getIntegerList("tickets.online_minutes");
        List<Integer> clears = pl.getConfig().getIntegerList("tickets.bounty_clears");
        List<String> afk = pl.getConfig().getStringList("tickets.afk_worlds");
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID u = p.getUniqueId();
            PCache pc = s.get(u);
            if (pc == null) continue;
            if (!day.equals(pc.day)) { pc.daily.clear(); pc.day = day; pc.onlineMin = 0; }
            Location now = p.getLocation();
            boolean moved = pc.lastLoc != null && pc.lastLoc.getWorld() == now.getWorld()
                    && (pc.lastLoc.distanceSquared(now) > 1.0 || Math.abs(pc.lastLoc.getYaw() - now.getYaw()) > 10);
            pc.lastLoc = now.clone();
            if (moved && !afk.contains(now.getWorld().getName())) {
                pc.onlineMin++;
                pc.onlineDirty = true;
                if (pc.onlineMin % 5 == 0) { s.saveOnline(u, pc.onlineMin); pc.onlineDirty = false; }
            }
            for (int m : mins) if (pc.onlineMin >= m && pc.dailyGet("src_online_" + m, day) == 0) {
                pc.daily.put("src_online_" + m, 1); // local guard; the DB insert-once is the real one
                s.addTickets(u, p.getName(), 1, "online", "src_online_" + m, "今天活跃在线 " + m + " 分钟");
            }
            int b = pl.rpg().bountyToday(u);
            for (int c : clears) if (b >= c && pc.dailyGet("src_bounty_" + c, day) == 0) {
                pc.daily.put("src_bounty_" + c, 1);
                s.addTickets(u, p.getName(), 1, "bounty", "src_bounty_" + c, "今天主线结算 " + c + " 局");
            }
        }
    }

    // ------------------------------------------------------------------ physical tickets (NeigeItems)

    private String niId() { return pl.getConfig().getString("tickets.ni_item", "ember_gacha_ticket"); }

    boolean isTicket(ItemStack it) {
        if (it == null || it.getType() == Material.AIR) return false;
        if (Bukkit.getPluginManager().getPlugin("NeigeItems") == null) return false;
        try {
            pers.neige.neigeitems.item.ItemInfo info = pers.neige.neigeitems.manager.ItemManager.INSTANCE.isNiItem(it);
            return info != null && niId().equals(info.getId());
        } catch (Throwable t) { return false; }
    }

    int countPhysical(Player p) {
        int n = 0;
        for (ItemStack it : p.getInventory().getStorageContents()) if (isTicket(it)) n += it.getAmount();
        return n;
    }

    /** take every physical ticket out of the inventory and put it in the wallet; put the items back if the DB write fails */
    void redeem(Player p) {
        final UUID u = p.getUniqueId();
        ItemStack[] inv = p.getInventory().getStorageContents();
        final java.util.List<ItemStack> taken = new java.util.ArrayList<ItemStack>();
        int n = 0;
        for (int i = 0; i < inv.length; i++) if (isTicket(inv[i])) { n += inv[i].getAmount(); taken.add(inv[i].clone()); p.getInventory().setItem(i, null); }
        if (n == 0) { p.sendMessage(GachaService.P + "背包里没有实物扭蛋券。"); return; }
        final int k = n;
        final String name = p.getName();
        pl.service().tx("redeem " + name, c -> {
            GachaService.ensureWallet(c, u, name);
            int[] w = GachaService.lockWallet(c, u);
            GachaService.setWallet(c, u, w[0] + k, w[1]);
            GachaService.ledger(c, u, name, k, 0, w[0] + k, w[1], "redeem", niId());
            c.commit();
            return w[0] + k;
        }, t -> {
            Player pp = Bukkit.getPlayer(u);
            if (t == null) {
                if (pp != null) { for (ItemStack it : taken) for (ItemStack left : pp.getInventory().addItem(it).values()) pp.getWorld().dropItem(pp.getLocation(), left);
                    pp.sendMessage(GachaService.P + "§c存入失败，券已放回背包。"); }
                else pl.getLogger().severe("[redeem] " + name + " offline after a failed redeem of " + k + " tickets — give back: ni give " + name + " " + niId() + " " + k);
                return;
            }
            PCache pc = pl.service().get(u);
            if (pc != null) pc.tickets = t;
            pl.getLogger().info("[redeem] " + name + " +" + k + " physical tickets → " + t);
            if (pp != null) pp.sendMessage(GachaService.P + "§a存入 " + k + " 张扭蛋券§7 · 现有 §f" + t);
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUse(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND || (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)) return;
        if (!isTicket(e.getItem())) return;
        e.setCancelled(true);
        redeem(e.getPlayer());
    }
}
