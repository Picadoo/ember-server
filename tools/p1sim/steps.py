import sys, random, collections
sys.path.insert(0, '.')
import p1sim as S, p1config
cfg = p1config.load('current')
def st(bt, be, ct, ce, lv, fam='burst'):
    bl = dict(S.item(fam if bt else 'none', 'blade', bt), enh=be)
    ch = dict(S.item(fam if ct else 'none', 'charm', ct), enh=ce)
    return S.stats(cfg, bl, ch, lv)
cases = [('q02', 'T1+0/T0', (1,0,0,0,11)), ('q02', 'T1+0/T1+0', (1,0,1,0,11)), ('q02', 'T1+3/T1+2', (1,3,1,2,13)), ('q02','T1+4/T1+3',(1,4,1,3,14)),
         ('q03', 'T1+4/T1+4', (1,4,1,4,15)), ('q03', 'T1+6/T1+5', (1,6,1,5,18)),
         ('q04', 'T1+6/T1+6', (1,6,1,6,20)), ('q04', 'T1+7/T1+7', (1,7,1,7,23)), ('q04','T1+9/T1+9',(1,9,1,9,24))]
for d in (0.3, 0.5):
    for k, lab, g in cases:
        s = st(*g); kn = S.Knobs(d); rng = random.Random(3); N = 1500
        where = collections.Counter(); ok = 0; secs = []
        for _ in range(N):
            r = S.run_map(cfg, k, s, kn, rng, kn.potion_keep)
            ok += r[0]; secs.append(r[3]); where[r[5] or 'clear'] += 1
        print('d%.1f %s %-10s B%.1f H%.0f clear %3d%%  deaths %s' % (d, k, lab, s['B'], s['H'], round(100*ok/N),
              {w: '%d%%' % round(100*c/N) for w, c in where.items() if w != 'clear'}))
