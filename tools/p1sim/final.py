import json, p1config, tune, p1sim
base = p1config.load()
cands = {'A': {'q03': 0.95, 'q04': 0.7, 'q05': 0.65, 'q06': 0.7, 'q07': 0.6},
         'B': {'q03': 0.95, 'q04': 0.75, 'q05': 0.65, 'q06': 0.7, 'q07': 0.65}}
out = {}
for name, fs in cands.items():
    ov = {}
    for k, f in fs.items():
        ov.update(tune.scale(base, k, hp=f, atk=f))
    cfg = tune.apply(base, ov)
    rows = tune.run(cfg, 200)
    print(name, fs); print(tune.show(rows), flush=True)
    out[name] = {'ov': ov, 'rows': rows}
rows0 = tune.run(base, 200)
print('BASE'); print(tune.show(rows0))
out['base'] = {'rows': rows0}
json.dump(out, open('/tmp/final.json', 'w'), ensure_ascii=False, default=str)
