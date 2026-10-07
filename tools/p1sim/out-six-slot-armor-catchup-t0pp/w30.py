import json, sys
sys.path.insert(0, '/workspace/minecraft/tools/p1sim')
import gear6diag
V = json.load(open('/workspace/d316/arms.json'))
nm, mode = sys.argv[1], sys.argv[2]
base = json.load(open('/workspace/d313/w30-%s.json' % mode))['base']
r = gear6diag.pooled(V[nm], 1200, mode)
json.dump(r, open('/workspace/d316/w30-%s-%s.json' % (nm, mode), 'w'))
pt, lo, hi, n = gear6diag.w30_paired(base, r)
print('%s %s W30 %.2f (base %.2f) Δ %+.2f [%+.2f, %+.2f] n=%d off@Q07 %.3f' % (nm, mode, r['W30'], base['W30'], pt, lo, hi, n, r['off0']), flush=True)
