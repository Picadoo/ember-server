# STATUS · TIP-CG W1a 短征选页「本周短征可冲」chase PAPI · D13312

> 2026-10-13 Asia/Shanghai · 余烬-插件岗

## 交付
- `%corerpg_p1_goal_short_chase%` / `_name` / `_day_line`：已解锁 sx 中优先 day_left>0；同档稳定序 SHORT_KEYS；可选对齐 sx_today
- `%corerpg_p1_goal_short_done%`（进度≥target→1 只读）
- `%corerpg_p1_goal_short_chase_ok%`（chase 非空→1 否则 0；TrMenu 灰态单键，吸取 TIP-CE 七段 and 教训）
- `/corerpg p1 enter short-goal`（及 `goal-short-chase` / `goal_short_chase`）→ 同 chase 键 → tryEnter；无解锁诚实拒
- 单测：`EmberGoalShortChaseTest` 有 day_left 优先 / 全满回落练习 / 未解锁跳过 / done·ok 边界；`EmberRunPapiTest` 路由五键
- jar：`1.65.808-d13312.local` · sha256 `169cf064e719…` · ECONOMY_BV **123** 未动 · **零改** addGoal / weekly_goals.targets / 徽 / 日帽 / SoftPity
- play：live `1.65.808-d13312.local` · storage=mysql · bv123 · MySQL connected

## 键清单
| 键 | 口径 |
|----|------|
| `goal_short_chase` | 已解锁短征优先 day_left>0；同档 sx01…sx19；sx_today 合格则优先；全满→练习回落；皆无→空 |
| `goal_short_chase_name` | 冲图显示名（无 chase→空） |
| `goal_short_chase_day_line` | 冲图今日有奖行（复用 dayLine；无 chase→空） |
| `goal_short_done` | short 周进度≥target → 1 否则 0（target≤0→0） |
| `goal_short_chase_ok` | chase 非空→1 否则 0（TrMenu 单条件灰） |
| enter `short-goal` | 解析为 chase 后同 enter &lt;map&gt; |

## 落点
- `EmberGoalShortChase.chaseKey` / `chaseOk` / `done`
- `EmberRunPapi.season` → `goalShortChase`（SEASON 节，goal* 前缀）
- `EmberRunService` case enter 别名

## 零改边界
- addGoal / weekly_goals.targets / 徽表 / goal reward
- 短征日帽 / S40–S58 / daily_kills / afk.tiers
- SoftPity / event_rate / Pack / balance_version 123
- 十九本 / 今日推 D / SoftPity.onSettle

## 下步
- 物品 W1b/c（TrMenu F 格 + Open 半行）等键

## 旁注
- TIP-CG hotfix D13312b：`1.65.809-d13312b.local` 谓词改 ShortPath.unlocked（测岗 `71d4f3356`）
