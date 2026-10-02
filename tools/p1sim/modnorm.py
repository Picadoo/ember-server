import sys, random
sys.path.insert(0, '.')
import p1sim as S, p1config, p2econ as E
cfg = p1config.load('current')
N = 1500
print('| 图 | dodge | 原样 | ' + ' | '.join(m['id'] for m in E.MODS) + ' |')
for k in cfg['order']:
    bt, be, ct, ce, lv = S.REF_GEAR[k]
    bl = dict(S.item('burst' if bt else 'none', 'blade', bt), enh=be)
    ch = dict(S.item('burst' if ct else 'none', 'charm', ct), enh=ce)
    st = S.stats(cfg, bl, ch, lv)
    for d in (0.3, 0.5):
        kn = S.Knobs(d)
        row = []
        for mod in [None] + E.MODS:
            c, cap = E.mod_cfg(cfg, k, mod)
            rng = random.Random(7)
            pots = kn.potion_keep if cap is None else min(cap, kn.potion_keep)
            row.append(sum(S.run_map(c, k, st, kn, rng, pots)[0] for _ in range(N)) / N)
        print('| %s | %.1f | %s |' % (k, d, ' | '.join('%d%%' % round(100 * x) for x in row)))
