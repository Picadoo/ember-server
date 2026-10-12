#!/usr/bin/env python3
"""D12376: light visual pass on short sx01–sx04 — themed pillars/trim only.
Loads existing MCA via d441.World; never stamps void / never rewrites floors.
Samples solid footing (floor block y) before placing — primary walk fy=63
(after shift_walk_level); sx02 elevated fork deck fy=68."""
from __future__ import annotations
import importlib.util
from pathlib import Path

HERE = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("d441", HERE / "d441_build_sx14_sx15_terrain.py")
d441 = importlib.util.module_from_spec(spec)
spec.loader.exec_module(d441)

MAP = Path("/workspace/minecraft/plugins/DungeonPlus/map")

# 1.12.2 block ids
STONEBRICK, OAK_LOG, GLOWSTONE = 98, 17, 89
SPRUCE_LOG, DARK_OAK = (17, 1), 162
WOOL_GRAY, WOOL_YELLOW = (35, 7), (35, 4)
NETHERRACK, RED_NETHER, MAGMA = 87, 215, 213
IRON_BARS, REDSTONE_LAMP = 101, 123
PACKED_ICE, SNOW, ICE = 174, 80, 79
PRISMARINE, FIRE = 168, 51

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
    # only place into airish so we don't rewrite walls
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


def decorate_sx01(w):
    """Stonebrick/oak pillars + glow cap; gray/yellow wool trim; few braziers. fy=63."""
    fy = 63
    placed = [0]
    coords = [
        # R0 / entry
        (-6, 8), (6, 8), (-6, 12), (6, 12),
        # R1
        (-8, 22), (8, 22), (-10, 34), (10, 34),
        # R2 hall
        (22, 32), (38, 32), (22, 48), (38, 40),
        # R3
        (24, 60), (42, 60), (24, 70), (42, 70),
        # Boss
        (-10, 96), (10, 96), (-12, 108), (12, 108),
    ]

    def body_fn(x, z):
        return OAK_LOG if (x + z) % 2 == 0 else STONEBRICK

    place_pillars(w, coords, fy, 4, body_fn, GLOWSTONE, placed)

    trim_n = 0
    for z in (10, 28, 40, 64, 100):
        for x in (-4, 0, 4):
            if abs(x) == 0 and z in (28, 40, 64):
                continue  # keep aisle clear of mid-path trim
            block = WOOL_GRAY if z % 20 < 10 else WOOL_YELLOW
            if trim_at(w, x, z, fy, block):
                trim_n += 1
    # side-hall trim in R2/R3
    for x, z in [(26, 36), (34, 44), (28, 66), (36, 72)]:
        if trim_at(w, x, z, fy, WOOL_YELLOW if (x + z) % 2 == 0 else WOOL_GRAY):
            trim_n += 1

    braz = 0
    for x, z in [(-8, 10), (8, 10), (-10, 28), (10, 28), (22, 36), (38, 44)]:
        if brazier(w, x, z, fy):
            braz += 1

    print(f"  sx01 pillars={placed[0]} trim={trim_n} braziers={braz}")
    return placed[0]


def decorate_sx02(w):
    """Wood logs + iron-bar rail accents; glow/lamp chain; prismarine damp. fy=63 + deck@68."""
    fy = 63
    placed = [0]
    ground = [
        (-6, 8), (6, 8), (-6, 12), (6, 12),
        (-8, 22), (8, 22), (-10, 34), (10, 34),
        (24, 60), (42, 60), (24, 70), (42, 70),
        (-10, 96), (10, 96), (-12, 108), (12, 108),
    ]

    def body_fn(x, z):
        return SPRUCE_LOG if (x + z) % 2 == 0 else DARK_OAK

    place_pillars(w, ground, fy, 4, body_fn, GLOWSTONE, placed)

    # elevated fork deck pillars / lamps (fy=68); skip glowstone-occupied corners
    deck = [
        (22, 40), (38, 40), (22, 44), (38, 44),
        (26, 34), (34, 50),
    ]
    for x, z in deck:
        cap = REDSTONE_LAMP if (x + z) % 4 == 0 else GLOWSTONE
        if pillar(w, x, z, 68, 3, SPRUCE_LOG if (x + z) % 2 == 0 else DARK_OAK, cap):
            placed[0] += 1

    # iron-bar rail accents along deck edges (not on walk centers)
    bars = 0
    for x, z in [(23, 36), (37, 36), (23, 48), (37, 48), (28, 35), (32, 49)]:
        if bars_column(w, x, z, 68, 2):
            bars += 1
    # ground-level bar accents near R1 door / boss entry
    for x, z in [(-8, 30), (8, 30), (-8, 92), (8, 92)]:
        if bars_column(w, x, z, fy, 3):
            bars += 1

    # prismarine damp accents (wall-root / side niches)
    pris = 0
    for x, z in [(-8, 12), (8, 12), (24, 42), (36, 42), (26, 66), (-8, 100)]:
        if trim_at(w, x, z, fy if z < 50 or z > 55 else 68, PRISMARINE, dy=1):
            pris += 1
    # deck prismarine
    for x, z in [(22, 42), (38, 42)]:
        if trim_at(w, x, z, 68, PRISMARINE, dy=1):
            pris += 1

    # lamp chain glow along ground aisle sides
    lamps = 0
    for z in (16, 50, 80):
        for x in (-6, 6):
            if trim_at(w, x, z, fy, REDSTONE_LAMP, dy=4):
                lamps += 1

    print(f"  sx02 pillars={placed[0]} bars={bars} prismarine={pris} lamps={lamps}")
    return placed[0]


def decorate_sx03(w):
    """Stonebrick pillars; packed-ice/snow/ice wall-root accents; gate-side iron bars. No ice re-floor."""
    fy = 63
    placed = [0]
    coords = [
        (-6, 8), (6, 8), (-6, 12), (6, 12),
        (-8, 22), (8, 22), (-10, 34), (10, 34),
        (22, 32), (38, 32), (22, 48), (38, 48),
        (24, 60), (42, 60), (24, 70), (42, 70),
        (-10, 96), (10, 96), (-12, 108), (12, 108),
    ]
    place_pillars(w, coords, fy, 4, STONEBRICK, GLOWSTONE, placed)

    # wall-root ice/snow accents (sparingly) — side niches only
    ice_n = 0
    for x, z, blk in [
        (-9, 20, SNOW), (9, 20, SNOW), (-9, 36, PACKED_ICE), (9, 36, ICE),
        (18, 30, SNOW), (40, 50, SNOW), (18, 50, PACKED_ICE), (40, 30, ICE),
        (22, 62, SNOW), (40, 72, PACKED_ICE), (-9, 94, SNOW), (9, 110, ICE),
    ]:
        if trim_at(w, x, z, fy, blk, dy=1):
            ice_n += 1

    # thicken iron bars at gate sides — Gate1 open LEFT near (20,36); Gate2 open RIGHT near (36,44)
    # place beside the side-pass, not in the walk pad center
    bars = 0
    for x, z in [
        # Gate1 left chamber flanks
        (17, 34), (17, 38), (22, 34), (22, 38),
        # Gate2 right chamber flanks
        (34, 42), (34, 46), (39, 42), (39, 46),
    ]:
        if bars_column(w, x, z, fy, 4):
            bars += 1

    print(f"  sx03 pillars={placed[0]} ice_accents={ice_n} gate_bars={bars}")
    return placed[0]


def decorate_sx04(w):
    """Well-mouth stonebrick pillars + glow; red-nether/netherrack trim; magma accents off-path.
    Well (d444): east pocket cx=38,cz=40; magma@y53; rim around 34–42 / 34–46. Do not plug aisle."""
    fy = 63
    placed = [0]
    # R0/entry + R1 + R3 + Boss (ground)
    ground = [
        (-6, 8), (6, 8), (-6, 12), (6, 12),
        (-8, 22), (8, 22), (-10, 34), (10, 34),
        (24, 60), (42, 60), (24, 70), (42, 70),
        (-10, 96), (10, 96), (-12, 108), (12, 108),
    ]
    place_pillars(w, ground, fy, 4, STONEBRICK, GLOWSTONE, placed)

    # well-mouth pillars around rim (off aisle center x≈30)
    well_rim = [
        (34, 36), (42, 36), (34, 44), (42, 44),
        (34, 40), (42, 40), (38, 34), (38, 46),
    ]
    place_pillars(w, well_rim, fy, 4, STONEBRICK, GLOWSTONE, placed)

    # red nether / netherrack trim near well rim (wall-height accents, not floor rewrite)
    trim_n = 0
    for x, z in [(33, 36), (43, 36), (33, 44), (43, 44), (38, 33), (38, 47)]:
        blk = RED_NETHER if (x + z) % 2 == 0 else NETHERRACK
        if trim_at(w, x, z, fy, blk, dy=2):
            trim_n += 1
        if trim_at(w, x, z, fy, blk, dy=3):
            trim_n += 1

    # MAGMA accents OFF path — place as fy+1 niche beside rim pillars, never aisle centers
    magma_n = 0
    for x, z in [(34, 35), (42, 35), (34, 45), (42, 45), (33, 40), (43, 40)]:
        foot, _ = get_block(w, x, fy, z)
        if not is_solid(foot):
            continue
        a1, _ = get_block(w, x, fy + 1, z)
        if is_solid(a1):
            continue
        w.set(x, fy + 1, z, MAGMA)
        magma_n += 1

    # a few red-nether body pillars near boss for furnace feel
    for x, z in [(-8, 100), (8, 100)]:
        if pillar(w, x, z, fy, 3, RED_NETHER, GLOWSTONE):
            placed[0] += 1

    print(f"  sx04 pillars={placed[0]} red_trim={trim_n} magma_accents={magma_n}")
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
        ("ember_short_sx01", decorate_sx01),
        ("ember_short_sx02", decorate_sx02),
        ("ember_short_sx03", decorate_sx03),
        ("ember_short_sx04", decorate_sx04),
    ]
    totals = {}
    all_ok = True
    for name, fn in jobs:
        path = str(MAP / name)
        print(f"Decorating {name}…")
        w = d441.World(path)
        n = fn(w)
        totals[name] = n
        if not footing_check(w, name, SAFE_POINTS):
            all_ok = False
        # sx02 also check elevated deck
        if name == "ember_short_sx02":
            deck_pts = [("deck", 30, 68, 40), ("deckL", 24, 68, 36), ("deckR", 36, 68, 48)]
            if not footing_check(w, name + "/deck", deck_pts):
                all_ok = False
        w.save()
        print(f"  dirty regions={len(w.dirty)} saved")
    print("DONE D12376 sx01–sx04 visual pass")
    print("pillars_per_map:", totals)
    print("footing_all_ok:", all_ok)


if __name__ == "__main__":
    main()
