# Tiny 1.12 (pre-flattening) Anvil top-down renderer: block colour + height shading + hillshade, markers and a label.
# usage (from a COPY of the world folder): python -c "import render_topdown as R; R.render('copy/ember_hub', cx, cz, r, 'out.png', 'label', [(x,z,'marker')])"  — needs pillow + NBT
import sys, os, zlib, struct, math, io
import nbt.nbt as N
from PIL import Image, ImageDraw, ImageFont
C = {1:(125,125,125),2:(95,159,53),3:(134,96,67),4:(110,110,110),5:(162,130,78),7:(60,60,60),8:(52,90,200),9:(52,90,200),10:(230,100,20),11:(230,100,20),
12:(219,207,163),13:(136,126,126),14:(143,140,125),15:(136,130,127),16:(115,115,115),17:(102,81,51),18:(60,120,40),19:(200,200,70),20:(200,230,240),
24:(216,203,155),35:(222,222,222),41:(249,236,78),42:(220,220,220),43:(160,160,160),44:(160,160,160),45:(150,97,83),47:(110,80,50),48:(90,110,90),49:(20,18,30),
50:(255,220,100),53:(162,130,78),54:(140,100,40),58:(120,80,40),61:(90,90,90),62:(120,90,70),67:(110,110,110),78:(250,250,250),79:(160,190,250),80:(250,250,250),
82:(160,166,179),85:(162,130,78),86:(220,130,20),87:(110,50,50),88:(84,64,51),89:(250,220,120),98:(122,122,122),101:(90,90,90),102:(200,230,240),103:(150,190,40),
108:(150,97,83),109:(122,122,122),112:(44,22,26),113:(44,22,26),114:(44,22,26),116:(60,30,60),121:(220,222,158),125:(162,130,78),126:(162,130,78),
133:(80,220,110),138:(120,220,220),139:(110,110,110),145:(70,70,70),152:(170,30,20),155:(236,233,226),156:(236,233,226),159:(160,110,90),161:(80,120,40),
162:(90,70,50),168:(100,170,150),169:(170,210,200),170:(200,170,20),171:(200,200,200),172:(150,92,66),174:(160,190,250),179:(180,100,40),
213:(200,80,20),214:(120,0,0),215:(110,20,20),251:(180,180,180),252:(180,180,180),31:(90,150,50),32:(120,90,40),37:(240,230,50),38:(220,40,40),
175:(90,150,50),81:(20,120,20),83:(140,190,90),111:(40,120,40),60:(110,70,40),63:(162,130,78),68:(162,130,78),65:(162,130,78),96:(162,130,78),
23:(110,110,110),25:(120,80,40),26:(180,40,40),64:(140,110,60),71:(200,200,200),107:(162,130,78),117:(200,200,100),118:(60,60,60),130:(40,40,50),
134:(110,85,50),135:(200,190,130),136:(150,110,70),163:(170,90,50),164:(70,45,25),180:(170,90,50),182:(170,90,50),183:(110,85,50),198:(230,230,230),
201:(170,125,170),206:(220,220,160),}
YMAX = 255
SKIP = {0, 166, 31, 175, 106, 30, 51}  # air, barrier(invisible), tall grass, vines, cobweb, fire
def region_chunks(path):
    with open(path, 'rb') as f: data = f.read()
    for i in range(1024):
        off = struct.unpack('>I', b'\0' + data[i*4:i*4+3])[0]
        if not off: continue
        o = off * 4096; ln = struct.unpack('>I', data[o:o+4])[0]; comp = data[o+4]
        raw = zlib.decompress(data[o+5:o+4+ln])
        yield N.NBTFile(buffer=io.BytesIO(raw))
def load(world, x0, z0, x1, z1):
    cols = {}
    for cx in range(x0 >> 4, (x1 >> 4) + 1):
        pass
    need = {(rx, rz) for rx in range(x0 >> 9, (x1 >> 9) + 1) for rz in range(z0 >> 9, (z1 >> 9) + 1)}
    for rx, rz in need:
        p = os.path.join(world, 'region', f'r.{rx}.{rz}.mca')
        if not os.path.exists(p): continue
        for ch in region_chunks(p):
            lv = ch['Level']; cx, cz = lv['xPos'].value, lv['zPos'].value
            if cx*16 > x1 or cx*16+15 < x0 or cz*16 > z1 or cz*16+15 < z0: continue
            secs = sorted(((s['Y'].value, bytes(s['Blocks'].value), bytes(s['Data'].value)) for s in lv['Sections']), reverse=True)
            for lx in range(16):
                for lz in range(16):
                    X, Z = cx*16+lx, cz*16+lz
                    if not (x0 <= X <= x1 and z0 <= Z <= z1): continue
                    found = None; water = 0
                    for (sy, bl, dt) in secs:
                        for ly in range(15, -1, -1):
                            if sy*16+ly > YMAX: continue
                            idx = ly*256 + lz*16 + lx; b = bl[idx]
                            if b in SKIP: continue
                            if b in (8, 9) and water < 6: water += 1; 
                            if b in (8, 9):
                                if found is None: found = [b, sy*16+ly, 0]
                                continue
                            d = (dt[idx >> 1] >> ((idx & 1) * 4)) & 15
                            if found is None: found = [b, sy*16+ly, 0, d]
                            elif found[0] in (8, 9): found[2] = water
                            if found[0] not in (8, 9) or found[2]: break
                        if found and (found[0] not in (8, 9) or found[2]): break
                    if found: cols[(X, Z)] = found
    return cols
WOOL = {0:(222,222,222),1:(235,125,50),2:(190,70,200),3:(100,140,220),4:(230,200,40),5:(90,190,40),6:(230,130,160),7:(65,65,65),8:(150,150,150),9:(40,120,150),
10:(120,40,180),11:(40,50,150),12:(90,55,30),13:(55,75,25),14:(160,40,35),15:(25,25,25)}
def colour(c):
    b = c[0]; d = c[3] if len(c) > 3 else 0
    if b in (35, 171, 159, 95, 160) : base = WOOL.get(d, (200, 200, 200)) if b != 159 else tuple(int(v*0.75) for v in WOOL.get(d, (160,110,90)))
    else: base = C.get(b, (255, 0, 255) if b not in C else C[b])
    if base == (255, 0, 255): base = (170, 150, 170)
    if b in (8, 9): k = max(0.55, 1 - 0.07 * c[2]); base = tuple(int(v*k) for v in base)
    return base
def render(world, cx, cz, r, out, label, marks=(), scale=None, bg=(24,24,28)):
    x0, z0, x1, z1 = cx - r, cz - r, cx + r, cz + r
    cols = load(world, x0, z0, x1, z1)
    w = x1 - x0 + 1; s = scale or max(1, 1200 // w)
    img = Image.new('RGB', (w*s, w*s + 40), bg); px = ImageDraw.Draw(img)
    ys = [v[1] for v in cols.values()] or [64]; ymin, ymax = min(ys), max(ys)
    for (X, Z), c in cols.items():
        base = colour(c); y = c[1]
        k = 0.8 + 0.35 * ((y - ymin) / max(1, ymax - ymin))
        n = cols.get((X, Z - 1)); w_ = cols.get((X - 1, Z))
        if n: k *= 1.12 if y > n[1] else (0.85 if y < n[1] else 1)
        if w_: k *= 1.06 if y > w_[1] else (0.92 if y < w_[1] else 1)
        col = tuple(max(0, min(255, int(v*k))) for v in base)
        px.rectangle([(X-x0)*s, (Z-z0)*s + 40, (X-x0)*s + s - 1, (Z-z0)*s + s - 1 + 40], fill=col)
    try: f = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 18); fs = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 14)
    except Exception: f = fs = ImageFont.load_default()
    px.rectangle([0, 0, w*s, 39], fill=(15, 15, 18)); px.text((10, 9), label + f'   |  N up, 1 block = {s}px, y {ymin}-{ymax}', fill=(255, 220, 120), font=f)
    for (mx, mz, t) in marks:
        X, Y = (mx - x0) * s + s // 2, (mz - z0) * s + s // 2 + 40
        px.ellipse([X-6, Y-6, X+6, Y+6], outline=(255, 40, 40), width=3)
        px.text((X+9, Y-9), t, fill=(255, 255, 255), font=fs, stroke_width=2, stroke_fill=(0, 0, 0))
    img.save(out); print(out, img.size, len(cols), 'cols')
