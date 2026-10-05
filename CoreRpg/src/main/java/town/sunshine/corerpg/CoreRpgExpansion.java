package town.sunshine.corerpg;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI identifier {@code corerpg}. D240 / ARCH S3-11: thin dispatcher — {@link CorePapi#route} picks the
 * section; bodies live in {@link CorePapiAccount} / {@link CorePapiKit} / {@link CorePapiProgress} /
 * {@link CorePapiStamina}; {@code p1_} goes to {@code EmberRunService.placeholder} (sections in {@code EmberRunPapi}).
 * Same keys and values as before; unknown keys still return {@code null}.
 */
public final class CoreRpgExpansion extends PlaceholderExpansion {
    private final CoreRpgPlugin plugin;
    public CoreRpgExpansion(CoreRpgPlugin plugin) { this.plugin = plugin; }
    @Override public String getIdentifier() { return "corerpg"; }
    @Override public String getAuthor() { return "sunshine-town"; }
    @Override public String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }
    @Override public boolean canRegister() { return true; }
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || params == null) return "";
        PlayerData data = plugin.getDataStore().get(player.getUniqueId());
        String key = params.toLowerCase();
        switch (CorePapi.route(key)) {
            case ACCOUNT: return CorePapiAccount.account(plugin, player, data, key);
            case CASH: return CorePapiAccount.cash(plugin, data, key);
            case KIT: return CorePapiKit.resolve(plugin, player, data, key);
            case PROGRESS: return CorePapiProgress.progress(plugin, data, key);
            case GATE: return CorePapiProgress.gate(plugin, player, data, key.substring(5));
            case P1: {
                town.sunshine.corerpg.p1.EmberRunService r = plugin.getEmberRuns();
                return r == null ? "" : r.placeholder(player, key.substring(3));
            }
            case STAMINA: return CorePapiStamina.resolve(plugin.getStaminaService(), data, key);
            default: return null;
        }
    }
}
