#!/usr/bin/env python3
"""D12480: light visual pass on short sx09–sx12 — themed pillars/trim only.
Loads existing MCA via d441.World; never stamps void / never rewrites floors.
Samples solid footing (floor block y) before placing — primary walk fy=63
(after shift_walk_level).
Paradigm: d12464_sx05_sx08_visual_pass.py / d12376_sx01_sx04_visual_pass.py helpers."""
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


# R0 / R1 / R3 / boss corners (safe pattern like d12376/d12464)
SAFE_CORNERS = [
    (-6, 8), (6, 8), (-6, 12), (6, 12),
    (-8, 22), (8, 22), (-10, 34), (10, 34),
    (24, 60), (42, 60), (24, 70), (42, 70),
    (-10, 96), (10, 96), (-12, 108), (12, 108),
]


def decorate_sx09(w):
    """Stonebrick/andesite/cobble pillars + iron-bar bank rails.
    Stepping stones (22,40)(30,40)(38,40) — NEVER pillar on stone centers.
    Bank pads (30,32)(30,48) + room/boss corners."""
    fy = 63
    placed = [0]

    def body_fn(x, z):
        # rotate stonebrick / andesite / cobble
        k = (x + z) % 3
        if k == 0:
            return STONEBRICK
        if k == 1:
            return ANDESITE
        return COBBLE

    place_pillars(w, SAFE_CORNERS, fy, 4, body_fn, GLOWSTONE, placed)

    # bank pads — NOT on stepping-stone jump centers
    bank_pads = [
        (30, 32), (30, 48),
        (28, 32), (32, 32), (28, 48), (32, 48),
        (26, 32), (34, 32), (26, 48), (34, 48),
        # side bank flanks (off canal aisle)
        (22, 36), (38, 36), (22, 44), (38, 44),
    ]
    place_pillars(w, bank_pads, fy, 3, body_fn, GLOWSTONE, placed)

    # iron bars on bank edges — not on step centers (22/30/38,40)
    bars = 0
    for x, z in [
        (28, 34), (32, 34), (28, 46), (32, 46),
        (26, 36), (34, 36), (26, 44), (34, 44),
        (24, 32), (36, 32), (24, 48), (36, 48),
        # room/gate side accents
        (-8, 30), (8, 30), (-8, 92), (8, 92),
    ]:
        if bars_column(w, x, z, fy, 2):
            bars += 1

    print(f"  sx09 pillars={placed[0]} bars={bars}")
    return placed[0]


def decorate_sx10(w):
    """Stonebrick/andesite pillars + iron bars at gate sides + gray/red wool trim.
    Pads at z=30,35,41,47,52 x=30 — decorate sides, never seal gate walk center."""
    fy = 63
    placed = [0]

    def body_fn(x, z):
        return STONEBRICK if (x + z) % 2 == 0 else ANDESITE

    place_pillars(w, SAFE_CORNERS, fy, 4, body_fn, GLOWSTONE, placed)

    # gate-chamber side pads — OFF center aisle x=30
    gate_pads = [
        (26, 30), (34, 30), (26, 35), (34, 35),
        (26, 41), (34, 41), (26, 47), (34, 47),
        (26, 52), (34, 52),
        (24, 33), (36, 33), (24, 45), (36, 45),
        (24, 50), (36, 50),
    ]
    place_pillars(w, gate_pads, fy, 3, body_fn, GLOWSTONE, placed)

    # iron bars at gate sides (flank walk, do not seal)
    bars = 0
    for x, z in [
        (28, 30), (32, 30), (28, 35), (32, 35),
        (28, 41), (32, 41), (28, 47), (32, 47),
        (28, 52), (32, 52),
        (27, 38), (33, 38), (27, 44), (33, 44),
    ]:
        if bars_column(w, x, z, fy, 3):
            bars += 1

    # gray/red wool trim — gate-readable, not mid-aisle
    trim_n = 0
    for x, z in [
        (24, 30), (36, 30), (24, 35), (36, 35),
        (24, 41), (36, 41), (24, 47), (36, 47),
        (24, 52), (36, 52),
        (-6, 14), (6, 14), (-8, 100), (8, 100),
        (24, 64), (40, 64),
    ]:
        blk = WOOL_GRAY if (x + z) % 2 == 0 else WOOL_RED
        if trim_at(w, x, z, fy, blk, dy=3):
            trim_n += 1
        if trim_at(w, x, z, fy, blk, dy=2):
            trim_n += 1

    print(f"  sx10 pillars={placed[0]} bars={bars} wool_trim={trim_n}")
    return placed[0]


def decorate_sx11(w):
    """Stonebrick symmetric twin-corridor pillars + glowstone.
    Pads (20,36)(20,46)(36,36)(36,46) — do NOT seal merge at (30,50)."""
    fy = 63
    placed = [0]

    place_pillars(w, SAFE_CORNERS, fy, 4, STONEBRICK, GLOWSTONE, placed)

    # symmetric corridor pads (mirror L/R)
    twin_pads = [
        (20, 36), (20, 46), (36, 36), (36, 46),
        (18, 36), (18, 46), (38, 36), (38, 46),
        (20, 40), (36, 40), (22, 36), (22, 46), (34, 36), (34, 46),
        # merge flanks — beside (30,50), not ON it
        (26, 50), (34, 50), (28, 52), (32, 52),
        (22, 50), (38, 50),
    ]
    place_pillars(w, twin_pads, fy, 3, STONEBRICK, GLOWSTONE, placed)

    # glow accents on corridor / merge door sides (not sealing merge walk)
    glow_n = 0
    for x, z in [
        (20, 38), (36, 38), (20, 44), (36, 44),
        (24, 50), (36, 50), (30, 48), (30, 54),
        (-8, 16), (8, 16), (22, 64), (40, 64),
    ]:
        if trim_at(w, x, z, fy, GLOWSTONE, dy=4):
            glow_n += 1
        if trim_at(w, x, z, fy, GLOWSTONE, dy=3):
            glow_n += 1

    print(f"  sx11 pillars={placed[0]} glow={glow_n}")
    return placed[0]


def decorate_sx12(w):
    """Stonebrick/andesite hub-ring pillars + wing iron bars + few hub braziers.
    Hub (30,40) r=3 — ring around, NOT center glow on hub pad.
    Wings (18,32)(40,32)(30,48)."""
    fy = 63
    placed = [0]

    def body_fn(x, z):
        return STONEBRICK if (x + z) % 2 == 0 else ANDESITE

    place_pillars(w, SAFE_CORNERS, fy, 4, body_fn, GLOWSTONE, placed)

    # hub ring around (30,40) r≈3 — not on center (30,40)
    hub_ring = [
        (27, 40), (33, 40), (30, 37), (30, 43),
        (27, 37), (33, 37), (27, 43), (33, 43),
        (25, 40), (35, 40), (30, 35), (30, 45),
    ]
    place_pillars(w, hub_ring, fy, 3, body_fn, GLOWSTONE, placed)

    # wing pads (off wing mouths centerline)
    wing_pads = [
        (18, 30), (18, 34), (16, 32), (20, 32),
        (40, 30), (40, 34), (38, 32), (42, 32),
        (28, 48), (32, 48), (30, 46), (30, 50),
        (22, 36), (38, 36),
    ]
    place_pillars(w, wing_pads, fy, 3, body_fn, GLOWSTONE, placed)

    # iron bars at wing mouths
    bars = 0
    for x, z in [
        (20, 30), (20, 34), (16, 30), (16, 34),
        (38, 30), (38, 34), (42, 30), (42, 34),
        (28, 46), (32, 46), (28, 50), (32, 50),
        (24, 40), (36, 40),
    ]:
        if bars_column(w, x, z, fy, 3):
            bars += 1

    # few braziers near hub (not on center pad)
    braz = 0
    for x, z in [
        (28, 38), (32, 38), (28, 42), (32, 42),
        (26, 40), (34, 40),
    ]:
        if brazier(w, x, z, fy):
            braz += 1

    print(f"  sx12 pillars={placed[0]} bars={bars} braziers={braz}")
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

SX09_EXTRA = [
    ("step22", 22, 63, 40),
    ("step30", 30, 63, 40),
    ("step38", 38, 63, 40),
    ("bank32", 30, 63, 32),
    ("bank48", 30, 63, 48),
]

SX10_EXTRA = [
    ("g30", 30, 63, 30),
    ("g35", 30, 63, 35),
    ("g41", 30, 63, 41),
    ("g47", 30, 63, 47),
    ("g52", 30, 63, 52),
]

SX11_EXTRA = [
    ("L36", 20, 63, 36),
    ("L46", 20, 63, 46),
    ("R36", 36, 63, 36),
    ("R46", 36, 63, 46),
    ("merge", 30, 63, 50),
]

SX12_EXTRA = [
    ("hub", 30, 63, 40),
    ("w18", 18, 63, 32),
    ("w40", 40, 63, 32),
    ("w48", 30, 63, 48),
]


def main():
    jobs = [
        ("ember_short_sx09", decorate_sx09, SX09_EXTRA),
        ("ember_short_sx10", decorate_sx10, SX10_EXTRA),
        ("ember_short_sx11", decorate_sx11, SX11_EXTRA),
        ("ember_short_sx12", decorate_sx12, SX12_EXTRA),
    ]
    totals = {}
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
    print("DONE D12480 sx09–sx12 visual pass")
    print("pillars_per_map:", totals)
    print("footing_all_ok:", all_ok)


if __name__ == "__main__":
    main()
