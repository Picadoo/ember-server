package town.sunshine.corerpg.p1;

/**
 * D245 (ARCH S4 · item provenance): the {@link EmberItemData.Origin} of a newly created Ember piece. Pure (no Bukkit).
 *
 * <p>Reward-ledger pieces ({@code cr_p1_reward} ITEM rows delivered by {@code EmberRunService.giveItem}): map = the run id's
 * map part ({@code q01}, {@code q03c} challenge, {@code q05a2} abyss floor, {@code r01} raid) or the whole run id when it has no
 * {@code -} ({@code starter}); source = {@link EmberEconomy#sourceForGrant} (S01 base, S13 abyss, S34 chest extra, S06 first
 * clear pick, S35 starter) plus {@code raid_item} → S12; time = the ledger row's creation (deterministic, so a re-delivery
 * of the same reward builds the same signed data). Mark redemption = {@code forge} / S28; OP test items = {@code admin} / X03.
 * Anything unregistered gets X00 (a test pins that every ITEM grant key the code writes resolves to a real row).</p>
 */
public final class EmberProvenance {

    private EmberProvenance() {}

    public static final String MAP_FORGE = "forge", MAP_ADMIN = "admin";
    public static final String SRC_MARK_REDEEM = "S28", SRC_FORGE_ROLL = "S59", SRC_ADMIN = "X03", SRC_UNKNOWN = "X00";

    /** REG source of an ITEM reward row ({@code key} = ledger reward key, {@code runId} = ledger run id) */
    public static String itemSource(String key, String runId) {
        String s = EmberEconomy.sourceForGrant(key, runId);
        if (s == null && EmberRaidService.G_ITEM.equals(key)) s = "S12"; // 团本结算 raid_item (REG S12 pays GEAR)
        return s == null ? SRC_UNKNOWN : s;
    }

    /** map / mode part of a ledger run id ({@code <map>[c|a<n>]-<ts36>-<rnd36>}); the run id itself when it has no '-' */
    public static String mapOf(String runId) {
        if (runId == null || runId.isEmpty()) return "unknown";
        String h = EmberEconomy.runHead(runId);
        return h != null ? h : runId;
    }

    /** provenance of a reward-ledger piece; {@code createdMs} = ledger row creation time */
    public static EmberItemData.Origin forReward(String key, String runId, long createdMs) {
        return EmberItemData.Origin.of(mapOf(runId), itemSource(key, runId), runId, createdMs / 1000L);
    }

    /** provenance of an 8-mark redemption ({@code rid} = the payment request id) */
    public static EmberItemData.Origin forRedeem(String rid, long nowMs) {
        return EmberItemData.Origin.of(MAP_FORGE, SRC_MARK_REDEEM, rid, nowMs / 1000L);
    }

    /** D430 provenance of a random forge roll */
    public static EmberItemData.Origin forForgeRoll(String rid, long nowMs) {
        return EmberItemData.Origin.of(MAP_FORGE, SRC_FORGE_ROLL, rid, nowMs / 1000L);
    }

    /** provenance of an OP test piece ({@code how} = give / givedup) */
    public static EmberItemData.Origin forAdmin(String how, long nowMs) {
        return EmberItemData.Origin.of(MAP_ADMIN, SRC_ADMIN, how, nowMs / 1000L);
    }
}
