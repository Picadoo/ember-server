package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 1.62 storage rules (pure, unit-tested): P1 material warehouse amounts, gear library filters / sort / paging, which
 * stored pieces a bulk dismantle may take, when a new drop goes straight into the library, and the soft-undo window.
 * No Bukkit types, so the whole decision table runs offline.
 */
public final class EmberStorageRules {

    private EmberStorageRules() {}

    /** per-id cap of the material warehouse (near-infinite: 2e9 like the legacy warehouse's per_slot_cap) */
    public static final long VAULT_CAP = 2000000000L;

    /** how much of {@code add} fits on top of {@code have} under the cap */
    public static long fits(long have, long add) {
        if (add <= 0) return 0;
        long room = VAULT_CAP - Math.max(0, have);
        return room <= 0 ? 0 : Math.min(room, add);
    }

    // ------------------------------------------------------------------ auto-stash of new gear

    public static final int STASH_LOW = 0;    // default: only while the backpack is nearly full
    public static final int STASH_ALWAYS = 1;
    public static final int STASH_OFF = 2;

    /**
     * A freshly delivered piece goes into the gear library instead of the backpack when the library works (MySQL),
     * the player did not turn it off, the piece would not be equipped / offered as an upgrade, the player already has
     * a piece of that slot in use, and either mode is ALWAYS or fewer than {@code minFree} backpack slots are free
     * (0 free always stashes rather than leaving the reward pending).
     */
    public static boolean autoStash(boolean usable, int mode, int freeSlots, int minFree, boolean upgradeCandidate, boolean hasActive) {
        if (!usable || mode == STASH_OFF || upgradeCandidate || !hasActive) return false;
        return mode == STASH_ALWAYS || freeSlots <= 0 || freeSlots < minFree;
    }

    public static String stashModeName(int mode) {
        return mode == STASH_ALWAYS ? "总是" : mode == STASH_OFF ? "关闭" : "背包快满时";
    }

    public static int nextStashMode(int mode) { return mode == STASH_LOW ? STASH_ALWAYS : mode == STASH_ALWAYS ? STASH_OFF : STASH_LOW; }

    // ------------------------------------------------------------------ gear library filters

    /** One stored piece plus its flags (the GUI / bulk dismantle input). */
    public static final class Entry {
        public final EmberItemData d; public final boolean locked, fav; public final long storedAt;
        public Entry(EmberItemData d, boolean locked, boolean fav, long storedAt) { this.d = d; this.locked = locked; this.fav = fav; this.storedAt = storedAt; }
    }

    /** Filter state: -1 / null = any. sort: 0 newest, 1 tier, 2 quality, 3 enhance (all descending, then newest). */
    public static final class Filter {
        public int quality = -1, tier = -1, sort = 0;
        public String family = null, slot = null;

        public static final String[] FAMILIES = {null, "scorch", "burst", "sustain", "none"};
        public static final String[] SLOTS = {null, "blade", "charm"};
        public static final String[] SORTS = {"最新存入", "阶 高→低", "成色 高→低", "强化 高→低"};

        public void cycleQuality() { quality = quality >= EmberTables.MAX_QUALITY ? -1 : quality + 1; }
        public void cycleTier() { tier = tier >= EmberTables.MAX_TIER ? -1 : tier + 1; }
        public void cycleSort() { sort = (sort + 1) % SORTS.length; }
        public void cycleFamily() { family = next(FAMILIES, family); }
        public void cycleSlot() { slot = next(SLOTS, slot); }

        private static String next(String[] all, String cur) {
            for (int i = 0; i < all.length; i++) if (eq(all[i], cur)) return all[(i + 1) % all.length];
            return all[0];
        }

        public boolean matches(EmberItemData d) {
            if (quality >= 0 && d.quality != quality) return false;
            if (tier >= 0 && d.tier != tier) return false;
            if (family != null && !family.equals(d.family)) return false;
            return slot == null || slot.equals(d.slot);
        }

        public boolean any() { return quality < 0 && tier < 0 && family == null && slot == null; }

        public String label() {
            List<String> p = new ArrayList<String>();
            if (quality >= 0) p.add("成色" + EmberItemData.qualityName(quality));
            if (family != null) p.add(EmberItemData.familyName(family));
            if (slot != null) p.add(EmberItemData.slotName(slot));
            if (tier >= 0) p.add("T" + tier);
            return p.isEmpty() ? "全部" : String.join(" · ", p);
        }
    }

    private static boolean eq(String a, String b) { return a == null ? b == null : a.equals(b); }

    /** filtered + sorted copy */
    public static List<Entry> view(List<Entry> all, Filter f) {
        List<Entry> out = new ArrayList<Entry>();
        for (Entry e : all) if (f.matches(e.d)) out.add(e);
        Comparator<Entry> newest = (a, b) -> Long.compare(b.storedAt, a.storedAt) != 0 ? Long.compare(b.storedAt, a.storedAt) : a.d.uid.compareTo(b.d.uid);
        Comparator<Entry> c;
        switch (f.sort) {
            case 1: c = (a, b) -> b.d.tier != a.d.tier ? b.d.tier - a.d.tier : newest.compare(a, b); break;
            case 2: c = (a, b) -> b.d.quality != a.d.quality ? b.d.quality - a.d.quality : newest.compare(a, b); break;
            case 3: c = (a, b) -> b.d.enhance != a.d.enhance ? b.d.enhance - a.d.enhance : newest.compare(a, b); break;
            default: c = newest;
        }
        Collections.sort(out, c);
        return out;
    }

    public static int pages(int n, int per) { return Math.max(1, (n + per - 1) / per); }

    public static <T> List<T> page(List<T> l, int page, int per) {
        int p = Math.max(0, Math.min(page, pages(l.size(), per) - 1));
        return l.subList(Math.min(l.size(), p * per), Math.min(l.size(), (p + 1) * per));
    }

    /**
     * What a bulk dismantle of the current view may take: never locked, favourite or equipped pieces ({@code equipped}
     * = the uids the loadout still points at), and never a piece the single dismantle would refuse.
     */
    public static List<Entry> bulkDismantle(List<Entry> view, Set<String> equipped) {
        List<Entry> out = new ArrayList<Entry>();
        for (Entry e : view) {
            if (e.locked || e.fav || (equipped != null && equipped.contains(e.d.uid))) continue;
            if (EmberUpgradeRules.dismantleCheck(e.d) != null) continue;
            out.add(e);
        }
        return out;
    }

    public static int blanksOf(List<Entry> l) {
        int n = 0;
        for (Entry e : l) n += EmberUpgradeRules.dismantleYield(e.d);
        return n;
    }

    // ------------------------------------------------------------------ soft undo

    /** true while a dismantle committed at {@code at} may still be undone at {@code now} */
    public static boolean undoOpen(long at, long now, int minutes) {
        return minutes > 0 && at > 0 && now >= at && now - at <= minutes * 60000L;
    }

    /** "3 分 20 秒" left of the undo window */
    public static String left(long at, long now, int minutes) {
        long ms = Math.max(0, at + minutes * 60000L - now);
        long s = ms / 1000;
        return String.format(Locale.ROOT, "%d 分 %02d 秒", s / 60, s % 60);
    }
}
