#!/usr/bin/env python3
"""D444: fix sx04 Director path + real terrain sx05–sx09 (D441 World reuse)."""
from __future__ import annotations
import importlib.util, math, os

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("d441", os.path.join(HERE, "d441_build_sx14_sx15_terrain.py"))
d441 = importlib.util.module_from_spec(spec)
spec.loader.exec_module(d441)

World, reset_from_raid = d441.World, d441.reset_from_raid
shift_walk_level, carve_corridor = d441.shift_walk_level, d441.carve_corridor
AIR, STONEBRICK, ANDESITE = d441.AIR, d441.STONEBRICK, d441.ANDESITE
IRON_BARS, MAGMA, GLOW, FENCE, TORCH = d441.IRON_BARS, d441.MAGMA, d441.GLOW, d441.FENCE, d441.TORCH
NETHERRACK, QUARTZ = 87, 155
GLASS, PACKED_ICE, WOOL_CYAN = 20, 174, (35, 9)
WOOL_GRAY, WOOL_ORANGE, PLANKS = (35, 7), (35, 1), 5
SANDSTONE, WATER = 24, 9
COBBLE = 4


def common(w, wall, floor=STONEBRICK):
    w.set_flat_void_level()
    w.stamp_void_box(-24, -8, 56, 128, y_bed=60)
    w.fill(-6, 64, 0, 6, 64, 14, floor)
    w.fill(-6, 69, 0, 6, 69, 14, floor)
    for yy in range(65, 69):
        w.fill(-6, yy, 0, -6, yy, 14, wall)
        w.fill(6, yy, 0, 6, yy, 14, wall)
        w.fill(-6, yy, 0, 6, yy, 0, wall)
    w.fill(-3, 65, 14, 3, 68, 14, AIR)
    w.set(0, 65, 5, GLOW)
    w.fill(-10, 64, 16, 10, 64, 36, floor)
    w.fill(-10, 69, 16, 10, 69, 36, floor)
    for yy in range(65, 69):
        w.fill(-10, yy, 16, -10, yy, 36, wall)
        w.fill(10, yy, 16, 10, yy, 36, wall)
        w.fill(-10, yy, 16, 10, yy, 16, wall)
        w.fill(-10, yy, 36, 10, yy, 36, wall)
    w.fill(10, 65, 28, 10, 68, 34, AIR)  # east door to R2
    w.fill(18, 64, 56, 42, 64, 82, floor)
    w.fill(18, 69, 56, 42, 69, 82, floor)
    for yy in range(65, 69):
        w.fill(18, yy, 56, 18, yy, 82, wall)
        w.fill(42, yy, 56, 42, yy, 82, wall)
        w.fill(18, yy, 56, 42, yy, 56, wall)
        w.fill(18, yy, 82, 42, yy, 82, wall)
    w.fill(19, 65, 57, 41, 68, 81, AIR)
    carve_corridor(w, [(20, 80), (8, 80), (8, 90), (0, 90), (0, 98)], width=1, floor=floor, wall=wall, y=64, h=4)
    w.fill(-14, 64, 88, 14, 64, 116, floor)
    w.fill(-14, 74, 88, 14, 74, 116, floor)
    for yy in range(65, 74):
        w.fill(-14, yy, 88, -14, yy, 116, wall)
        w.fill(14, yy, 88, 14, yy, 116, wall)
        w.fill(-14, yy, 88, 14, yy, 88, wall)
        w.fill(-14, yy, 116, 14, yy, 116, wall)
    w.fill(-13, 65, 89, 13, 73, 115, AIR)
    for side in (-12, 12):
        w.set(side, 65, 102, GLOW)
        for yy in range(65, 70):
            w.set(side, yy, 100, AIR)
            w.set(side, yy, 102, AIR)
            w.set(side, yy, 104, AIR)
    w.fill(-2, 64, 100, 2, 64, 104, QUARTZ)
    w.set(0, 65, 102, GLOW)
    w.fill(-2, 65, 88, 2, 68, 88, AIR)


def open_r2(w, wall, floor=STONEBRICK):
    """Standard open R2 hall with clear main aisle x=28-32 (Director doors)."""
    w.fill(14, 64, 26, 42, 64, 54, floor)
    w.fill(14, 69, 26, 42, 69, 54, floor)
    for yy in range(65, 69):
        w.fill(14, yy, 26, 14, yy, 54, wall)
        w.fill(42, yy, 26, 42, yy, 54, wall)
        w.fill(14, yy, 26, 42, yy, 26, wall)
        w.fill(14, yy, 54, 42, yy, 54, wall)
    w.fill(15, 65, 27, 41, 68, 53, AIR)
    # main aisle from R1 door (x16) to R2 north door (z52)
    carve_corridor(w, [(12, 31), (30, 31), (30, 39), (30, 52), (32, 56)], width=2, floor=floor, wall=wall, y=64, h=4)


def pad(w, x, z, floor=STONEBRICK, r=2, y=64):
    for dx in range(-r, r + 1):
        for dz in range(-r, r + 1):
            w.set(x + dx, y, z + dz, floor)
            for yy in range(y + 1, y + 4):
                w.set(x + dx, yy, z + dz, AIR)
            w.set(x + dx, y + 4, z + dz, floor)


def build_sx04(dest):
    """Spiral well as SIDE decoration; main aisle clear for Director (fix PARTIAL)."""
    w = World(dest)
    common(w, NETHERRACK, STONEBRICK)
    open_r2(w, NETHERRACK, STONEBRICK)
    # decorative well OFF aisle (east pocket) — glass grate, spiral rail around
    cx, cz = 38, 40
    w.fill(cx - 3, 54, cz - 3, cx + 3, 63, cz + 3, AIR)
    w.fill(cx - 3, 53, cz - 3, cx + 3, 53, cz + 3, MAGMA)
    w.fill(cx - 3, 63, cz - 3, cx + 3, 63, cz + 3, GLASS)
    for i in range(16):
        ang = i / 16 * 2 * math.pi
        x = int(round(cx + 4 * math.cos(ang)))
        z = int(round(cz + 4 * math.sin(ang)))
        y = 64 - (i % 8) // 2
        w.set(x, y, z, NETHERRACK)
        w.set(x, y + 1, z, FENCE)
    for z in (32, 40, 48):
        pad(w, 30, z)
        w.set(30, 65, z, GLOW)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx04 aisle-clear + side spiral well")


def build_sx05(dest):
    """Wind corridor: narrow bridges over void gap (cannot walk around)."""
    w = World(dest)
    common(w, WOOL_CYAN, STONEBRICK)
    open_r2(w, WOOL_CYAN, STONEBRICK)
    # cut void chasm z=36-42 except 2-wide bridge at x=29-31
    w.fill(15, 60, 36, 41, 68, 42, AIR)
    w.fill(15, 59, 36, 41, 59, 42, AIR)  # void below
    # bridges (two segments = readable wind gaps)
    w.fill(29, 64, 36, 31, 64, 42, STONEBRICK)
    w.fill(29, 69, 36, 31, 69, 42, STONEBRICK)
    for yy in range(65, 69):
        w.set(28, yy, 39, FENCE)
        w.set(32, yy, 39, FENCE)
    # side niche dodge pads
    pad(w, 24, 34)
    pad(w, 36, 44)
    pad(w, 30, 32)
    pad(w, 30, 48)
    w.set(30, 65, 39, GLOW)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx05 wind narrow bridges")


def build_sx06(dest):
    """Ring corridor: center blocked, must walk around ≥ half ring."""
    w = World(dest)
    common(w, WOOL_GRAY, STONEBRICK)
    open_r2(w, WOOL_GRAY, STONEBRICK)
    # solid center plug
    w.fill(26, 64, 34, 34, 68, 46, ANDESITE)
    # ring path around
    carve_corridor(
        w,
        [(18, 31), (22, 31), (22, 48), (38, 48), (38, 31), (30, 31), (30, 52)],
        width=1,
        floor=STONEBRICK,
        wall=WOOL_GRAY,
        y=64,
        h=4,
    )
    for c in [(22, 36), (22, 48), (38, 48), (38, 36), (30, 48)]:
        pad(w, *c)
        w.set(c[0], 65, c[1], GLOW)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx06 ring corridor")


def build_sx07(dest):
    """Tower ascent: ≥2 floors of stairs in R2."""
    w = World(dest)
    common(w, ANDESITE, STONEBRICK)
    # R2 multi-level
    w.fill(14, 64, 26, 42, 80, 54, ANDESITE)
    carve_corridor(w, [(12, 31), (20, 31)], width=1, y=64, h=4)
    # floor 0
    w.fill(20, 64, 28, 40, 64, 50, STONEBRICK)
    w.fill(20, 65, 28, 40, 67, 50, AIR)
    # stairs to y68
    for i in range(5):
        w.fill(22, 64 + i, 30 + i, 26, 64 + i, 32 + i, STONEBRICK)
        for yy in range(65 + i, 68 + i):
            w.fill(22, yy, 30 + i, 26, yy, 32 + i, AIR)
    # floor 1 y68
    w.fill(20, 68, 34, 40, 68, 50, STONEBRICK)
    w.fill(20, 69, 34, 40, 71, 50, AIR)
    pad(w, 30, 40, y=68)
    # stairs to y72
    for i in range(5):
        w.fill(34, 68 + i, 36 + i, 38, 68 + i, 38 + i, STONEBRICK)
        for yy in range(69 + i, 72 + i):
            w.fill(34, yy, 36 + i, 38, yy, 38 + i, AIR)
    # floor 2 y72 → exit north down to R3 y64
    w.fill(20, 72, 40, 40, 72, 52, STONEBRICK)
    w.fill(20, 73, 40, 40, 75, 52, AIR)
    pad(w, 30, 48, y=72)
    w.set(30, 73, 48, GLOW)
    for i in range(8):
        w.fill(28, 72 - i, 52 + i // 2, 34, 72 - i, 54 + i // 2, STONEBRICK)
    carve_corridor(w, [(32, 56), (32, 58)], width=1, y=64, h=4)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx07 tower ≥2 floors")


def build_sx08(dest):
    """Split-level: lower court → stairs → upper hall (≥1 floor height)."""
    w = World(dest)
    common(w, PLANKS, STONEBRICK)
    open_r2(w, PLANKS, STONEBRICK)
    # lower half z=28-38
    # upper half z=40-52 at y68 with hole between
    w.fill(16, 65, 40, 40, 67, 52, AIR)
    w.fill(16, 64, 40, 40, 64, 52, AIR)  # void under upper
    w.fill(16, 68, 40, 40, 68, 52, PLANKS)
    w.fill(16, 72, 40, 40, 72, 52, PLANKS)
    w.fill(16, 69, 40, 40, 71, 52, AIR)
    # stairs lower→upper
    for i in range(5):
        w.fill(28, 64 + i, 36 + i, 32, 64 + i, 38 + i, PLANKS)
        for yy in range(65 + i, 68 + i):
            w.fill(28, yy, 36 + i, 32, yy, 38 + i, AIR)
    pad(w, 30, 32)
    pad(w, 30, 46, y=68)
    pad(w, 24, 46, y=68)
    pad(w, 36, 46, y=68)
    # stairs down to R3
    for i in range(5):
        w.fill(28, 68 - i, 50 + i, 32, 68 - i, 52 + i, PLANKS)
    carve_corridor(w, [(30, 54), (32, 56)], width=1, y=64, h=4)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx08 split-level")


def build_sx09(dest):
    """Canal stepping stones ≥3 discrete pads over lava/water."""
    w = World(dest)
    common(w, SANDSTONE, STONEBRICK)
    open_r2(w, SANDSTONE, STONEBRICK)
    # canal trench z=36-44 full width except stones
    w.fill(16, 60, 36, 40, 64, 44, AIR)
    w.fill(16, 60, 36, 40, 60, 44, MAGMA)
    w.fill(16, 61, 36, 40, 62, 44, WATER)
    # ≥3 discrete stepping stones
    stones = [(22, 40), (30, 40), (38, 40)]
    for x, z in stones:
        w.fill(x - 1, 64, z - 1, x + 1, 64, z + 1, STONEBRICK)
        for yy in range(65, 68):
            w.fill(x - 1, yy, z - 1, x + 1, yy, z + 1, AIR)
        w.set(x, 65, z, GLOW)
    # banks
    pad(w, 30, 32)
    pad(w, 30, 48)
    # side approach to first/last stone
    carve_corridor(w, [(18, 31), (22, 34), (22, 40)], width=0, y=64, h=3)
    carve_corridor(w, [(38, 40), (38, 46), (32, 52)], width=0, y=64, h=3)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx09 canal ≥3 stepping stones")


def main():
    for name, fn in [
        ("ember_short_sx04", build_sx04),
        ("ember_short_sx05", build_sx05),
        ("ember_short_sx06", build_sx06),
        ("ember_short_sx07", build_sx07),
        ("ember_short_sx08", build_sx08),
        ("ember_short_sx09", build_sx09),
    ]:
        print(f"Building {name}…")
        fn(reset_from_raid(name))
    print("DONE sx04–sx09")


if __name__ == "__main__":
    main()
