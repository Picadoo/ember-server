#!/usr/bin/env python3
"""D450: light visual pass on Q01–Q03 — themed pillars/trim only.
Loads existing MCA via d441.World; never stamps void / never rewrites floors.
Samples solid footing (floor block y) before placing — Q01/Q02 floor@63, Q03 r1@59 / lower@55."""
from __future__ import annotations
import importlib.util
from pathlib import Path

HERE = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("d441", HERE / "d441_build_sx14_sx15_terrain.py")
d441 = importlib.util.module_from_spec(spec)
spec.loader.exec_module(d441)

MAP = Path("/workspace/minecraft/plugins/DungeonPlus/map")

STONEBRICK, OAK_LOG, GLOWSTONE = 98, 17, 89
WOOL_GREEN, WOOL_YELLOW = (35, 13), (35, 4)
NETHERRACK, RED_NETHER = 87, 215
WOOL_RED, REDSTONE_LAMP = (35, 14), 123
COBBLE, SOUL_SAND, IRON_BARS = 4, 88, 101

AIRISH = {0, 8, 9, 10, 11, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 83, 90, 93, 94, 104, 105, 106, 111, 115, 132, 141, 142, 175}


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


def pillar(w, x, z, floor_y, h, body, cap=None):
    """floor_y = solid floor block; body fills floor_y+1 .. floor_y+h; cap at floor_y+h+1."""
    foot, _ = get_block(w, x, floor_y, z)
    if not is_solid(foot):
        print(f"  skip pillar ({x},{floor_y},{z}) footing={foot}")
        return False
    above, _ = get_block(w, x, floor_y + 1, z)
    if is_solid(above) and above not in (body if isinstance(body, int) else body[0],):
        # already a wall/column — skip to avoid rewriting structure
        print(f"  skip pillar ({x},{floor_y},{z}) occupied above={above}")
        return False
    for dy in range(1, h + 1):
        w.set(x, floor_y + dy, z, body)
    if cap is not None:
        w.set(x, floor_y + h + 1, z, cap)
    return True


def trim_wool(w, x, z, floor_y, block):
    foot, _ = get_block(w, x, floor_y, z)
    if is_solid(foot):
        w.set(x, floor_y + 3, z, block)


def decorate_q01(w):
    """Courtyard: oak / stonebrick pillars + glowstone; green/yellow wool trim. Floor@63."""
    fy = 63
    placed = 0
    for x, z in [
        (-10, 22), (10, 22), (-10, 34), (10, 34),
        (24, 34), (38, 34), (24, 40), (42, 40),
        (26, 56), (42, 56), (24, 64), (42, 64),
        (-10, 88), (10, 88), (-10, 96), (10, 96),
    ]:
        body = OAK_LOG if (x + z) % 2 == 0 else STONEBRICK
        if pillar(w, x, z, fy, 4, body, GLOWSTONE):
            placed += 1
    for z in (24, 56, 90):
        for x in range(-6, 7, 3):
            trim_wool(w, x, z, fy, WOOL_GREEN if z != 56 else WOOL_YELLOW)
    print(f"  q01 pillars placed={placed}")


def decorate_q02(w):
    """Ash: netherrack / red nether brick + glowstone/lamp. Floor@63."""
    fy = 63
    placed = 0
    for x, z in [
        (-8, 24), (8, 24), (-8, 36), (8, 36),
        (18, 50), (38, 50), (18, 60), (38, 60),
        (-8, 78), (10, 78), (-8, 88), (10, 88),
        (-10, 110), (10, 110), (-10, 116), (10, 116),
    ]:
        body = RED_NETHER if (x + z) % 4 == 0 else NETHERRACK
        cap = REDSTONE_LAMP if body == RED_NETHER else GLOWSTONE
        if pillar(w, x, z, fy, 4, body, cap):
            placed += 1
    for z in (26, 56, 112):
        for x in (-4, 0, 4):
            trim_wool(w, x, z, fy, WOOL_RED)
    print(f"  q02 pillars placed={placed}")


def decorate_q03(w):
    """Crypt: stonebrick/cobble + soul-sand/iron bars. r1 floor@59; lower@55; entry@63."""
    placed = 0
    # entry corridor
    for x, z in [(-4, 8), (4, 8)]:
        if pillar(w, x, z, 63, 3, STONEBRICK, GLOWSTONE):
            placed += 1
    # r1 upper hall
    for x, z in [(-10, 26), (10, 26), (-10, 38), (10, 38)]:
        if pillar(w, x, z, 59, 4, STONEBRICK, GLOWSTONE):
            placed += 1
    # r2 lower tomb
    for x, z in [(-38, 52), (-22, 52), (-38, 64), (-22, 64)]:
        if pillar(w, x, z, 55, 4, COBBLE, GLOWSTONE):
            placed += 1
    # r3 fold
    for x, z in [(-8, 84), (12, 84), (-8, 94), (12, 94)]:
        if pillar(w, x, z, 55, 4, STONEBRICK, GLOWSTONE):
            placed += 1
    # boss hall
    for x, z in [(-12, 112), (14, 112), (-12, 124), (14, 124)]:
        if pillar(w, x, z, 55, 4, COBBLE, GLOWSTONE):
            placed += 1
    # soul-sand trim + iron bars near doorways
    for x, z, fy in [(0, 40, 59), (-29, 70, 55), (2, 100, 55)]:
        foot, _ = get_block(w, x, fy, z)
        if is_solid(foot):
            w.set(x - 2, fy + 1, z, SOUL_SAND)
            w.set(x + 2, fy + 1, z, SOUL_SAND)
            for dy in range(2, 5):
                w.set(x - 3, fy + dy, z, IRON_BARS)
                w.set(x + 3, fy + dy, z, IRON_BARS)
    print(f"  q03 pillars placed={placed}")


def main():
    jobs = [
        ("ember_daily_v1", decorate_q01),
        ("ember_daily_ash_v1", decorate_q02),
        ("ember_daily_crypt_v1", decorate_q03),
    ]
    for name, fn in jobs:
        path = str(MAP / name)
        print(f"Decorating {name}…")
        w = d441.World(path)
        fn(w)
        w.save()
        print(f"  dirty regions={len(w.dirty)} saved")
    print("DONE D450 visual pass")


if __name__ == "__main__":
    main()
