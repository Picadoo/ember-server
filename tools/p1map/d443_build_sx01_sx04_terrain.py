#!/usr/bin/env python3
"""D443: real Anvil terrain for sx01–sx04 (first shorts players see).
Reuses D441 World. Floors at 64 then shift_walk_level → 63.
"""
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

COBBLE, MOSSY_SB = 4, (98, 1)
PLANKS, FENCE_GATE = 5, 107
ICE, PACKED_ICE, SNOW = 79, 174, 80
NETHERRACK, MAGMA_B = 87, 213
GLASS, IRON_BLOCK = 20, 42
QUARTZ = 155
WOOL_ORANGE, WOOL_LIGHT_BLUE = (35, 1), (35, 3)


def common(w, wall, floor=STONEBRICK, boss_y=64):
    w.set_flat_void_level()
    w.stamp_void_box(-24, -8, 56, 128, y_bed=60)
    # R0
    w.fill(-6, 64, 0, 6, 64, 14, floor)
    w.fill(-6, 69, 0, 6, 69, 14, floor)
    for yy in range(65, 69):
        w.fill(-6, yy, 0, -6, yy, 14, wall)
        w.fill(6, yy, 0, 6, yy, 14, wall)
        w.fill(-6, yy, 0, 6, yy, 0, wall)
    w.fill(-3, 65, 14, 3, 68, 14, AIR)
    w.set(0, 65, 5, GLOW)
    # R1
    w.fill(-10, 64, 16, 10, 64, 36, floor)
    w.fill(-10, 69, 16, 10, 69, 36, floor)
    for yy in range(65, 69):
        w.fill(-10, yy, 16, -10, yy, 36, wall)
        w.fill(10, yy, 16, 10, yy, 36, wall)
        w.fill(-10, yy, 16, 10, yy, 16, wall)
        w.fill(-10, yy, 36, 10, yy, 36, wall)
    w.fill(10, 65, 28, 10, 68, 34, AIR)
    # R3
    w.fill(18, 64, 56, 42, 64, 82, floor)
    w.fill(18, 69, 56, 42, 69, 82, floor)
    for yy in range(65, 69):
        w.fill(18, yy, 56, 18, yy, 82, wall)
        w.fill(42, yy, 56, 42, yy, 82, wall)
        w.fill(18, yy, 56, 42, yy, 56, wall)
        w.fill(18, yy, 82, 42, yy, 82, wall)
    w.fill(19, 65, 57, 41, 68, 81, AIR)
    carve_corridor(w, [(20, 80), (8, 80), (8, 90), (0, 90), (0, 98)], width=1, floor=floor, wall=wall, y=64, h=4)
    # Boss
    w.fill(-14, boss_y, 88, 14, boss_y, 116, floor)
    w.fill(-14, boss_y + 10, 88, 14, boss_y + 10, 116, floor)
    for yy in range(boss_y + 1, boss_y + 10):
        w.fill(-14, yy, 88, -14, yy, 116, wall)
        w.fill(14, yy, 88, 14, yy, 116, wall)
        w.fill(-14, yy, 88, 14, yy, 88, wall)
        w.fill(-14, yy, 116, 14, yy, 116, wall)
    w.fill(-13, boss_y + 1, 89, 13, boss_y + 9, 115, AIR)
    for side in (-12, 12):
        w.set(side, boss_y + 1, 102, GLOW)
        for yy in range(boss_y + 1, boss_y + 6):
            w.set(side, yy, 100, AIR)
            w.set(side, yy, 102, AIR)
            w.set(side, yy, 104, AIR)
    w.fill(-2, boss_y, 100, 2, boss_y, 104, QUARTZ)
    w.set(0, boss_y + 1, 102, GLOW)
    w.fill(-2, boss_y + 1, 88, 2, boss_y + 4, 88, AIR)


def pad(w, x, z, floor=STONEBRICK, r=2, y=64):
    for dx in range(-r, r + 1):
        for dz in range(-r, r + 1):
            w.set(x + dx, y, z + dz, floor)
            for yy in range(y + 1, y + 4):
                w.set(x + dx, yy, z + dz, AIR)
            w.set(x + dx, y + 4, z + dz, floor)


def build_sx01(dest):
    """Linear sentry post: courtyard → straight corridor → boss (DESIGN: 线性地面)."""
    w = World(dest)
    common(w, ANDESITE, STONEBRICK)
    # R2: open straight hall (readable linear — not raid leftover)
    w.fill(14, 64, 26, 42, 64, 54, STONEBRICK)
    w.fill(14, 69, 26, 42, 69, 54, STONEBRICK)
    for yy in range(65, 69):
        w.fill(14, yy, 26, 14, yy, 54, ANDESITE)
        w.fill(42, yy, 26, 42, yy, 54, ANDESITE)
        w.fill(14, yy, 26, 42, yy, 26, ANDESITE)
        w.fill(14, yy, 54, 42, yy, 54, ANDESITE)
    w.fill(15, 65, 27, 41, 68, 53, AIR)
    # center walk aisle + wall niches
    carve_corridor(w, [(12, 31), (30, 31), (30, 39), (30, 52), (32, 56)], width=2, y=64, h=4)
    for z in (32, 40, 48):
        pad(w, 30, z)
        w.set(30, 65, z, TORCH)
        w.set(22, 65, z, IRON_BARS)
        w.set(38, 65, z, IRON_BARS)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx01 linear sentry corridor")


def build_sx02(dest):
    """Elevation + fork bridges: climb to Y68, left/right lamp bridges rejoin."""
    w = World(dest)
    common(w, WOOL_ORANGE, PLANKS)
    # solid R2 volume then carve elevated
    w.fill(14, 64, 26, 42, 72, 54, ANDESITE)
    # ground stub from R1
    carve_corridor(w, [(12, 31), (18, 31)], width=1, floor=PLANKS, wall=WOOL_ORANGE, y=64, h=4)
    # stairs 64→68
    for i in range(5):
        w.fill(18 + i, 64 + i, 30, 20 + i, 64 + i, 32, PLANKS)
        for yy in range(65 + i, 69 + i):
            w.fill(18 + i, yy, 30, 20 + i, yy, 32, AIR)
    # upper deck Y=68
    w.fill(22, 68, 28, 40, 68, 52, PLANKS)
    w.fill(22, 72, 28, 40, 72, 52, PLANKS)
    w.fill(22, 69, 28, 40, 71, 52, AIR)
    # FORK: left bridge z=34-36 x=24-28 → mid; right bridge z=44-46
    # center blocked at mid so must pick a fork
    w.fill(28, 68, 38, 34, 71, 42, ANDESITE)
    # left path
    carve_corridor(w, [(24, 32), (24, 36), (26, 36), (26, 48), (32, 48), (36, 48)], width=1, floor=PLANKS, wall=WOOL_ORANGE, y=68, h=3)
    # right path
    carve_corridor(w, [(36, 32), (36, 36), (38, 36), (38, 48), (36, 48)], width=1, floor=PLANKS, wall=WOOL_ORANGE, y=68, h=3)
    for c in [(24, 36), (36, 36), (36, 48)]:
        pad(w, *c, floor=PLANKS, y=68)
        w.set(c[0], 69, c[1], GLOW)
    # stairs down to R3 y64
    for i in range(5):
        w.fill(30, 68 - i, 50 + i, 34, 68 - i, 52 + i, PLANKS)
    carve_corridor(w, [(32, 56), (32, 58)], width=1, floor=PLANKS, y=64, h=4)
    # shift only y64 floors; elevated 68 stays (shift only moves y=64 solids)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx02 elevated fork bridges @y68")


def build_sx03(dest):
    """Fog corridor + ≥2 sequential gate chambers (side-pass when gate wall blocks center)."""
    w = World(dest)
    common(w, WOOL_LIGHT_BLUE, PACKED_ICE)
    w.fill(14, 64, 26, 42, 69, 54, ANDESITE)
    w.fill(16, 64, 28, 40, 64, 52, PACKED_ICE)
    w.fill(16, 69, 28, 40, 69, 52, PACKED_ICE)
    w.fill(16, 65, 28, 40, 68, 52, AIR)
    # snow/ice accents
    for z in range(28, 53, 2):
        w.set(17, 65, z, SNOW)
        w.set(39, 65, z, SNOW)
    # Gate1 z=36: center ice wall, LEFT open chamber
    for yy in range(64, 69):
        w.fill(24, yy, 35, 40, yy, 37, PACKED_ICE)
    for yy in range(64, 69):
        w.fill(16, yy, 35, 23, yy, 37, AIR)
    w.fill(16, 64, 35, 23, 64, 37, PACKED_ICE)
    # Gate2 z=44: center wall, RIGHT open
    for yy in range(64, 69):
        w.fill(16, yy, 43, 32, yy, 45, ICE)
    for yy in range(64, 69):
        w.fill(33, yy, 43, 40, yy, 45, AIR)
    w.fill(33, 64, 43, 40, 64, 45, PACKED_ICE)
    carve_corridor(w, [(12, 31), (20, 31), (20, 36), (20, 40), (36, 40), (36, 44), (32, 52), (32, 56)], width=1, floor=PACKED_ICE, wall=WOOL_LIGHT_BLUE, y=64, h=4)
    for c in [(20, 36), (36, 44)]:
        pad(w, *c, floor=PACKED_ICE)
        w.set(c[0], 65, c[1], GLOW)
    # center plug
    for yy in range(64, 69):
        w.fill(26, yy, 38, 30, yy, 42, ANDESITE)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx03 frost dual gates ≥2")


def build_sx04(dest):
    """Square spiral around open well (DESIGN 螺旋下井) — no solid cylinder sealing doors."""
    w = World(dest)
    common(w, NETHERRACK, STONEBRICK)
    # open R2 hall (Director door path stays free)
    w.fill(14, 64, 26, 42, 64, 54, STONEBRICK)
    w.fill(14, 69, 26, 42, 69, 54, STONEBRICK)
    for yy in range(65, 69):
        w.fill(14, yy, 26, 14, yy, 54, NETHERRACK)
        w.fill(42, yy, 26, 42, yy, 54, NETHERRACK)
        w.fill(14, yy, 26, 42, yy, 26, NETHERRACK)
        w.fill(14, yy, 54, 42, yy, 54, NETHERRACK)
    w.fill(15, 65, 27, 41, 68, 53, AIR)
    carve_corridor(w, [(12, 31), (18, 31)], width=1, y=64, h=4)
    # central well pit (glass grate at y63 — visible depth, mobs cannot fall)
    w.fill(26, 54, 36, 34, 62, 44, AIR)
    w.fill(26, 53, 36, 34, 53, 44, MAGMA_B)
    w.fill(26, 63, 36, 34, 63, 44, GLASS)
    # square spiral ramps: 4 sides × descending, then climb out south to R3
    # top ring y64 around pit
    for x in range(24, 37):
        for z in (34, 46):
            w.set(x, 64, z, STONEBRICK)
            for yy in range(65, 68):
                w.set(x, yy, z, AIR)
    for z in range(34, 47):
        for x in (24, 36):
            w.set(x, 64, z, STONEBRICK)
            for yy in range(65, 68):
                w.set(x, yy, z, AIR)
    # descend east side 64→60
    for i in range(5):
        w.fill(36, 64 - i, 34 + i, 38, 64 - i, 36 + i, STONEBRICK)
        for yy in range(65 - i, 68 - i):
            w.fill(36, yy, 34 + i, 38, yy, 36 + i, AIR)
    # south side 60→56
    for i in range(5):
        w.fill(34 - i, 60 - i, 46, 36 - i, 60 - i, 48, STONEBRICK)
        for yy in range(61 - i, 64 - i):
            w.fill(34 - i, yy, 46, 36 - i, yy, 48, AIR)
    # west bottom pad y56
    w.fill(24, 56, 36, 28, 56, 44, STONEBRICK)
    pad(w, 26, 40, y=56)
    w.set(26, 57, 40, GLOW)
    # climb north then east back to y64 toward R3 door
    for i in range(8):
        w.fill(24, 56 + i, 34 - i // 2, 28, 56 + i, 36 - i // 2, STONEBRICK)
    carve_corridor(w, [(30, 48), (32, 52), (32, 56)], width=1, y=64, h=4)
    for c in [(30, 34), (36, 40), (30, 46), (26, 40)]:
        pad(w, *c)
        w.set(c[0], 65, c[1], GLOW)
    # R3 link already in common; reinforce
    carve_corridor(w, [(32, 56), (32, 64), (32, 72), (20, 80), (0, 90), (0, 98)], width=1, y=64, h=4)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx04 square spiral well (open pit, doors clear)")



def main():
    for name, fn in [
        ("ember_short_sx01", build_sx01),
        ("ember_short_sx02", build_sx02),
        ("ember_short_sx03", build_sx03),
        ("ember_short_sx04", build_sx04),
    ]:
        print(f"Building {name}…")
        fn(reset_from_raid(name))
    print("DONE sx01–sx04")


if __name__ == "__main__":
    main()
