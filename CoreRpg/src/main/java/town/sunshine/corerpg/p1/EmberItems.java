package town.sunshine.corerpg.p1;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.NiBridge;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Bukkit side of the trusted item identity: reads/writes the {@code ember_v1} NBT compound, checks the
 * HMAC signature (key file {@code plugins/CoreRpg/ember-v1-item.key}, generated on first use, git-ignored,
 * never logged) and that the NI id on the stack matches the template the data claims.
 */
public final class EmberItems {

    public static final String KEY_FILE = "ember-v1-item.key";
    public static final String SIG = "sig";

    private final CoreRpgPlugin plugin;
    private final NiBridge ni;
    private volatile byte[] key;

    public EmberItems(CoreRpgPlugin plugin, NiBridge ni) {
        this.plugin = plugin;
        this.ni = ni;
    }

    /** Result of reading one stack. {@code data} may be non-null even when {@code problem} is set (for inspect). */
    public static final class Read {
        public final EmberItemData data;
        public final String problem;
        Read(EmberItemData data, String problem) { this.data = data; this.problem = problem; }
        public boolean ok() { return data != null && problem == null; }
    }

    public boolean signing() { return EmberMode.get() == null || EmberMode.get().b("security.sign_items", true); }

    private synchronized byte[] key() {
        if (key != null) return key;
        File f = new File(plugin.getDataFolder(), KEY_FILE);
        try {
            if (f.exists()) {
                byte[] k = Base64.getDecoder().decode(new String(Files.readAllBytes(f.toPath()), StandardCharsets.US_ASCII).trim());
                if (k.length >= 32) { key = k; return key; }
                plugin.getLogger().warning("[" + EmberMode.MODE_ID + "] " + KEY_FILE + " too short, regenerating is unsafe; P1 items cannot be verified");
                return null;
            }
            byte[] k = new byte[32];
            new SecureRandom().nextBytes(k);
            plugin.getDataFolder().mkdirs();
            Files.write(f.toPath(), Base64.getEncoder().encodeToString(k).getBytes(StandardCharsets.US_ASCII));
            f.setReadable(false, false); f.setReadable(true, true);
            f.setWritable(false, false); f.setWritable(true, true);
            plugin.getLogger().info("[" + EmberMode.MODE_ID + "] generated item signing key " + KEY_FILE + " (keep it, do not commit)");
            key = k;
            return key;
        } catch (Throwable t) {
            plugin.getLogger().warning("[" + EmberMode.MODE_ID + "] item key unavailable: " + t.getClass().getSimpleName());
            return null;
        }
    }

    public boolean hasData(ItemStack item) { return NmsNbt.has(item, EmberItemData.NBT_KEY); }

    /** @return null when the stack carries no ember_v1 compound */
    public Read read(ItemStack item) {
        Map<String, Object> m = NmsNbt.read(item, EmberItemData.NBT_KEY);
        if (m == null) return null;
        EmberItemData d = EmberItemData.fromMap(m);
        String bad = d.validate();
        if (bad != null) return new Read(d, bad);
        if (item.getAmount() != 1) return new Read(d, "stack amount " + item.getAmount() + " (duplicate)");
        String niId = ni.getNiId(item);
        if (niId == null || !niId.equals(d.ni)) return new Read(d, "NI id on stack " + niId + " != " + d.ni);
        if (signing()) {
            byte[] k = key();
            if (k == null) return new Read(d, "no signing key");
            Object sig = m.get(SIG);
            if (!d.verify(k, sig == null ? null : String.valueOf(sig))) return new Read(d, "bad signature");
        }
        return new Read(d, null);
    }

    /** Builds a fresh NI stack for {@code d} with signed NBT and P1 display lines; null if the template is missing. */
    public ItemStack create(EmberItemData d) {
        ItemStack base = ni.createNiItem(d.ni);
        if (base == null) return null;
        Map<String, Object> m = d.toMap();
        if (signing()) {
            byte[] k = key();
            if (k == null) return null;
            m.put(SIG, d.sign(k));
        }
        ItemStack out = NmsNbt.write(base, EmberItemData.NBT_KEY, m);
        if (out == null) return null;
        applyLore(out, d);
        return out;
    }

    /**
     * Endgame #10 / F-review (D127): rewrite the display lines of P1 items made before D101 (old 「标准｜锋刃0%」 names)
     * to the current wording — template lore + {@link #applyLore}. Display only: the signed ember_v1 data is untouched,
     * and only valid, verified stacks are touched. Returns how many stacks were rewritten.
     */
    public int relabel(org.bukkit.entity.Player p) {
        org.bukkit.inventory.PlayerInventory inv = p.getInventory();
        ItemStack[] all = inv.getContents();
        int n = 0;
        for (int i = 0; i < all.length; i++) {
            ItemStack it = all[i];
            if (it == null || !hasData(it)) continue;
            Read r = read(it);
            if (r == null || !r.ok()) continue;
            ItemStack fresh = ni.createNiItem(r.data.ni);
            if (fresh == null) continue;
            applyLore(fresh, r.data);
            ItemMeta want = fresh.getItemMeta(), have = it.getItemMeta();
            if (want == null || have == null) continue;
            List<String> wl = want.hasLore() ? want.getLore() : new ArrayList<String>();
            List<String> hl = have.hasLore() ? have.getLore() : new ArrayList<String>();
            String wn = want.hasDisplayName() ? want.getDisplayName() : null, hn = have.hasDisplayName() ? have.getDisplayName() : null;
            if (wl.equals(hl) && (wn == null ? hn == null : wn.equals(hn))) continue;
            have.setLore(wl);
            if (wn != null) have.setDisplayName(wn);
            it.setItemMeta(have);
            inv.setItem(i, it);
            n++;
        }
        return n;
    }

    /**
     * Appends the §19.3 info lines (成色/精工/强化 + base composition). Display only, never read back.
     * Never uses the legacy lore keys 物理伤害 / 生命力 / 物理防御 that StatService parses.
     */
    public void applyLore(ItemStack item, EmberItemData d) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList<String>();
        EmberTables t = EmberMode.tables();
        lore.add("");
        if (d.isArmor()) { // D318 六槽: armor lines (follows the charm; no stat number of its own)
            lore.addAll(EmberSixSlot.armorLore(d));
            meta.setLore(lore);
            item.setItemMeta(meta);
            return;
        }
        lore.add(ChatColor.GOLD + d.shortLabel());
        double g = EmberFormula.growth(t, d.quality, d.craft, d.enhance);
        if (d.isBlade()) {
            lore.add(ChatColor.GRAY + String.format(Locale.ROOT, "攻击 %.1f（成长 ×%.2f），另加余烬等级部分", t.weaponA(d.tier) * g, g));
            lore.add(ChatColor.DARK_GRAY + "拿在主手生效；副本里按 F 放烬斩");
        } else {
            lore.add(ChatColor.GRAY + String.format(Locale.ROOT, "生命 +%.1f · 防御 %.0f（成长 ×%.2f）", t.charmH(d.tier) * g, t.charmD(d.tier), g));
            lore.add(ChatColor.DARK_GRAY + "放在背包里生效（只算选定的一件；第一件自动选定，换件：手持它点装备页「已选护符」）");
        }
        lore.add(ChatColor.DARK_GRAY + (d.bound ? "绑定" : "未绑定") + " · " + EmberCompare.sourceName(d.source)); // D99: uid only via admin inspect
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
