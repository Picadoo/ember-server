"""Apply proposal_2c.json (runs-yml path -> value) to both ember-v1-runs.yml copies and EmberP1Main.yml,
editing text in place (comments kept). D60, balance_version 2. Idempotent."""
import json, re, os
ROOT = os.path.join(os.path.dirname(__file__), '..', '..')
P = json.load(open(os.path.join(os.path.dirname(__file__), 'proposal_2c.json')))
RUNS = ['CoreRpg/src/main/resources/ember-v1-runs.yml', 'plugins/CoreRpg/ember-v1-runs.yml']
MM = 'plugins/MythicMobs/Mobs/EmberP1Main.yml'
ROLE_MM = {'melee': 'Melee', 'ranged': 'Ranged', 'heavy': 'Heavy', 'caster': 'Caster', 'elite': 'Elite'}

def sub_field(line, field, val):
    n, c = re.subn(r'(\b%s:\s*)-?[0-9.]+' % field, lambda m: m.group(1) + str(val), line, count=1)
    assert c == 1, (field, line)
    return n

def patch_runs(text):
    L = text.split('\n')
    # locate map blocks
    starts = {m: i for i, l in enumerate(L) for m in [l.strip()[:-1]] if re.match(r'^  q0\d:$', l)}
    keys = sorted(starts)
    for qi, q in enumerate(keys):
        a = starts[q]; b = starts[keys[qi + 1]] if qi + 1 < len(keys) else len(L)
        ov = {k[len(q) + 1:]: v for k, v in P.items() if k.startswith(q + '.')}
        if not ov: continue
        sec = None; skill = -1; in_skills = False
        for i in range(a, b):
            l = L[i]; s = l.strip()
            if re.match(r'^    \w+:', l): sec = s.split(':')[0]; in_skills = False
            if sec == 'mobs':
                m = re.match(r'^      (\w+):\s*\{', l)
                if m:
                    r = m.group(1)
                    for f in ('hp', 'atk'):
                        if 'mobs.%s.%s' % (r, f) in ov: L[i] = l = sub_field(l, f, ov['mobs.%s.%s' % (r, f)])
            elif sec == 'boss':
                m = re.match(r'^      (hp|atk):\s*\d+', l)
                if m and 'boss.' + m.group(1) in ov: L[i] = sub_field(l, m.group(1), ov['boss.' + m.group(1)])
                if re.match(r'^      skills:', l): in_skills = True; continue
                if re.match(r'^      \w+:', l) and not l.startswith('      skills'): in_skills = False
                if in_skills:
                    if s.startswith('- {'):
                        skill += 1
                        k = 'boss.skills.%d.dmg' % skill
                        if k in ov:
                            head, sep, tail = l.partition('follow:')
                            L[i] = sub_field(head, 'dmg', ov[k]) + sep + tail
                    elif s.startswith('follow:'):
                        k = 'boss.skills.%d.follow.dmg' % skill
                        if k in ov: L[i] = sub_field(l, 'dmg', ov[k])
    return '\n'.join(L)

def patch_mm(text):
    for k, v in P.items():
        q, kind = k.split('.')[0], k.split('.')[1]
        if kind == 'mobs':
            role, f = k.split('.')[2], k.split('.')[3]; name = 'Ember%s%s' % (q.upper(), ROLE_MM[role])
        elif k.endswith('boss.hp') or k.endswith('boss.atk'):
            f = k.split('.')[2]; name = 'Ember%sBoss' % q.upper()
        else:
            continue
        field = 'Health' if f == 'hp' else 'Damage'
        pat = re.compile(r'(^%s:\n(?:  .*\n|\s*\n)*?  %s: )[0-9.]+' % (name, field), re.M)
        text, c = pat.subn(lambda m: m.group(1) + str(v), text, count=1)
        assert c == 1, (name, field)
    return text

if __name__ == '__main__':
    t = patch_runs(open(os.path.join(ROOT, RUNS[0]), encoding='utf-8').read())
    t = re.sub(r'^rule_version: .*$', 'rule_version: g04-1\n# §23.2 parameter changes bump balance_version (1 = book initial values incl. D31; 2 = §2c plan B, D60)\nbalance_version: 2', t, count=1, flags=re.M) if 'balance_version:' not in t else t
    for p in RUNS: open(os.path.join(ROOT, p), 'w', encoding='utf-8').write(t)
    mp = os.path.join(ROOT, MM); new = patch_mm(open(mp, encoding='utf-8').read()); open(mp, 'w', encoding='utf-8').write(new)
    print('applied', len(P))
