#!/usr/bin/env python3
"""D318 六槽 T1-2 · Java ↔ p1sim same-parameter cross-check export (offline; no balance change, no default-run change).

  python3 six_t1_export.py OUT.tsv.gz ['<SIX json>']     # default SIX = the signed F arm (follow all, charm cost ×1.0)

Writes one row per cell, every float as float.hex() (exact):
  header  '#cfg <json>'  — the tables p1sim used (p1config.load(): live yml + Java constants), floats as hex
  rows    arm fam bt ct be ce q f lv aq0 aq1 aq2 aq3 af0 af1 af2 af3 empty B H M D
arm g6  = the T0″ G6 grid (3 fam × blade T0–3 × charm T0–3 × blade enh {0,3,6,10} × charm enh 0–10 × q 0–3 × f 0–3 ×
          Lv {1,15,30} = 101,376), armor = the charm's quality / craft (migration invariant);
arm var = the same 101,376 cells with seeded armor quality / craft 0–3 per slot (often above the charm's) and ~1/5 empty
          slots (`empty` bit mask; p1sim sees an empty slot as a q0 f0 piece, Java gets null).
Java side: CoreRpg EmberSixP1simTest reads this file, builds EmberTables from the header and compares B / H / M / D with
Double.doubleToLongBits (exact, no tolerance). p1sim has no "no charm" state (a player always holds at least the T0
charm), so the no-charm cells are Java-only (six-slot vs 2-slot) in that test.
"""
import gzip
import itertools
import json
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config  # noqa: E402
import p1sim  # noqa: E402

F_ARM = {"w": [0.8, 0.05, 0.05, 0.05, 0.05], "drop": 1, "start": "q01", "up_all": True,
         "cost": {"charm": 1.0, "armor": 0.05}, "arm_cap": True, "round": "c1a0", "follow": "all"}
FAMS = ('burst', 'scorch', 'sustain')


def hx(v):
    return float(v).hex()


def cfg_header(cfg, six):
    keys = ('A', 'h', 'D', 'e', 'q', 'f', 'lvl_atk', 'lvl_hp', 'lvl_base', 'lvl_cap', 'def_k', 'def_floor', 'sustain_hp')
    out = {}
    for k in keys:
        v = cfg[k]
        out[k] = [hx(x) for x in v] if isinstance(v, (list, tuple)) else (v if isinstance(v, int) and k in ('lvl_base', 'lvl_cap') else hx(v))
    out['w'] = [hx(x) for x in six['w']]
    out['follow'] = six.get('follow')
    return out


def main(path, six):
    cfg = p1config.load()
    assert six.get('follow') == 'all', 'T1 is the F arm (follow all)'
    rnd = random.Random(318)
    n = 0
    with gzip.open(path, 'wt', encoding='ascii', compresslevel=6) as out:
        out.write('#cfg ' + json.dumps(cfg_header(cfg, six), sort_keys=True) + '\n')
        for arm in ('g6', 'var'):
            for fam, bt, ct, be, ce, q, f, lv in itertools.product(FAMS, range(4), range(4), (0, 3, 6, 10), range(11), range(4), range(4), (1, 15, 30)):
                bl = dict(p1sim.item(fam if bt else 'none', 'blade', bt), enh=be)
                ch = dict(p1sim.item(fam if ct else 'none', 'charm', ct), enh=ce, q=q, f=f)
                if arm == 'g6':
                    aq, af, empty = [q] * 4, [f] * 4, 0
                else:
                    aq, af, empty = [], [], 0
                    for i in range(4):
                        if rnd.random() < 0.2:
                            empty |= 1 << i
                            aq.append(0); af.append(0)
                        else:
                            aq.append(rnd.randrange(4)); af.append(rnd.randrange(4))
                armor = [dict(ch, slot=s, q=aq[i], f=af[i]) for i, s in enumerate(p1sim.ARMOR_SLOTS)]
                p1sim.SIX = six
                try:
                    st = p1sim.stats(cfg, bl, ch, lv, None, armor)
                    _, d = p1sim.hp_def(cfg, ch, armor)
                finally:
                    p1sim.SIX = None
                out.write('%s\t%s\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%s\t%s\t%d\t%s\t%s\t%s\t%s\n' % (
                    arm, fam, bt, ct, be, ce, q, f, lv, '\t'.join(map(str, aq)), '\t'.join(map(str, af)), empty,
                    hx(st['B']), hx(st['H']), hx(st['M']), hx(d)))
                n += 1
    print('six_t1_export: %d rows → %s (SIX %s)' % (n, path, json.dumps(six, sort_keys=True)))


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print(__doc__)
        sys.exit(2)
    main(sys.argv[1], json.loads(sys.argv[2]) if len(sys.argv) > 2 else F_ARM)
