#!/usr/bin/env python3
"""D442: themed Anvil terrain for sx16–sx19 (shadow / tide / prism / string).
Reuses D441 World helpers. Floors at y=64 then shift_walk_level → y=63.
"""
from __future__ import annotations
import importlib.util, os, sys

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("d441", os.path.join(HERE, "d441_build_sx14_sx15_terrain.py"))
d441 = importlib.util.module_from_spec(spec)
spec.loader.exec_module(d441)

World = d441.World
reset_from_raid = d441.reset_from_raid
shift_walk_level = d441.shift_walk_level
carve_corridor = d441.carve_corridor
AIR, STONEBRICK, ANDESITE = d441.AIR, d441.STONEBRICK, d441.ANDESITE
IRON_BARS, MAGMA, GLOW, FENCE = d441.IRON_BARS, d441.MAGMA, d441.GLOW, d441.FENCE
TORCH = d441.TORCH

# 1.12 extras
GLASS = 20
STAINED_GLASS_BLACK = (95, 15)  # black
STAINED_GLASS_GRAY = (95, 7)
STAINED_GLASS_CYAN = (95, 9)
STAINED_GLASS_PURPLE = (95, 10)
PRISMARINE = 168
SEA_LANTERN = 169
PACKED_ICE = 174
WATER = 9
NOTE = 25
WOOL_BLACK = (35, 15)
WOOL_CYAN = (35, 9)
WOOL_PURPLE = (35, 10)
QUARTZ = 155


def common_shell(w: World, accent_wall, accent_floor=None):
    af = accent_floor or STONEBRICK
    w.set_flat_void_level()
    w.stamp_void_box(-24, -8, 56, 128, y_bed=60)
    # R0 spawn
    w.fill(-6, 64, 0, 6, 64, 14, af)
    w.fill(-6, 69, 0, 6, 69, 14, af)
    for yy in range(65, 69):
        w.fill(-6, yy, 0, -6, yy, 14, accent_wall)
        w.fill(6, yy, 0, 6, yy, 14, accent_wall)
        w.fill(-6, yy, 0, 6, yy, 0, accent_wall)
    w.fill(-3, 65, 14, 3, 68, 14, AIR)
    w.set(0, 65, 5, GLOW)
    # R1 courtyard
    w.fill(-10, 64, 16, 10, 64, 36, af)
    w.fill(-10, 69, 16, 10, 69, 36, af)
    for yy in range(65, 69):
        w.fill(-10, yy, 16, -10, yy, 36, accent_wall)
        w.fill(10, yy, 16, 10, yy, 36, accent_wall)
        w.fill(-10, yy, 16, 10, yy, 16, accent_wall)
        w.fill(-10, yy, 36, 10, yy, 36, accent_wall)
    w.fill(10, 65, 28, 10, 68, 34, AIR)
    # R3 hall
    w.fill(18, 64, 56, 42, 64, 82, af)
    w.fill(18, 69, 56, 42, 69, 82, af)
    for yy in range(65, 69):
        w.fill(18, yy, 56, 18, yy, 82, accent_wall)
        w.fill(42, yy, 56, 42, yy, 82, accent_wall)
        w.fill(18, yy, 56, 42, yy, 56, accent_wall)
        w.fill(18, yy, 82, 42, yy, 82, accent_wall)
    w.fill(19, 65, 57, 41, 68, 81, AIR)
    carve_corridor(w, [(20, 80), (8, 80), (8, 90), (0, 90), (0, 98)], width=1, floor=af, wall=accent_wall, y=64, h=4)
    # Boss arena + niches
    w.fill(-14, 64, 88, 14, 64, 116, af)
    w.fill(-14, 74, 88, 14, 74, 116, af)
    for yy in range(65, 74):
        w.fill(-14, yy, 88, -14, yy, 116, accent_wall)
        w.fill(14, yy, 88, 14, yy, 116, accent_wall)
        w.fill(-14, yy, 88, 14, yy, 88, accent_wall)
        w.fill(-14, yy, 116, 14, yy, 116, accent_wall)
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


def r2_solid(w, wall):
    w.fill(14, 64, 26, 42, 69, 56, wall)


def pad(w, x, z, floor=STONEBRICK, r=2):
    for dx in range(-r, r + 1):
        for dz in range(-r, r + 1):
            w.set(x + dx, 64, z + dz, floor)
            for yy in range(65, 69):
                w.set(x + dx, yy, z + dz, AIR)
            w.set(x + dx, 69, z + dz, floor)


def build_sx16(dest):
    """≥3 staggered shadow-wall side passes (cannot go straight)."""
    w = World(dest)
    common_shell(w, WOOL_BLACK, STONEBRICK)
    r2_solid(w, ANDESITE)
    # open full R2 floor then place 3 offset shadow walls
    w.fill(16, 64, 28, 40, 64, 54, STONEBRICK)
    w.fill(16, 69, 28, 40, 69, 54, STONEBRICK)
    w.fill(16, 65, 28, 40, 68, 54, AIR)
    # Wall1 z=34: block left/center, open RIGHT x=34-39
    w.fill(16, 64, 33, 33, 68, 35, STAINED_GLASS_BLACK)
    w.fill(16, 65, 33, 33, 68, 35, STAINED_GLASS_BLACK)
    for yy in range(64, 69):
        w.fill(16, yy, 33, 33, yy, 35, STAINED_GLASS_BLACK)
    # Wall2 z=40: block right/center, open LEFT x=16-24
    for yy in range(64, 69):
        w.fill(25, yy, 39, 40, yy, 41, STAINED_GLASS_GRAY)
    # Wall3 z=46: block left/center, open RIGHT
    for yy in range(64, 69):
        w.fill(16, yy, 45, 33, yy, 47, STAINED_GLASS_BLACK)
    # re-open side passes + combat pads
    for x in range(34, 40):
        for z in (34, 40, 46):
            pass
    # openings air
    for yy in range(64, 69):
        w.fill(34, yy, 33, 39, yy, 35, AIR)  # pass1
        w.fill(16, yy, 39, 24, yy, 41, AIR)  # pass2
        w.fill(34, yy, 45, 39, yy, 47, AIR)  # pass3
    # floors under openings
    w.fill(34, 64, 33, 39, 64, 35, STONEBRICK)
    w.fill(16, 64, 39, 24, 64, 41, STONEBRICK)
    w.fill(34, 64, 45, 39, 64, 47, STONEBRICK)
    # entry from R1 + exit north
    # continuous walk: R1 → right pass1 → cross to left pass2 → cross to right pass3 → R3
    carve_corridor(w, [(12, 31), (36, 31), (36, 34), (36, 37), (20, 37), (20, 40), (20, 43), (36, 43), (36, 46), (32, 52), (32, 56)], width=1, y=64, h=4)
    for c in [(36, 34), (20, 40), (36, 46)]:
        pad(w, *c)
        w.set(c[0], 65, c[1], GLOW)
        w.set(c[0], 64, c[1], WOOL_BLACK)
    # block straight center (leave east/west corridors at x≈20 and x≈36 free)
    for yy in range(64, 69):
        w.fill(27, yy, 37, 29, yy, 43, ANDESITE)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx16 shadow walls ≥3 at z=34/40/46 side-pass")


def build_sx17(dest):
    """≥2 tide gates: center high-tide wall, side ebb channel."""
    w = World(dest)
    common_shell(w, WOOL_CYAN, PRISMARINE)
    r2_solid(w, ANDESITE)
    w.fill(16, 64, 28, 40, 64, 54, PRISMARINE)
    w.fill(16, 69, 28, 40, 69, 54, PRISMARINE)
    w.fill(16, 65, 28, 40, 68, 54, AIR)
    # Gate1 z=36: center packed-ice wall, LEFT ebb open
    for yy in range(64, 70):
        w.fill(24, yy, 35, 38, yy, 37, PACKED_ICE)
    for yy in range(64, 69):
        w.fill(16, yy, 35, 23, yy, 37, AIR)
    w.fill(16, 64, 35, 23, 64, 37, PRISMARINE)
    # water accent beside gate (still)
    for x in range(24, 38):
        w.set(x, 64, 34, WATER)
        w.set(x, 64, 38, WATER)
    # Gate2 z=44: center wall, RIGHT ebb open
    for yy in range(64, 70):
        w.fill(16, yy, 43, 32, yy, 45, PACKED_ICE)
    for yy in range(64, 69):
        w.fill(33, yy, 43, 40, yy, 45, AIR)
    w.fill(33, 64, 43, 40, 64, 45, PRISMARINE)
    for x in range(16, 32):
        w.set(x, 64, 42, WATER)
        w.set(x, 64, 46, WATER)
    carve_corridor(w, [(12, 31), (20, 31), (20, 36), (20, 40), (36, 40), (36, 44), (32, 52), (32, 56)], width=1, floor=PRISMARINE, y=64, h=4)
    for c in [(20, 36), (36, 44)]:
        pad(w, *c, floor=PRISMARINE)
        w.set(c[0], 65, c[1], SEA_LANTERN)
    w.set(30, 65, 36, SEA_LANTERN)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx17 tide gates ≥2 at z=36/44")


def build_sx18(dest):
    """≥3 crystal prism corners (glass walls, zigzag ≠ sx14 coords)."""
    w = World(dest)
    common_shell(w, STAINED_GLASS_PURPLE, QUARTZ)
    r2_solid(w, ANDESITE)
    path = [
        (12, 32),
        (24, 32),   # CORNER1 → north
        (24, 32),
        (24, 48),   # CORNER2 → east
        (38, 48),   # CORNER3 → north
        (38, 54),
        (32, 54),
    ]
    carve_corridor(w, path, width=1, floor=QUARTZ, wall=STAINED_GLASS_CYAN, y=64, h=4)
    # thicken glass walls along path
    corners = [(24, 32), (24, 48), (38, 48)]
    for x, z in corners:
        pad(w, x, z, floor=QUARTZ)
        w.set(x, 64, z, STAINED_GLASS_PURPLE)
        w.set(x, 65, z, GLOW)
        for yy in range(65, 69):
            w.set(x + 2, yy, z, STAINED_GLASS_CYAN)
            w.set(x - 2, yy, z, STAINED_GLASS_CYAN)
    # block center shortcut
    w.fill(26, 64, 34, 36, 68, 46, ANDESITE)
    # re-open legs
    for z in range(32, 49):
        for dx in (-1, 0, 1):
            w.set(24 + dx, 64, z, QUARTZ)
            for yy in range(65, 69):
                w.set(24 + dx, yy, z, AIR)
    for x in range(24, 39):
        for dz in (-1, 0, 1):
            w.set(x, 64, 48 + dz, QUARTZ)
            for yy in range(65, 69):
                w.set(x, yy, 48 + dz, AIR)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx18 prism corners", corners)


def build_sx19(dest):
    """≥2 string/resonance nodes: barrier + side resonance door."""
    w = World(dest)
    common_shell(w, WOOL_PURPLE, STONEBRICK)
    r2_solid(w, ANDESITE)
    w.fill(16, 64, 28, 40, 64, 54, STONEBRICK)
    w.fill(16, 69, 28, 40, 69, 54, STONEBRICK)
    w.fill(16, 65, 28, 40, 68, 54, AIR)
    # Node1 z=36: fence/iron-bar wall across, resonance open LEFT + note block
    for yy in range(64, 69):
        w.fill(22, yy, 35, 40, yy, 37, IRON_BARS)
    for yy in range(64, 69):
        w.fill(16, yy, 35, 21, yy, 37, AIR)
    w.fill(16, 64, 35, 21, 64, 37, STONEBRICK)
    w.set(18, 65, 36, NOTE)
    w.set(18, 66, 36, GLOW)
    # Node2 z=46: barrier, resonance open RIGHT
    for yy in range(64, 69):
        w.fill(16, yy, 45, 34, yy, 47, FENCE)
    for yy in range(65, 68):
        w.fill(16, yy, 45, 34, yy, 47, IRON_BARS)
    for yy in range(64, 69):
        w.fill(35, yy, 45, 40, yy, 47, AIR)
    w.fill(35, 64, 45, 40, 64, 47, STONEBRICK)
    w.set(38, 65, 46, NOTE)
    w.set(38, 66, 46, GLOW)
    carve_corridor(w, [(12, 31), (18, 31), (18, 36), (18, 41), (38, 41), (38, 46), (32, 52), (32, 56)], width=1, y=64, h=4)
    for c in [(18, 36), (38, 46)]:
        pad(w, *c)
    # center blocked (keep side resonance corridors free)
    for yy in range(64, 69):
        w.fill(26, yy, 39, 30, yy, 43, ANDESITE)
    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx19 string nodes ≥2 at z=36/46")


def main():
    for name, fn in [
        ("ember_short_sx16", build_sx16),
        ("ember_short_sx17", build_sx17),
        ("ember_short_sx18", build_sx18),
        ("ember_short_sx19", build_sx19),
    ]:
        print(f"Building {name}…")
        dest = reset_from_raid(name)
        fn(dest)
    print("DONE all sx16–sx19")


if __name__ == "__main__":
    main()
