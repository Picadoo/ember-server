#!/usr/bin/env python3
"""D449: light visual pass on Q04–Q07 — themed pillars/trim only.
Loads existing MCA via d441.World._ensure_chunk; never stamps void / never rewrites floors."""
from __future__ import annotations
import importlib.util, math, os
from pathlib import Path

HERE = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("d441", HERE / "d441_build_sx14_sx15_terrain.py")
d441 = importlib.util.module_from_spec(spec)
spec.loader.exec_module(d441)

MAP = Path("/workspace/minecraft/plugins/DungeonPlus/map")
PRISMARINE, SEA_LANTERN, WOOL_CYAN = 168, 169, (35, 9)
QUARTZ, GLOWSTONE, IRON_BARS = 155, 89, 101
PACKED_ICE, SNOW, WOOL_WHITE = 174, 80, (35, 0)
COBBLE, RAIL, REDSTONE_LAMP, FENCE_NETHER = 4, 66, 123, 113


def pillar(w, x, z, y0, h, body, cap=None):
    for dy in range(1, h + 1):
        w.set(x, y0 + dy, z, body)
    if cap is not None:
        w.set(x, y0 + h + 1, z, cap)


def decorate_q04(w):
    y = 64
    for x, z in [(-8, 20), (8, 20), (-8, 32), (8, 32),
                 (-40, 50), (-32, 50), (-40, 64), (-32, 64),
                 (8, 85), (20, 85), (8, 97), (20, 97)]:
        pillar(w, x, z, y, 4, PRISMARINE, SEA_LANTERN)
    for i in range(8):
        ang = i / 8 * 2 * math.pi
        pillar(w, int(round(0 + 10 * math.cos(ang))), int(round(129 + 10 * math.sin(ang))),
               y, 3, PRISMARINE, SEA_LANTERN)
    for z in (24, 90):
        for x in range(-6, 7, 3):
            w.set(x, y + 3, z, WOOL_CYAN)


def decorate_q05(w):
    for x, z, y in [
        (-6, 18, 64), (6, 18, 64), (-6, 28, 64), (6, 28, 64),
        (34, 48, 72), (46, 48, 72), (34, 58, 72), (46, 58, 72),
        (-10, 84, 80), (2, 84, 80), (-10, 94, 80), (2, 94, 80),
        (-8, 128, 80), (8, 128, 80), (-8, 138, 80), (8, 138, 80),
    ]:
        pillar(w, x, z, y, 5, QUARTZ, GLOWSTONE)
    for x, z, y in [(0, 40, 64), (40, 70, 72), (0, 110, 80)]:
        for dy in range(2, 5):
            w.set(x - 2, y + dy, z, IRON_BARS)
            w.set(x + 2, y + dy, z, IRON_BARS)


def decorate_q06(w):
    y = 64
    for x, z in [
        (-6, 18), (6, 18), (-6, 28), (6, 28),
        (34, 50), (46, 50), (34, 60), (46, 60),
        (34, 84), (46, 84), (34, 94), (46, 94),
        (-8, 120), (8, 120), (-8, 130), (8, 130),
    ]:
        pillar(w, x, z, y, 4, PACKED_ICE, SNOW)
    for z in (22, 88, 124):
        for x in (-4, 0, 4):
            w.set(x, y + 4, z, WOOL_WHITE)


def decorate_q07(w):
    y = 64
    for x, z in [
        (-6, 18), (6, 18), (-6, 28), (6, 28),
        (34, 50), (46, 50), (34, 60), (46, 60),
        (-8, 82), (4, 82), (-8, 92), (4, 92),
        (-8, 122), (8, 122), (-8, 132), (8, 132),
    ]:
        pillar(w, x, z, y, 4, COBBLE, REDSTONE_LAMP)
    for z in range(20, 100, 4):
        w.set(10, y + 1, z, RAIL)
        w.set(-10, y + 1, z, RAIL)
    for z in (24, 56, 88):
        for dy in range(2, 5):
            w.set(12, y + dy, z, FENCE_NETHER)
            w.set(-12, y + dy, z, FENCE_NETHER)


def main():
    jobs = [
        ("ember_daily_tide_v1", decorate_q04),
        ("ember_daily_spire_v1", decorate_q05),
        ("ember_daily_frost_v1", decorate_q06),
        ("ember_daily_rail_v1", decorate_q07),
    ]
    for name, fn in jobs:
        path = str(MAP / name)
        print(f"Decorating {name}…")
        w = d441.World(path)
        fn(w)
        w.save()
        print(f"  dirty regions={len(w.dirty)} saved")
    print("DONE D449 visual pass")


if __name__ == "__main__":
    main()
