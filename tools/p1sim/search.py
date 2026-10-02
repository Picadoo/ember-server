import sys, json
import p1config, tune
base = p1config.load()
ov = {}
grid = [1.0, 0.95, 0.9, 0.85, 0.8, 0.75, 0.7, 0.65, 0.6, 0.55, 0.5]
mode = sys.argv[1] if len(sys.argv) > 1 else 'both'
chosen = {}
for i, k in enumerate(['q02', 'q03', 'q04', 'q05', 'q06', 'q07']):
    for f in grid:
        if mode == 'both':
            o = tune.scale(base, k, hp=f, atk=f)
        elif mode == 'hp':
            o = tune.scale(base, k, hp=f)
        else:
            o = tune.scale(base, k, atk=f)
        cfg = tune.apply(base, dict(ov, **o))
        rows = tune.run(cfg, 80)
        r = rows[i + 1]
        fr = r['front_rate'] or 0
        print(k, f, 'front %.0f%%' % (100 * fr), 'day', r['fc_day'], flush=True)
        if fr >= 0.40:
            break
    ov.update(o); chosen[k] = f
print(json.dumps(chosen))
cfg = tune.apply(base, ov)
print(tune.show(tune.run(cfg, 150)))
json.dump(ov, open('/tmp/ov_%s.json' % mode, 'w'), ensure_ascii=False, indent=0)
