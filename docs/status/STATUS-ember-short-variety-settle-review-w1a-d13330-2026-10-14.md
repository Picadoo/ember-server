# STATUS · TIP-CY W1a 短征花样结算回看 · D13330

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **CLOSED live** · PASS `176e2fe03` · tip `a1f5a2537` · 总控收口 2026-10-14 08:36 Asia/Shanghai

## 交付
- `EmberShortVarietySettle`：settle 从 session 取 `affix`/`eventKind`（trim；空=无）写入 period 索引仓 `p1_short_var_affix` / `p1_short_var_event`（`@all`；对齐 ShortSettle int 编码 · KNOWN/EVENTS 1-based）
- 每次短征 settle **覆盖**到下次；`hit` / `affixLabel` / `eventLabel` / `line`（无 hit→「本局无花样」；有→「词缀 / 事件」）
- 中文标签复用 `EmberRunMaps.Variety.label` / `eventLabel`；未知 id 诚实回退 id
- 挂钩：`EmberShortService.settle` 旁路 `remember(pd, s)`（try/catch 吞；有奖/无奖/重复 settle 都记）
- PAPI：`%corerpg_p1_short_var_hit%` / `_affix_label` / `_event_label` / `_line`（Section.SHORT）
- 单测：`EmberShortVarietySettleTest`（空/仅affix/仅event/两者/覆盖/零经济键）；`EmberRunPapiTest` 路由四键
- `EmberCounters`：登记 `p1_short_var_affix` / `p1_short_var_event`（顺带补登记既有 `p1_short_settle_last`）
- jar：`1.65.823-d13330.local` · sha256 `62cc4277282b…` · ECONOMY_BV **123** 未动
- play：live `1.65.823-d13330.local` · storage=mysql · bv123 · MySQL connected
- **零发** event_core / affix_shard / NI；**零改** rate / variety yaml / 日帽 / S / targets / soft-reward / ShortPath / Cast / Aid / ShortSettle map 语义 / TrMenu（物品岗）

## 键
| 项 | 口径 |
|----|------|
| `short_var_hit` | hit→1 否则 0 |
| `short_var_affix_label` | 中文词缀；空→空 |
| `short_var_event_label` | 中文事件；空→空 |
| `short_var_line` | 有 hit 拼标签；无→本局无花样 |

## 落点
- `EmberShortVarietySettle`（新）
- `EmberShortService.settle`：variety remember
- `EmberRunPapi`：route / isShortDayKey / `shortVariety`
- `EmberCounters`：period 登记
- `pom.xml` / `plugin.yml`：1.65.823-d13330.local

## 零改边界
- event_rate / variety weights / 新 kind / variety yaml
- 日帽 / S40–S58 / targets / soft-reward claim / ShortPath resolve / Cast / Aid / todayRot
- ShortSettle map 记忆语义（只旁路加 variety）
- TrMenu yml · ECONOMY_BV 123 · sx20

## 下步
- 物品 W1b / 测岗均已齐 · 总控收口 CLOSED

## 测岗抽测（TIP-CY spot）
- **PASS** · 2026-10-14 08:36 Asia/Shanghai · 证据 `/workspace/tmp/tip-cy-variety-settle/` · 详 `STATUS-ember-short-variety-settle-review-tip-cy-spot-d13330-2026-10-14.md`
- live jar `1.65.823-d13330.local` · feat `e5cb2ddd6` · tip `a1f5a2537` · 批 `afeb9a210` · 有奖/无奖 V 回看 + PAPI + 零经济

## 续 · 测岗抽测 PASS（2026-10-14 Asia/Shanghai · 余烬-测试）
- PASS live · feat `e5cb2ddd6` · 证据 `/workspace/tmp/tip-cy-variety-settle/` · STATUS `176e2fe03`
- **CLOSED live**（总控收口）· tip `a1f5a2537` · jar `1.65.823-d13330.local`
