package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

/**
 * D241 / ARCH S3-12: one affixed-elite behaviour (D138 / D171 / D181 / D189 / D196) as a primitive. Each affix is one
 * class ({@code Affix*}) holding its Bukkit-free numbers: cadence, intro line, damage packet, hit geometry and the
 * pure state decisions of its tick. {@code EmberRunDirector} keeps entities, particles, sounds and chat, and calls
 * these (one-line delegates where tests already pinned a Director helper). Numbers come only from
 * {@link EmberRunMaps.Variety} (bv58 unchanged). p1sim models the same cadence / damage per cast (see
 * {@code docs/design/DESIGN-ember-affix-primitives-d241.md}).
 */
public interface AffixBehavior {

    /** yml id (one of {@link EmberRunMaps.Variety#KNOWN}) */
    String id();

    /** behaviour family */
    AffixFamily family();

    /** Bukkit {@code Particle} enum name of the idle glow around the elite */
    String fx();

    /**
     * Seconds from promotion (+1.5 s, see {@link AffixCycle#firstNext}) to the first cast attempt. Affixes without a
     * live tick (split / shield / molten) keep the historical blazing cadence (unused).
     */
    double firstEvery(EmberRunMaps.Variety v);

    /** player-facing "how to play it" half of the promotion chat line */
    String how(EmberRunMaps.Variety v);

    /** Chinese tag (elite name + chat) */
    default String label() { return EmberRunMaps.Variety.label(id()); }
}
