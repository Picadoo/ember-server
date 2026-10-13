# STATUS · TIP-CP W1a+b+c+d 短征轻量 variety 房内可感 · D13321

> 2026-10-14 02:30 Asia/Shanghai · 余烬-插件岗

## 交付
- **W1a** `varietyEligible` 显式纳入 `shortExpedition`（全员 firstCleared 仍为调用方门闩）；复用既有 `maps.variety.roll` / `event_rate`；**零改** yaml 数值；禁新 kind；禁首通局
- **W1b** 命中房：`EmberShortVarietyFeel` HUD/喊话「【短征花样】词缀精英 / 房间事件」；短征路径不承诺核心/碎片
- **W1c** Director promote / 房间事件在短征三房真跑（既有路径；短征文案走 ShortVarietyFeel）
- **W1d** 短征 settle **零发** `event_core` / `affix_shard`（不走 `varietyGrants`；单测钉死）
- **W1e 本窗不做**（todayRot 事件偏置）
- 单测：`EmberShortVarietyFeelTest` + `EmberSessionServiceTest.varietyEligible_plainMainOrShort`
- jar：`1.65.813-d13321.local` · sha256 `f42797a6d70c…` · ECONOMY_BV **123** 未动
- play：live `1.65.813-d13321.local` · storage=mysql · bv123 · MySQL tables ready
- **W1e 本窗不做**

## 落点
| 文件 | 改动 |
|------|------|
| `EmberShortVarietyFeel` | eligible / HUD / economy-key 检测（新建） |
| `EmberSessionService` | varietyEligible 6 参（含 shortExpedition）；call site 传 `m.shortExpedition` |
| `EmberRunDirector` | short promote/event：ShortVarietyFeel cue；tell 无核心/碎片承诺 |
| `EmberShortService` | W1d 注释钉死 |
| `ember-v1-runs.yml` | D138 注释补 short 轻量 variety（数值零改） |

## 零改边界
- 日帽 / S40–S58 数额 / `event_rate` / `event_core` / `affix_shard` 配置值
- Cast / EliteAid / BossAid / HP / 掉落 / MM / 庭内菜单 / sx20
- todayRot cue（D13320）/ SoftPity / NEED / Pack / afk.tiers / daily_kills
- balance_version 123 · 无新 variety kind · 无 W1e 偏置

## 下步
- 测岗已 PASS（见 tip-cp-spot / 总控收口）
- W1e todayRot 偏置另窗（不可独批）

## 测岗抽测（TIP-CP spot）
- **PASS** · 2026-10-14 02:36 Asia/Shanghai · 证据 `/workspace/tmp/tip-cp-short-variety/` · 详 `STATUS-ember-tip-cp-short-variety-spot-2026-10-14.md`
- live jar `1.65.813-d13321.local` · 首通无花样 · 重打 HUD 绿 · settle 零核心/碎片 · W1e 仍不做
