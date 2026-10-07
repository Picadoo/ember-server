# D316 G1 (static 42 cells × 3 sets): builddiv.measure fixes the stats once (p1sim.stats with the scenario armor) and
# then fights with SIX = None, so identical stats ⇒ identical clear rates seed for seed. Check stats identity per cell.
import sys, json
sys.path.insert(0, '/workspace/minecraft/tools/p1sim')
import p1sim, builddiv
W = [0.8, 0.05, 0.05, 0.05, 0.05]
arms = {'F': {'w': W, 'follow': 'all'}, 'E': {'w': W, 'follow': 'enh'}, 'A(D313)': {'w': W}}
ctxs = [k + kind for k in p1sim.REF_GEAR for kind in 'rc']
print('| 臂 | 甲场景 | 与 2 槽 stats 逐位相同（14 情境 × 3 套 = %d；躲避不改 stats，覆盖 42 格 × 3 套） | H 变化（Q04r / Q07r / Q07c，burst） |' % (len(ctxs) * 3))
print('|---|---|---|---|')
for nm, six in arms.items():
    for sc in ('eq', 'enh1', 'tierdown', 'q0', 'f0'):
        same = 0; hs = {}
        for fam in ('burst', 'scorch', 'sustain'):
            for c in ctxs:
                cfg, key, bl, ch, lv, rep = builddiv.gear(c, {'set': fam})
                p1sim.SIX = None
                s2 = p1sim.stats(cfg, bl, ch, lv)
                p1sim.SIX = six
                s6 = p1sim.stats(cfg, bl, ch, lv, None, builddiv.armor_of({'six': six, 'armor': sc}, ch))
                p1sim.SIX = None
                same += all(s6[k] == s2[k] for k in ('B', 'H', 'M', 'set', 'awk'))
                if fam == 'burst' and c in ('q04r', 'q07r', 'q07c'):
                    hs[c] = 100 * (s6['H'] / s2['H'] - 1)
        print('| %s | %s | %d/%d | %+.2f%% / %+.2f%% / %+.2f%% |' % (nm, sc, same, len(ctxs) * 3, hs['q04r'], hs['q07r'], hs['q07c']))
