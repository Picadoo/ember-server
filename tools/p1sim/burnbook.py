"""M03: one player's 焚烬 burns — a line-for-line mirror of CoreRpg EmberBurnBook.java (+ EmberSetRules constants).

- One burn per target (per caster: every player has an own book, so two 焚烬 players on one target both tick).
- A new burn ticks at +1 s, +2 s, +3 s, +4 s (+ D141 extra ticks) with the per-tick amount fixed at ignition (snapshot).
- Re-igniting only moves the end to now + ticks s: next tick time and snapshot stay, missed ticks are never back-filled.
- At most 5 burning targets; a 6th ends the one ignited (or re-ignited) longest ago.
- due(now): at most one tick per burn per call; finished burns are dropped. A tick on a dead / gone target removes the
  burn without damage (EmberSetService.burnTick); the caller does that check.
- transfer (D141 燎原): the dead target's burn moves to `to` with what was left (same snapshot, at most max_ms of it).
Times are integer milliseconds like the Java (callers convert sim seconds with ms()). tools/p1sim/javacheck/ runs the
real Java class and this mirror on the same event scripts (selfcheck).
"""

INTERVAL_MS = 1000   # EmberSetRules.BURN_INTERVAL_MS
TICKS = 4            # EmberSetRules.BURN_TICKS
MAX_TARGETS = 5      # EmberSetRules.BURN_MAX_TARGETS


def ms(t):
    return int(round(t * 1000.0))


class Burn:
    __slots__ = ('target', 'per_tick', 'next_tick_at', 'end_at', 'last_applied', 'ticks_done')

    def __init__(self, target, per_tick, now):
        self.target, self.per_tick = target, per_tick
        self.next_tick_at = now + INTERVAL_MS
        self.end_at = now
        self.last_applied = now
        self.ticks_done = 0


class BurnBook:
    def __init__(self, ticks=TICKS):
        self.burns = {}  # insertion-ordered like the Java LinkedHashMap
        self.ticks = max(1, int(ticks))

    def set_ticks(self, t):
        self.ticks = max(1, int(t))

    def transfer(self, frm, to, now, max_ms):
        b = self.burns.pop(frm, None) if frm is not None else None
        if b is None or to is None or to in self.burns or b.end_at <= now:
            return False
        n = Burn(to, b.per_tick, now)
        n.end_at = min(b.end_at, now + max(0, max_ms))
        n.next_tick_at = min(b.next_tick_at, b.end_at)
        self.burns[to] = n
        return True

    def ignite(self, target, per_tick, now):
        """@return the target evicted to make room, or None"""
        if target is None or not (per_tick > 0) or per_tick == float('inf'):
            return None
        b = self.burns.get(target)
        if b is not None:
            b.end_at = now + self.ticks * INTERVAL_MS  # only the end moves
            b.last_applied = now
            return None
        evicted = None
        if len(self.burns) >= MAX_TARGETS:
            oldest = None
            for x in self.burns.values():
                if oldest is None or x.last_applied < oldest.last_applied:
                    oldest = x
            if oldest is not None:
                del self.burns[oldest.target]
                evicted = oldest.target
        nb = Burn(target, per_tick, now)
        nb.end_at = now + self.ticks * INTERVAL_MS
        self.burns[target] = nb
        return evicted

    def due(self, now):
        out = []
        for key in list(self.burns):
            b = self.burns[key]
            if b.next_tick_at <= now and b.next_tick_at <= b.end_at:
                out.append((b.target, b.per_tick))
                b.ticks_done += 1
                b.next_tick_at += INTERVAL_MS
            if b.next_tick_at > b.end_at:
                del self.burns[key]
        return out

    def next_due(self):
        """earliest pending tick time (ms) or None — the sim's next burn event (the server polls every 50 ms)"""
        ts = [b.next_tick_at for b in self.burns.values() if b.next_tick_at <= b.end_at]
        return min(ts) if ts else None

    def remove(self, target):
        self.burns.pop(target, None)

    def clear(self):
        self.burns.clear()

    def get(self, target):
        return self.burns.get(target)

    def __len__(self):
        return len(self.burns)
