"""D15 / B2.151 follow-up: book maps → (1) CoreRpg resource p1-book-maps.yml (builder input),
(2) runs yml rooms / doors / points / boss / event for the switched maps, (3) DP dungeon options + config.
python3 gen.py q01 q02 ...   (maps to switch; q05 is hand-made in P1MapLayout.spireV1 and only written to the yml)"""
import json, re, sys, os
from book import parse, extra_points, ROOT
ROOFED = {'q02', 'q03'}
LOW_GUARD = {'q05': ['R2', 'RB'], 'q04': ['R1']}   # Q04 R1 双渠平台: edge beam + rail, channels visible
ROOFED_ROOMS = {'q06': ['R2']}                       # Q06 R2 军械仓 with a gable roof (decor kind 7)
# B2.181 / book §5 shapes. [kind, x0, x1, z0, z1, y0, y1, id, data]: 0 crenellated solid outside the cavity,
# 1 tube [1, cx, r, cz, 0, y0, y1], 3 block box anywhere (id:data), 6 cap on wall tops at y0 (id:data),
# 7 gable along z (ridge at the x centre) from y0 to y1 (id:data)
DK, BR, LOGX, LOGZ, PLANK, GLASS, GLOW, CAUL, WATER, SNOW, AIR = 162, 98, 5, 9, 5, 20, 89, 118, 9, 80, 0
DECOR = {
    'q01': [[3, -4, 4, 9, 10, 69, 70, DK, LOGX],                       # R0 门顶横梁 9 宽 2 厚，最高 F+6
            [3, -5, -5, 1, 1, 64, 64, CAUL, 0], [3, 5, 5, 1, 1, 64, 64, CAUL, 0],   # 熄灭火盆外形
            [3, -5, -5, 1, 1, 63, 63, GLOW, 0], [3, 5, 5, 1, 1, 63, 63, GLOW, 0],   # 灯源藏内
            [3, -10, -8, 23, 25, 64, 65, BR, 0], [3, 8, 10, 23, 25, 64, 65, BR, 0],  # R1 两处掩体（书坐标，高 2）
            [0, 43, 45, 42, 50, 62, 70],                                 # R2 东墙外兵营屋壳 9×6×3（避开 E 口 z35..41）
            [3, 15, 17, 51, 53, 62, 74, BR, 0], [3, 47, 49, 51, 53, 62, 74, BR, 0],   # R3 四角石柱：两根完整 F+10
            [3, 15, 17, 83, 85, 62, 69, BR, 2], [3, 47, 49, 83, 85, 62, 69, BR, 2]],  # 两根断裂（裂石砖，矮）
    'q02': [[3, -5, 5, 11, 12, 69, 70, DK, LOGX],                      # R0 入口横梁 11 宽 2 厚 F+6
            [1, -11, 1, 6, 0, 62, 76], [1, 11, 1, 6, 0, 62, 76],         # 左右炉管外壳 直径 3 贴外墙
            [0, 24, 32, 69, 72, 62, 68],                                 # R2 北墙外侧炉壳 9×5×4
            [3, 27, 29, 67, 67, 65, 67, GLASS, 0], [3, 27, 29, 68, 68, 65, 67, GLOW, 0],   # 发光炉口 3×3 隔玻璃
            [1, -16, 2, 80, 0, 62, 72], [1, -17, 2, 92, 0, 62, 72],      # R3 西墙外沉渣罐 直径 5 高 8，错位
            [0, -7, 7, 141, 149, 62, 82]],                               # RB 北墙外主炉 15×18×9
    # Q03 (D55: sunken, so everything is cut into the wall layers / ceilings, not outside shapes)
    'q03': ([[3, -13, -13, z, z, 61, 63, AIR, 0] for z in (24, 39)]                    # R1 碑龛 每 5 格 1 深（西墙避开 C12）
            + [[3, -14, -14, z, z, 61, 63, BR, 3] for z in (24, 39)]                   # 龛底石碑（錾制石砖）
            + [[3, 13, 13, z, z, 61, 63, AIR, 0] for z in (24, 29, 34, 39)]
            + [[3, 14, 14, z, z, 61, 63, BR, 3] for z in (24, 29, 34, 39)]
            + [[3, x, x, 43, 43, 61, 63, AIR, 0] for x in (-10, -5, 0, 5, 10)]
            + [[3, x, x, 44, 44, 61, 63, BR, 3] for x in (-10, -5, 0, 5, 10)]
            + [[3, x, x, 21, 21, 61, 63, AIR, 0] for x in (-10, -5, 5, 10)]           # 南墙避开 C01
            + [[3, x, x, 20, 20, 61, 63, BR, 3] for x in (-10, -5, 5, 10)]
            + [[3, -9, 9, 22, 42, 68, 68, AIR, 0], [3, -9, -7, 22, 42, 69, 70, BR, 0], [3, 7, 9, 22, 42, 69, 70, BR, 0],   # R1 阶梯拱顶
               [3, -6, 6, 22, 42, 69, 69, AIR, 0], [3, -6, -4, 22, 42, 70, 71, BR, 0], [3, 4, 6, 22, 42, 70, 71, BR, 0],   # 最低 F+8，
               [3, -3, 3, 22, 42, 70, 70, AIR, 0], [3, -3, 3, 22, 42, 71, 72, BR, 0],                                    # 最高 F+11
               [3, -9, 9, 20, 21, 66, 72, BR, 0], [3, -9, 9, 43, 44, 66, 72, BR, 0]]   # 拱顶两端山墙
            + [[3, 0, 0, z, z, 71, 71, GLOW, 0] for z in (24, 28, 32, 36, 40)]          # 拱顶灯
            + [[3, -43, -42, z0, z0 + 2, 57, 58, PLANK, 1] for z0 in (49, 53, 64, 68)]  # R2 西墙棺匣 3×2×2（嵌在墙里），
            + [[3, -41, -41, z0, z0 + 2, 57, 58, AIR, 0] for z0 in (49, 53, 64, 68)]    # 前面 1 格龛口；E 口 z57..63 不动
            + [[3, -10, 14, 76, 98, 64, 64, AIR, 0], [3, -10, 14, 76, 98, 66, 66, BR, 0]]   # R3 分节拱顶：抬高 1 格，
            + [[3, -10, 14, z, z, 64, 64, DK, LOGX] for z in (77, 82, 87, 92, 97)]       # 每 5 格一道横肋
            + [[3, 2, 2, z, z, 65, 65, GLOW, 0] for z in (79, 84, 89, 94)]
            + [[3, 17, 23, 78, 94, 49, 49, BR, 0], [3, 17, 23, 78, 94, 50, 59, AIR, 0],   # R3 东墙外更低的空墓库（不可进入）
               [3, 15, 16, 80, 82, 58, 60, GLASS, 0], [3, 15, 16, 90, 92, 58, 60, GLASS, 0]]   # 两处封死观察窗
            + [[3, x, x, z, z, 49, 49, GLOW, 0] for x in (18, 22) for z in (80, 86, 92)]
            + [[3, -6, 6, 139, 144, 55, 55, BR, 0], [3, -6, 6, 139, 144, 56, 62, AIR, 0],   # RB 北墙凹室（战斗边界外）
               [3, -5, 5, 141, 144, 56, 59, BR, 0], [3, -5, 5, 141, 144, 60, 60, BR, 3]]     # 石棺台 宽 11 高 5 深 4
            + [[3, x, x, z, z, 59, 61, AIR, 0] for x in (-17, 17) for z in (109, 137)]       # 四角灯龛
            + [[3, x, x, z, z, 59, 61, GLOW, 0] for x in (-18, 18) for z in (109, 137)]),
    'q04': [[3, -25, -21, 22, 46, 59, 62, WATER, 0], [3, 21, 25, 22, 46, 59, 62, WATER, 0],   # R1 两条水渠 水面 y62
            [0, -43, -29, 81, 85, 62, 74],                               # R2 北墙外泵机外壳 15×10×5
            [1, -38, 1, 83, 0, 74, 82], [1, -34, 1, 83, 0, 74, 82],      # 两根直径 3 管道
            [0, -12, 12, 161, 163, 62, 82]]                              # RB 闸门立面 25×18
           + [[3, x, x, 164, 165, 62, 84, BR, 0] for x in (-10, -6, -2, 2, 6, 10)],   # 六道竖向肋梁
    'q06': [[6, -21, 21, 17, 47, 73, 73, SNOW, 0],                      # R1 两侧墙顶厚雪
            [7, 26, 54, 50, 78, 73, 77, PLANK, 1],                       # R2 双坡屋顶 屋脊 F+13
            [0, 34, 46, 113, 114, 62, 72],                               # R3 北墙外垒盾墙 11×8（中部出口为通道，保持开口）
            [0, -8, 8, 157, 165, 62, 86],                                # RB 北哨楼 17×9×22
            [0, -12, -9, 157, 160, 62, 90], [0, 9, 12, 157, 160, 62, 90]],   # 左右方形塔肩
    'q07': [[3, -24, -20, 30, 34, 62, 75, BR, 0],                       # R1 西侧吊装柱 5×5
            [3, -20, -8, 31, 33, 74, 75, DK, LOGX],                      # 13 格横臂，下缘 F+10
            [3, 35, 45, 77, 77, 64, 69, PLANK, 1],                       # R2 远端 11×6 封闭货门立面
            [0, -14, -6, 113, 117, 62, 76],                              # R3 北墙外废压机 9×12×5
            [3, -27, -24, 118, 121, 62, 84, BR, 0], [3, 24, 27, 118, 121, 62, 84, BR, 0],     # RB 四根 4×4 支柱 高 20
            [3, -27, -24, 167, 170, 62, 84, BR, 0], [3, 24, 27, 167, 170, 62, 84, BR, 0],
            [3, -27, 27, 119, 120, 81, 82, DK, LOGX], [3, -27, 27, 168, 169, 81, 82, DK, LOGX],   # 31+ 跨大吊架，梁下 > F+16
            [3, -26, -25, 118, 170, 81, 82, DK, LOGZ], [3, 25, 26, 118, 170, 81, 82, DK, LOGZ]],
}
DATE = '2026-10-02'


def yml_list(v):
    return json.dumps(v).replace('"', '')


def resource(maps):
    lines = ['# GENERATED by tools/p1map/gen.py from docs/design-ember-v1.0-P1.md ch. 11-17 (do not edit by hand)', 'maps:']
    for k, m in maps.items():
        lines.append(f'  {k}:')
        lines.append(f'    name: {m["name"]}')
        lines.append(f'    template: {m["template"]}')
        lines.append(f'    base: {m["base"]}')
        lines.append(f'    roofed: {"true" if k in ROOFED else "false"}')
        lines.append(f'    low_guard: {yml_list(LOW_GUARD.get(k, []))}')
        lines.append(f'    roofed_rooms: {yml_list(ROOFED_ROOMS.get(k, []))}')
        lines.append(f'    decor: {yml_list(DECOR.get(k, []))}')
        lines.append(f'    spawn: {yml_list(m["spawn"])}')
        lines.append('    rooms:')
        for r in m['rooms']:
            lines.append('      - {id: %s, x0: %d, x1: %d, z0: %d, z1: %d, f: %d, h: %d}' % (r['id'], r['x0'], r['x1'], r['z0'], r['z1'], r['f'], r['h']))
        lines.append('    corridors:')
        for c in m['corridors']:
            lines.append('      - {id: %s, nodes: %s}' % (c['id'], yml_list(c['nodes'])))
    return '\n'.join(lines) + '\n'


def rooms_block(m):
    R = {r['id']: r for r in m['rooms']}
    door_of = {'R1': 'G2', 'R2': 'G3', 'R3': 'GB'}
    out = ['    rooms:']
    for rid in ('R1', 'R2', 'R3'):
        r = R[rid]; six = m['points'][rid]
        extra = extra_points(r, six, list(m['doors'].values()))
        pts = six + extra
        out.append(f'      {rid.lower()}:')
        out.append(f'        label: {r["label"]}')
        out.append(f'        trigger: {yml_list([r["x0"], r["f"] - 1, r["z0"], r["x1"], r["f"] + 4, r["z1"]])}')
        out.append(f'        points: {yml_list(pts)}')
        out.append(f'        door: {yml_list(m["doors"][door_of[rid]])}')
    return out


def patch_runs(text, k, m):
    q = text.index(f'\n  {k}:\n') + 1
    nxt = re.search(r'\n  q0\d:\n', text[q + 5:])
    e = q + 5 + nxt.start() + 1 if nxt else len(text)
    blk = text[q:e]
    lines = blk.split('\n')
    R = {r['id']: r for r in m['rooms']}
    old_rooms = {}
    # keep a / b compositions per room from the existing block
    cur = None
    for ln in lines:
        mm = re.match(r'^      (r[1-3]):$', ln)
        if mm: cur = mm.group(1); old_rooms[cur] = {}; continue
        if cur and re.match(r'^        [ab]: ', ln): old_rooms[cur][ln.strip()[0]] = ln
        if re.match(r'^    \S', ln): cur = None
    res, skip = [], False
    i = 0
    while i < len(lines):
        ln = lines[i]
        top = re.match(r'^    ([a-z_]+):', ln)
        if top:
            key = top.group(1)
            skip = False
            if key in ('clear', 'links', 'rails'):
                skip = True; i += 1
                # drop the comment lines right above as well
                while res and res[-1].startswith('    #'): res.pop()
                continue
            if key == 'template': ln = f'    template: {m["template"]}'
            if key == 'map_version': ln = f'    map_version: {m["template"]}@d15-{DATE}'
            if key == 'spawn':
                sx, sy, sz = m['spawn']
                while res and res[-1].startswith('    #'): res.pop()
                res.append(f'    # D15: book ch. {10 + int(k[1:])} walkable white box ({m["template"]}, /corerpg p1 mapbuild {k})')
                res.append(f'    spawn: {yml_list(m["spawn"])}')
                res.append(f'    spread: {yml_list([[sx - 2, sy, sz], [sx + 2, sy, sz]])}')
                i += 1
                continue
            if key == 'spread': i += 1; continue
            if key == 'rooms':
                rb = rooms_block(m)
                # re-insert a / b
                outb = []
                for x in rb:
                    outb.append(x)
                    mm = re.match(r'^        door: ', x)
                    if mm:
                        rid = outb[-5].strip().rstrip(':')
                        for ab in ('a', 'b'):
                            if ab in old_rooms.get(rid, {}): outb.append(old_rooms[rid][ab])
                res.extend(outb)
                i += 1
                while i < len(lines) and (lines[i].startswith('      ') or lines[i].startswith('    #') and False): i += 1
                continue
        if skip:
            if ln.startswith('      ') or ln.startswith('    #') or ln.strip() == '':
                if ln.strip() == '': skip = False; res.append(ln)
                i += 1; continue
            skip = False
        res.append(ln); i += 1
    blk = '\n'.join(res)
    rb = R['RB']
    bx, by, bz = m['boss']
    blk = re.sub(r'(\n      at: )\[[^\]]*\]\n      area: \[[^\]]*\]\n', lambda _: f'\n      at: {yml_list(m["boss"])}\n      area: {yml_list([rb["x0"], rb["f"] - 1, rb["z0"], rb["x1"], rb["f"] + min(rb["h"], 10), rb["z1"]])}\n      wait_in_area: true\n', blk, count=1)
    # boss adds (Q03): two points left / right of the boss
    blk = re.sub(r'(\n        # book: boss x±8[^\n]*)?(\n        points: )\[\[[^\n]*\]\](\n    event:)', lambda mm: f'\n        # book: boss x±8, z−4 (D15 hall){mm.group(2)}{yml_list([[bx - 8, by, bz - 4], [bx + 8, by, bz - 4]])}{mm.group(3)}', blk)
    E = R['E']
    blk = re.sub(r'\n    event:\n      after: \S+\n      anchor: \[[^\]]*\]\n      area: \[[^\]]*\]', f'\n    event:\n      after: r2\n      anchor: {yml_list(m["event"])}\n      area: {yml_list([E["x0"], E["f"] - 1, E["z0"], E["x1"], E["f"] + 6, E["z1"]])}', blk)
    return text[:q] + blk + text[e:]


def patch_safe_holo(text, k, m):
    """§9 / §20.5: book safe points + hologram anchors, inserted after spread: (replaced when present)."""
    q = text.index(f'\n  {k}:\n') + 1
    nxt = re.search(r'\n  q0\d:\n', text[q + 5:])
    e = q + 5 + nxt.start() + 1 if nxt else len(text)
    blk = re.sub(r'    # §9 safe points[^\n]*\n    safe: [^\n]*\n    holo: [^\n]*\n', '', text[q:e])
    sf = ', '.join(f'{r}: {yml_list(m["safe"][r])}' for r in ('r0', 'r1', 'r2', 'r3', 'rb'))
    hl = ', '.join(f'{h}: {yml_list(m["holo"][h])}' for h in ('entry', 'event', 'exit'))
    ins = (f'    # §9 safe points (reconnect ≤ 120 s → last cleared room; boss alive → rb) and hologram anchors (book ch. {10 + int(k[1:])})\n'
           f'    safe: {{{sf}}}\n    holo: {{{hl}}}\n')
    blk = re.sub(r'(\n    spread: [^\n]*\n)', lambda mm: mm.group(1) + ins, blk, count=1)
    return text[:q] + blk + text[e:]


def patch_dp(k, m):
    d = 'EmberQ' + k[1:]
    p = os.path.join(ROOT, 'plugins/DungeonPlus/dungeon', d, 'option.yml')
    s = open(p, encoding='utf-8').read()
    sx, sy, sz = m['spawn']
    s = re.sub(r'\$setmap\{name=[a-z_]+\}', '$setmap{name=%s}' % m['template'], s)
    s = re.sub(r'\$setspawn\{location=[-\d,]+\}', '$setspawn{location=%d,%d,%d}' % (sx, sy, sz), s)
    s = re.sub(r'\$teleport\{location=[-\d,]+;defspawn=true\}', '$teleport{location=%d,%d,%d;defspawn=true}' % (sx, sy, sz), s)
    s = re.sub(r'复用 \S+ 地图模板；', '地图 %s（D15：书第 %d 章白盒，可步行，首领厅按书尺寸）；' % (m['template'], 10 + int(k[1:])), s, count=1)
    s = '\n'.join(l for l in s.split('\n') if not (l.startswith('# ') and ('clear:' in l or 'rails' in l or '告示牌只在实例内' in l)))
    open(p, 'w', encoding='utf-8').write(s)
    c = os.path.join(ROOT, 'plugins/DungeonPlus/config.yml')
    s = open(c, encoding='utf-8').read()
    s = re.sub(r'\n(\s*# [^\n]*\n)?\s*"%s": 1' % d, '', s, count=1)
    anchor = '  # ember-v1.0-P1 B2.151'
    s = s.replace(anchor, '  %s:\n    "%s": 1\n' % (m['template'], d) + anchor, 1)
    open(c, 'w', encoding='utf-8').write(s)


if __name__ == '__main__':
    maps = parse()
    open(os.path.join(ROOT, 'CoreRpg/src/main/resources/p1-book-maps.yml'), 'w', encoding='utf-8').write(resource(maps))
    runs = [os.path.join(ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml'), os.path.join(ROOT, 'plugins/CoreRpg/ember-v1-runs.yml')]
    t = open(runs[0], encoding='utf-8').read()
    args = sys.argv[1:]
    if args and args[0] == 'safe':
        for k in maps: t = patch_safe_holo(t, k, maps[k])
        args = []
    for k in args:
        t = patch_runs(t, k, maps[k])
        patch_dp(k, maps[k])
    for p in runs: open(p, 'w', encoding='utf-8').write(t)
