#!/usr/bin/env python3
"""D441: build themed terrain for ember_short_sx14 (≥3 forced corners) and sx15 (spiral ≥2 turns).
Writes 1.12.2 Anvil MCA under plugins/DungeonPlus/map/ (gitignore). No WorldEdit required.
"""
from __future__ import annotations
import io, os, shutil, struct, zlib, copy
from collections import defaultdict
from nbt import nbt
from anvil import Region

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
MAP_ROOT = os.path.join(ROOT, "plugins", "DungeonPlus", "map")
RAID = os.path.join(MAP_ROOT, "ember_raid")

# --- block ids (1.12) ---
AIR, STONE, COBBLE, STONEBRICK = 0, 1, 4, 98
ANDESITE, IRON_BARS, MAGMA, GLOW = (1, 5), 101, 213, 89  # andesite = id1 data5
BRICK_WALL, FENCE, TORCH = 139, 85, 50
SLAB_SB, STAIR_SB = (44, 5), 109  # stonebrick slab
RED_NETHER, OBSIDIAN = 215, 49


class World:
    def __init__(self, path: str):
        self.path = path
        self.regions: dict[tuple[int, int], dict] = {}  # (rx,rz) -> {(lcx,lcz): NBTFile}
        self.dirty: set[tuple[int, int]] = set()
        os.makedirs(os.path.join(path, "region"), exist_ok=True)
        # template chunk from raid
        tr = Region.from_file(os.path.join(RAID, "region", "r.0.0.mca"))
        buf = io.BytesIO()
        tr.chunk_data(0, 0).write_file(buffer=buf)
        self.template_bytes = buf.getvalue()

    def _ensure_chunk(self, cx: int, cz: int):
        rx, rz = cx >> 5, cz >> 5
        lcx, lcz = cx & 31, cz & 31
        if (rx, rz) not in self.regions:
            self.regions[(rx, rz)] = {}
            p = os.path.join(self.path, "region", f"r.{rx}.{rz}.mca")
            if os.path.exists(p):
                r = Region.from_file(p)
                for i in range(32):
                    for j in range(32):
                        try:
                            cd = r.chunk_data(i, j)
                            if cd is not None:
                                self.regions[(rx, rz)][(i, j)] = cd
                        except Exception:
                            pass
        bag = self.regions[(rx, rz)]
        if (lcx, lcz) not in bag:
            bag[(lcx, lcz)] = self._blank_chunk(cx, cz)
            self.dirty.add((rx, rz))
        return bag[(lcx, lcz)]

    def _blank_chunk(self, cx: int, cz: int):
        raw = nbt.NBTFile(buffer=io.BytesIO(self.template_bytes))
        level = raw["Level"]
        level["xPos"].value = cx
        level["zPos"].value = cz
        level["Entities"] = nbt.TAG_List(name="Entities", type=nbt.TAG_Compound)
        level["TileEntities"] = nbt.TAG_List(name="TileEntities", type=nbt.TAG_Compound)
        # clear all sections to air
        for s in level["Sections"]:
            s["Blocks"].value = bytes(4096)
            s["Data"].value = bytes(2048)
            s["BlockLight"].value = bytes(2048)
            s["SkyLight"].value = bytes([0xFF] * 2048)
        # ensure sections Y=3..6 exist (48-111)
        have = {s["Y"].value for s in level["Sections"]}
        for y in range(3, 8):
            if y not in have:
                sec = nbt.TAG_Compound()
                sec.tags.append(nbt.TAG_Byte(name="Y", value=y))
                sec.tags.append(nbt.TAG_Byte_Array(name="Blocks"))
                sec["Blocks"].value = bytes(4096)
                sec.tags.append(nbt.TAG_Byte_Array(name="Data"))
                sec["Data"].value = bytes(2048)
                sec.tags.append(nbt.TAG_Byte_Array(name="BlockLight"))
                sec["BlockLight"].value = bytes(2048)
                sec.tags.append(nbt.TAG_Byte_Array(name="SkyLight"))
                sec["SkyLight"].value = bytes([0xFF] * 2048)
                level["Sections"].append(sec)
        return raw

    def _section(self, raw, sy: int):
        level = raw["Level"]
        for s in level["Sections"]:
            if s["Y"].value == sy:
                return s
        sec = nbt.TAG_Compound()
        sec.tags.append(nbt.TAG_Byte(name="Y", value=sy))
        for name, n in [("Blocks", 4096), ("Data", 2048), ("BlockLight", 2048), ("SkyLight", 2048)]:
            sec.tags.append(nbt.TAG_Byte_Array(name=name))
            sec[name].value = bytes([0xFF] * n) if name == "SkyLight" else bytes(n)
        level["Sections"].append(sec)
        return sec

    def set(self, x: int, y: int, z: int, block):
        if isinstance(block, tuple):
            bid, meta = block
        else:
            bid, meta = block, 0
        cx, cz = x >> 4, z >> 4
        raw = self._ensure_chunk(cx, cz)
        sy = y >> 4
        sec = self._section(raw, sy)
        lx, ly, lz = x & 15, y & 15, z & 15
        i = ly * 256 + lz * 16 + lx
        blocks = bytearray(sec["Blocks"].value)
        data = bytearray(sec["Data"].value)
        blocks[i] = bid & 0xFF
        if i % 2 == 0:
            data[i // 2] = (data[i // 2] & 0xF0) | (meta & 0xF)
        else:
            data[i // 2] = (data[i // 2] & 0x0F) | ((meta & 0xF) << 4)
        sec["Blocks"].value = bytes(blocks)
        sec["Data"].value = bytes(data)
        self.dirty.add((cx >> 5, cz >> 5))

    def fill(self, x1, y1, z1, x2, y2, z2, block):
        xa, xb = sorted((x1, x2))
        ya, yb = sorted((y1, y2))
        za, zb = sorted((z1, z2))
        for x in range(xa, xb + 1):
            for y in range(ya, yb + 1):
                for z in range(za, zb + 1):
                    self.set(x, y, z, block)

    def box(self, x1, y1, z1, x2, y2, z2, wall, fill=None):
        """Hollow box: walls+floor+ceil; optional interior fill (usually AIR)."""
        self.fill(x1, y1, z1, x2, y1, z2, wall)  # floor
        self.fill(x1, y2, z1, x2, y2, z2, wall)  # ceil
        self.fill(x1, y1, z1, x1, y2, z2, wall)
        self.fill(x2, y1, z1, x2, y2, z2, wall)
        self.fill(x1, y1, z1, x2, y2, z1, wall)
        self.fill(x1, y1, z2, x2, y2, z2, wall)
        if fill is not None and y2 - y1 > 1 and x2 - x1 > 1 and z2 - z1 > 1:
            self.fill(x1 + 1, y1 + 1, z1 + 1, x2 - 1, y2 - 1, z2 - 1, fill)

    def save(self):
        for (rx, rz) in self.dirty:
            bag = self.regions[(rx, rz)]
            path = os.path.join(self.path, "region", f"r.{rx}.{rz}.mca")
            locations = bytearray(4096)
            timestamps = bytearray(4096)
            data_blob = bytearray()
            sector_index = 2
            for (lcx, lcz), nbtfile in bag.items():
                buf = io.BytesIO()
                nbtfile.write_file(buffer=buf)
                compressed = zlib.compress(buf.getvalue())
                payload = struct.pack(">I", len(compressed) + 1) + bytes([2]) + compressed
                pad = (4096 - (len(payload) % 4096)) % 4096
                payload += b"\x00" * pad
                nsectors = len(payload) // 4096
                if nsectors > 255:
                    raise RuntimeError(f"chunk too big {lcx},{lcz}")
                loc_index = 4 * (lcx + lcz * 32)
                locations[loc_index : loc_index + 3] = struct.pack(">I", sector_index)[1:]
                locations[loc_index + 3] = nsectors
                data_blob.extend(payload)
                sector_index += nsectors
            with open(path, "wb") as f:
                f.write(locations)
                f.write(timestamps)
                f.write(data_blob)
            print(f"  wrote {path} chunks={len(bag)} size={os.path.getsize(path)}")

    def ensure_chunk_air(self, cx: int, cz: int):
        """Materialize chunk as all-air (no vanilla gen later)."""
        self._ensure_chunk(cx, cz)

    def stamp_void_box(self, x1, z1, x2, z2, y_bed=60):
        """Pre-create every chunk in bbox as air + thin bedrock slab so nothing generates."""
        xa, xb = sorted((x1, x2))
        za, zb = sorted((z1, z2))
        for cx in range(xa >> 4, (xb >> 4) + 1):
            for cz in range(za >> 4, (zb >> 4) + 1):
                self.ensure_chunk_air(cx, cz)
        # bedrock raft under playable area (fast section fill)
        for x in range(xa, xb + 1):
            for z in range(za, zb + 1):
                self.set(x, y_bed, z, 7)  # bedrock

    def set_flat_void_level(self):
        """Force superflat void so unloaded holes stay empty."""
        import gzip
        lp = os.path.join(self.path, "level.dat")
        try:
            f = nbt.NBTFile(filename=lp)
        except Exception:
            with gzip.open(lp, "rb") as gz:
                f = nbt.NBTFile(buffer=gz)
        d = f["Data"]
        d["generatorName"].value = "flat"
        d["generatorOptions"].value = "3;minecraft:air;127;"
        d["MapFeatures"].value = 0
        # write gzip compressed like vanilla
        buf = io.BytesIO()
        f.write_file(buffer=buf)
        with gzip.open(lp, "wb") as gz:
            gz.write(buf.getvalue())
        print("  level.dat → flat void")



def reset_from_raid(dest_name: str) -> str:
    """Fresh map: keep level.dat from raid, empty region/ (no leftover shell floors)."""
    dest = os.path.join(MAP_ROOT, dest_name)
    if os.path.exists(dest):
        shutil.rmtree(dest)
    os.makedirs(os.path.join(dest, "region"), exist_ok=True)
    for name in ("level.dat", "level.dat_old", "uid.dat", "session.lock"):
        src = os.path.join(RAID, name)
        if os.path.exists(src):
            shutil.copy2(src, os.path.join(dest, name))
    return dest


def carve_corridor(w: World, points, width=1, floor=STONEBRICK, wall=ANDESITE, y=63, h=4):
    """Connect polyline with walkable corridor; walls beside."""
    def densify(a, b):
        x1, z1 = a
        x2, z2 = b
        steps = max(abs(x2 - x1), abs(z2 - z1), 1)
        out = []
        for i in range(steps + 1):
            t = i / steps
            out.append((int(round(x1 + (x2 - x1) * t)), int(round(z1 + (z2 - z1) * t))))
        return out

    cells = []
    for i in range(len(points) - 1):
        cells.extend(densify(points[i], points[i + 1]))
    seen = set()
    for x, z in cells:
        if (x, z) in seen:
            continue
        seen.add((x, z))
        for dx in range(-width, width + 1):
            for dz in range(-width, width + 1):
                w.set(x + dx, y, z + dz, floor)
                for yy in range(y + 1, y + h):
                    w.set(x + dx, yy, z + dz, AIR)
                w.set(x + dx, y + h, z + dz, floor)  # ceiling
        # side walls (outer ring)
        for dx in range(-width - 1, width + 2):
            for dz in range(-width - 1, width + 2):
                if abs(dx) <= width and abs(dz) <= width:
                    continue
                # only if not already floor path neighbor interior
                if (x + dx, z + dz) in seen:
                    continue
                for yy in range(y, y + h + 1):
                    cur = wall
                    w.set(x + dx, yy, z + dz, cur)



def shift_walk_level(w: World, x1, z1, x2, z2):
    """MC feet at Y stand on block Y-1. Our builds put floors at 64; shift solids 64→63 and air out 64."""
    from anvil import Region
    # operate via w.set using known build: for each xz if we set 64 solid types, move down
    solids = {STONEBRICK, COBBLE, 7, MAGMA, ANDESITE[0] if isinstance(ANDESITE,tuple) else ANDESITE}
    # brute: readback not available — instead re-set from a second pass list
    # Use region mutation: for all dirty chunks, swap
    for (rx, rz), bag in w.regions.items():
        for (lcx, lcz), raw in bag.items():
            level = raw["Level"]
            cx = (rx << 5) + lcx
            cz = (rz << 5) + lcz
            # get section y=4 (64-79)
            sec4 = None
            sec3 = None
            for s in level["Sections"]:
                if s["Y"].value == 4: sec4 = s
                if s["Y"].value == 3: sec3 = s
            if sec4 is None:
                continue
            if sec3 is None:
                sec3 = w._section(raw, 3)
            b4 = bytearray(sec4["Blocks"].value)
            d4 = bytearray(sec4["Data"].value)
            b3 = bytearray(sec3["Blocks"].value)
            d3 = bytearray(sec3["Data"].value)
            changed = False
            for ly in range(16):  # within sec4, ly=0 is world y=64
                wy = 64 + ly
                if wy != 64:
                    continue
                for lz in range(16):
                    for lx in range(16):
                        i = ly * 256 + lz * 16 + lx
                        bid = b4[i]
                        if bid == 0:
                            continue
                        # move to y=63 → sec3 ly=15
                        i3 = 15 * 256 + lz * 16 + lx
                        b3[i3] = bid
                        # copy nibble
                        if i % 2 == 0:
                            meta = d4[i // 2] & 0x0F
                        else:
                            meta = (d4[i // 2] >> 4) & 0x0F
                        if i3 % 2 == 0:
                            d3[i3 // 2] = (d3[i3 // 2] & 0xF0) | meta
                        else:
                            d3[i3 // 2] = (d3[i3 // 2] & 0x0F) | (meta << 4)
                        b4[i] = 0
                        if i % 2 == 0:
                            d4[i // 2] = d4[i // 2] & 0xF0
                        else:
                            d4[i // 2] = d4[i // 2] & 0x0F
                        changed = True
            if changed:
                sec4["Blocks"].value = bytes(b4)
                sec4["Data"].value = bytes(d4)
                sec3["Blocks"].value = bytes(b3)
                sec3["Data"].value = bytes(d3)
                w.dirty.add((rx, rz))

def rift_seams(w: World, points, y=64):
    """Place magma/iron-bar 'rift crack' accents along outer corners."""
    for i, (x, z) in enumerate(points):
        if i % 2 == 0:
            w.set(x, y + 1, z, MAGMA)
            w.set(x, y + 2, z, IRON_BARS)


def build_sx14(dest: str):
    """Forced ≥3 corners zigzag rift corridor."""
    w = World(dest)
    w.set_flat_void_level()
    w.stamp_void_box(-24, -8, 56, 128, y_bed=60)
    # clear build volume to air first (avoid raid leftover walkable shortcuts)
    # fresh empty regions — no raid-shell clear needed

    # --- R0 spawn / 裂门垫 ---
    w.fill(-6, 64, 0, 6, 64, 14, STONEBRICK)
    w.fill(-6, 69, 0, 6, 69, 14, STONEBRICK)
    for yy in range(65, 69):
        w.fill(-6, yy, 0, -6, yy, 14, ANDESITE)
        w.fill(6, yy, 0, 6, yy, 14, ANDESITE)
        w.fill(-6, yy, 0, 6, yy, 0, ANDESITE)
    # open north into R1
    w.fill(-3, 65, 14, 3, 68, 14, AIR)
    for x in range(-4, 5):
        w.set(x, 65, 5, TORCH) if x % 2 == 0 else None
    w.set(0, 65, 5, GLOW)

    # --- R1 裂门庭 ---
    w.fill(-10, 64, 16, 10, 64, 36, STONEBRICK)
    w.fill(-10, 69, 16, 10, 69, 36, STONEBRICK)
    for yy in range(65, 69):
        w.fill(-10, yy, 16, -10, yy, 36, ANDESITE)
        w.fill(10, yy, 16, 10, yy, 36, ANDESITE)
        w.fill(-10, yy, 16, 10, yy, 16, ANDESITE)
        w.fill(-10, yy, 36, 10, yy, 36, ANDESITE)
    # east door only (matches door x≈16)
    w.fill(10, 65, 28, 10, 68, 34, AIR)
    # rift seam accents on floor
    for z in (20, 24, 28, 32):
        w.set(-8, 64, z, MAGMA)
        w.set(8, 64, z, MAGMA)
        w.set(-8, 65, z, IRON_BARS)
        w.set(8, 65, z, IRON_BARS)

    # --- R2 折裂廊: ≥3 forced corners (cannot go straight) ---
    # Path polyline (centerline), width=1 → 3-block corridor
    path = [
        (12, 31),   # leave R1 east door
        (22, 31),   # east
        (22, 31),   # CORNER1 stay
        (36, 31),   # east to far wall — CORNER1 turn north at end
        (36, 44),   # CORNER2 turn west
        (22, 44),   # CORNER3 turn north
        (22, 54),   # to R2 north door
        (32, 54),   # jog east to door center (29-35)
    ]
    # Fill solid wall volume for R2 then carve path
    w.fill(14, 64, 26, 42, 69, 56, ANDESITE)  # solid fill
    carve_corridor(w, path, width=1, floor=STONEBRICK, wall=ANDESITE, y=64, h=4)
    # extra corner markers (visible ≥90° turns)
    corners = [(22, 31), (36, 31), (36, 44), (22, 44)]
    for x, z in corners:
        w.set(x, 64, z, MAGMA)
        w.set(x, 65, z, GLOW)
        w.set(x + 1, 65, z, IRON_BARS)
        w.set(x - 1, 65, z, IRON_BARS)
        # combat pad (still must approach via corridor — pads sit ON path corners)
        for dx in range(-2, 3):
            for dz in range(-2, 3):
                w.set(x + dx, 64, z + dz, STONEBRICK)
                for yy in range(65, 69):
                    w.set(x + dx, yy, z + dz, AIR)
                w.set(x + dx, 69, z + dz, STONEBRICK)
    # block center shortcut (between east/west legs) so path still ≥3 corners
    w.fill(24, 64, 34, 34, 68, 41, ANDESITE)
    # re-open only the north-south leg cells at x=36 and west leg at z=44 / east at z=31
    for z in range(31, 45):
        for dx in (-1, 0, 1):
            w.set(36 + dx, 64, z, STONEBRICK)
            for yy in range(65, 69):
                w.set(36 + dx, yy, z, AIR)
    for x in range(22, 37):
        for dz in (-1, 0, 1):
            w.set(x, 64, 44 + dz, STONEBRICK)
            for yy in range(65, 69):
                w.set(x, yy, 44 + dz, AIR)
    for x in range(22, 37):
        for dz in (-1, 0, 1):
            w.set(x, 64, 31 + dz, STONEBRICK)
            for yy in range(65, 69):
                w.set(x, yy, 31 + dz, AIR)

    # --- R3 裂冠甬道 ---
    w.fill(18, 64, 56, 42, 64, 82, STONEBRICK)
    w.fill(18, 69, 56, 42, 69, 82, STONEBRICK)
    for yy in range(65, 69):
        w.fill(18, yy, 56, 18, yy, 82, ANDESITE)
        w.fill(42, yy, 56, 42, yy, 82, ANDESITE)
        w.fill(18, yy, 56, 42, yy, 56, ANDESITE)
        w.fill(18, yy, 82, 42, yy, 82, ANDESITE)
    # clear interior
    w.fill(19, 65, 57, 41, 68, 81, AIR)
    # west door toward boss (door ~18,91)
    w.fill(18, 65, 78, 18, 68, 82, AIR)
    # connector west+north to boss arena
    carve_corridor(
        w,
        [(20, 80), (8, 80), (8, 90), (0, 90), (0, 98)],
        width=1,
        floor=STONEBRICK,
        wall=ANDESITE,
        y=64,
        h=4,
    )

    # --- Boss 裂冠终厅 + side niches for Cast dodge ---
    w.fill(-14, 64, 88, 14, 64, 116, STONEBRICK)
    w.fill(-14, 74, 88, 14, 74, 116, STONEBRICK)
    for yy in range(65, 74):
        w.fill(-14, yy, 88, -14, yy, 116, ANDESITE)
        w.fill(14, yy, 88, 14, yy, 116, ANDESITE)
        w.fill(-14, yy, 88, 14, yy, 88, ANDESITE)
        w.fill(-14, yy, 116, 14, yy, 116, ANDESITE)
    w.fill(-13, 65, 89, 13, 73, 115, AIR)
    # side niches (±10)
    for side in (-12, 12):
        w.fill(side - 1 if side > 0 else side, 64, 98, side if side > 0 else side + 1, 64, 106, STONEBRICK)
        for yy in range(65, 70):
            w.set(side, yy, 98, AIR)
            w.set(side, yy, 102, AIR)
            w.set(side, yy, 106, AIR)
        w.set(side, 65, 102, GLOW)
    # boss pad
    w.fill(-2, 64, 100, 2, 64, 104, (1, 6))  # polished andesite
    w.set(0, 65, 102, GLOW)
    # entry from south
    w.fill(-2, 65, 88, 2, 68, 88, AIR)

    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print("sx14 corners forced at", corners)


def build_sx15(dest: str):
    """Spiral ramp ≥2 full turns around hollow atrium."""
    import math

    w = World(dest)
    w.set_flat_void_level()
    w.stamp_void_box(-24, -8, 56, 128, y_bed=60)

    # R0 spawn
    w.fill(-6, 64, 0, 6, 64, 14, STONEBRICK)
    w.fill(-6, 69, 0, 6, 69, 14, STONEBRICK)
    for yy in range(65, 69):
        w.fill(-6, yy, 0, -6, yy, 14, ANDESITE)
        w.fill(6, yy, 0, 6, yy, 14, ANDESITE)
        w.fill(-6, yy, 0, 6, yy, 0, ANDESITE)
    w.fill(-3, 65, 14, 3, 68, 14, AIR)

    # R1 坡门庭
    w.fill(-10, 64, 16, 10, 64, 36, STONEBRICK)
    w.fill(-10, 69, 16, 10, 69, 36, STONEBRICK)
    for yy in range(65, 69):
        w.fill(-10, yy, 16, -10, yy, 36, ANDESITE)
        w.fill(10, yy, 16, 10, yy, 36, ANDESITE)
        w.fill(-10, yy, 16, 10, yy, 16, ANDESITE)
        w.fill(-10, yy, 36, 10, yy, 36, ANDESITE)
    w.fill(10, 65, 28, 10, 68, 34, AIR)  # east to spiral

    # --- R2 spiral atrium center (30, 40), radius 7-9, ≥2 turns, rise 64→72 ---
    cx, cz = 30, 40
    # solid outer cylinder shell then carve spiral
    for x in range(cx - 12, cx + 13):
        for z in range(cz - 12, cz + 13):
            dist = math.hypot(x - cx, z - cz)
            if dist <= 11.5:
                for yy in range(64, 78):
                    w.set(x, yy, z, ANDESITE)
    # hollow core (cannot cut through)
    for x in range(cx - 4, cx + 5):
        for z in range(cz - 4, cz + 5):
            if math.hypot(x - cx, z - cz) <= 4.2:
                for yy in range(64, 78):
                    w.set(x, yy, z, AIR)
                w.set(x, 63, z, (1, 5))  # andesite pit floor visual

    turns = 2.25
    steps = 180
    r_inner, r_outer = 5.0, 9.0
    for i in range(steps + 1):
        t = i / steps
        ang = t * turns * 2 * math.pi
        y = int(64 + t * 8)  # 64 → 72
        for rad in (r_inner, (r_inner + r_outer) / 2, r_outer - 0.5):
            x = int(round(cx + rad * math.cos(ang)))
            z = int(round(cz + rad * math.sin(ang)))
            w.set(x, y, z, STONEBRICK)
            w.set(x, y + 1, z, AIR)
            w.set(x, y + 2, z, AIR)
            w.set(x, y + 3, z, AIR)
            # rail
            if i % 3 == 0:
                w.set(x, y + 1, z, FENCE)
    # mid-height combat ring (room points ~30,64,39 — raise local floor pocket + stairs)
    for ang_i in range(0, 360, 10):
        import math as _m
        rad = 8.0
        x = int(round(cx + rad * _m.cos(_m.radians(ang_i))))
        z = int(round(cz + rad * _m.sin(_m.radians(ang_i))))
        w.set(x, 64, z, STONEBRICK)
        for yy in range(65, 68):
            w.set(x, yy, z, AIR)
    # small pad at (30,39) for Director points
    for dx in range(-3, 4):
        for dz in range(-3, 4):
            w.set(30 + dx, 64, 39 + dz, STONEBRICK)
            for yy in range(65, 69):
                w.set(30 + dx, yy, 39 + dz, AIR)
    # entry bridge from R1 into spiral at angle 0
    carve_corridor(w, [(12, 31), (20, 31), (22, 36), (25, 40)], width=1, y=64, h=4)
    # exit at top (~2.25 turns) toward R3
    ang_end = turns * 2 * math.pi
    ex = int(round(cx + 7 * math.cos(ang_end)))
    ez = int(round(cz + 7 * math.sin(ang_end)))
    ey = 72
    carve_corridor(w, [(ex, ez), (ex + 4, ez + 6), (32, 54)], width=1, floor=STONEBRICK, wall=ANDESITE, y=ey, h=4)
    # stair down ledge to R3 floor
    for s in range(8):
        w.fill(30, 72 - s, 54 + s, 34, 72 - s, 55 + s, STONEBRICK)

    # R3 旋冠甬道 at y64
    w.fill(18, 64, 56, 42, 64, 82, STONEBRICK)
    w.fill(18, 69, 56, 42, 69, 82, STONEBRICK)
    for yy in range(65, 69):
        w.fill(18, yy, 56, 18, yy, 82, ANDESITE)
        w.fill(42, yy, 56, 42, yy, 82, ANDESITE)
        w.fill(18, yy, 56, 42, yy, 56, ANDESITE)
        w.fill(18, yy, 82, 42, yy, 82, ANDESITE)
    w.fill(19, 65, 57, 41, 68, 81, AIR)
    carve_corridor(w, [(20, 80), (8, 80), (8, 90), (0, 90), (0, 98)], width=1, y=64, h=4)

    # Boss 旋冠终厅 + inner side platforms
    w.fill(-14, 64, 88, 14, 64, 116, STONEBRICK)
    w.fill(-14, 74, 88, 14, 74, 116, STONEBRICK)
    for yy in range(65, 74):
        w.fill(-14, yy, 88, -14, yy, 116, ANDESITE)
        w.fill(14, yy, 88, 14, yy, 116, ANDESITE)
        w.fill(-14, yy, 88, 14, yy, 88, ANDESITE)
        w.fill(-14, yy, 116, 14, yy, 116, ANDESITE)
    w.fill(-13, 65, 89, 13, 73, 115, AIR)
    for side in (-12, 12):
        w.set(side, 65, 102, GLOW)
        for yy in range(65, 70):
            w.set(side, yy, 100, AIR)
            w.set(side, yy, 102, AIR)
            w.set(side, yy, 104, AIR)
    w.fill(-2, 64, 100, 2, 64, 104, (1, 6))
    w.set(0, 65, 102, GLOW)
    w.fill(-2, 65, 88, 2, 68, 88, AIR)

    shift_walk_level(w, -20, -5, 50, 120)
    w.save()
    print(f"sx15 spiral center=({cx},{cz}) turns={turns} exit=({ex},{ez},y~{ey})")


def main():
    print("Building sx14…")
    d14 = reset_from_raid("ember_short_sx14")
    build_sx14(d14)
    print("Building sx15…")
    d15 = reset_from_raid("ember_short_sx15")
    build_sx15(d15)
    print("DONE")


if __name__ == "__main__":
    main()
