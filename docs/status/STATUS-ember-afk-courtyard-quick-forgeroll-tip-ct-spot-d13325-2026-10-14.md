# 状态 · TIP-CT 挂机庭顺手随机锻（O）抽测

**裁决：** **PASS** · feat `2705550cd` · tip `07738f8bb` · PAPI `788f7f788` · 批 `08e77d55d` · jar `1.65.817-d13325.local` · 证据 `/workspace/tmp/tip-ct-afk-forgeroll/`  
**范围：** `ember_p1_afk` Layout `#USOQVRE#` · O「顺手随机锻」→ `corerpg p1 forgeroll from afk` → `ember_p1_forgeroll` · 返回「返回挂机庭」→ `ember_p1_afk`（非工坊）· 工坊 F `forgeroll from forge` · 返回「返回工坊」→ `ember_p1_forge` · **未撤** E/e U/u R/r Q/q/P/D/W/w/L/G/C/S/V/F · **零第五花料** · **零改** forgeroll 价/权重/QUALITY_W{70,23,7,0} / afk.tiers / daily_kills / Cast/Aid/variety / sx20 / densify · 零半行独档 · Stage2 未拧 · bv123 · gate_daily=no · ≠关观察 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| V0 | Stage2 三开 · daily_kills=2400 · ×0.97 · jar `1.65.817-d13325.local` · bv123 · QUALITY_W/FLOOR 零 diff · tip 仅 afk+forgeroll+forge.yml · feat RunService/Papi · 零 Cast/Aid/variety/sx20 · TrMenu 77 | **PASS** | `00-jar.json` · `00-switches-pre.json` · `00-tip-files.json` · `00-trmenu-reload77.txt` · `10-menu-static.json` |
| V1 | 庭内见 O「顺手随机锻」· 点进 `ember_p1_forgeroll`（from afk） | **PASS** | live slot21 `30-afk-open.json` · `55-click-O-forgeroll.json` title 余烬·随机锻造 |
| V2 | 随机锻页返回「返回挂机庭」→ 回 `ember_p1_afk`（非误落工坊） | **PASS** | `55-click-O-forgeroll.json` backName/papi · `60-click-R-back.json` title 挂机庭 |
| V3 | 工坊 F→from forge；返回「返回工坊」 | **PASS** | `66-forge-F-back.json` F@46 点击 · backName/papi 返回工坊 · title 余烬·锻造 |
| V4 | 四花+P+D+W/w/L/G/C/S/O/V/F 仍在；Open/去哪花有「本页也可顺手随机锻」辅半行 | **PASS** | `30-afk-open.json` · `70-spot-E-P-D-W-L-G-C-O.json` · `10-menu-static.json` |
| V5 | 零改 Cast/Aid/variety/sx20；零第五花；零 densify；零半行独档；afk.tiers/reroll/brand/forgerollRules 指纹未拧 | **PASS** | tip/feat/batch 文件表 · `90-endstate.json` afk `51e2e286df1c` · roll `b3246dd1a317` · QUALITY_W {70,23,7,0} |
| V8 | gate_daily=no | **PASS** | `80-gate-daily.json`「未知主线本 daily」· papi=no |

## 旁注

- TrMenu reload **77** 已落地（latest.log 03:38–03:39 CST）；菜单 live=repo
- Layout O 替 digit 4；C 转化 / G 烙纹 / L 洗练仍在；对照 TIP-CS / TIP-CR / TIP-CQ 庭内键未撤
- 工坊 F **在 Layout**（`NFGPmWbB#` F@46）；V3 点 F 绿
- jar sha256 `0205fd183e49…` 与 plugins/ + server-runtime/ 一致 · HEAD `788f7f788`（forgeroll_back PAPI）
- 24/24 PASS · tip **未勾 CLOSED**（总控收口）
- V0_price 初跑假红：featDiff 上下文含既有 marks()/blanks() 用法行；+行零价改；已剔

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers 未拧 · EmberForgeRollRules QUALITY_W/FLOOR/marks/blanks/coins 未拧 · 洗练/烙纹/转化 growth+rules 未拧 · ×0.97 未改 · bv123 · ≠关观察 · 未改 YAML/jar · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `2705550cd` · tip `07738f8bb` · **勿 CLOSED** · 2026-10-14 03:45 Asia/Shanghai
