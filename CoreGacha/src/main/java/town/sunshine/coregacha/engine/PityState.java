package town.sunshine.coregacha.engine;

/** Mutable per-(player, pity group, banner) state the engine reads and advances. */
public final class PityState {
    /** pulls since the last 传说 (0 … hardPity-1) */
    public int pity5;
    /** pulls since the last 史诗-or-better (0 … tier2Every-1) */
    public int pity4;
    /** last 传说 item id on this banner (no-repeat rule), null = none */
    public String lastLegend;
    /** spark points on this banner */
    public int spark;

    public PityState() { }
    public PityState(int pity5, int pity4, String lastLegend, int spark) { this.pity5 = pity5; this.pity4 = pity4; this.lastLegend = lastLegend; this.spark = spark; }
    public PityState copy() { return new PityState(pity5, pity4, lastLegend, spark); }
}
