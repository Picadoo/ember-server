package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1.12.2 lore persistence (docs/design/DESIGN-ember-enhance-socket.md §3).
 * Display: §8强化 §f+N
 * Marker:  §8§o#ember_en:N
 * Socket:  §8孔{slot}: …  +  §8§o#ember_sk:1=id;2=
 * sockets[i]: null=locked, ""=empty unlocked, gemId=filled.
 */
public final class GearLore {

    public static final String EN_MARKER = "#ember_en:";
    public static final String SK_MARKER = "#ember_sk:";
    public static final String AFF_MARKER = "#ember_aff:";

    private static final Pattern DISPLAY_EN = Pattern.compile("强化\\s*\\+(\\d+)");
    private static final Pattern DISPLAY_SLOT = Pattern.compile("^孔(\\d+)\\s*[:：]\\s*(.*)$");
    private static final Pattern DISPLAY_AFF = Pattern.compile("^次要\\s*[:：]");

    private GearLore() {}

    public static int readEnhance(ItemStack stack) {
        String marker = findPlainStarting(stack, EN_MARKER);
        if (marker != null) {
            try {
                return clamp(Integer.parseInt(marker.substring(EN_MARKER.length()).trim()));
            } catch (NumberFormatException ignored) {}
        }
        ItemMeta meta = stack == null ? null : stack.getItemMeta();
        if (meta == null || !meta.hasLore()) return 0;
        for (String line : meta.getLore()) {
            if (line == null) continue;
            String plain = ChatColor.stripColor(line);
            if (plain == null) continue;
            Matcher m = DISPLAY_EN.matcher(plain);
            if (m.find()) {
                try { return clamp(Integer.parseInt(m.group(1))); } catch (NumberFormatException ignored) {}
            }
        }
        return 0;
    }

    /** Length = maxSlots. null=locked, ""=empty, gemId=filled. */
    public static String[] readSockets(ItemStack stack, int maxSlots) {
        String[] out = new String[Math.max(0, maxSlots)];
        for (int i = 0; i < out.length; i++) out[i] = null;

        String marker = findPlainStarting(stack, SK_MARKER);
        if (marker != null) {
            String body = marker.substring(SK_MARKER.length()).trim();
            if (!body.isEmpty()) {
                String[] parts = body.split(";");
                for (String part : parts) {
                    if (part == null) continue;
                    int eq = part.indexOf('=');
                    if (eq <= 0) continue;
                    try {
                        int slot = Integer.parseInt(part.substring(0, eq).trim());
                        if (slot < 1 || slot > out.length) continue;
                        String val = part.substring(eq + 1).trim();
                        if ("-".equals(val)) out[slot - 1] = null;
                        else out[slot - 1] = val;
                    } catch (NumberFormatException ignored) {}
                }
            }
            return out;
        }

        ItemMeta meta = stack == null ? null : stack.getItemMeta();
        if (meta == null || !meta.hasLore()) return out;
        for (String line : meta.getLore()) {
            if (line == null) continue;
            String plain = ChatColor.stripColor(line);
            if (plain == null) continue;
            Matcher m = DISPLAY_SLOT.matcher(plain);
            if (!m.find()) continue;
            int slot = Integer.parseInt(m.group(1));
            if (slot < 1 || slot > out.length) continue;
            String rest = m.group(2) == null ? "" : m.group(2).trim();
            if (rest.contains("未解锁")) out[slot - 1] = null;
            else if (rest.contains("空") || rest.isEmpty()) out[slot - 1] = "";
            else out[slot - 1] = rest;
        }
        return out;
    }

    public static void write(ItemStack stack, int level, String[] sockets,
                             String displayPrefix, String markerPrefix,
                             String socketEmpty, String socketLocked,
                             String socketMarkerPrefix) {
        if (stack == null) return;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;
        List<String> old = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList<String>();
        List<String> kept = new ArrayList<String>();
        for (String line : old) {
            if (line == null) continue;
            String plain = ChatColor.stripColor(line);
            if (plain == null) plain = "";
            if (plain.startsWith(EN_MARKER) || plain.startsWith(SK_MARKER)) continue;
            if (DISPLAY_EN.matcher(plain).find()) continue;
            if (DISPLAY_SLOT.matcher(plain).find()) continue;
            kept.add(line);
        }

        List<String> out = new ArrayList<String>(kept);
        String dPref = displayPrefix != null ? displayPrefix : "§8强化 §f+";
        out.add(colorize(dPref) + level);

        if (sockets != null) {
            for (int i = 0; i < sockets.length; i++) {
                String valueColored;
                if (sockets[i] == null) {
                    valueColored = colorize(socketLocked != null ? socketLocked : "§7未解锁");
                } else if (sockets[i].isEmpty()) {
                    valueColored = colorize(socketEmpty != null ? socketEmpty : "§7空");
                } else {
                    valueColored = ChatColor.WHITE + sockets[i];
                }
                out.add(colorize("§8孔" + (i + 1) + ": ") + valueColored);
            }
        }

        String mPref = markerPrefix != null ? markerPrefix : ("§8§o" + EN_MARKER);
        out.add(colorize(mPref) + level);
        String skPref = socketMarkerPrefix != null ? socketMarkerPrefix : ("§8§o" + SK_MARKER);
        out.add(colorize(skPref) + buildSocketMarkerBody(sockets));

        meta.setLore(out);
        stack.setItemMeta(meta);
    }

    public static String buildSocketMarkerBody(String[] sockets) {
        if (sockets == null || sockets.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sockets.length; i++) {
            if (i > 0) sb.append(';');
            sb.append(i + 1).append('=');
            if (sockets[i] == null) sb.append('-');
            else sb.append(sockets[i]);
        }
        return sb.toString();
    }

    public static String[] syncSockets(String[] previous, int enhanceLevel, int[] unlockAt, int max) {
        String[] out = new String[max];
        for (int i = 0; i < max; i++) {
            int need = (unlockAt != null && i < unlockAt.length) ? unlockAt[i] : Integer.MAX_VALUE;
            if (enhanceLevel < need) {
                out[i] = null;
            } else {
                String prev = (previous != null && i < previous.length) ? previous[i] : "";
                if (prev == null) out[i] = "";
                else out[i] = prev;
            }
        }
        return out;
    }

    public static int firstEmptyUnlocked(String[] sockets) {
        if (sockets == null) return -1;
        for (int i = 0; i < sockets.length; i++) {
            if (sockets[i] != null && sockets[i].isEmpty()) return i;
        }
        return -1;
    }

    private static String findPlainStarting(ItemStack stack, String prefix) {
        if (stack == null) return null;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || !meta.hasLore()) return null;
        for (String line : meta.getLore()) {
            if (line == null) continue;
            String plain = ChatColor.stripColor(line);
            if (plain != null && plain.startsWith(prefix)) return plain;
        }
        return null;
    }

    private static int clamp(int n) {
        if (n < 0) return 0;
        if (n > 10) return 10;
        return n;
    }


    /** Secondary affix: id + rolled value. */
    public static final class Affix {
        public final String id;
        public final int value;
        public Affix(String id, int value) {
            this.id = id;
            this.value = value;
        }
    }

    /**
     * Strip old #ember_aff markers and matching 次要 display lines; keep enhance/socket/main lore.
     * Then append {@code rolls} of marker+display for the given affixes.
     * Marker body: id=value;id2=value2  (prefixed §8§o#ember_aff:)
     * Display: §7次要: §f<name> +N<suffix>
     */
    public static void rewriteAffixes(ItemStack stack, List<Affix> affixes,
                                      Map<String, String> displayNames,
                                      Map<String, String> suffixes) {
        if (stack == null) return;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;
        List<String> old = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList<String>();
        List<String> kept = new ArrayList<String>();
        for (String line : old) {
            if (line == null) continue;
            String plain = ChatColor.stripColor(line);
            if (plain == null) plain = "";
            if (plain.startsWith(AFF_MARKER)) continue;
            if (DISPLAY_AFF.matcher(plain).find()) continue;
            kept.add(line);
        }
        if (affixes != null) {
            for (Affix a : affixes) {
                if (a == null || a.id == null || a.id.isEmpty()) continue;
                String name = displayNames != null && displayNames.containsKey(a.id)
                        ? displayNames.get(a.id) : a.id;
                String suffix = suffixes != null && suffixes.containsKey(a.id)
                        ? suffixes.get(a.id) : "";
                if (suffix == null) suffix = "";
                kept.add(colorize("§7次要: §f") + name + " +" + a.value + suffix);
            }
            StringBuilder body = new StringBuilder();
            for (Affix a : affixes) {
                if (a == null || a.id == null || a.id.isEmpty()) continue;
                if (body.length() > 0) body.append(';');
                body.append(a.id).append('=').append(a.value);
            }
            if (body.length() > 0) {
                kept.add(colorize("§8§o" + AFF_MARKER) + body.toString());
            }
        }
        meta.setLore(kept);
        stack.setItemMeta(meta);
    }

    public static List<Affix> readAffixes(ItemStack stack) {
        List<Affix> out = new ArrayList<Affix>();
        String marker = findPlainStarting(stack, AFF_MARKER);
        if (marker == null) return out;
        String body = marker.substring(AFF_MARKER.length()).trim();
        if (body.isEmpty()) return out;
        // support id=value;id2=value2  OR legacy id / id:value
        String[] parts = body.split(";");
        for (String part : parts) {
            if (part == null || part.isEmpty()) continue;
            int eq = part.indexOf('=');
            int colon = part.indexOf(':');
            String id;
            int value = 0;
            if (eq > 0) {
                id = part.substring(0, eq).trim();
                try { value = Integer.parseInt(part.substring(eq + 1).trim()); }
                catch (NumberFormatException e) { value = 0; }
            } else if (colon > 0) {
                id = part.substring(0, colon).trim();
                try { value = Integer.parseInt(part.substring(colon + 1).trim()); }
                catch (NumberFormatException e) { value = 0; }
            } else {
                id = part.trim();
            }
            if (!id.isEmpty()) out.add(new Affix(id, value));
        }
        return out;
    }

    public static String colorize(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s.replace('§', '&'));
    }
}
