# 状态 · TIP-CS 挂机庭顺手转化（C）抽测

**裁决：** **PASS** · feat `0eb85b785` · tip `b5aea9bc5` · PAPI `87f15944b` · 批 `3ab6a4792` · jar `1.65.816-d13324.local` · 证据 `/workspace/tmp/tip-cs-afk-convert/`  
**范围：** `ember_p1_afk` Layout `#PLWGwCD#` · C「顺手转化」→ `corerpg p1 convert from afk` → `ember_p1_convert` · 返回「返回挂机庭」→ `ember_p1_afk`（非工坊）· 工坊 V 命令 `convert from forge` · 返回「返回工坊」→ `ember_p1_forge` · **未撤** E/e U/u R/r Q/q/P/D/W/w/L/G/S · **零第五花料** · **零改** convert 周帽/价/族表 / afk.tiers / daily_kills / Cast/Aid/variety / sx20 · 零半行独档 · Stage2 未拧 · bv123 · gate_daily=no · ≠关观察 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| V0 | Stage2 三开 · daily_kills=2400 · ×0.97 · jar `1.65.816-d13324.local` · bv123 · WEEKLY_CAP=1/QUALITY_CAP=2 零 diff · tip 仅 afk+convert+forge.yml · feat Growth/papi/pom · 零 Cast/Aid/variety/sx20 · TrMenu 77 | **PASS** | `00-jar.json` · `00-switches-pre.json` · `00-tip-files.json` · `00-trmenu-reload77.txt` · `10-menu-static.json` |
| V1 | 庭内见 C「顺手转化」· 点进 `ember_p1_convert`（from afk） | **PASS** | live slot15 `30-afk-open.json` · `55-click-C-convert.json` title 每周转化 |
| V2 | 转化页返回「返回挂机庭」→ 回 `ember_p1_afk`（非误落工坊） | **PASS** | `55-click-C-convert.json` backName/papi · `60-click-R-back.json` title 挂机庭 |
| V3 | 工坊 V→from forge；返回「返回工坊」 | **PASS** | `66-forge-V-back.json` 命令路径（V 不在 Layout）· backName/papi 返回工坊 · title 余烬·锻造 |
| V4 | 四花+P+D+W/w/L/G/S/C 仍在；Open/去哪花有「本页也可顺手转化」辅半行 | **PASS** | `30-afk-open.json` · `70-spot-E-P-D-W-L-G-C.json` · `10-menu-static.json` |
| V5 | 零改 Cast/Aid/variety/sx20；零第五花；零半行独档；afk.tiers/reroll/brand/convertRules 指纹未拧 | **PASS** | tip/feat/batch 文件表 · `90-endstate.json` afk `51e2e286df1c` · convert `4fa25b2f44a5` · weekly 1 |
| V8 | gate_daily=no | **PASS** | `80-gate-daily.json`「未知主线本 daily」· papi=no |

## 旁注

- TrMenu reload **77** 已落地（latest.log 03:23–03:24 CST）；菜单 live=repo
- Layout C 替 digit 3；G 烙纹 / L 洗练仍在；对照 TIP-CR / TIP-CQ / TIP-BY..CL 庭内键未撤
- **工坊 V 不在 Layout**（既有债 · `NFGPmWbB#` 无 V）；Icon V 命令已改 `convert from forge`；V3 走命令路径绿 · 非本 tip 引入
- jar sha256 `18f4e64a0249…` 与 plugins/ + server-runtime/ 一致 · HEAD `87f15944b`（convert_back PAPI）
- 24/24 PASS · tip **未勾 CLOSED**（总控收口）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers 未拧 · EmberConvertRules WEEKLY_CAP/QUALITY_CAP/blanks/coins 未拧 · 洗练/烙纹 growth 未拧 · ×0.97 未改 · bv123 · ≠关观察 · 未改 YAML/jar · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `0eb85b785` · tip `b5aea9bc5` · **勿 CLOSED** · 2026-10-14 03:28 Asia/Shanghai
