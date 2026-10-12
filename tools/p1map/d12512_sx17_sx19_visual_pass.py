#!/usr/bin/env python3
"""D12512: light visual pass on short sx17–sx19 — themed pillars/trim only.
Loads existing MCA via d441.World; never stamps void / never rewrites floors.
Samples solid footing (floor block y) before placing — primary walk fy=63
(after shift_walk_level).
Paradigm: d12496_sx13_sx16_visual_pass.py / d12480 helpers.
Closes sx01–19 visual overlay line (TIP-X)."""
from __future__ import annotations
import importlib.util
from pathlib import Path

HERE = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("d441", HERE / "d441_build_sx14_sx15_terrain.py")
d441 = importlib.util.module_from_spec(spec)
spec.loader.exec_module(d441)

MAP = Path("/workspace/minecraft/plugins/DungeonPlus/map")

# 1.12.2 block ids
PRISMARINE = 168
SEA_LANTERN = 169
QUARTZ = 155
ANDESITE = (1, 5)
STONEBRICK = 98
IRON_BARS = 101
WOOL_CYAN = (35, 9)
WOOL_PURPLE = (35, 10)
STAINED_GLASS_PURPLE = (95, 10)
STAINED_GLASS_CYAN = (95, 9)
GLOWSTONE = 89

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


# R0 / R1 / R3 / boss corners (safe pattern like d12496/d12480)
SAFE_CORNERS = [
    (-6, 8), (6, 8), (-6, 12), (6, 12),
    (-8, 22), (8, 22), (-10, 34), (10, 34),
    (24, 60), (42, 60), (24, 70), (42, 70),
    (-10, 96), (10, 96), (-12, 108), (12, 108),
]


def decorate_sx17(w):
    """Prismarine pillars + sea-lantern caps + cyan wool trim; gate-side iron bars.
    Tide gates z=36 (LEFT ebb open x16–23) / z=44 (RIGHT ebb open x33–40);
    pads (20,36)(36,44). NEVER fill water / NEVER seal ebb channels."""
    fy = 63
    placed = [0]

    place_pillars(w, SAFE_CORNERS, fy, 4, PRISMARINE, SEA_LANTERN, placed)

    # gate flanks — OFF pad centers (already hold sea-lantern) and OFF ebb mouths
    # Gate1 z=36: LEFT open x16–23 — decorate RIGHT bank / wall flanks only
    # Gate2 z=44: RIGHT open x33–40 — decorate LEFT bank / wall flanks only
    gate_flanks = [
        # south/north of gate1 packed-ice wall (x24–38), clear bank — not water z=34/38
        (24, 33), (26, 33), (28, 33), (32, 33), (36, 33), (38, 33),
        (24, 39), (26, 39), (28, 39), (32, 39), (36, 39), (38, 39),
        # south/north of gate2 wall (x16–32), clear bank — not water z=42/46
        (16, 41), (18, 41), (20, 41), (24, 41), (28, 41), (30, 41),
        (16, 47), (18, 47), (20, 47), (24, 47), (28, 47), (30, 47),
        # pad side flanks (not ON pads)
        (18, 36), (22, 36), (20, 35), (20, 37),
        (34, 44), (38, 44), (36, 43), (36, 45),
        # room accents along tide hall sides
        (14, 32), (42, 32), (14, 50), (42, 50),
    ]
    place_pillars(w, gate_flanks, fy, 3, PRISMARINE, SEA_LANTERN, placed)

    # iron bars at gate sides — do not seal LEFT/RIGHT ebb opens
    bars = 0
    for x, z in [
        # gate1: mark LEFT open mouth edges (x23/24) and RIGHT wall ends — not fill x16–23
        (23, 35), (23, 37), (24, 35), (24, 37),
        (38, 35), (38, 37),
        # gate2: mark RIGHT open mouth edges (x32/33) and LEFT wall ends — not fill x33–40
        (32, 43), (32, 45), (33, 43), (33, 45),
        (16, 43), (16, 45),
        # room accents
        (-8, 30), (8, 30), (-8, 92), (8, 92),
    ]:
        if bars_column(w, x, z, fy, 3):
            bars += 1

    # cyan wool trim — hall accents, not mid-ebb / not water row
    trim_n = 0
    for x, z in [
        (22, 32), (28, 32), (34, 32),
        (22, 50), (28, 50), (34, 50),
        (14, 36), (42, 36), (14, 44), (42, 44),
        (-6, 14), (6, 14), (-8, 100), (8, 100),
        (24, 64), (40, 64),
    ]:
        if trim_at(w, x, z, fy, WOOL_CYAN, dy=3):
            trim_n += 1
        if trim_at(w, x, z, fy, WOOL_CYAN, dy=2):
            trim_n += 1

    print(f"  sx17 pillars={placed[0]} bars={bars} cyan_wool={trim_n}")
    return placed[0], bars, trim_n


def decorate_sx18(w):
    """Quartz/andesite pillars + corner-flank iron bars + purple/cyan stained glass.
    Corners (24,32)(24,48)(38,48) — decorate OFF corner centers; never seal zigzag walk."""
    fy = 63
    placed = [0]

    def body_fn(x, z):
        return QUARTZ if (x + z) % 2 == 0 else ANDESITE

    place_pillars(w, SAFE_CORNERS, fy, 4, body_fn, GLOWSTONE, placed)

    # corner flanks — OFF corner centers (already glass+glow)
    corner_flanks = [
        # around (24,32)
        (22, 32), (26, 32), (24, 30), (24, 34),
        (22, 30), (26, 30), (22, 34), (26, 34),
        (20, 32), (28, 32), (24, 28), (24, 36),
        # around (24,48)
        (22, 48), (26, 48), (24, 46), (24, 50),
        (22, 46), (26, 46), (22, 50), (26, 50),
        (20, 48), (28, 48),
        # around (38,48)
        (36, 48), (40, 48), (38, 46), (38, 50),
        (36, 46), (40, 46), (36, 50), (40, 50),
        (34, 48), (42, 48), (38, 44), (38, 52),
    ]
    place_pillars(w, corner_flanks, fy, 3, body_fn, GLOWSTONE, placed)

    # iron bars at corner flanks — do not seal zigzag walk through corners
    bars = 0
    for x, z in [
        (21, 31), (27, 31), (21, 33), (27, 33),
        (21, 47), (27, 47), (21, 49), (27, 49),
        (35, 47), (41, 47), (35, 49), (41, 49),
        (23, 29), (25, 29), (23, 51), (25, 51),
        (37, 45), (39, 45), (37, 51), (39, 51),
        (-8, 30), (8, 30), (-8, 92), (8, 92),
    ]:
        if bars_column(w, x, z, fy, 3):
            bars += 1

    # purple / cyan stained glass trim
    trim_n = 0
    for x, z in [
        (20, 30), (28, 30), (20, 50), (28, 50),
        (34, 46), (42, 46), (34, 50), (42, 50),
        (22, 28), (26, 28), (36, 44), (40, 44),
        (-6, 14), (6, 14), (-8, 100), (8, 100),
        (24, 64), (40, 64),
    ]:
        blk = STAINED_GLASS_PURPLE if (x + z) % 2 == 0 else STAINED_GLASS_CYAN
        if trim_at(w, x, z, fy, blk, dy=3):
            trim_n += 1
        if trim_at(w, x, z, fy, blk, dy=2):
            trim_n += 1

    print(f"  sx18 pillars={placed[0]} bars={bars} glass_trim={trim_n}")
    return placed[0], bars, trim_n


def decorate_sx19(w):
    """Stonebrick/andesite pillars + node-side iron bars + purple wool.
    Nodes z=36 LEFT open + note(18,36) / z=46 RIGHT open + note(38,46);
    pads (18,36)(38,46). NEVER cover note blocks; never seal resonance side passes."""
    fy = 63
    placed = [0]

    def body_fn(x, z):
        return STONEBRICK if (x + z) % 2 == 0 else ANDESITE

    place_pillars(w, SAFE_CORNERS, fy, 4, body_fn, GLOWSTONE, placed)

    # node flanks — OFF pad centers (notes at fy+1) and OFF resonance opens
    # Node1 z=36: LEFT open x16–21 — decorate RIGHT of barrier / open mouth edges only
    # Node2 z=46: RIGHT open x35–40 — decorate LEFT of barrier / open mouth edges only
    node_flanks = [
        # beside node1 pad — not ON (18,36)
        (16, 36), (20, 36), (18, 34), (18, 38),
        (16, 34), (20, 34), (16, 38), (20, 38),
        # barrier south/north banks (x22–40 iron-bar wall) — clear stone, not mid-pass
        (22, 33), (26, 33), (30, 33), (34, 33), (38, 33),
        (22, 39), (26, 39), (30, 39), (34, 39), (38, 39),
        # beside node2 pad — not ON (38,46)
        (36, 46), (40, 46), (38, 44), (38, 48),
        (36, 44), (40, 44), (36, 48), (40, 48),
        # fence/barrier banks z=45/47 (x16–34)
        (16, 43), (20, 43), (24, 43), (28, 43), (32, 43),
        (16, 49), (20, 49), (24, 49), (28, 49), (32, 49),
        # hall accents
        (14, 32), (42, 32), (14, 52), (42, 52),
    ]
    place_pillars(w, node_flanks, fy, 3, body_fn, GLOWSTONE, placed)

    # iron bars at node sides — mark open mouth edges; do not seal LEFT/RIGHT resonance
    bars = 0
    for x, z in [
        # node1 LEFT open mouth edges (x21/22)
        (21, 35), (21, 37), (22, 35), (22, 37),
        (40, 35), (40, 37),
        # node2 RIGHT open mouth edges (x34/35)
        (34, 45), (34, 47), (35, 45), (35, 47),
        (16, 45), (16, 47),
        # room accents
        (-8, 30), (8, 30), (-8, 92), (8, 92),
    ]:
        if bars_column(w, x, z, fy, 3):
            bars += 1

    # purple wool trim — not on note pads
    trim_n = 0
    for x, z in [
        (14, 36), (22, 36), (14, 46), (42, 46),
        (22, 32), (30, 32), (34, 52), (30, 52),
        (-6, 14), (6, 14), (-8, 100), (8, 100),
        (24, 64), (40, 64),
    ]:
        if trim_at(w, x, z, fy, WOOL_PURPLE, dy=3):
            trim_n += 1
        if trim_at(w, x, z, fy, WOOL_PURPLE, dy=2):
            trim_n += 1

    print(f"  sx19 pillars={placed[0]} bars={bars} purple_wool={trim_n}")
    return placed[0], bars, trim_n


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

SX17_EXTRA = [
    ("pad2036", 20, 63, 36),
    ("pad3644", 36, 63, 44),
    ("ebbL", 20, 63, 36),
    ("ebbR", 36, 63, 44),
    ("gate36", 30, 63, 36),
    ("gate44", 24, 63, 44),
]

SX18_EXTRA = [
    ("c2432", 24, 63, 32),
    ("c2448", 24, 63, 48),
    ("c3848", 38, 63, 48),
    ("legN", 24, 63, 40),
    ("legE", 30, 63, 48),
]

SX19_EXTRA = [
    ("pad1836", 18, 63, 36),
    ("pad3846", 38, 63, 46),
    ("noteL", 18, 63, 36),
    ("noteR", 38, 63, 46),
    ("openL", 18, 63, 36),
    ("openR", 38, 63, 46),
]


def main():
    jobs = [
        ("ember_short_sx17", decorate_sx17, SX17_EXTRA),
        ("ember_short_sx18", decorate_sx18, SX18_EXTRA),
        ("ember_short_sx19", decorate_sx19, SX19_EXTRA),
    ]
    totals = {}
    extras = {}
    all_ok = True
    for name, fn, extra in jobs:
        path = str(MAP / name)
        print(f"Decorating {name}…")
        w = d441.World(path)
        n, bars, trim = fn(w)
        totals[name] = n
        extras[name] = {"bars": bars, "trim": trim}
        if not footing_check(w, name, SAFE_POINTS):
            all_ok = False
        if not footing_check(w, name + "/theme", extra):
            all_ok = False
        w.save()
        print(f"  dirty regions={len(w.dirty)} saved")
    print("DONE D12512 sx17–sx19 visual pass")
    print("pillars_per_map:", totals)
    print("extras_per_map:", extras)
    print("footing_all_ok:", all_ok)


if __name__ == "__main__":
    main()
