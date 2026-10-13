# 状态 · TIP-CJ 挂机庭顺手推荐花（P）抽测

**裁决：** **PASS** · tip `09fd8d29e` · menu `f85daf617` · jar `1.65.811-d13315.local` · 证据 `/workspace/tmp/tip-cj-afk-rec-flower/`  
**范围：** `ember_p1_afk` Layout P · `afk_rec_flower*` / `_ok` · enter `afk-rec-flower` **预览** · 优先 enhance→upgrade→refine→quality · `_ok=0` 灰→工坊 · **零第五花料** · **未撤** E/e U/u R/r Q/q · **零抬** daily_kills/afk.tiers/价 · Stage2 未拧 · ≠关观察

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 庭内见 P「顺手推荐花」；Open「仓够推荐见本页 P」；去哪花可提推荐花 | **PASS** | `10-menu-static-fixed.json` · `30-afk-gray.json` · `60-afk-bright.json` |
| 2 | 仓够点 P → enter afk-rec-flower → **预览**（非 confirm）；kind 优先 enhance | **PASS** | `50-papi-bright.json` kind=enhance · `65-click-P-preview.json` · `66-enter-afk-rec-flower.json`（强化预览；无成功/已扣） |
| 3 | `_ok=0` 灰→工坊；单条件诚实 | **PASS** | `20-papi-gray.json` ok=0/empty · `40-click-P-gray.json` →「余烬 · 锻造」 |
| 4 | E/e/U/u/R/r/Q/q 未撤且仍可用 | **PASS** | Layout `#US4QVRE#`/`#ueIqFrH#` · live 全在 · `70-click-E-still.json` 强化预览 |
| 5 | 零第五花料；零抬 daily_kills/afk.tiers/价；Stage2 未拧 | **PASS** | tip 仅 RecFlower/Papi/enter · afk_tiers_md5 `51e2e286df1c` · daily_kills=2400 · 三开 true · taken_tele 0.97 · gate_daily=no · `90-endstate.json` |

## 旁注

- 仓够抽测以 shard 入库 → enhance 优先（gap=0）；币不足时预览仍诚实报缺币，**未**静默 confirm
- TrMenu reload **77** 已落地（latest.log 00:35/00:38）；菜单 live=repo `ember_p1_afk.yml`
- 静态初跑 extractIcon 缺 `m` 旗曾假红，已以 `10-menu-static-fixed.json` 重核绿（活态全程绿）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers 未拧 · ×0.97 未改 · gate_daily=no · ≠关观察 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB
