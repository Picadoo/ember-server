# 状态 · D401 抽测：短征可选周目标钩

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**  
**证据：** `/workspace/tmp/d401-short-weekly-goal/` · summary `99-summary.json`  
**对照：** DESIGN `DESIGN-ember-short-weekly-goal-2026-10-10.md` · 施工 `STATUS-ember-short-weekly-goal-d401-2026-10-10.md` · 菜单 `…-menu-d401…` · papi-menu `…-papi-menu-d401…`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `746a5259` |
| 菜单 tip | `c38796fd` |
| PAPI 菜单 tip | `674cc32b` |
| jar（抽测主窗） | `1.65.111-d401.local`（sha256 `12d954dcc7b8745fec8350fb7c7c6d48abc8fc8e8e9725d6df91563e067fa6b4`） |
| bv（抽测主窗） | **69** |
| 并行旁注 | 抽测中 play 被 d403→d404 换 jar（结束 live `1.65.113-d404.local` · bv70）；**未**主动换 jar；V3 聊天证据落在 d401 窗；V4 在 short:5 仍 live 的后续 jar 续完 |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | `targets.short=5` · OPTIONAL | **PASS** | live runs `short: 5`；必做四键未抬；goals 分母 **0/4**；可选注「不算进全部完成」 |
| 2 | Q07+ 有奖通关涨进度 | **PASS** | `D401WkYes` sx01 settle reward → 聊天「周目标 · 短征（可选） **1/5**」（`40-after-c1`） |
| 3 | 满 5 → S19 +15 徽 | **PASS** | 4/5 后再有奖通关 →「周目标完成…余烬徽 **+15**（共 15）· 本周 **0/4**」（`61-v3-after`）；持久 `✔ 5/5` · 徽 15（`75-yes-v3-persist`） |
| 4 | 不算全完成 / bonus | **PASS** | 完成句带「本周 0/4」；goalsDone=0 / total=4；无「全部完成」发奖句 |
| 5 | 第 4+ 无结算不计 | **PASS** | `D401WkV4` 三有奖至 day=3/3 · short=3；第4次 `no-reward day=3/3` · short **仍 3**（`74-v4-c4-after`） |
| 6 | 赛季格 6 可见可进短征 | **PASS** | `ember_p1_season` Layout `#12345 6#`；格 6 挂 `%corerpg_p1_goal_short%`（非假 ✔）；同屏「周目标 · 短征（可选）」· lore 真进度；点击→`ember_p1_short`（`31-season-menu`） |
| 7 | goals 分母仍 4 | **PASS** | 菜单「本周目标：0/4」· PAPI `%corerpg_p1_goals%`→`0/4` · `/corerpg p1 goals`「完成 0/4」 |
| 8 | afk / gate_daily / 三开关 / S40–S44 | **PASS** | `daily_kills=2400`；`gate_daily=no`；six_slot enabled/migrate/set_bonus **true**；S40–S44 clear 仍 80/4/3；**≠关观察** |
| 5b | Q07 未通不计 | **PASS** | `D401WkNo` 有奖 settle 无周目标进度句（`21-no-q07-after`） |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| PAPI 假红 | 离本/换图瞬间 `papi parse` 偶发 Failed to find player；hub 稳定后复测绿；进度以聊天 settle 句为准 |
| DP 冷却 | 离开后 <5s 再进被拒；补测 hub 等待 ≥8s |
| 并行换 jar | d403/d404 重启打断 V4 中段；数据落库后续测；结束 live 已非 d401 jar（记录；未拧观察开关） |

## 不动

afk.tiers / daily_kills · gate_daily · Stage2 三开关 / ×0.97 · S40–S44 量 · 主仓切分支 · 关观察
