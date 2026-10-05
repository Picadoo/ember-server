package town.sunshine.corerpg.p1.encounter;

/**
 * D236–D239 / ARCH S3 encounter primitives — package entry.
 * <p>Goal (ARCH §5 S3): turn repeat-run variety / boss moves / counterplay from
 * "one Java branch per content pack" into "yml compositions of existing primitives".
 * <p>Four families:
 * <ul>
 *   <li>{@link RoomObjective} / {@link EmberRoomObjective} — room clear goals (timer / hold /
 *       escort / chain / …). <b>Live adapter in D239</b>: D179/D191 Bukkit-free helpers + family
 *       labels; Director keeps FX / ticks and thin delegates.</li>
 *   <li>{@link BossMove} / {@link EmberBossMove} — one telegraphed boss (or elite-twist) skill
 *       as a data view over {@code EmberRunMaps.Skill}. <b>Live adapter in D239</b>: schedule
 *       helpers ({@code dueSkill} / {@code gatedNext} / …); cast loop / FX stay in Director.</li>
 *   <li>{@link EmberCounterplay} — counterplay windows: wall stun (D188), whiff stun (D192),
 *       break / interrupt (D193). Live since D236.</li>
 *   <li>{@link RevivePoint} — D106 raid revive triggers. Live since D236.</li>
 * </ul>
 * Next slices: Papi 分节; affix behaviour primitives; more Director FX behind adapters.
 */
public final class EmberEncounter {

    private EmberEncounter() { }

    /** human label for logs / STATUS */
    public static final String SLICE = "S3-10 BossMove + RoomObjective adapters";

    /** D239 deliverable id */
    public static final String D = "D239";
}
