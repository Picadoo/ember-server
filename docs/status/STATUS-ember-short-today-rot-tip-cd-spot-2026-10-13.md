# 状态 · TIP-CD 短征今日推（D · short-today）抽测

**裁决：** **PASS** · menu tip `5611a4337` · jar tip/feat `47c8d5af8` · jar **`1.65.805-d13309.local`** · 证据 `/workspace/tmp/tip-cd-short-today/`  
**范围：** `ember_p1_short` D「今日推」+ Open 半行 · 冒险 H 今日推半行 · `enter short-today` → 当日 `sx_today` · **零抬**日帽/S表/体力 · **≠关观察** · **未改** afk.tiers / daily_kills / Stage2 三开关 / gate_daily / ×0.97

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | D「今日推」名=`sx_today_name`（上海今日 **sx11 / 烬镜对廊**） | **PASS** | `30-papi.json` · `40-short-open-twice.json`（slot50 beacon） |
| 2 | 点 D / `enter short-today`：门禁同该本（Q01 拒 / 体力净扣 30 / 入场名片烬镜） | **PASS** | `20-q01-gate.json` · `50-enter-short-today.json`（stam 90→60） |
| 3 | 同日两次打开映射不变；跨日变由单测可证 | **PASS** | `40-short-open-twice.json` · `12-unit-rot.json`（EmberShortTodayRotTest 3/0） |
| 4 | Open tell / 冒险 H 有今日推半行；十九本/B/Z/满态未坏 | **PASS** | `10-menu-static.json` · `45-adventure-H.json` · Open「今日推：烬镜对廊」 |
| 5 | 零抬日帽/S表/体力；Stage2 未拧；gate_daily 仍拒 | **PASS** | `90-endstate.json` · sx11 cost=30 cap=3 · daily_kills=2400 ·「未知主线本 daily」 |

## 旁注

- 今日上海 epochDay=20739 → floorMod 10 → **sx11**（与 tip 钉死一致）
- TrMenu 77 已由总控侧 reload；本抽确认 live jar `1.65.805-d13309.local` · menus 自动载入 short/adventure
- 进本验证走 `enter short-today`（= D 左键 command）；TrMenu clickWindow 事务偶发无回包，不挡绿
- 单测 `EmberShortTodayRotTest`：mod 边界 + 同日稳定 + 负 epoch floorMod

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers md5 `51e2e286df1c` · ×0.97 未改 · ≠关观察 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB · gate_daily 仍拒
