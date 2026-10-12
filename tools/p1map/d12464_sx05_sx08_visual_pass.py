#!/usr/bin/env python3
"""D12464: light visual pass on short sx05–sx08 — themed pillars/trim only.
Loads existing MCA via d441.World; never stamps void / never rewrites floors.
Samples solid footing (floor block y) before placing — primary walk fy=63
(after shift_walk_level); sx07 decks @68/72; sx08 upper @68.
Paradigm: d12376_sx01_sx04_visual_pass.py helpers."""
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


def decorate_sx05(w):
    """Andesite/cobble pillars + iron-bar rail accents beside bridge (not on walk).
    Bridge aisle ~x29-31 z36-42 — keep clear. Pads: (24,34)(36,44)(30,32)(30,48)."""
    fy = 63
    placed = [0]
    # R0 / R1 / R3 / boss corners (safe pattern like d12376)
    safe = [
        (-6, 8), (6, 8), (-6, 12), (6, 12),
        (-8, 22), (8, 22), (-10, 34), (10, 34),
        (24, 60), (42, 60), (24, 70), (42, 70),
        (-10, 96), (10, 96), (-12, 108), (12, 108),
    ]
    # bridge-side pads — NOT on aisle x29-31 z36-42
    bridge_pads = [
        (24, 34), (36, 44), (30, 32), (30, 48),
        (24, 44), (36, 34), (22, 40), (38, 40),
    ]

    def body_fn(x, z):
        return ANDESITE if (x + z) % 2 == 0 else COBBLE

    place_pillars(w, safe, fy, 4, body_fn, GLOWSTONE, placed)
    place_pillars(w, bridge_pads, fy, 3, body_fn, GLOWSTONE, placed)

    # iron bars as rail accents BESIDE bridge, not on walk center
    bars = 0
    for x, z in [
        (28, 36), (28, 40), (28, 42),  # west of aisle
        (32, 36), (32, 40), (32, 42),  # east of aisle
        (27, 38), (33, 38),
    ]:
        if bars_column(w, x, z, fy, 2):
            bars += 1
    # gate / room side bar accents
    for x, z in [(-8, 30), (8, 30), (-8, 92), (8, 92)]:
        if bars_column(w, x, z, fy, 3):
            bars += 1

    print(f"  sx05 pillars={placed[0]} bars={bars}")
    return placed[0]


def decorate_sx06(w):
    """Stonebrick/andesite pillars + gray/red wool trim on ring path pads.
    Center plug 26-34,34-46 — do NOT seal ring walk. Pads: (22,36)(22,48)(38,48)(38,36)(30,48)."""
    fy = 63
    placed = [0]
    safe = [
        (-6, 8), (6, 8), (-6, 12), (6, 12),
        (-8, 22), (8, 22), (-10, 34), (10, 34),
        (24, 60), (42, 60), (24, 70), (42, 70),
        (-10, 96), (10, 96), (-12, 108), (12, 108),
    ]
    # ring path pads (off center plug)
    ring_pads = [
        (22, 36), (22, 48), (38, 48), (38, 36), (30, 48),
        (22, 42), (38, 42), (26, 32), (34, 32), (26, 50), (34, 50),
    ]

    def body_fn(x, z):
        return STONEBRICK if (x + z) % 2 == 0 else ANDESITE

    place_pillars(w, safe, fy, 4, body_fn, GLOWSTONE, placed)
    place_pillars(w, ring_pads, fy, 3, body_fn, GLOWSTONE, placed)

    # gray/red wool trim — ring readable, never mid-aisle centers
    trim_n = 0
    for x, z in [
        (20, 36), (20, 48), (40, 48), (40, 36),
        (22, 34), (38, 34), (22, 50), (38, 50),
        (24, 64), (40, 64), (-6, 14), (6, 14),
        (-8, 100), (8, 100),
    ]:
        blk = WOOL_GRAY if (x + z) % 2 == 0 else WOOL_RED
        if trim_at(w, x, z, fy, blk, dy=3):
            trim_n += 1
        if trim_at(w, x, z, fy, blk, dy=2):
            trim_n += 1

    print(f"  sx06 pillars={placed[0]} wool_trim={trim_n}")
    return placed[0]


def decorate_sx07(w):
    """Stonebrick pillars + glowstone caps; decks @68/72; few crown braziers.
    Ground fy=63; deck pads (30,40@68)(30,48@72)."""
    fy = 63
    placed = [0]
    ground = [
        (-6, 8), (6, 8), (-6, 12), (6, 12),
        (-8, 22), (8, 22), (-10, 34), (10, 34),
        (24, 60), (42, 60), (24, 70), (42, 70),
        (-10, 96), (10, 96), (-12, 108), (12, 108),
    ]
    place_pillars(w, ground, fy, 4, STONEBRICK, GLOWSTONE, placed)

    # mid deck @68
    deck68 = [
        (30, 40), (22, 40), (38, 40), (26, 36), (34, 44),
        (22, 48), (38, 48),
    ]
    for x, z in deck68:
        if pillar(w, x, z, 68, 3, STONEBRICK, GLOWSTONE):
            placed[0] += 1

    # upper deck / crown @72
    deck72 = [
        (30, 48), (26, 48), (34, 48), (30, 44), (30, 52),
    ]
    for x, z in deck72:
        if pillar(w, x, z, 72, 3, STONEBRICK, GLOWSTONE):
            placed[0] += 1

    # glow accents on ground window-corridor sides
    glow_n = 0
    for x, z in [(-8, 16), (8, 16), (-8, 50), (8, 50), (22, 64), (40, 72)]:
        if trim_at(w, x, z, fy, GLOWSTONE, dy=4):
            glow_n += 1

    # few braziers at crown (fy=72)
    braz = 0
    for x, z in [(28, 50), (32, 50), (30, 54), (26, 46), (34, 46)]:
        if brazier(w, x, z, 72):
            braz += 1

    print(f"  sx07 pillars={placed[0]} glow={glow_n} crown_braziers={braz}")
    return placed[0]


def decorate_sx08(w):
    """Stonebrick pillars both levels; iron bars between levels; wool trim on stair sides.
    Lower fy=63, upper hall ~68."""
    fy = 63
    placed = [0]
    lower = [
        (-6, 8), (6, 8), (-6, 12), (6, 12),
        (-8, 22), (8, 22), (-10, 34), (10, 34),
        (24, 60), (42, 60), (24, 70), (42, 70),
        (-10, 96), (10, 96), (-12, 108), (12, 108),
    ]
    place_pillars(w, lower, fy, 4, STONEBRICK, GLOWSTONE, placed)

    # upper hall pillars @68
    upper = [
        (22, 36), (38, 36), (22, 48), (38, 48),
        (26, 40), (34, 40), (30, 34), (30, 50),
        (24, 64), (40, 64),
    ]
    for x, z in upper:
        if pillar(w, x, z, 68, 3, STONEBRICK, GLOWSTONE):
            placed[0] += 1

    # iron bars between levels — vertical accents spanning lower→upper feel
    bars = 0
    for x, z in [
        (24, 38), (36, 38), (24, 46), (36, 46),
        (28, 36), (32, 36), (28, 48), (32, 48),
        # stair-side flanks (do not block stair walk)
        (26, 56), (34, 56), (26, 58), (34, 58),
    ]:
        if bars_column(w, x, z, fy, 4):
            bars += 1
    # upper-level rail accents
    for x, z in [(23, 40), (37, 40), (23, 44), (37, 44)]:
        if bars_column(w, x, z, 68, 2):
            bars += 1

    # wool trim on stair sides (gray/red) — not on stair tread centers
    trim_n = 0
    for x, z in [
        (25, 55), (35, 55), (25, 59), (35, 59),
        (22, 62), (40, 62), (-6, 14), (6, 14),
        (-8, 100), (8, 100),
    ]:
        blk = WOOL_GRAY if (x + z) % 2 == 0 else WOOL_RED
        if trim_at(w, x, z, fy, blk, dy=2):
            trim_n += 1
        if trim_at(w, x, z, fy, blk, dy=3):
            trim_n += 1
    # upper trim
    for x, z in [(24, 42), (36, 42), (24, 46), (36, 46)]:
        blk = WOOL_RED if (x + z) % 2 == 0 else WOOL_GRAY
        if trim_at(w, x, z, 68, blk, dy=2):
            trim_n += 1

    print(f"  sx08 pillars={placed[0]} bars={bars} wool_trim={trim_n}")
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


def main():
    jobs = [
        ("ember_short_sx05", decorate_sx05),
        ("ember_short_sx06", decorate_sx06),
        ("ember_short_sx07", decorate_sx07),
        ("ember_short_sx08", decorate_sx08),
    ]
    totals = {}
    all_ok = True
    for name, fn in jobs:
        path = str(MAP / name)
        print(f"Decorating {name}…")
        w = d441.World(path)
        n = fn(w)
        totals[name] = n
        if name != "ember_short_sx08":
            if not footing_check(w, name, SAFE_POINTS):
                all_ok = False
        if name == "ember_short_sx07":
            deck_pts = [
                ("deck68", 30, 68, 40),
                ("deck72", 30, 72, 48),
                ("deckL", 22, 68, 40),
            ]
            if not footing_check(w, name + "/deck", deck_pts):
                all_ok = False
        if name == "ember_short_sx08":
            # split-level: R2 walk is upper @68 (center plug void at fy63)
            sx08_walk = [
                ("spawn", 0, 63, 5),
                ("r0", 0, 63, 10),
                ("r1", 0, 63, 29),
                ("r2_up", 30, 68, 40),
                ("r3", 32, 63, 69),
                ("boss", 0, 63, 102),
                ("upL", 22, 68, 40),
                ("upR", 38, 68, 40),
                ("stair", 30, 63, 55),
            ]
            if not footing_check(w, name + "/split", sx08_walk):
                all_ok = False
        w.save()
        print(f"  dirty regions={len(w.dirty)} saved")
    print("DONE D12464 sx05–sx08 visual pass")
    print("pillars_per_map:", totals)
    print("footing_all_ok:", all_ok)


if __name__ == "__main__":
    main()
