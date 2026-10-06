"""D244 / ARCH S4-3: the affix numbers p1sim uses, read from tools/p1sim/affix-table.json — generated from the D241
primitives (CoreRpg p1.encounter.Affix* + AffixCycle) on the live variety block by EmberAffixExportTest (that test fails
when code / yml and the checked-in table drift; rules.validate refuses a table whose yml inputs differ from the live
variety). Nothing here is hand-copied: first hit / period / damage per cast come from the table.

Modelled in p1sim (unchanged coverage): shield (hp), split (adds), blazing / venom / jailer / arcane (periodic telegraphed
hit at the table's first_hit_s / period_s), firechain (exposure model on the table's first_burn_s / burn_every_s).
Exported but not modelled (as before D244): regen, charge, frost, mortar, molten.

Tools:  python3 affixtable.py show   — the table rows p1sim uses
        python3 affixtable.py diff   — D244 before/after: pre-D244 hand-derived cadence vs the table, per affix
"""
import os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import rules

MODELLED = ('shield', 'split', 'blazing', 'venom', 'jailer', 'arcane', 'firechain')
PERIODIC = ('blazing', 'venom', 'jailer', 'arcane')


def table():
    return rules.data('affixtable')


def grace():
    return float(table()['grace_s'])


def row(cfg, kind):
    """the exported row for `kind`; refuses when this cfg's variety numbers are not the ones the table was built from
    (a what-if on affix numbers needs a re-export from Java, so the sim never silently mixes the two)"""
    r = table()['affixes'][kind]
    live = (cfg.get('variety') or {}).get(kind) or {}
    inp = r.get('yml') or {}
    for k, x in live.items():
        if str(k) not in inp or abs(float(x) - float(inp[str(k)])) > 1e-9:
            raise rules.RuleError('variety.%s.%s = %s but affix-table.json was exported with %s — re-export '
                                  '(cd CoreRpg && mvn -o test -Dtest=EmberAffixExportTest -Daffix.export=write)'
                                  % (kind, k, x, inp.get(str(k))))
    return r


def legacy(cfg, kind):
    """pre-D244 p1sim cadence (hand-derived from yml; kept only for the diff report): (first, period, dmg×atk)"""
    b = (cfg.get('variety') or {}).get(kind) or {}
    if kind in ('blazing', 'venom', 'jailer'):
        return 1.5 + float(b['every']), float(b['every']), float(b['dmg'])
    if kind == 'arcane':
        cyc = float(b['every']) + float(b.get('spin', 3.0))
        return 1.5 + cyc, cyc, float(b['dmg'])
    if kind == 'firechain':
        return 1.5 + float(b.get('warn', 1.2)), float(b.get('tick', 1.0)), float(b['dmg'])
    return None


def main():
    import p1config
    cfg = p1config.load()
    t = table()
    print('# affix-table.json (%s, balance_version %s, grace %.1f s)' % (t['generated_by'].split('/')[-1], t['balance_version'], t['grace_s']))
    if len(sys.argv) > 1 and sys.argv[1] == 'diff':
        print('| 词缀 | 族 | sim 用 | 首击 s 旧 → 新 | 周期 s 旧 → 新 | 伤害 ×atk 旧 → 新 |')
        print('|---|---|---|---:|---:|---:|')
        for kind, r in t['affixes'].items():
            old = legacy(cfg, kind)
            if kind in PERIODIC:
                new = (r['first_hit_s'], r['period_s'], r['dmg_atk'])
            elif kind == 'firechain':
                new = (r['first_burn_s'], r['burn_every_s'], r['dmg_atk'])
                old = (old[0], old[1], old[2])
            else:
                print('| %s | %s | %s | — | — | — |' % (kind, r['family'], '是' if kind in MODELLED else '否（未建模）'))
                continue
            print('| %s | %s | 是 | %.1f → %.1f | %.1f → %.1f | %.2f → %.2f |' % (kind, r['family'], old[0], new[0], old[1], new[1], old[2], new[2]))
        print('\n火链：表里是首次可烧时间 / 每人烧伤间隔；sim 再按 FIRECHAIN_EXPOSURE 折成平均间隔（tick / exposure），首击 = first_burn + 一个平均间隔（旧：link + 一个平均间隔）。')
        return
    for kind, r in t['affixes'].items():
        print(kind, {k: v for k, v in r.items() if k not in ('yml', 'label')})


if __name__ == '__main__':
    main()
