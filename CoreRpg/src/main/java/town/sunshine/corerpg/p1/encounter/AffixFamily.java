package town.sunshine.corerpg.p1.encounter;

/**
 * D241 / ARCH S3-12: behaviour family of an affix primitive — the "small set of generic effect types" (ARCH §5 S3:
 * 光束 / 链 / 圈 …). A new affix should be a new row of an existing family + yml numbers wherever possible.
 */
public enum AffixFamily {
    /** telegraphed ground circle, then one hit per player inside (blazing / mortar / jailer) */
    CIRCLE,
    /** telegraphed straight strip from the elite towards a player (charge) */
    STRIP,
    /** two crossing lines at the elite's feet, "+" / "x" alternating (venom) */
    CROSS,
    /** a beam that turns over a half circle after its warning; once per player per cast (arcane) */
    BEAM,
    /** a burning link to another room mob; per-player burn cooldown (firechain) */
    TETHER,
    /** always-on slow aura around the elite (frost) */
    AURA,
    /** periodic self-heal channel that heavy damage interrupts (regen) */
    CHANNEL,
    /** delayed circle where the elite died (molten) */
    DEATH_BLAST,
    /** spawns weaker copies when the elite dies (split) */
    DEATH_SPAWN,
    /** pure stat change at promotion (shield) */
    STAT
}
