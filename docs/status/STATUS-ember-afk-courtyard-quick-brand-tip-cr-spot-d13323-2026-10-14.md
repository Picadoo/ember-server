# 状态 · TIP-CR 挂机庭顺手烙纹（G）抽测

**裁决：** **PASS** · feat `cf35d9948` · tip `ce2f8c95f` · 批 `d1004caaf` · jar `1.65.815-d13323.local` · 证据 `/workspace/tmp/tip-cr-afk-brand/`  
**范围：** `ember_p1_afk` Layout `#PLWGw3D#` · G「顺手烙纹」→ `corerpg p1 brand from afk` → `ember_p1_brand` · 返回「返回挂机庭」→ `ember_p1_afk`（非工坊）· 工坊 G → `brand from forge` · 返回「返回工坊」→ `ember_p1_forge` · **未撤** E/e U/u R/r Q/q/P/D/W/w/L/S · **零第五花料** · **零改** craftShards/定向价/次数 / 洗练 pity / afk.tiers / daily_kills / Cast/Aid/variety / sx20 · 零宝石本窗 · 零半行独档 · Stage2 未拧 · bv123 · gate_daily=no · ≠关观察

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| V0 | Stage2 三开 · daily_kills=2400 · ×0.97 · jar `1.65.815-d13323.local` · bv123 · craftShards=40/定向价/次数/洗练 pity 零 diff · tip 仅 afk+brand+forge.yml · feat Growth/papi/pom · 零 Cast/Aid/variety/sx20 · TrMenu 77 | **PASS** | `00-jar.json` · `00-switches-pre.json` · `00-tip-files.json` · `00-trmenu-reload77.txt` · `10-menu-static-fixed.json` |
| V1 | 庭内见 G「顺手烙纹」· 点进 `ember_p1_brand`（from afk） | **PASS** | live slot13 `30-afk-open.json` · `55-click-G-brand.json` title 烙纹定向 |
| V2 | 烙纹页返回「返回挂机庭」→ 回 `ember_p1_afk`（非误落工坊） | **PASS** | `55-click-G-brand.json` backName/papi · `60-click-b-back.json` title 挂机庭 |
| V3 | 工坊 G→from forge；返回「返回工坊」 | **PASS** | `65-forge-open.json` G@47 · `66-forge-G-back.json` backName/papi 返回工坊 · title 余烬·锻造 |
| V4 | 四花+P+D+W/w/L/S 仍在；Open/去哪花有「本页也可顺手烙纹」辅半行 | **PASS** | `30-afk-open.json` · `70-spot-E-P-D-W-L-G.json` · `10-menu-static-fixed.json` |
| V5 | 零改 Cast/Aid/variety/sx20；零第五花；零洗练再拧；零宝石本窗；零半行独档；afk.tiers/reroll/brand 指纹未拧 | **PASS** | tip/feat/batch 文件表 · `90-endstate.json` afk `51e2e286df1c` · reroll `0aaa91cdaa62` · brand `b902def314af` · craft 40 |
| V8 | gate_daily=no | **PASS** | `80-gate-daily.json`「未知主线本 daily」· papi=no |

## 旁注

- TrMenu reload **77** 已落地（latest.log 03:04 CST 启服载入）；菜单 live=repo
- 静态初跑 extractIcon `/m $` EOL 假红（S1_G_icon / open_S / keys / brand_b）+ craftShards() 上下文误触 pity 检；已以 `10-menu-static-fixed.json` 重核绿（活态全程绿）
- Layout G 替 digit 2；L 洗练仍在；对照 TIP-CQ / TIP-BY..CL 庭内键未撤
- jar sha256 `e7de5a90f3a4…` 与 plugins/ + server-runtime/ 一致

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers 未拧 · craftShards/pin_brand/MAX_CHARGES 未拧 · 洗练 pity/pool 未拧 · ×0.97 未改 · bv123 · ≠关观察 · 未改 YAML/jar · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **总控收口：** CLOSED live · feat `cf35d9948` · tip `ce2f8c95f` · PASS · 收口待 push · 2026-10-14 03:10 Asia/Shanghai
