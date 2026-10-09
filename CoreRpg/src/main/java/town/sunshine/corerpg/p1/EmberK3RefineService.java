package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * D341 K3 · same-slot armor refine · <b>offline skeleton</b> (gate closed).
 * <p>Pure preview/commit-intent layer: calls {@link EmberK3RefineRules#plan}; never touches Bukkit / live yml /
 * play jar. Switch {@link EmberSixSlot#k3RefineEnabled()} defaults off → every call refuses (same as live today).
 * Future Bukkit wiring (EmberForgeService txn + menu) consumes {@link CommitIntent#items} via store.commitTxn —
 * not registered as a player command in this offline prep.
 */
public final class EmberK3RefineService {

    private EmberK3RefineService() {}

    /** ledger kind for cr_p1_txn (when live wiring lands) */
    public static final String KIND = "k3_refine";
    /** material row retire state (destroy + audit; not undoable — design §2) */
    public static final String RETIRE_MATERIAL = "k3_consumed";

    /**
     * Ready-to-commit payload. {@code error != null} → do not mutate; material stays.
     * On success: {@code items} = [target craft=max, material retired]; {@code cost} is {@link Cost#NONE};
     * {@code audit} is the one-line payload for logs.
     */
    public static final class CommitIntent {
        public final String error;
        public final Cost cost;
        public final List<TxnItem> items;
        public final String note;
        public final String audit;
        public final EmberK3RefineRules.Plan plan;

        CommitIntent(String error, Cost cost, List<TxnItem> items, String note, String audit,
                     EmberK3RefineRules.Plan plan) {
            this.error = error;
            this.cost = cost == null ? Cost.NONE : cost;
            this.items = items == null ? Collections.<TxnItem>emptyList() : Collections.unmodifiableList(items);
            this.note = note;
            this.audit = audit;
            this.plan = plan;
        }

        static CommitIntent fail(EmberK3RefineRules.Plan p) {
            return new CommitIntent(p == null ? EmberK3RefineRules.REFUSE_OFF : p.error,
                    Cost.NONE, Collections.<TxnItem>emptyList(), null, null, p);
        }

        static CommitIntent ok(EmberK3RefineRules.Plan p, List<TxnItem> items, String audit) {
            return new CommitIntent(null, Cost.NONE, items, p.note, audit, p);
        }

        public boolean ok() { return error == null; }
    }

    /** preview only — same rules as plan; no mutation. */
    public static EmberK3RefineRules.Plan preview(EmberItemData target, EmberItemData material, boolean materialWorn) {
        return EmberK3RefineRules.preview(target, material, materialWorn);
    }

    /**
     * Build a commit intent from rules. Switch off / refuse → empty items, no destroy.
     * Success → two TxnItems (target update + material retire) + audit line; zero cost.
     * Does <b>not</b> call the DB — caller (future Forge wiring) applies via commitTxn.
     *
     * @param operatorId optional player uuid/name for the audit suffix (may be null)
     */
    public static CommitIntent commitIntent(EmberItemData target, EmberItemData material, boolean materialWorn,
                                            String operatorId) {
        EmberK3RefineRules.Plan p = EmberK3RefineRules.plan(target, material, materialWorn);
        if (!p.ok()) return CommitIntent.fail(p);
        List<TxnItem> items = Arrays.asList(
                new TxnItem(target, p.targetAfter, null),
                new TxnItem(material, null, RETIRE_MATERIAL));
        String audit = p.audit;
        if (operatorId != null && !operatorId.isEmpty()) {
            audit = String.format(Locale.ROOT, "%s op=%s", p.audit, operatorId);
        }
        return CommitIntent.ok(p, items, audit);
    }

    /** overload without operator tag */
    public static CommitIntent commitIntent(EmberItemData target, EmberItemData material, boolean materialWorn) {
        return commitIntent(target, material, materialWorn, null);
    }
}
