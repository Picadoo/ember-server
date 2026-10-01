package town.sunshine.corerpg;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * B2.137: one-shot confirmation tokens for destructive item actions (分解: old ScrapService and P1 dismantle).
 * A preview issues a token bound to the player, the action kind and a fingerprint of the held item; the chat line
 * carries a clickable [确认] that runs the confirm command with the token. The token expires after 30 s, is single
 * use, and is refused when the held item changed in the meantime.
 */
public final class ConfirmTokens {

    private ConfirmTokens() {}

    public static final long TTL_MS = 30000L;
    private static final SecureRandom RND = new SecureRandom();
    private static final String ALPHA = "abcdefghijkmnpqrstuvwxyz23456789";

    private static final class Entry {
        final String kind, token, fp;
        final long until;
        Entry(String kind, String token, String fp, long until) { this.kind = kind; this.token = token; this.fp = fp; this.until = until; }
    }

    private static final Map<UUID, Entry> PENDING = new ConcurrentHashMap<UUID, Entry>();

    public static String issue(Player p, String kind, String fingerprint) {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) sb.append(ALPHA.charAt(RND.nextInt(ALPHA.length())));
        String t = sb.toString();
        PENDING.put(p.getUniqueId(), new Entry(kind, t, fingerprint, System.currentTimeMillis() + TTL_MS));
        return t;
    }

    /** @return null when the token is valid for this kind and fingerprint (and consumes it), else the refusal text */
    public static String consume(Player p, String kind, String token, String fingerprint) {
        UUID id = p.getUniqueId();
        Entry e = PENDING.get(id);
        if (token == null || token.isEmpty()) return "需要先预览，再点击聊天栏里的 [确认分解]";
        if (e == null || !e.kind.equals(kind) || !e.token.equals(token)) return "该确认已失效（已使用或已被新的预览替换），请重新预览";
        PENDING.remove(id);
        if (System.currentTimeMillis() > e.until) return "确认已超时（" + (TTL_MS / 1000) + " 秒），请重新预览";
        if (!e.fp.equals(fingerprint)) return "手持物品已变化，未分解；请重新预览";
        return null;
    }

    public static void forget(UUID id) { PENDING.remove(id); }

    /** Sends "prefix [label]" where [label] runs {@code command} on click. */
    public static void sendClick(Player p, String prefix, String label, String command, String hover) {
        TextComponent head = new TextComponent(prefix);
        TextComponent btn = new TextComponent(label);
        btn.setColor(net.md_5.bungee.api.ChatColor.RED);
        btn.setBold(true);
        btn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        btn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        TextComponent tail = new TextComponent(ChatColor.DARK_GRAY + "  (" + (TTL_MS / 1000) + " 秒内有效)");
        p.spigot().sendMessage(head, btn, tail);
    }

    static boolean isWeapon(ItemStack s) {
        if (s == null || s.getType() == Material.AIR) return false;
        String n = s.getType().name();
        return n.endsWith("_SWORD") || n.endsWith("_AXE");
    }

    /** True when the main-hand item is a weapon and no other inventory slot holds one (destroying it leaves the player unarmed). */
    public static boolean onlyWeapon(Player p) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (!isWeapon(hand) || hand.getAmount() > 1) return false;
        int held = p.getInventory().getHeldItemSlot();
        ItemStack[] all = p.getInventory().getStorageContents();
        for (int i = 0; i < all.length; i++) if (i != held && isWeapon(all[i])) return false;
        return !isWeapon(p.getInventory().getItemInOffHand());
    }
}
