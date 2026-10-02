"""1.28.0 re-run of the §2c proposal (plan B) + room-size sensitivity.
The sim has no walk time / geometry; the only knob book-size rooms can plausibly move is
`engage` (melee bodies hitting at once: wider rooms + P7/P8 spread spawns) -> 4 (default) vs 3."""
import json, p1config, tune, p1sim
base = p1config.load()
B = json.load(open('proposal_2c.json')) if False else None
fs = {'q03': 0.95, 'q04': 0.75, 'q05': 0.65, 'q06': 0.7, 'q07': 0.65}
ov = {}
for k, f in fs.items():
    ov.update(tune.scale(base, k, hp=f, atk=f))
cfgB = tune.apply(base, ov)
def run(cfg, engage):
    kn = p1sim.Knobs(0.5, engage=engage)
    rows, _ = p1sim.summarize(cfg, kn, 200, 60 * (cfg['stamina_day'] // cfg['run_cost']))
    return rows
for name, cfg in (('BASE', base), ('B', cfgB)):
    for e in (4, 3):
        print(name, 'engage', e); print(tune.show(run(cfg, e)), flush=True)
