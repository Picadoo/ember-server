# STATUS · TIP-CO 联验 D13320 短征今日推本局内可感 · 2026-10-14 Asia/Shanghai

> **VERDICT: PASS** · feat `1a3e1a3de` · jar `1.65.812-d13320.local` · sha256 `7187d02e566d…` · ECONOMY_BV **123**
> 证据 `/workspace/tmp/tip-co-today-inrun/` · 测岗证据-only（零改玩法/YAML/jar）

## 上游
- feat `1a3e1a3de` feat(TIP-CO): todayRot enter/room/settle feel for short today (D13320)
- live: CoreRpg `1.65.812-d13320.local` · play 已上 · storage=mysql · bv123
- 插件岗 STATUS：`docs/status/STATUS-ember-short-today-inrun-feel-2026-10-14.md`

## 验收对照
| # | 项 | 结果 |
|---|---|---|
| V0 | Stage2 三开 · daily_kills=2400 · ×0.97 · S 表未拧 · jar 对上 | **PASS** · six_slot enabled/migrate/set_bonus · dk2400 · tele0.97 · sx12 cost30/cap3/coin80 · live `1.65.812-d13320.local` |
| V1 | 进 todayKey 本见「今日推·本」title/AB/tell；非今日本无标记 | **PASS** · sx12 烬枢转厅：title+AB+tell 全绿；sx01 无「今日推·本」/第N房 |
| V2 | todayRot 开第2房 AB「今日推 · 第N房」；同房15s不刷；非 todayRot 无 | **PASS** · 第1/2/3房均见；同房重进 thAbs=[]；sx01 环枢侧厢无今日推房 AB |
| V3 | 有奖结算「今日推完成」+ `[再进今日推]`；满态诚实 | **PASS** · 日奖1/3 结算见完成+按钮；再进 cmd 触达（DP 5s 冷却诚实退体力）；settleCue 需 pays |
| V4 | 零改 Cast/Aid yml、零 sx20、零庭内菜单、W1d 未做 | **PASS** · tip 仅 java+docs；skills/afk_menu md5 未变；无 sx20；W1d 本窗不做 |
| V8 | gate_daily=no | **PASS** ·「未知主线本 daily」· papi `no` |

## 旁注（不挡绿）
- 今日上海 epochDay=20740 → **sx12 / 烬枢转厅**（相对 TIP-CD 日 sx11 已跨日）
- 单测 `EmberTodayRotFeelTest` 5/0（谓词/mark/节流/文案/persist）
- 再进今日推：实例关闭后立刻 `enter short-today` 触 DP「少于 5 秒」冷却，预留体力已退还——门禁诚实，非按钮坏
- 握刃：ni `gear_ember_blade`；钻石剑兜底（本抽偶发「主手没有刃」提示，不挡通关）
- ≠关观察；login/proxy/MariaDB 未动；禁切分支/stash/reset/force-push；未改 YAML/jar

## 证据索引
`00-switches-pre.json` · `00-tip-files.json` · `05-live-jar.json` · `12-unit-feel.json` · `13-epoch-expect.json` · `30-papi.json` · `50-today-cues.json` · `51-nontoday-cues.json` · `52-nontoday-room2.json` · `60-today-room2.json` · `61-room-throttle.json` · `70-settle.json` · `71-reenter.json` · `80-gate-daily.json` · `90-endstate.json` · `99-summary.json` · `tip-co-spot.js` · `run.log`

## STATUS: PASS · TIP-CO D13320 · 2026-10-14 Asia/Shanghai

> **总控收口：** CLOSED live · feat `1a3e1a3de` · PASS `bd7a68ce8` · jar `1.65.812-d13320.local` · 2026-10-14 02:14 Asia/Shanghai
