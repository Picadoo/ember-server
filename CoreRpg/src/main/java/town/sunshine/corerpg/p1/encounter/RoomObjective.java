package town.sunshine.corerpg.p1.encounter;

/**
 * D236 stub: a room clear goal as a data-driven primitive
 * (timer / hold-point / escort / chain-kill / unscathed / breach / …).
 * <p>Live room ticks still live in {@code EmberRunDirector}; variety events (D138 / D171 / D179 /
 * D191) are hand-written branches today. This interface is the seam for a later adapter so new
 * room events can compose existing objective types from yml.
 * <p>Not wired in D236 — Session/Settlement and RoomObjective adapters are the next cuts.
 */
public interface RoomObjective {

    /** objective id (yml event / room key) */
    String id();

    /** short family: {@code clear} / {@code hold} / {@code escort} / {@code chain} / {@code event} / … */
    String family();

    /** true once the room (or event) has met its win condition */
    boolean complete();
}
