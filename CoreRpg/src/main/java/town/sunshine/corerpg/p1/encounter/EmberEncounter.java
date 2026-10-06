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
 *   <li>{@link AffixBehavior} / {@link EmberAffixes} — D241: one primitive class per affix ({@code Affix*}, 12) in
 *       {@link AffixFamily} families + the shared {@link AffixCycle} telegraph clock and {@link EmberShape} geometry.
 *       Director keeps entities / FX / chat; fixed-seed damage replay vs the 46736db code is identical.</li>
 * </ul>
 * Next: S4 (gear structure merge doc + source_map); more Director FX behind adapters when content resumes.
 */
public final class EmberEncounter {

    private EmberEncounter() { }

    /** human label for logs / STATUS */
    public static final String SLICE = "S3-12 affix behaviour primitives";

    /** D239 deliverable id */
    public static final String D = "D241";
}
