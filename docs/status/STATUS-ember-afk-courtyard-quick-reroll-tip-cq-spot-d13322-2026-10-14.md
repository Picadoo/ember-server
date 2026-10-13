# 状态 · TIP-CQ 挂机庭顺手洗练（L）抽测

**裁决：** **PASS** · feat `a342f34cf` · tip `6804ed53a` · 批 `33d94eb40` · jar `1.65.814-d13322.local` · 证据 `/workspace/tmp/tip-cq-afk-reroll/`  
**范围：** `ember_p1_afk` Layout `#PLW2w3D#` · L「顺手洗练」→ `corerpg p1 reroll from afk` → `ember_p1_reroll` · 返回「返回挂机庭」→ `ember_p1_afk`（非工坊）· **未撤** E/e U/u R/r Q/q/P/D/W/w/S · **零第五花料** · **零改** 价/pity/pool / afk.tiers / daily_kills / Cast/Aid/variety / sx20 · Stage2 未拧 · bv123 · gate_daily=no · ≠关观察

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| V0 | Stage2 三开 · daily_kills=2400 · ×0.97 · jar `1.65.814-d13322.local` · bv123 · 价/pity/pool 零 diff · tip 仅 afk.yml · feat 仅 Growth/pom · 零 Cast/Aid/variety/sx20 · TrMenu 77 | **PASS** | `00-jar.json` · `00-switches-pre.json` · `00-tip-files.json` · `00-trmenu-reload77.txt` |
| V1 | 庭内见 L「顺手洗练」· 点进 `ember_p1_reroll`（from afk） | **PASS** | live slot11 `30-afk-open.json` · `55-click-L-reroll.json` title 词条洗练 |
| V2 | 洗练页返回「返回挂机庭」→ 回 `ember_p1_afk`（非误落工坊） | **PASS** | `55-click-L-reroll.json` backName/papi · `60-click-B-back.json` title 挂机庭 |
| V3 | 四花+P+D+W/w/S 仍在；Open/去哪花有「本页也可顺手洗练」辅半行 | **PASS** | `30-afk-open.json` · `70-spot-E-P-D-W-L.json` · `10-menu-static-fixed.json` |
| V4 | 零改 Cast/Aid/variety/sx20；零第五花；零半行独档；afk.tiers/reroll 指纹未拧 | **PASS** | tip/feat/batch 文件表 · `90-endstate.json` afk `51e2e286df1c` · reroll `0aaa91cdaa62` |
| V8 | gate_daily=no | **PASS** | `80-gate-daily.json`「未知主线本 daily」· papi=no |

## 旁注

- TrMenu reload **77** 已落地（latest.log 02:46 CST）；菜单 live=repo
- 静态初跑 extractIcon `/m $` EOL 假红（S1_L_icon / open_S / keys），已以 `10-menu-static-fixed.json` 重核绿（活态全程绿）
- Layout L 替 digit 1；Icon `1` 灰坡层格为死键保留（2/3/4 仍在）
- jar sha256 `fe7bb5ac8e3b…` 与 plugins/ + server-runtime/ 一致

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers 未拧 · reroll 价/pity/pool 未拧 · ×0.97 未改 · bv123 · ≠关观察 · 未改 YAML/jar · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **总控收口：** CLOSED live · feat `a342f34cf` · tip `6804ed53a` · PASS · 收口待 push · 2026-10-14 02:52 Asia/Shanghai
