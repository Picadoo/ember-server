# 状态 · TIP-CK 挂机庭顺手分解（D）抽测

**裁决：** **PASS** · tip `b9e86a017` · jar `1.65.811-d13315.local` · 证据 `/workspace/tmp/tip-ck-afk-dismantle/`  
**范围：** `ember_p1_afk` Layout D（`#P1 2 3D#`）· `held_dismantle_yield` · `corerpg p1 dismantle` 预览→聊天 `[确认分解]` · **未撤** E/e U/u R/r Q/q/P · **零第五花料** · **零抬** dismantleYield/afk.tiers/daily_kills · Stage2 未拧 · ≠关观察

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | Layout 含 D（`#P1 2 3D#`）；Icon 名「顺手分解」；lore `%corerpg_p1_held_dismantle_yield%`；`command: corerpg p1 dismantle`+close | **PASS** | `10-menu-static-fixed.json` · live slot16「顺手分解」`30-afk-open.json` |
| 2 | Open / 去哪花 含「本页也可顺手拆」 | **PASS** | Open tell · S lore · `30-afk-open.json` |
| 3 | 手持可拆件点 D → 预览 tell + `[确认分解]` 可成交（未真拆） | **PASS** | junkseed drop→take→持 · `65-click-D-preview.json` 得胚×2 · confirm `tok:…` · 未点确认 |
| 4 | 不可拆件（admin give）点 D → 不假成功 | **PASS** | `45-click-D-admin.json`「这件是测试（不可分解）」 |
| 5 | E/U/R/Q/P 仍可点（抽 E/P） | **PASS** | live 全在 · `70-click-E-P-still.json` |
| 6 | 零第五花料；git tip 无 dismantleYield/afk.tiers/daily_kills；Stage2 未拧 | **PASS** | tip 仅 `ember_p1_afk.yml` · afk_tiers_md5 `51e2e286df1c` · daily_kills=2400 · 三开 true · taken_tele 0.97 · `90-endstate.json` |

## 旁注

- TrMenu reload **77** 已落地（latest.log 00:54 CST）；菜单 live=repo
- 静态初跑 extractIcon 缺 `m` 旗曾假红，已以 `10-menu-static-fixed.json` 重核绿（活态全程绿）
- 可拆路径：`junkseed`（src=drop）→ `gearlib take` → 庭内点 D；确认令牌已捕获但**未**执行真拆
- jar 仍为 `1.65.811-d13315.local`（本票 TrMenu-only，不要求新 jar）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers 未拧 · ×0.97 未改 · ≠关观察 · 未改 YAML/jar · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB
