package town.sunshine.corerpg.p1.encounter;

/**
 * D236 / ARCH S3 encounter primitives — package entry.
 * <p>Goal (ARCH §5 S3): turn repeat-run variety / boss moves / counterplay from
 * "one Java branch per content pack" into "yml compositions of existing primitives".
 * This first slice ships the <b>interfaces + first adapters</b> only; no new content,
 * no balance number changes ({@code balance_version} stays 58).
 * <p>Four families (each gets a type here; only counterplay + revive points have live adapters in D236):
 * <ul>
 *   <li>{@link RoomObjective} — room clear goals (timer / hold / escort / chain / …). Stub in D236;
 *       live logic still in {@code EmberRunDirector} room ticks.</li>
 *   <li>{@link BossMove} — one telegraphed boss (or elite-twist) skill as a data view over
 *       {@code EmberRunMaps.Skill}. Stub in D236; Skill parse + Director cast loop unchanged.</li>
 *   <li>{@link EmberCounterplay} — counterplay windows: wall stun (D188), whiff stun (D192),
 *       break / interrupt (D193). <b>First live adapter</b>: pure decision + stun timing extracted
 *       from Director; Director keeps FX / tell / apply and delegates predicates here.</li>
 *   <li>{@link RevivePoint} — D106 raid revive triggers (room open / boss spawn / half-HP /
 *       adds phase / last-phase extra). Why-strings centralised; Director + RunService call sites
 *       use the enum instead of literal Chinese.</li>
 * </ul>
 * Next slices (documented, not in this cut): Session/Settlement extract from EmberRunService;
 * RoomObjective / BossMove adapters; more Director FX moved behind the counterplay adapter.
 */
public final class EmberEncounter {

    private EmberEncounter() { }

    /** human label for logs / STATUS */
    public static final String SLICE = "S3-7 encounter primitives (counterplay + revive adapters)";

    /** D236 deliverable id */
    public static final String D = "D236";
}
