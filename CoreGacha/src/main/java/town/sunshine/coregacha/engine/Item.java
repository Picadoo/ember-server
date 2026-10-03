package town.sunshine.coregacha.engine;

import java.util.Collections;
import java.util.Map;

/**
 * One cosmetic. kind: badge | tag | aura | pet | show | corerpg. Everything is display only — the engine has no other
 * reward type (design §1). {@code ref} = the CoreRpg shop id for kind corerpg. {@code props} = render data
 * (symbol, particle, head, label …), read by the plugin only.
 */
public final class Item {
    public static final java.util.Set<String> KINDS = Collections.unmodifiableSet(new java.util.HashSet<String>(
            java.util.Arrays.asList("badge", "tag", "aura", "pet", "show", "corerpg")));

    public final String id, name, kind, ref, icon;
    public final Tier tier;
    public final Map<String, Object> props;

    public Item(String id, String name, Tier tier, String kind, String ref, String icon, Map<String, Object> props) {
        this.id = id; this.name = name; this.tier = tier; this.kind = kind; this.ref = ref; this.icon = icon;
        this.props = props == null ? Collections.<String, Object>emptyMap() : Collections.unmodifiableMap(props);
    }

    public boolean external() { return "corerpg".equals(kind); }

    public String prop(String k, String def) { Object o = props.get(k); return o == null ? def : String.valueOf(o); }

    public String display() { return tier.color + stripColor(name); }

    public static String stripColor(String s) { return s == null ? "" : s.replaceAll("§.", ""); }

    public String kindLabel() {
        switch (kind) {
            case "badge": return "聊天徽记";
            case "tag": return "扭蛋称号";
            case "aura": return "光环";
            case "pet": return "宠物";
            case "show": return "烟火特效";
            default: return prop("kind_label", "外观商店件");
        }
    }

    @Override public String toString() { return id; }
}
