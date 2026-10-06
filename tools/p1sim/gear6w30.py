#!/usr/bin/env python3
"""D246: farm-route W30 with large samples (gear6diag.pooled). Usage:
  python3 gear6w30.py '<json {name: SIX | null}>' [PLAYERS] [MODE base|rot] > out.json
Prints one line per variant (W30, the two half-sample W30s, week-12 share, off-family share at the Q07 first clear / week 12)."""
import json, sys
import gear6diag

if __name__ == '__main__':
    V = json.loads(sys.argv[1])
    n = int(sys.argv[2]) if len(sys.argv) > 2 else 1200
    mode = sys.argv[3] if len(sys.argv) > 3 else 'base'
    out = {}
    for name, six in V.items():
        r = gear6diag.pooled(six, n, mode)
        out[name] = r
        print('%-10s %s n=%d W30 %s (halves %s) w12 %.3f off@Q07 %.3f off@w12 %.3f T3tgt2@w12 %.3f rej_curoff %.2f' % (
            name, mode, r['n'], r['W30'] and round(r['W30'], 2), [h and round(h, 2) for h in r['W30_halves']], r['w12'],
            r['off0'], r['off12'], r['tgt12'], r['rej_curoff']), file=sys.stderr, flush=True)
    if 'base' in out and out['base']['W30']:
        for name, r in out.items():
            if name != 'base' and r['W30']:
                pt, lo, hi, n2 = gear6diag.w30_paired(out['base'], r)
                print('%-10s ΔW30 vs base %+.2f wk, paired 95%% CI [%+.2f, %+.2f] (n=%d)' % (name, pt, lo, hi, n2),
                      file=sys.stderr, flush=True)
    json.dump(out, sys.stdout)
