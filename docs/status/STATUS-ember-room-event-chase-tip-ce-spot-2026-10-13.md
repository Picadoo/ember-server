# 状态 · TIP-CE 房间事件「冲一下」抽测

**裁决：** **FAIL** · menu tip `6916b3ce0` · jar tip/feat `1e4a1e5b7` · jar **`1.65.806-d13310.local`** · 证据 `/workspace/tmp/tip-ce-event-chase/`  
**范围：** `ember_p1_adventure` E「房间事件 · 冲一下」+ Open 半行 · `enter event-chase` · **零抬** event_rate/NEED/Pack · **≠关观察** · **未改** afk.tiers / daily_kills / Stage2 三开关 / gate_daily / ×0.97

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | E「房间事件 · 冲一下」；lore soft_hope + chase_name | **PASS** | `55-enter-retest.json`（残誓地窖 / 事件保底 0/1）· `10-menu-static.json` |
| 2 | 点 E → enter event-chase；门禁同该 Q 本（预留 30） | **PASS** | `55-enter-retest.json`（Q03 创建实例 · stam 90→60）· 点击同拒 |
| 3a | 体力&lt;30 灰（iron_bars） | **PASS** | `40-stamina-gray.json` / `55-enter-retest.json` |
| 3b | **无 chase 灰**（gray dye「无已通普通」） | **FAIL** | `21-no-chase-retest.json`：全 q*done=0 / chase 空 仍 **fire_charge**「点击进本」；点 E 命令诚实拒「暂无可冲」 |
| 3c | soft_ready≥1 亮 | **旁注** | soft_ready=0（dry 未攒）；默认 fire_charge 可进可辨于体力灰；shiny 态静态有 |
| 4 | Open「冲一下见本页 E」；H今日推/Q七格未坏 | **PASS** | Open tell 命中 · H 短征 · Q1–7 七格在 |
| 5 | git 无抬 event_rate/NEED/Pack；Stage2 未拧 | **PASS** | `00-jar-tip-zero-raise.json` · event_rate=0.85 · NEED=1 · bv123 · daily_kills=2400 |

## 硬红（挡绿）

- **无 chase 灰态未命中**：E icons priority1 条件为七段 `check papi %q0Ndone% < 1 and …`。实测全 0 仍落默认火球可点（同 D408 复合 and 未命中类）。建议改单条件（如 chase 空 / 嵌套单 check），TrMenu reload 后复测。
- 服务端门禁诚实：无 chase 时 `enter event-chase` / 点 E 均拒「暂无可冲的已通普通本」——**不**构成假结算；仅菜单外观灰态缺。

## 旁注

- live jar `1.65.806-d13310.local` sha256 `17dd90f68ee3…` · TrMenu 77 已载 · adventure 自动/手动 reload 后仍复现灰态缺
- chase 解析：featured 未通 → 已通最高 q03「残誓地窖」（unlock+firstclear 后进本绿）
- soft_ready=0 未强制 dry；shiny 分支静态 `event_soft_ready% >= 1` + shiny:true 在
- gate_daily 仍拒「未知主线本 daily」· ≠关观察 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers md5 `51e2e286df1c` · ×0.97 未改 · event_rate 0.85 / event_core 1 / SoftPity.NEED 1 未抬
