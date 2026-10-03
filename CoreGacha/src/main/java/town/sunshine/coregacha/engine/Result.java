package town.sunshine.coregacha.engine;

/** One pull outcome, with the counters before / after (written to the audit table as is). */
public final class Result {
    public final Tier tier;
    public final Item item;
    public final boolean dup;
    public final int shards;
    public final int pity5Before, pity5After, pity4Before, pity4After, sparkAfter;
    /** base | soft | hard | tier2 */
    public final String rule;

    public Result(Tier tier, Item item, boolean dup, int shards, int p5b, int p5a, int p4b, int p4a, int sparkAfter, String rule) {
        this.tier = tier; this.item = item; this.dup = dup; this.shards = shards;
        this.pity5Before = p5b; this.pity5After = p5a; this.pity4Before = p4b; this.pity4After = p4a; this.sparkAfter = sparkAfter; this.rule = rule;
    }
}
