# 状态 · TIP-CG 复测 d13312b · 本周短征可冲 chase 谓词 hotfix

**裁决：** **PASS** · menu tip `0abdb2746` · hotfix tip `e0be3251f` · jar **`1.65.809-d13312b.local`** (sha256 `495731c9d25a…` · bv**123**) · 证据 `/workspace/tmp/tip-cg-short-goal/reverify/`  
**对照 FAIL：** `71d4f3356`（Q01 alone chase 空 · unlocked≠progressFlag）  
**范围：** 硬复测 chase = EmberShortPath.unlocked(progressFlag/q01)；灰态三档；D/十九本/B/Z；零抬 short:5/徽15；≠关观察  
**测岗：** 余烬-测试岗 · 2026-10-13 23:47+ Asia/Shanghai · evidence-only 未改玩法码 · 禁 admin unlock sx

## 硬复测

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 干净号仅 Q01 首通（勿 admin unlock sx）→ chase 非空 · chase_ok=1 · name/day_line 有值 | **PASS** | `30-papi-after-q01.json` · `31-no-admin-unlock.json`：chase=`sx11` · ok=`1` · name=`烬镜对廊` · day=`今日有奖 0/3` |
| 2 | 点 F / `enter short-goal` → 进可冲本（非「暂无可冲」）；无 chase 拒文含「先首通 Q01」 | **PASS** | `50-enter-short-goal.json` 预留体力30入 sx11；`21-no-chase-enter.json`「暂无可冲的已解锁短征（先首通 Q01）。」 |
| 3 | 灰态三档诚实；D/十九本/B/Z；零抬 short:5·徽·Stage2 | **PASS** | 无冲口 gray dye · 体力不足 iron_bars · 本周可选已满；`10-menu-static.json` Z→adventure；`90-endstate.json` short=5 reward=15 dk=2400 bv123 |

## 纪律

Stage2 三开仍 true · afk.tiers md5 `51e2e286df1c` · ×0.97 未改 · gate_daily 仍拒 · 观察窗仍开 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB · jar live `1.65.809-d13312b.local` 已确认 Enabling

## 旁注（不挡绿）

- 首发减负 −5 → 净扣 25；门禁仍「预留体力 30」（`V2_stamina_gate_30` reserved30+firstRelief）
- 用户 `TipCgRvC` / 无 chase `TipCgRvNo2`；单测 EmberGoalShortChaseTest 8/0（含 onlyQ01ProgressFlagOpensChase）
