# 状态 · TIP-CE 房间事件「冲一下」复测 PASS

**裁决：** **PASS** · menu tip `1b4a9d78e` · jar tip `59b6cf85b` · jar **`1.65.807-d13310b.local`** · 证据 `/workspace/tmp/tip-ce-event-chase-reverify/`  
**对照 FAIL：** spot `87941360f` · 证据 `/workspace/tmp/tip-ce-event-chase/` · 硬红「无 chase 仍火球」  
**范围：** `ember_p1_adventure` E 灰态改 `event_chase_ok < 1` → gray dye「无已通普通」· **零抬** event_rate/NEED/Pack · **≠关观察** · **未改** afk.tiers / daily_kills / Stage2 三开关 / gate_daily / ×0.97

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **硬** | 全 q*done=0 / chase_ok=0 → E **gray dye「无已通普通」**（非火球） | **PASS** | `20-no-chase-hard.json`：mat=`dye` · name含无已通普通 · chase_ok=`0` · enter拒「暂无可冲」 |
| 旁1 | 有 chase 可进 event-chase（门禁同 Q） | **PASS** | `50-enter-event-chase.json` / `30-papi.json`（q03 残誓地窖 · chase_ok=1） |
| 旁2 | 体力&lt;30 灰（iron_bars） | **PASS** | `40-stamina-gray.json` |
| 旁3 | 零抬 event_rate/NEED/Pack；Stage2 未拧 | **PASS** | `00-jar-tip-zero-raise.json` · `90-endstate.json` · rate=0.85 · NEED=1 · bv123 · daily_kills=2400 |
| 旁4 | gate_daily 仍拒 | **PASS** | `60-gate-daily.json`（未知主线本 daily · papi=no） |
| 旁5 | Open「冲一下见本页 E」；H今日推/Q七格 live | **PASS** | Open tell 命中 · `V4_H_Q_intact_live` qCount=7 |

## 硬绿（旧 FAIL 消）

- 旧：七段 `q*done and…` 复合未命中 → 默认火球可点（`21-no-chase-retest.json`）
- 新：单条件 `check papi %corerpg_p1_event_chase_ok% < 1` → **gray dye「无已通普通」**；`%corerpg_p1_event_chase_ok%`=0；点 E / `enter event-chase` 仍诚实拒

## 旁注

- live jar `1.65.807-d13310b.local` sha256 `81a0d8d7cf0b…` · TrMenu **77** reload（duplicate keys priority/display WARN 同 BY，E 结构可读非坏）
- 有 chase：featured→q03「残誓地窖」· stamina≥30 火球可进；体力=10 iron_bars「体力不足」
- soft_ready=0 未强制 dry；可进火球可辨于灰态
- 静态 `S1_H_Q_intact` 曾因 iconBlock 未吃引号键 `'1'`..`'7'` 假红；**live 七格在**，不挡绿
- **≠关观察** · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers md5 `51e2e286df1c` · ×0.97 未改 · event_rate 0.85 / event_core 1 / SoftPity.NEED 1 未抬 · jar tip 未碰 SoftPity/yml 产量

## tip / 产物

| 项 | 值 |
|----|-----|
| 修菜单 tip | **`1b4a9d78e`** |
| 修 jar tip | **`59b6cf85b`** |
| FAIL 上游 | **`87941360f`** |
| jar | `1.65.807-d13310b.local` |
| adventure md5 | `94722eec0eb2d5566a63d4b95fcb2fec` |
| 结论 | **PASS** |
