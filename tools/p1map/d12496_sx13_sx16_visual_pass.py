#!/usr/bin/env python3
"""D12496: light visual pass on short sx13–sx16 — themed pillars/trim only.
Loads existing MCA via d441.World; never stamps void / never rewrites floors.
Samples solid footing (floor block y) before placing — primary walk fy=63
(after shift_walk_level); sx15 elevated spiral decks stay higher ~67–72.
Paradigm: d12480_sx09_sx12_visual_pass.py / d12464 / d12376 helpers."""
from __future__ import annotations
import importlib.util
from pathlib import Path

HERE = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("d441", HERE / "d441_build_sx14_sx15_terrain.py")
d441 = importlib.util.module_from_spec(spec)
spec.loader.exec_module(d441)

MAP = Path("/workspace/minecraft/plugins/DungeonPlus/map")

# 1.12.2 block ids
STONEBRICK = 98
ANDESITE = (1, 5)
COBBLE = 4
GLOWSTONE = 89
IRON_BARS = 101
WOOL_GRAY = (35, 7)
WOOL_RED = (35, 14)
NETHERRACK = 87
FIRE = 51

AIRISH = {
    0, 8, 9, 10, 11, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69,
    70, 72, 75, 76, 77, 83, 90, 93, 94, 104, 105, 106, 111, 115, 132, 141, 142, 175,
}


def get_block(w, x, y, z):
    cx, cz = x >> 4, z >> 4
    raw = w._ensure_chunk(cx, cz)
    sy = y >> 4
    for s in raw["Level"]["Sections"]:
        if s["Y"].value == sy:
            lx, ly, lz = x & 15, y & 15, z & 15
            i = ly * 256 + lz * 16 + lx
            bid = s["Blocks"].value[i]
            data = s["Data"].value[i // 2]
            meta = (data & 0xF) if i % 2 == 0 else ((data >> 4) & 0xF)
            return bid, meta
    return 0, 0


def is_solid(bid: int) -> bool:
    return bid not in AIRISH


def body_id(body) -> int:
    return body if isinstance(body, int) else body[0]


def pillar(w, x, z, floor_y, h, body, cap=None):
    """floor_y = solid floor block; body fills floor_y+1 .. floor_y+h; cap at floor_y+h+1."""
    foot, _ = get_block(w, x, floor_y, z)
    if not is_solid(foot):
        print(f"  skip pillar ({x},{floor_y},{z}) footing={foot}")
        return False
    above, _ = get_block(w, x, floor_y + 1, z)
    if is_solid(above) and above != body_id(body):
        print(f"  skip pillar ({x},{floor_y},{z}) occupied above={above}")
        return False
    for dy in range(1, h + 1):
        w.set(x, floor_y + dy, z, body)
    if cap is not None:
        w.set(x, floor_y + h + 1, z, cap)
    return True


def trim_at(w, x, z, floor_y, block, dy=3):
    foot, _ = get_block(w, x, floor_y, z)
    if not is_solid(foot):
        return False
    above, _ = get_block(w, x, floor_y + dy, z)
    if is_solid(above):
        return False
    w.set(x, floor_y + dy, z, block)
    return True


def brazier(w, x, z, floor_y):
    """Netherrack niche at fy+1 + fire at fy+2; skip if occupied or no solid foot."""
    foot, _ = get_block(w, x, floor_y, z)
    if not is_solid(foot):
        return False
    a1, _ = get_block(w, x, floor_y + 1, z)
    a2, _ = get_block(w, x, floor_y + 2, z)
    if is_solid(a1) or is_solid(a2):
        return False
    w.set(x, floor_y + 1, z, NETHERRACK)
    w.set(x, floor_y + 2, z, FIRE)
    return True


def bars_column(w, x, z, floor_y, h=3):
    """Iron-bar vertical accents; skip if footing soft or solid occupy."""
    foot, _ = get_block(w, x, floor_y, z)
    if not is_solid(foot):
        return False
    above, _ = get_block(w, x, floor_y + 1, z)
    if is_solid(above) and above != IRON_BARS:
        return False
    for dy in range(1, h + 1):
        w.set(x, floor_y + dy, z, IRON_BARS)
    return True


def place_pillars(w, coords, fy, h, body_fn, cap, counter):
    for x, z in coords:
        body = body_fn(x, z) if callable(body_fn) else body_fn
        if pillar(w, x, z, fy, h, body, cap):
            counter[0] += 1


# R0 / R1 / R3 / boss corners (safe pattern like d12376/d12464/d12480)
SAFE_CORNERS = [
    (-6, 8), (6, 8), (-6, 12), (6, 12),
    (-8, 22), (8, 22), (-10, 34), (10, 34),
    (24, 60), (42, 60), (24, 70), (42, 70),
    (-10, 96), (10, 96), (-12, 108), (12, 108),
]


def decorate_sx13(w):
    """Stonebrick/andesite/cobble pillars + beam-side iron bars.
    Beams z=34/40/46 plank strips x=22–36 (2-wide incl z+1) — NEVER pillar on beam walk.
    Pads (30,32)(30,50); counterweight ends already andesite — skip via occupied."""
    fy = 63
    placed = [0]

    def body_fn(x, z):
        k = (x + z) % 3
        if k == 0:
            return STONEBRICK
        if k == 1:
            return ANDESITE
        return COBBLE

    place_pillars(w, SAFE_CORNERS, fy, 4, body_fn, GLOWSTONE, placed)

    # bank pads — NOT on beam walk face (z=34/35/40/41/46/47 x=22–36)
    bank_pads = [
        (30, 32), (30, 50),
        (28, 32), (32, 32), (26, 32), (34, 32),
        (28, 48), (32, 48), (26, 50), (34, 50),
        # between-beam bank flanks (solid bank, off plank)
        (26, 37), (34, 37), (26, 43), (34, 43),
        (28, 37), (32, 37), (28, 43), (32, 43),
        (24, 32), (36, 32), (24, 50), (36, 48),
    ]
    place_pillars(w, bank_pads, fy, 3, body_fn, GLOWSTONE, placed)

    # iron bars along beam sides on bank edges — not on plank walk
    bars = 0
    for x, z in [
        # south of beam34 / between beams / north of beam46 on bank
        (28, 33), (30, 33), (32, 33), (34, 33), (36, 33),
        (28, 39), (30, 39), (32, 39), (34, 39), (36, 39),
        (28, 45), (30, 45), (32, 45), (34, 45), (36, 45),
        # room accents
        (-8, 30), (8, 30), (-8, 92), (8, 92),
    ]:
        if bars_column(w, x, z, fy, 2):
            bars += 1

    print(f"  sx13 pillars={placed[0]} bars={bars}")
    return placed[0]


def decorate_sx14(w):
    """Stonebrick/andesite pillars OFF zigzag corner centers + side bars + gray/red wool.
    Corners (22,31)(36,31)(36,44)(22,44) — flanks only; never seal corner walk."""
    fy = 63
    placed = [0]

    def body_fn(x, z):
        return STONEBRICK if (x + z) % 2 == 0 else ANDESITE

    place_pillars(w, SAFE_CORNERS, fy, 4, body_fn, GLOWSTONE, placed)

    # corner flanks — OFF corner centers
    corner_flanks = [
        (21, 31), (23, 31), (35, 31), (37, 31),
        (21, 44), (23, 44), (35, 44), (37, 44),
        (22, 30), (22, 32), (36, 30), (36, 32),
        (22, 43), (22, 45), (36, 43), (36, 45),
        # outer corner rings
        (20, 31), (24, 31), (34, 31), (38, 31),
        (20, 44), (24, 44), (34, 44), (38, 44),
        (21, 30), (23, 30), (35, 30), (37, 30),
        (21, 45), (23, 45), (35, 45), (37, 45),
    ]
    place_pillars(w, corner_flanks, fy, 3, body_fn, GLOWSTONE, placed)

    # iron bars at corner sides (do not seal walk through corner)
    bars = 0
    for x, z in [
        (20, 30), (24, 30), (34, 30), (38, 30),
        (20, 32), (24, 32), (34, 32), (38, 32),
        (20, 43), (24, 43), (34, 43), (38, 43),
        (20, 45), (24, 45), (34, 45), (38, 45),
        (19, 31), (25, 31), (33, 31), (39, 31),
        (19, 44), (25, 44), (33, 44), (39, 44),
    ]:
        if bars_column(w, x, z, fy, 3):
            bars += 1

    # gray/red wool trim — crack-readable, not mid-corner walk
    trim_n = 0
    for x, z in [
        (20, 29), (24, 29), (34, 29), (38, 29),
        (20, 46), (24, 46), (34, 46), (38, 46),
        (18, 31), (26, 31), (32, 31), (40, 31),
        (18, 44), (26, 44), (32, 44), (40, 44),
        (-6, 14), (6, 14), (-8, 100), (8, 100),
        (24, 64), (40, 64),
    ]:
        blk = WOOL_GRAY if (x + z) % 2 == 0 else WOOL_RED
        if trim_at(w, x, z, fy, blk, dy=3):
            trim_n += 1
        if trim_at(w, x, z, fy, blk, dy=2):
            trim_n += 1

    print(f"  sx14 pillars={placed[0]} bars={bars} wool_trim={trim_n}")
    return placed[0]


def decorate_sx15(w):
    """Stonebrick pillars along outer rim / slope segments + rim bars + atrium-side glow.
    Spiral atrium center (30,40) — never pillar on centerline walk.
    Elevated spiral decks sampled at real floor y (~67–72)."""
    fy = 63
    placed = [0]

    place_pillars(w, SAFE_CORNERS, fy, 4, STONEBRICK, GLOWSTONE, placed)

    # outer rim / slope entry at ground fy — off atrium centerline
    rim63 = [
        (20, 32), (22, 32), (26, 30), (38, 32),
        (20, 36), (20, 44), (40, 36), (40, 44),
        (22, 48), (38, 48), (38, 36),
        (18, 32), (22, 32),
    ]
    place_pillars(w, rim63, fy, 3, STONEBRICK, GLOWSTONE, placed)

    # elevated slope / exit deck pillars (sample real floor)
    elev = [
        # mid slope ~67–69
        (34, 36, 67), (22, 37, 69), (23, 35, 69), (22, 39, 69), (23, 44, 69),
        # upper rim ~71
        (34, 44, 71), (35, 40, 71), (36, 43, 71), (38, 40, 71), (34, 46, 71),
        # exit deck ~72
        (30, 47, 72), (32, 50, 72), (31, 48, 72), (33, 52, 72),
    ]
    for x, z, ey in elev:
        if pillar(w, x, z, ey, 3, STONEBRICK, GLOWSTONE):
            placed[0] += 1

    # iron bars on rim edges (ground + a few elevated)
    bars = 0
    for x, z in [
        (19, 32), (21, 32), (19, 36), (21, 44),
        (39, 36), (39, 44), (37, 32), (37, 48),
        (24, 31), (36, 31),
    ]:
        if bars_column(w, x, z, fy, 2):
            bars += 1
    for x, z, ey in [(33, 44, 71), (36, 40, 71), (35, 43, 71), (31, 47, 72), (33, 50, 72)]:
        if bars_column(w, x, z, ey, 2):
            bars += 1

    # glowstone on atrium sides — not sealing center (30,40)
    glow_n = 0
    for x, z in [
        (27, 40), (33, 40), (30, 37), (30, 43),
        (26, 40), (34, 38), (28, 38), (32, 42),
        (-8, 16), (8, 16), (22, 64), (40, 64),
    ]:
        if trim_at(w, x, z, fy, GLOWSTONE, dy=4):
            glow_n += 1
        if trim_at(w, x, z, fy, GLOWSTONE, dy=3):
            glow_n += 1

    print(f"  sx15 pillars={placed[0]} bars={bars} glow={glow_n}")
    return placed[0]


def decorate_sx16(w):
    """Stonebrick/andesite pillars beside side pads/walls + shadow-wall side iron bars.
    Shadow walls z=34/40/46; pads (36,34)(20,40)(36,46); opens RIGHT/LEFT/RIGHT.
    Never fill shadow walls solid / never seal side-pass openings."""
    fy = 63
    placed = [0]

    def body_fn(x, z):
        return STONEBRICK if (x + z) % 2 == 0 else ANDESITE

    place_pillars(w, SAFE_CORNERS, fy, 4, body_fn, GLOWSTONE, placed)

    # beside pads / clear flanks (not ON pad centers that already hold glow niche)
    pad_flanks = [
        (34, 34), (38, 34), (35, 34), (37, 34),
        (18, 40), (22, 40), (19, 40), (21, 40),
        (34, 46), (38, 46), (35, 46), (37, 46),
        (36, 33), (36, 35), (20, 39), (20, 41),
        (36, 45), (36, 47),
        # wall-side open banks (clear stonebrick, not sealing pass)
        (34, 33), (38, 33), (34, 35), (38, 35),
        (18, 39), (22, 39), (18, 41), (22, 41),
        (34, 45), (38, 45), (34, 47), (38, 47),
    ]
    place_pillars(w, pad_flanks, fy, 3, body_fn, GLOWSTONE, placed)

    # iron bars along shadow-wall sides — clear bank only; do not seal RIGHT/LEFT/RIGHT opens
    # opens sit at pad mouths (36,34)/(20,40)/(36,46) — keep those columns free of bar walls
    bars = 0
    for x, z in [
        # z=33/35 beside wall34 (east bank clear x=34–38; skip sealing pad mouth mid)
        (34, 33), (35, 33), (37, 33), (38, 33),
        (34, 35), (35, 35), (37, 35), (38, 35),
        # z=39/41 beside wall40 — west open bank x=18–22
        (18, 39), (19, 39), (21, 39), (22, 39),
        (18, 41), (19, 41), (21, 41), (22, 41),
        # z=45/47 beside wall46 — east bank
        (34, 45), (35, 45), (37, 45), (38, 45),
        (34, 47), (35, 47), (37, 47), (38, 47),
        # room accents
        (-8, 30), (8, 30), (-8, 92), (8, 92),
    ]:
        if bars_column(w, x, z, fy, 3):
            bars += 1

    print(f"  sx16 pillars={placed[0]} bars={bars}")
    return placed[0]


def footing_check(w, name, points):
    print(f"  footing recheck {name}:")
    ok = True
    for label, x, y, z in points:
        bid, meta = get_block(w, x, y, z)
        solid = is_solid(bid)
        print(f"    {label:8s} ({x},{y},{z}) bid={bid}:{meta} solid={solid}")
        if not solid:
            ok = False
    return ok


SAFE_POINTS = [
    ("spawn", 0, 63, 5),
    ("r0", 0, 63, 10),
    ("r1", 0, 63, 29),
    ("r2", 30, 63, 40),
    ("r3", 32, 63, 69),
    ("boss", 0, 63, 102),
]

SX13_EXTRA = [
    ("pad32", 30, 63, 32),
    ("pad50", 30, 63, 50),
    ("beam34", 30, 63, 34),
    ("beam40", 30, 63, 40),
    ("beam46", 30, 63, 46),
]

SX14_EXTRA = [
    ("c2231", 22, 63, 31),
    ("c3631", 36, 63, 31),
    ("c3644", 36, 63, 44),
    ("c2244", 22, 63, 44),
]

SX15_EXTRA = [
    ("atrium", 30, 63, 40),
    ("entry", 25, 63, 40),
    ("rim20", 20, 63, 32),
    ("elev71", 34, 71, 44),
    ("exit72", 32, 72, 50),
]

SX16_EXTRA = [
    ("padR34", 36, 63, 34),
    ("padL40", 20, 63, 40),
    ("padR46", 36, 63, 46),
    ("wall34", 30, 63, 34),
    ("wall40", 30, 63, 40),
]


def main():
    jobs = [
        ("ember_short_sx13", decorate_sx13, SX13_EXTRA),
        ("ember_short_sx14", decorate_sx14, SX14_EXTRA),
        ("ember_short_sx15", decorate_sx15, SX15_EXTRA),
        ("ember_short_sx16", decorate_sx16, SX16_EXTRA),
    ]
    totals = {}
    extras = {}
    all_ok = True
    for name, fn, extra in jobs:
        path = str(MAP / name)
        print(f"Decorating {name}…")
        w = d441.World(path)
        n = fn(w)
        totals[name] = n
        if not footing_check(w, name, SAFE_POINTS):
            all_ok = False
        if not footing_check(w, name + "/theme", extra):
            all_ok = False
        w.save()
        print(f"  dirty regions={len(w.dirty)} saved")
    print("DONE D12496 sx13–sx16 visual pass")
    print("pillars_per_map:", totals)
    print("footing_all_ok:", all_ok)


if __name__ == "__main__":
    main()
