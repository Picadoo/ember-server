package town.sunshine.corerpg.p1.encounter;

/**
 * D236: which counterplay window a boss move can open (yml keys on {@code maps.*.boss.skills}).
 * Caps and type gates live in {@link EmberCounterplay} / {@code EmberRunMaps.Skill} parse
 * (unchanged numbers: wall ≤ 3 s, whiff ≤ 2 s, break_hp ≤ 0.5, break_stun ≤ 2 s).
 */
public enum CounterplayKind {
    /** D188 撞墙破绽 — charge clipped by a real wall → root / no skill / no melee */
    WALL("wall_stun"),
    /** D192 落空破绽 — someone stood inside at warn start and nobody was hit → stagger */
    WHIFF("whiff_stun"),
    /** D193 破招 — party deals break_hp × max HP during the channel → interrupt + stagger */
    BREAK("break_hp");

    /** yml key under a skill map */
    public final String ymlKey;

    CounterplayKind(String ymlKey) { this.ymlKey = ymlKey; }
}
