# STATUS · TIP-CO W1a+b+c 短征「今日推」本局内可感 · D13320

> 2026-10-14 02:05 Asia/Shanghai · 余烬-插件岗

## 交付
- session `todayRot`：进本 `m.shortExpedition && key.equalsIgnoreCase(todayKey())` 写入；非今日本不标
- **W1a** 进本：title/AB/tell「今日推·本」（叠 EmberMapEnterFeel 短征行）
- **W1b** 分房：`onRoomStarted` AB「今日推 · 第N房」；同房节流 ≥15s
- **W1c** 有奖 settle：tell「今日推完成」+ chat `[再进今日推]`→`enter short-today`；满态走既有无奖诚实线
- 单测：`EmberTodayRotFeelTest` 谓词 / mark / 节流 / 文案 / session persist（5 PASS）
- jar：`1.65.812-d13320.local` · sha256 `7187d02e566d…` · ECONOMY_BV **123** 未动
- play：live `1.65.812-d13320.local` · storage=mysql · bv123 · MySQL tables ready
- **W1d 本窗不做**（选页半行）

## 落点
| 文件 | 改动 |
|------|------|
| `EmberTodayRotFeel` | 谓词 / mark / room throttle / enter·settle cue（新建） |
| `EmberRunSession` | `todayRot` + 节流 transient；`toMap`/`fromMap` |
| `EmberSessionService` | start 写 todayRot；verifyEntry enterCue |
| `EmberRunService.onRoomStarted` | todayRot → roomCue |
| `EmberShortService.settle` | 有奖 → settleCue |

## 零改边界
- 日帽 / S40–S58 数额 / todayKey 算法 / 体力扣费
- Cast / EliteAid / BossAid / HP / 掉落 / MM / 庭内菜单 / sx20
- afk.tiers / daily_kills / SoftPity / event_rate / NEED / Pack
- balance_version 123 · 无持久账本抬奖励

## 下步
- 等测岗抽测：今日本有标记、非今日无；零抬表
- W1d 选页半行另窗（不可独批）

## 测岗抽测（TIP-CO spot）
- **PASS** · 2026-10-14 Asia/Shanghai · 证据 `/workspace/tmp/tip-co-today-inrun/` · 详 `STATUS-ember-short-today-inrun-feel-tip-co-spot-2026-10-14.md`
- live jar `1.65.812-d13320.local` · 今日 sx12 烬枢转厅 · enter/room/settle 三节绿 · W1d 仍不做
