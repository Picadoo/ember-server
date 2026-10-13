# 状态 · TIP-CG 本周短征可冲（F · short-goal）抽测

**裁决：** **FAIL** · menu tip `0abdb2746` · jar tip/feat `0d5594e6e` · jar **`1.65.808-d13312.local`** · 证据 `/workspace/tmp/tip-cg-short-goal/`  
**范围：** `ember_p1_short` F「本周短征可冲」+ Open 半行 · `enter short-goal` · 灰态单条件 · **零抬** short:5 / 徽15 / 日帽 · **≠关观察** · **未改** afk.tiers / daily_kills / Stage2 三开关 / gate_daily / ×0.97  
**测岗：** 余烬-测试岗 · 2026-10-13 23:36+ Asia/Shanghai · evidence-only 未改玩法码

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | F「本周短征可冲」；lore 含 goal_short + chase | **PASS**（菜单静+亮态） | `10-menu-static.json` · `40-short-open.json`（slot51 gold_ingot；冲图烬镜对廊） |
| 2 | 点 F / `enter short-goal`；门禁同该本；有奖可计进度 | **FAIL** | 见硬红；`30-papi-after-q01.json` · `31-unlock-probe.json` · `50-enter-short-goal.json` |
| 3 | 灰态单条件：chase_ok&lt;1 / done≥1 / stamina&lt;30 | **PASS**（单条件·非七段 and） | `10-menu-static.json`（conds=3 · no seven-and）· `20-no-chase-gray.json` 无冲口灰 · `45-stam-gray.json` 体力不足 iron_bars · `55-done-gray.json` 本周可选已满 |
| 4 | Open「本周短征可冲见本页 F」；D/十九本/B/Z 未坏 | **PASS** | `40-short-open.json` Open hit · D「今日推 · 烬镜对廊」· 静态十九 enter / B 工坊 / Z 返回冒险仍在 |
| 5 | 零抬 short:5/日帽/徽；Stage2 未拧 | **PASS** | `90-endstate.json` · targets.short=5 · reward=15 · daily_kills=2400 · bv123 · gate_daily「未知主线本 daily」 |

## 硬红（挡绿）

**Q01 首通 alone 不产生 chase**（与短征进本/EmberShortPath 门禁不一致）：

1. `corerpg p1 runs firstclear <player> q01` 后 `%corerpg_p1_access_q01%=1`，但 `%corerpg_p1_goal_short_chase%` 空、`goal_short_chase_ok=0`（`30-papi-after-q01.json`）
2. `enter short-goal` →「暂无可冲的已解锁短征（先解锁任意 sx01–sx19）。」（无 admin unlock 时）
3. 探针：仅当 `corerpg p1 runs unlock <player> sx01|sx02|sx03|sx11` 后 chase 才解析为 **sx11 / 烬镜对廊**，enter 才入场（`31-unlock-probe.json` · `50-enter-short-goal.json`）

**根因（证据）：** W1a `EmberRunPapi.goalShortChase` / `enter short-goal` 谓词用 `runs.unlocked(d, m)`（查 `p1_unlock_<sxKey>`），短征 `requires: q01` 从不经 unlocks 链写入各 sx 的 C_UNLOCK；进本真实门禁是 `EmberSkillKit.mapAccessUnlocked` / `EmberShortPath.unlocked` → `progressFlag(q01)`。同构于 TIP-CE「错误门禁谓词」类 FAIL。  
**建议修（插件岗，本抽不改码）：** chase 谓词改为 `progressFlag(d, m.requires 或 q01)`（对齐 EmberShortPath），勿依赖 `runs unlock sx*`。

## 旁注（不抬硬红、不挡本 FAIL）

- 静态 Z 图标仍在（`arrow`「返回冒险」→ `ember_p1_adventure`）；抽测脚本 iconBlock 误报 `Z_still=false`，非产品坏
- 无 chase 灰 UI 真绿（gray dye「无冲口」）；脚本 `chaseOkZero` 曾被钓鱼竿聊天噪声污染 papi 解析，不挡灰态诚实
- 入场后体力读数差 25（非 30）——可能会话内预留/并发；入场名片烬镜对廊已出；不单独抬为硬红
- `goal_short` 文案在未 Q07 时显示「首进或首通 Q07 后开放」（D401 既有）；done 灰测用 admin `runs season goal short 5`（毕业周显示 4/4 按比例，done 位仍 1）
- 单测 `EmberGoalShortChaseTest` 7/0 绿（纯逻辑；未覆盖 live unlocked 谓词）
- TrMenu reload 77 总控已做；live jar `1.65.808-d13312.local` sha256 `169cf064e719…`
- 观察窗仍开；未切分支/stash/reset/force-push；未碰 login/proxy/MariaDB

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers md5 `51e2e286df1c` · ×0.97 未改 · targets.short=5 · goal reward=15 · ECONOMY_BV/bv **123** · EmberShortService.addGoal("short",1) 仍在 · ≠关观察 · gate_daily 仍拒
