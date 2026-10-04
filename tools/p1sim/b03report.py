"""B03/B04 report: growthrun.py weeks over B03_DIR/{off,ub,rg,rw}-{0.3,0.5,0.7}.md (+ *-0.5-h.md hold-out) -> out-build-diversity-b03-b04.md.
  P1SIM_RULES=<snapshot> B03_DIR=/tmp/bd/b03 python3 tools/p1sim/b03report.py"""
import subprocess, os, re, sys
os.chdir(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..'))
env = dict(os.environ)  # pass P1SIM_RULES=<snapshot>
B = os.path.join(os.environ.get('B03_DIR', '/tmp/bd/b03'), '')
LAB = {'off': '无成长', 'ub': '上限效果检查（免费满成长）', 'rg': '真实成本 · 先装备（gear）', 'rw': '真实成本 · 先成长（growth）'}
def weeks(f):
    return subprocess.run(['python3', 'tools/p1sim/growthrun.py', 'weeks', f], capture_output=True, text=True, env=env).stdout
def parse(txt):
    head = re.search(r'= ([\d.]+|未达)', txt)
    rows = re.findall(r'^  (.+?): W30 (\S+) · P50 (\S+) · P90 (\S+) · 第 12 周仍未拥有双极品 (\d+)%', txt, re.M)
    return head.group(1) if head else '—', rows
out = []
P = out.append
P('# 构筑多样性 · B03 / B04：上限效果检查 vs 真实成本成长，双极品时间线（2026-10-04 CST）\n')
P('对应审查 B03（把"第 1 天就拥有完整成果"的上限检查和真实成本成长分开）与 B04（指标改名 + 分布）。')
P('rules sha256 9806c4d10d1fc270（balance_version 29，/tmp/bd/rules-now2.json）。300 名玩家 × Q07 首通后 12 周，`growthrun.py <mode> <build> --players 300 --weeks 12 --abyss --raid --goals --every-week --dodge D`；`growthrun.py weeks <文件>` 汇总。\n')
P('## 两个输出\n')
P('| 名称 | 命令 | 含义 |')
P('|---|---|---|')
P('| **上限效果检查** | `growthrun.py p2econ \'{"talents":["t1b","t2a"],"honors":"all","affixes":{"blade":"b_set","charm":"c_tele"},"affix_tier":4}\'` | 成长免费、第一天就满（第三排按套装自动加上）。只回答"最多能快多少"，**不是玩家曲线** |')
P('| **真实成本成长模拟** | `growthrun.py p2econ-real \'{"talents":["t1b","t2a"],"affixes":{"blade":"b_set","charm":"c_tele"},"policy":"gear"|"growth"}\'`（`realcost.py`） | 天赋点逐步获得（首通 Q03/Q05/Q07、首次挑战通关、首次团本通关、深渊 5 层），按排花币（800/2000/4000）购买；勋记按条件解锁；词条只在当前穿戴的目标族 T3 上按 `EmberAffix.roll` 洗练 / 锁定，花币 + 碎片，每次结算最多 3 次 |')
P('\n**指标（B04）**：W30 = 候选策略中达到 30% 双极品拥有率的最早插值周。时钟从玩家自己的 Q07 首通算起，整周行，线性插值。同时报该策略的 P50 / P90（50% / 90% 玩家拥有双极品的插值周）和"第 12 周仍未拥有双极品"的比例。旧名"两件极品最快周"会被误读成"最快的玩家"或"一般玩家"，两者都不是。\n')
P('## 总表（每个方案取 W30 最早的策略）\n')
P('| 躲避 | 方案 | W30 | 相对无成长 | 该策略 | P50 | P90 | 第 12 周未拥有 | 全部策略里未拥有的范围 |')
P('|---:|---|---:|---:|---|---:|---:|---:|---|')
full = []
for d in ('0.3', '0.5', '0.7'):
    base = None
    for v in ('off', 'ub', 'rg', 'rw'):
        f = B + '%s-%s.md' % (v, d)
        t = weeks(f); w, rows = parse(t)
        full.append((d, v, t))
        if v == 'off':
            base = float(w)
        r = rows[0]
        un = [int(x[4]) for x in rows]
        P('| %s | %s | %s | %s | %s | %s | %s | %s%% | %d–%d%% |' % (d, LAB[v], w, '—' if v == 'off' else '%+.2f' % (float(w) - base), r[0], r[2], r[3], r[4], min(un), max(un)))
P('\n读法：P90 在所有方案、所有躲避下 12 周内都达不到；即使最佳策略，第 12 周仍有 11–42% 的玩家没有双极品。上限检查只提前 0.3–0.7 周；真实成本下 W30 不提前，反而推迟 0.2–0.7 周（d0.3 的先成长提前 0.4），因为天赋和洗练与强化抢同一份币 / 碎片。')
P('\n**噪声**：300 名玩家时 W30 的标准误约 0.35 周（二项 30% 附近、周粒度插值）；同一方案内不同策略之间 W30 相差可达 0.9 周。所以上表 |Δ| ≤ 0.7 周的差别都在 ±2 个标准误内，**不能说成长显著改变了双极品时间线**，只能说"变化 ≤ 0.7 周、方向取决于是否计成本"。')
P('\n与旧表（`out-growth-d141-d143.md` 的"两件极品最快"）对比：旧 无成长 7.80 / 5.44 / 4.42、成长 Δ −0.20 / +0.06 / −0.42（d0.3 / 0.5 / 0.7）。新口径（自己 Q07 首通起算、每周行插值、M01–M03 修正）无成长 7.00 / 5.22 / 4.31；上限 Δ −0.67 / −0.30 / −0.31。旧表的 Δ 是"免费满成长"，对应现在的上限检查，量级一致（≤ 0.7 周）。')
HOLD = [B + x for x in ('off-0.5-h.md', 'ub-0.5-h.md', 'rg-0.5-h.md')]
P('\n## 留出种子复跑（M05，d0.5，玩家种子 +100000）\n')
if all(os.path.getsize(f) > 1000 for f in HOLD):
    P('| 方案 | 主跑 W30 | 留出 W30 | 主跑 P50 | 留出 P50 | 主跑 第 12 周未拥有 | 留出 |')
    P('|---|---:|---:|---:|---:|---:|---:|')
    for v in ('off', 'ub', 'rg'):
        w1, r1 = parse(weeks(B + '%s-0.5.md' % v)); w2, r2 = parse(weeks(B + '%s-0.5-h.md' % v))
        P('| %s | %s | %s | %s | %s | %s%% | %s%% |' % (LAB[v], w1, w2, r1[0][2], r2[0][2], r1[0][4], r2[0][4]))
else:
    P('（复跑中；结果写入后重新生成本文件。）')
P('\n## 真实成本账本（realcost.py，每周末，中位 / 比例）\n')
for d in ('0.3', '0.5', '0.7'):
    for v in ('rg', 'rw'):
        txt = open(B + '%s-%s.md' % (v, d), encoding='utf-8').read()
        i = txt.find('真实成本成长（realcost.py）')
        P('### %s · 躲避 %s\n' % (LAB[v], d))
        P(txt[i:].split('\n', 1)[1].strip() if i >= 0 else '（无）')
        P('')
P('要点：大多数路线只有 4–5 个天赋点；第三排（第 6 点 = 团本首胜 + 深渊 5 层，4,000 币）只在"团本 + 周目标"路线第 8–12 周买齐（89–100%），"深渊 + 周目标"有 6 点但只有约 2% 买（币先交深渊层费）。到第 12 周中位洗练 15–17 次、13.5–17k 币 + 2.6–3k 碎片，约 85% 的人刃 / 符上有目标词条且到封顶档。')
P('\n局限：`p2econ.raid_once` 用固定的团本通过率表，成长不影响模拟里的团本成功率（团本里的成长效应见 `out-build-diversity-raid.md`，≤ 2pp）；图录勋记的洗练折扣不建模（`reroll_coin` 列为未建模键）。')
P('\n## 各方案全部策略\n')
for d, v, t in full:
    P('### %s · 躲避 %s\n\n```\n%s```\n' % (LAB[v], d, t))
open('tools/p1sim/out-build-diversity-b03-b04.md', 'w', encoding='utf-8').write('\n'.join(out) + '\n')
print('ok')
