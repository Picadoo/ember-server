#!/usr/bin/env python3
"""D446: real terrain sx10–sx13 (D441 World reuse)."""
from __future__ import annotations
import importlib.util, os

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("d441", os.path.join(HERE, "d441_build_sx14_sx15_terrain.py"))
d441 = importlib.util.module_from_spec(spec)
spec.loader.exec_module(d441)
World, reset_from_raid = d441.World, d441.reset_from_raid
shift_walk_level, carve_corridor = d441.shift_walk_level, d441.carve_corridor
AIR, STONEBRICK, ANDESITE, IRON_BARS = d441.AIR, d441.STONEBRICK, d441.ANDESITE, d441.IRON_BARS
GLOW, FENCE, QUARTZ = d441.GLOW, d441.FENCE, 155
WOOL_BLACK, WOOL_PURPLE, WOOL_LIME, WOOL_BROWN = (35, 15), (35, 10), (35, 5), (35, 12)
PLANKS = 5


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
    w.fill(10, 65, 28, 10, 68, 34, AIR)
    w.fill(18, 64, 56, 42, 64, 82, floor)
    w.fill(18, 69, 56, 42, 69, 82, floor)
    for yy in range(65, 69):
        w.fill(18, yy, 56, 18, yy, 82, wall)
        wfills = w.fill
        wfills(42, yy, 56, 42, yy, 82, wall)
        wfills(18, yy, 56, 42, yy, 56, wall)
        wfills(18, yy, 82, 42, yy, 82, wall)
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


def pad(w, x, z, floor=STONEBRICK, r=2, y=64):
    for dx in range(-r, r + 1):
        for dz in range(-r, r + 1):
            w.set(x + dx, y, z + dz, floor)
            for yy in range(y + 1, y + 4):
                w.set(x + dx, yy, z + dz, AIR)
            w.set(x + dx, y + 4, z + dz, floor)


def gate_wall(w, z, wall, open_x=(28, 34)):
    for yy in range(64, 70):
        w.fill(14, yy, z, 42, yy, z, wall)
    w.fill(open_x[0], 65, z, open_x[1], 68, z, AIR)
    for x in open_x:
        for yy in range(65, 69):
            w.set(x - 1 if x == open_x[0] else x + 1, yy, z, IRON_BARS)


def build_sx10(dest):
    """≥3 sealed gate chambers."""
    w = World(dest)
    common(w, WOOL_BLACK, STONEBRICK)
    w.fill(14, 64, 26, 42, 64, 54, STONEBRICK)
    w.fill(14, 69, 26, 42, 69, 54, STONEBRICK)
    for yy in range(65, 69):
        w.fill(14, yy, 26, 14, yy, 54, WOOL_BLACK)
        w.fill(42, yy, 26, 42, yy, 54, WOOL_BLACK)
        w.fill(14, yy, 26, 42, yy, 26, WOOL_BLACK)
        w.fill(14, yy, 54, 42, yy, 54, WOOL_BLACK)
    w.fill(15, 65, 27, 41, 68, 53, AIR)
    carve_corridor(w, [(12, 31), (30, 31)], width=1, y=64, h=4)
    for z in (32, 38, 44, 50):
        gate_wall(w, z, WOOL_BLACK)
    for z in (30, 35, 41, 47, 52):
        pad(w, 30, z)
        w.set(30, 65, z, GLOW)
    carve_corridor(w, [(30, 52), (32, 56)], width=1, y=64, h=4)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx10 ≥3 sealed gates")


def build_sx11(dest):
    """Symmetric L/R corridors → center mirror door."""
    w = World(dest)
    common(w, WOOL_PURPLE, STONEBRICK)
    w.fill(14, 64, 26, 42, 64, 54, STONEBRICK)
    w.fill(14, 69, 26, 42, 69, 54, STONEBRICK)
    for yy in range(65, 69):
        w.fill(14, yy, 26, 14, yy, 54, WOOL_PURPLE)
        w.fill(42, yy, 26, 42, yy, 54, WOOL_PURPLE)
        w.fill(14, yy, 26, 42, yy, 26, WOOL_PURPLE)
        w.fill(14, yy, 54, 42, yy, 54, WOOL_PURPLE)
    w.fill(15, 65, 27, 41, 68, 53, AIR)
    # center plug until "both sides" — solid mid wall with door at z=50
    w.fill(26, 64, 28, 30, 68, 48, ANDESITE)
    carve_corridor(w, [(12, 31), (20, 31), (20, 48), (30, 48), (30, 52)], width=1, y=64, h=4)  # left
    carve_corridor(w, [(12, 31), (36, 31), (36, 48), (30, 48)], width=1, y=64, h=4)  # right
    for c in [(20, 36), (20, 46), (36, 36), (36, 46), (30, 50)]:
        pad(w, *c)
        w.set(c[0], 65, c[1], GLOW)
    carve_corridor(w, [(30, 52), (32, 56)], width=1, y=64, h=4)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx11 L/R twin corridors")


def build_sx12(dest):
    """Hub + ≥2 radial side rooms."""
    w = World(dest)
    common(w, WOOL_LIME, STONEBRICK)
    w.fill(14, 64, 26, 42, 64, 54, STONEBRICK)
    w.fill(14, 69, 26, 42, 69, 54, STONEBRICK)
    for yy in range(65, 69):
        w.fill(14, yy, 26, 14, yy, 54, WOOL_LIME)
        w.fill(42, yy, 26, 42, yy, 54, WOOL_LIME)
        w.fill(14, yy, 26, 42, yy, 26, WOOL_LIME)
        w.fill(14, yy, 54, 42, yy, 54, WOOL_LIME)
    w.fill(15, 65, 27, 41, 68, 53, AIR)
    # hub center
    pad(w, 30, 40, r=3)
    w.set(30, 65, 40, GLOW)
    # west wing
    carve_corridor(w, [(30, 40), (18, 40), (18, 34)], width=1, y=64, h=4)
    pad(w, 18, 32, r=2)
    # east wing
    carve_corridor(w, [(30, 40), (40, 40), (40, 34)], width=1, y=64, h=4)
    pad(w, 40, 32, r=2)
    # north to R3 after hub
    carve_corridor(w, [(12, 31), (30, 31), (30, 40), (30, 52), (32, 56)], width=1, y=64, h=4)
    pad(w, 30, 48)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx12 hub + 2 wings")


def build_sx13(dest):
    """≥3 balance beams over void with counterweight ends."""
    w = World(dest)
    common(w, WOOL_BROWN, STONEBRICK)
    w.fill(14, 64, 26, 42, 64, 54, STONEBRICK)
    w.fill(14, 69, 26, 42, 69, 54, STONEBRICK)
    for yy in range(65, 69):
        w.fill(14, yy, 26, 14, yy, 54, WOOL_BROWN)
        w.fill(42, yy, 26, 42, yy, 54, WOOL_BROWN)
        w.fill(14, yy, 26, 42, yy, 26, WOOL_BROWN)
        w.fill(14, yy, 54, 42, yy, 54, WOOL_BROWN)
    w.fill(15, 65, 27, 41, 68, 53, AIR)
    carve_corridor(w, [(12, 31), (30, 31)], width=1, y=64, h=4)
    # three beams over void trenches
    for z0 in (34, 40, 46):
        w.fill(16, 60, z0, 40, 64, z0 + 2, AIR)
        # beam plank strip
        w.fill(22, 64, z0, 36, 64, z0 + 1, PLANKS)
        # counterweight ends
        w.fill(20, 64, z0 - 1, 21, 66, z0 + 2, ANDESITE)
        w.fill(37, 64, z0 - 1, 38, 66, z0 + 2, ANDESITE)
        w.set(29, 65, z0, GLOW)
    pad(w, 30, 32)
    pad(w, 30, 50)
    carve_corridor(w, [(30, 50), (32, 56)], width=1, y=64, h=4)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx13 ≥3 balance beams")


def main():
    for name, fn in [
        ("ember_short_sx10", build_sx10),
        ("ember_short_sx11", build_sx11),
        ("ember_short_sx12", build_sx12),
        ("ember_short_sx13", build_sx13),
    ]:
        print(f"Building {name}…")
        fn(reset_from_raid(name))
    print("DONE sx10–sx13")


if __name__ == "__main__":
    main()
