package town.sunshine.corerpg;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI identifier {@code ember} for hologram ladder lines.
 * Examples: %ember_ladder_power_1_name%, %ember_power_score%
 */
public final class EmberLadderExpansion extends PlaceholderExpansion {

    private final CoreRpgPlugin plugin;

    public EmberLadderExpansion(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String getIdentifier() { return "ember"; }
    @Override public String getAuthor() { return "sunshine-town"; }
    @Override public String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }
    @Override public boolean canRegister() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params == null) return "";
        String key = params.toLowerCase();
        LadderService ladder = plugin.getLadderService();

        // Personal
        if ("power_score".equals(key)) {
            if (player == null) return "0";
            if (ladder != null) {
                return String.valueOf(ladder.recomputePower(player));
            }
            PlayerData data = plugin.getDataStore().get(player.getUniqueId());
            return String.valueOf(data.getPowerScore());
        }
        if ("abyss_best".equals(key)) {
            if (player == null) return "0";
            PlayerData data = plugin.getDataStore().get(player.getUniqueId());
            return String.valueOf(data.getAbyssBest());
        }
        if ("weekly_best_sec".equals(key)) {
            if (player == null) return "0";
            PlayerData data = plugin.getDataStore().get(player.getUniqueId());
            return String.valueOf(data.getWeeklyBestSec());
        }

        // Ladder: ladder_<board>_<N>_<name|value>
        if (key.startsWith("ladder_") && ladder != null) {
            String rest = key.substring("ladder_".length());
            // power_1_name / abyss_2_value / speed_3_name
            int us1 = rest.indexOf('_');
            if (us1 <= 0) return null;
            String board = rest.substring(0, us1);
            String rem = rest.substring(us1 + 1);
            int us2 = rem.indexOf('_');
            if (us2 <= 0) return null;
            String numStr = rem.substring(0, us2);
            String field = rem.substring(us2 + 1);
            int rank;
            try {
                rank = Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                return null;
            }
            if (rank < 1) return "name".equals(field) ? "---" : "0";

            LadderService.Entry entry;
            if ("power".equals(board)) entry = ladder.getPowerAt(rank);
            else if ("abyss".equals(board)) entry = ladder.getAbyssAt(rank);
            else if ("speed".equals(board)) entry = ladder.getSpeedAt(rank);
            else return null;

            if ("name".equals(field)) return ladder.nameOrEmpty(entry);
            if ("value".equals(field)) return ladder.valueOrZero(entry);
            return null;
        }

        return null;
    }
}
