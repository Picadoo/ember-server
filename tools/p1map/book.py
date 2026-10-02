"""Parse the P1 book map chapters (docs/design-ember-v1.0-P1.md ch. 11-17) into structured layouts.
python3 book.py            -> prints JSON for all maps
"""
import json, re, sys, os
ROOT = os.path.join(os.path.dirname(__file__), '..', '..')
BOOK = os.path.join(ROOT, 'docs', 'design-ember-v1.0-P1.md')
TEMPLATES = {'q01': 'ember_daily', 'q02': 'ember_daily_ash', 'q03': 'ember_daily_crypt', 'q04': 'ember_daily_tide',
             'q05': 'ember_daily_spire', 'q06': 'ember_daily_frost', 'q07': 'ember_daily_rail'}
NUM = r'(-?\d+)'
PT = r'\(\s*' + NUM + r',\s*' + NUM + r',\s*' + NUM + r'\)'


def parse():
    text = open(BOOK, encoding='utf-8').read()
    out = {}
    chunks = re.split(r'^# (1[1-7]) (Q0\d) (\S+)：副本与地图制作任务书\s*$', text, flags=re.M)
    for i in range(1, len(chunks), 4):
        key, name, body = chunks[i + 1].lower(), chunks[i + 2], chunks[i + 3]
        body = body.split('\n# ')[0]
        m = {'key': key, 'name': name, 'template': TEMPLATES[key] + '_v1', 'base': TEMPLATES[key]}
        sp = re.search(r'入场点 ' + PT, body); bp = re.search(r'首领点 ' + PT, body)
        m['spawn'] = [int(v) for v in sp.groups()]; m['boss'] = [int(v) for v in bp.groups()]
        rooms = []
        for r in re.finditer(r'^\|(R[0-3B]|E) (\S+)\|' + NUM + r'\.\.' + NUM + r'\|' + NUM + r'\.\.' + NUM + r'\|' + NUM + r'／' + NUM + r'\|', body, re.M):
            g = r.groups()
            rooms.append({'id': g[0], 'label': g[1], 'x0': int(g[2]), 'x1': int(g[3]), 'z0': int(g[4]), 'z1': int(g[5]), 'f': int(g[6]), 'h': int(g[7])})
        m['rooms'] = rooms
        cors = []
        for c in re.finditer(r'^\|(C[0-9BE]+)\|([^|]+)\|7／6\|', body, re.M):
            nodes = [[int(a), int(b), int(cc)] for a, b, cc in re.findall(PT, c.group(2))]
            cors.append({'id': c.group(1), 'nodes': nodes})
        m['corridors'] = cors
        doors = {}
        for d in re.finditer(r'^\|(G[1-3B])\|' + PT + r'\|([xz]) 恒定\|', body, re.M):
            g = d.groups(); x, y, z = int(g[1]), int(g[2]), int(g[3])
            box = [x, y, z - 3, x, y + 5, z + 3] if g[4] == 'x' else [x - 3, y, z, x + 3, y + 5, z]
            doors[g[0]] = box
        m['doors'] = doors
        pts = {}
        for p in re.finditer(r'^\|(R[1-3])\|((?:' + PT + r'；?)+)\|', body, re.M):
            pts[p.group(1)] = [[int(a), int(b), int(c)] for a, b, c in re.findall(PT, p.group(2))]
        m['points'] = pts
        e = re.search(r'E 中心 ' + PT, body)
        m['event'] = [int(v) for v in e.groups()]
        out[key] = m
    return out


def extra_points(room, six, doors):
    """P7/P8 (D45 rule): inside the net range, >= 4 from any door face, not on a book point, 3 in from the walls."""
    cand = []
    for x in range(room['x0'] + 3, room['x1'] - 2):
        for z in range(room['z0'] + 3, room['z1'] - 2):
            if any(abs(x - p[0]) < 3 and abs(z - p[2]) < 3 for p in six): continue
            if any(b[0] - 4 <= x <= b[3] + 4 and b[2] - 4 <= z <= b[5] + 4 for b in doors): continue
            cand.append((x, z))
    cx = (room['x0'] + room['x1']) / 2
    # one on each side, as far from the centre line as the walls allow, near the middle in z
    cz = (room['z0'] + room['z1']) / 2
    left = sorted([c for c in cand if c[0] < cx], key=lambda c: (abs(c[1] - cz), c[0]))
    right = sorted([c for c in cand if c[0] > cx], key=lambda c: (abs(c[1] - cz), -c[0]))
    res = []
    for side in (left, right):
        if side: res.append([side[0][0], room['f'], side[0][1]])
    return res


if __name__ == '__main__':
    print(json.dumps(parse(), ensure_ascii=False, indent=1))
