# D316 G0: base + t0p_A dynamic runs with the new code vs D313 t0p-a.pkl (same seeds, CRN), field by field
import os, sys, json, pickle
sys.path.insert(0, '/workspace/minecraft/tools/p1sim')
import gear6
A = json.load(open('/workspace/d316/arms.json'))['t0p_A']
R = pickle.load(open('/workspace/d313/t0p-a.pkl', 'rb'))
n = bad = 0
for nm, six in (('base', None), ('t0p_A', A)):
    for d in (0.3, 0.5, 0.7):
        for s in range(1000, 1000 + int(sys.argv[1])):
            k, new = gear6._prog_job((nm, six, d, s))
            old = R[k]
            for m in old:
                for f, v in old[m].items():
                    w = new[m][f]
                    if f in ('armor',) and w is not None:
                        w = [tuple(x[:3]) for x in w]; v = [tuple(x) for x in v]
                    if f == 'charm_te' and w is not None:
                        w = tuple(w[:3]); v = tuple(v)
                    n += 1
                    if w != v:
                        bad += 1
                        if bad < 5: print('DIFF', k, m, f, v, w)
print('G0 prog fields compared %d · differing %d' % (n, bad))
