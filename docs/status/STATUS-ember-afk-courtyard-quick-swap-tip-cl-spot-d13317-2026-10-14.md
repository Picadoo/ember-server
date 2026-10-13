# 状态 · TIP-CL 挂机庭顺手互换（W/w）抽测

**裁决：** **PASS** · tip `4671d3ea4` · jar `1.65.811-d13315.local` · 证据 `/workspace/tmp/tip-cl-afk-swap/`  
**范围：** `ember_p1_afk` Layout `#P1W2w3D#` · W/w 顺手互换 · `held_swap_cost` · `corerpg p1 swap` / `swap confirm`（w 无 close）· 甲灰 `held_is_armor==1` · **未撤** E/e U/u R/r Q/q/P/D/S · **零第五花料** · **零改** Swap价/afk.tiers/daily_kills · Stage2 未拧 · ≠关观察

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | Layout `#P1W2w3D#`；Icon W「顺手互换」→ `corerpg p1 swap`；w → `swap confirm`（无 close）；lore `%corerpg_p1_held_swap_cost%`；甲灰 `held_is_armor==1` | **PASS** | `10-menu-static-fixed.json` · live slot12/14 `30-afk-open.json` |
| 2 | Open / 去哪花 含「本页也可顺手换轨」 | **PASS** | Open tell · S lore · `30-afk-open.json` |
| 3 | 手持刃点 W → 预览；点 w → confirm 路径诚实（未真换光） | **PASS** | `55-click-W-preview.json` / `60-click-w-confirm.json`「互换要两件同部位…再点预览」 |
| 4 | 手持甲 → W/w 灰 + tell，不假成功 | **PASS** | `70-afk-armor.json` 灰玻璃 · `75-click-Ww-armor.json`「护甲随护符…」 |
| 5 | 抽测 E 或 P、D 仍在 | **PASS** | live 全在 · `80-click-E-D-still.json` |
| 6 | E/e/U/u/R/r/Q/q/P/D/S 仍在；git tip 仅 afk.yml；零改 Swap价/afk.tiers/daily_kills；Stage2 未拧 | **PASS** | tip 仅 `ember_p1_afk.yml` · afk_tiers_md5 `51e2e286df1c` · daily_kills=2400 · 三开 true · taken_tele 0.97 · `90-endstate.json` |

## 旁注

- TrMenu reload **77** 已落地（latest.log 01:05 CST）；菜单 live=repo
- 静态初跑 extractIcon `/m $` EOL 假红，已以 `10-menu-static-fixed.json` 重核绿（活态全程绿）
- 刃路径：双 give blade → 庭内点 W/w；服务端诚实要求副手同部位（未真换）
- 甲路径：`corerpg p1 give scorch head` → held_is_armor=1 → W/w 灰
- jar 仍为 `1.65.811-d13315.local`（本票 TrMenu-only，不要求新 jar）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers 未拧 · ×0.97 未改 · ≠关观察 · 未改 YAML/jar · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **总控收口：** CLOSED live · menu `4671d3ea4` · PASS · 2026-10-14 01:11 Asia/Shanghai
