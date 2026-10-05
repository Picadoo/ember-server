package town.sunshine.corerpg.p1.encounter;

/**
 * D236 stub / D239 adapter seam: a room clear goal as a data-driven primitive
 * (timer / hold-point / escort / chain-kill / unscathed / breach / …).
 * <p>Live room ticks still live in {@code EmberRunDirector}; the live adapter
 * {@link EmberRoomObjective} owns Bukkit-free event helpers (D179 hold/relay/beacon,
 * D191 breach/chain/unscathed) and family labels so new room events can compose
 * existing objective types from yml.
 */
public interface RoomObjective {

    /** objective id (yml event / room key) */
    String id();

    /** short family: {@code clear} / {@code hold} / {@code escort} / {@code chain} / {@code event} / … */
    String family();

    /** true once the room (or event) has met its win condition */
    boolean complete();
}
