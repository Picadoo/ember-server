import sys, json, p1config, tune
base = p1config.load()
ov = json.loads(sys.argv[2]) if len(sys.argv) > 2 else {}
k = sys.argv[1]; i = ['q01','q02','q03','q04','q05','q06','q07'].index(k)
for mf in (1.0, 0.9, 0.8, 0.7):
    for bf in (1.0, 0.8, 0.65, 0.5):
        o = tune.scale(base, k, hp=mf, atk=mf, boss_hp=bf, boss_atk=mf)
        r = tune.run(tune.apply(base, dict(ov, **o)), 60)[i]
        print(k, 'mob', mf, 'boss_hp', bf, 'front %.0f%%' % (100*(r['front_rate'] or 0)), 'day', r['fc_day'], flush=True)
