import json, math, sys
import os as _os
BD = _os.environ.get('SIMFIX_DIR', '/tmp/bd')  # where simfix_check.py wrote its pre_*/post_* json
def L(p): return json.load(open(p))
def ci_ind(p1, n1, p2, n2):
    return 1.96 * math.sqrt(p1 * (1 - p1) / n1 + p2 * (1 - p2) / n2)
def ci_pair(w1, w0):
    d = [a - b for a, b in zip(w1, w0)]; n = len(d); m = sum(d) / n
    v = sum((x - m) ** 2 for x in d) / (n - 1)
    return m, 1.96 * math.sqrt(v / n)
out = []
P = lambda x: '%.1f' % (100 * x)
pa, pb, qa, qb = (L(BD + '/%s_raid_%s.json' % (w, r)) for w in ('pre', 'post') for r in ('a', 'b'))
out.append('## 1. 团本 R01–R03 基线通关率（成长关闭，池 36 人 Q07+2 周，每格 3000 局）\n')
out.append('| 团本 | 躲避 | 人数 | 修复前 运行 A | 修复前 运行 B | A−B（同命令同种子） | 修复后 A | 修复后 B | 修复后 − 修复前均值 | 95% 区间半宽 |')
out.append('|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|')
key = lambda j: tuple(j)
def idx(d): return {key(j): r for j, r in d['results']}
ia, ib, ja, jb = idx(pa), idx(pb), idx(qa), idx(qb)
maxab = 0; same = True; shifts = []
for k in ia:
    for n in ('3', '4', '5'):
        a, b, c, d = ia[k]['eval'][n]['rate'], ib[k]['eval'][n]['rate'], ja[k]['eval'][n]['rate'], jb[k]['eval'][n]['rate']
        same &= ja[k]['eval'][n]['wins'] == jb[k]['eval'][n]['wins']
        maxab = max(maxab, abs(a - b))
        pm = (a + b) / 2
        h = ci_ind(pm, 6000, c, 3000)
        shifts.append((c - pm, h, k, n))
        out.append('| %s | %.1f | %s | %s | %s | %+.1f | %s | %s | **%+.1f** | ±%.1f |' % (k[1], k[2], n, P(a), P(b), 100 * (a - b), P(c), P(d), 100 * (c - pm), 100 * h))
out.append('\n修复前同一命令两次运行最大差 %.1f 个百分点（M01：成员首次出手用了未设种子的全局随机）；修复后两次运行逐局结果完全一致：%s。' % (100 * maxab, '是' if same else '否'))
sig = [s for s in shifts if abs(s[0]) > s[1]]
out.append('修复后 − 修复前：%d/%d 格超出 95%% 区间；最大 %+.1f（%s %s d%.1f %s 人）。\n' % (len(sig), len(shifts), 100 * max(shifts, key=lambda s: abs(s[0]))[0], *(lambda s: (s[2][1], '', s[2][2], s[3]))(max(shifts, key=lambda s: abs(s[0])))))
# composition
pc, qc = idx(L(BD + '/pre_comp.json')), idx(L(BD + '/post_comp.json'))
out.append('## 2. 焚烬人数受控对照（d0.5，同一 36 人池，k 名改穿焚烬，其余非焚烬；3000 局）\n')
out.append('| 团本 | 人数 | 焚烬人数 k | 修复前 | 修复后 | 差 | 95% 区间半宽 | 修复后中位用时 s（前→后） |')
out.append('|---|---:|---:|---:|---:|---:|---:|---|')
for k in pc:
    a, c = pc[k], qc[k]
    h = ci_ind(a['rate'], a['n'], c['rate'], c['n'])
    out.append('| %s | %d | %d | %s | %s | **%+.1f** | ±%.1f | %s→%s |' % (k[1], k[3], k[4], P(a['rate']), P(c['rate']), 100 * (c['rate'] - a['rate']), 100 * h,
               '%.0f' % a['secs'] if a['secs'] else '—', '%.0f' % c['secs'] if c['secs'] else '—'))
# graid
pg, qg = L(BD + '/pre_graid.json'), L(BD + '/post_graid.json')
ipg, iqg = idx(pg), idx(qg)
out.append('\n## 3. 成长终验 v7 三套构筑 vs 关闭（团本，每格 1500 局；修复后为同队同种子配对区间）\n')
out.append('| 团本 | 躲避 | 构筑 | 人数 | 修复前 关→开（差） | 修复后 关→开（差） | 修复后配对 95% 区间 | 区间是否全部落在 ±3 内 |')
out.append('|---|---:|---|---:|---|---|---|---|')
offp = {(k[1], k[2]): ia[k] for k in ia}; offp_b = {(k[1], k[2]): ib[k] for k in ib}; offq = {(k[1], k[2]): ja[k] for k in ja}
cnt = {'in': 0, 'out': 0, 'point_out': 0}
for k in iqg:
    raid, d, b = k[1], k[2], k[3]
    for n in ('3', '4', '5'):
        # pre: the off baseline of the SAME trial count is not in pre_graid; use pre raid A at 3000 (noise noted)
        p0, p1 = offp[(raid, d)]['eval'][n]['rate'], ipg[k]['eval'][n]['rate']
        q1 = iqg[k]['eval'][n]
        # post off at 1500 trials = first 1500 of the 3000 run? line-ups depend on trials count -> rerun needed; use paired from graid off
        p0b = offp_b[(raid, d)]['eval'][n]['rate']
        p0m = (p0 + p0b) / 2
        w0 = offq[(raid, d)]['eval'][n]['wins'][:len(q1['wins'])]
        q0 = sum(w0) / len(w0)
        m, h = ci_pair(q1['wins'], w0)
        lo, hi = m - h, m + h
        inside = lo >= -0.03 and hi <= 0.03
        cnt['in' if inside else 'out'] += 1
        cnt['point_out'] += abs(m) > 0.03
        hp = ci_ind(p0m, 6000, p1, 1500)
        out.append('| %s | %.1f | %s | %s | %s→%s（%+.1f ±%.1f） | %s→%s（**%+.1f**） | [%+.1f, %+.1f] | %s |' % (raid, d, b, n, P(p0m), P(p1), 100 * (p1 - p0m), 100 * hp,
                   P(q0), P(q1['rate']), 100 * m, 100 * lo, 100 * hi, '是' if inside else '**否**'))
out.append('\n修复后 %d 格中：配对 95%% 区间完全落在 ±3 内 %d 格，区间越过 ±3 %d 格（其中点估计本身越界 %d 格）。' % (cnt['in'] + cnt['out'], cnt['in'], cnt['out'], cnt['point_out']))
# solo
ps_, qs_ = idx(L(BD + '/pre_solo.json')), idx(L(BD + '/post_solo.json'))
out.append('\n## 4. 单人 Q01–Q07 参考装（普通 n = 书 §3.2 参考装，挑战 c = T3+6；每格 3 种子 × 2000 局）\n')
out.append('### 4a. 成长关闭的基线：修复前 → 修复后（纯模型修正）\n')
out.append('| 套装 | 格数 | 平均差 | 最大升 | 最大降 | 超出独立 95% 区间的格 |')
out.append('|---|---:|---:|---:|---:|---:|')
def cells(r):
    keys = sorted(set(k.split('/')[0] for k in r))
    return {c: sum(r[k] for k in r if k.split('/')[0] == c) / 3 for c in keys}
for fam in ('burst', 'scorch', 'sustain'):
    ds = []
    for q in range(1, 8):
        a, c = cells(ps_[('solo', 'q%02d' % q, fam, 'off')]), cells(qs_[('solo', 'q%02d' % q, fam, 'off')])
        for cc in a:
            ds.append((c[cc] - a[cc], ci_ind(a[cc], 6000, c[cc], 6000), 'q%02d %s' % (q, cc)))
    big = [x for x in ds if abs(x[0]) > x[1]]
    mx = max(ds, key=lambda x: x[0]); mn = min(ds, key=lambda x: x[0])
    out.append('| %s | %d | %+.2f | %+.1f（%s） | %+.1f（%s） | %d |' % (fam, len(ds), 100 * sum(x[0] for x in ds) / len(ds), 100 * mx[0], mx[2], 100 * mn[0], mn[2], len(big)))
out.append('\n### 4b. 成长终验构筑 − 关闭（max |Δ|，同代码内比较）：修复前 vs 修复后\n')
out.append('| 图 | 套装 | 构筑 | 修复前 最大升 / 最大降 | 修复后 最大升 / 最大降 | 修复后 >3 的格 |')
out.append('|---|---|---|---|---|---|')
over_pre = over_post = 0
for q in range(1, 8):
    for fam in ('burst', 'scorch', 'sustain'):
        a0, c0 = cells(ps_[('solo', 'q%02d' % q, fam, 'off')]), cells(qs_[('solo', 'q%02d' % q, fam, 'off')])
        for b in ('t1a+t2a+row3', 't1b+t2a+row3', 't1c+t2a+row3'):
            a1, c1 = cells(ps_[('solo', 'q%02d' % q, fam, b)]), cells(qs_[('solo', 'q%02d' % q, fam, b)])
            dp = {cc: a1[cc] - a0[cc] for cc in a0}; dq = {cc: c1[cc] - c0[cc] for cc in c0}
            op = [cc for cc in dp if abs(dp[cc]) > 0.03]; oq = [cc for cc in dq if abs(dq[cc]) > 0.03]
            over_pre += len(op); over_post += len(oq)
            out.append('| Q%02d | %s | %s | %+.1f / %+.1f | %+.1f / %+.1f | %s |' % (q, fam, b, 100 * max(dp.values()), 100 * min(dp.values()), 100 * max(dq.values()), 100 * min(dq.values()),
                       ', '.join('%s %+.1f' % (cc, 100 * dq[cc]) for cc in oq) or '—'))
out.append('\n超过 ±3 的格：修复前 %d，修复后 %d（共 %d 格）。' % (over_pre, over_post, 7 * 3 * 3 * 6))
print('\n'.join(out))

# ---- pools (hold-out player pools) and gpools (v7 growth on 7 pools, paired + clustered)
import statistics as S
pre = {tuple(j): r for j, r in json.load(open(BD + '/pre_pools.json'))['results']}
post = {tuple(j): r for j, r in json.load(open(BD + '/post_pools.json'))['results']}
o = []
o.append('\n## 1b. 留出玩家池：6 个未参与上表的玩家池（种子 17000…67000），d0.5，每池每格 2000 局\n')
o.append('单一玩家池的基线差（上表 −6 左右）主要是"抽到哪 36 个人"的差异：池与池之间的标准差就有 3–4 个百分点（3 人格），比单池内 3000 局的抽样区间（约 ±2）还大。这正是 M05 说的聚类误差：玩家池是一个随机效应，只报单池逐局区间会低估不确定性。\n')
o.append('| 团本 | 人数 | 修复前 6 池 | 修复后 6 池 | 平均差（修复后 − 修复前） | 池间标准差 前 / 后 | 平均差 95% 区间（6 池，t₅） |')
o.append('|---|---:|---|---|---:|---|---:|')
for raid in ('r01', 'r02', 'r03'):
    for n in ('3', '4', '5'):
        a = [pre[k]['eval'][n]['rate'] for k in sorted(pre) if k[1] == raid]; b = [post[k]['eval'][n]['rate'] for k in sorted(post) if k[1] == raid]
        d = [y - x for x, y in zip(a, b)]
        o.append('| %s | %s | %s | %s | %+.1f | %.1f / %.1f | ±%.1f |' % (raid, n, ' '.join('%.0f' % (100 * x) for x in a), ' '.join('%.0f' % (100 * x) for x in b),
                 100 * S.mean(d), 100 * S.stdev(a), 100 * S.stdev(b), 100 * 2.571 * S.stdev(d) / math.sqrt(len(d))))
try:
    gp = {tuple(j): r for j, r in json.load(open(BD + '/post_gpools.json'))['results']}
    o.append('\n## 3b. 成长终验 v7（修复后）在 7 个玩家池上：配对（同池同队同种子）+ 按池聚类\n')
    o.append('每池每格 1500 局。"合并配对区间"把 7 池的逐局配对差合在一起（10500 局）；"池间"列是 7 个池各自的 Δ（看效应是否随抽到的玩家变化）；"聚类区间"= 7 个池 Δ 的均值 ± t₆·sd/√7。\n')
    o.append('| 团本 | 构筑 | 人数 | 7 池各自 Δ（pp） | 合并配对 Δ [95%] | 聚类 Δ [95%] | 落在 ±3 内？ |')
    o.append('|---|---|---:|---|---|---|---|')
    allin = cross = 0
    for raid in ('r01', 'r02', 'r03'):
        ks = sorted(k for k in gp if k[1] == raid)
        for b in ('t1a+t2a+row3', 't1b+t2a+row3', 't1c+t2a+row3'):
            for n in ('3', '4', '5'):
                per, W1, W0 = [], [], []
                for k in ks:
                    r = gp[k]
                    assert r[b][n]['lineup'] == r['off'][n]['lineup']
                    per.append(r[b][n]['rate'] - r['off'][n]['rate'])
                    W1 += r[b][n]['wins']; W0 += r['off'][n]['wins']
                m, h = ci_pair(W1, W0)
                cm, ch = S.mean(per), 2.447 * S.stdev(per) / math.sqrt(len(per))
                lo, hi = min(m - h, cm - ch), max(m + h, cm + ch)
                ok = lo >= -0.03 and hi <= 0.03
                allin += ok; cross += not ok
                o.append('| %s | %s | %s | %s | %+.1f [%+.1f, %+.1f] | %+.1f [%+.1f, %+.1f] | %s |' % (raid, b, n, ' '.join('%+.1f' % (100 * x) for x in per),
                         100 * m, 100 * (m - h), 100 * (m + h), 100 * cm, 100 * (cm - ch), 100 * (cm + ch), '是' if ok else '**否**'))
    o.append('\n%d 格中 %d 格两种区间都在 ±3 内，%d 格至少一种区间越过 ±3。' % (allin + cross, allin, cross))
except FileNotFoundError:
    pass
print('\n'.join(o))
