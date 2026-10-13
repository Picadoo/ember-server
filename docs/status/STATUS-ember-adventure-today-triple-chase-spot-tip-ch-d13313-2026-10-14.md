# STATUS · TIP-CH 联验 D13313 冒险今日三冲 · 2026-10-14 Asia/Shanghai

> **VERDICT: PASS** · menu tip `76f0a6c5a` · jar tip `ef967a409` · jar `1.65.810-d13313.local` · sha256 `ccd19244e0adb3a6d66fa7766bc027ac7b31535825ddaae69dd0e90d72c08193` · TrMenu **77** · ECONOMY_BV **123**
> 证据 `/workspace/tmp/tip-ch-today-triple/` · 测岗证据-only（零改玩法码）

## 上游
- 菜单 tip `76f0a6c5a` feat(TIP-CH): adventure menu 今日三冲 P 格 + Open 指针
- 插件 tip `ef967a409` feat(TIP-CH): today_triple*/ok PAPI + today-triple enter · jar `1.65.810-d13313.local`
- live: CoreRpg `1.65.810-d13313.local` enabled · TrMenu reload 77（duplicate keys WARN 同构，非结构坏）

## 验收对照
| # | 项 | 结果 |
|---|---|---|
| 1 | 冒险页 P「今日三冲」· lore 三行+推荐名 · Open「今日三冲见本页 P」 | PASS · compass · 今日推/冲一下/本周短征+推荐烬枢转厅 |
| 2 | 点 P / `enter today-triple` · 智路由今日推有奖→周→事件 | PASS · kind=`today` → sx12 烬枢转厅；unit EmberTodayTripleTest 9/0 钉序 |
| 3 | 灰态单条件 ok&lt;1 / stamina&lt;30（非七段 and） | PASS · 暂无推荐 gray dye；体力不足 iron_bars |
| 4 | H/E 未撤；短征 D·F 未坏 | PASS · 短征 / 冲一下 / 今日推 / 本周短征可冲 同屏 |
| 5 | 零抬日帽/short:5/徽/event_rate/NEED；Stage2 未拧 | PASS · short:5 · reward:15 · event_rate:0.85 · dk:2400 · bv123 · tele×0.97 · Stage2 三开 |

## 旁注（不挡绿）
- 今日上海 epoch → **sx12** 烬枢转厅（相对 TIP-CD 日 sx11 已跨日）
- 进本「已预留体力 30」+ 首发减负 −5 → 净扣 25（今日一次）；门禁仍按 30；体力&lt;30 灰诚实
- dayclaim 管理命令对本抽未改 kind（仍 today）；路由序以单测 + 初态 kind=today 为准
- gate_daily 仍拒「未知主线本 daily」
- ≠关观察；login/proxy/MariaDB 未动；禁切分支/stash/reset

## 证据索引
`00-switches-pre.json` · `10-menu-static.json` · `12-unit-triple.json` · `20-no-ok-gray.json` · `30-papi-after-q01.json` · `40-adventure-open.json` · `41-short-DF.json` · `45-stam-gray.json` · `50-enter-today-triple.json` · `52-stam-gate-reinterpret.json` · `55-route-probe.json` · `60-gate-daily.json` · `90-endstate.json` · `99-summary.json`

## STATUS: PASS · TIP-CH D13313 · 2026-10-14 Asia/Shanghai
