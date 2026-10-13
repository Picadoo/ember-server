# 状态 · TIP-CW 短征结算「下一环」菜单化抽测

**裁决：** **PASS** · feat `8c2dc7d95` · 入口 `d13eabcba` · 菜单 tip `374a142fd` · jar `1.65.821-d13328b.local` · 证据 `/workspace/tmp/tip-cw-short-settle/`  
**范围：** 有奖 settle → `[下一环]` 开 `ember_p1_short_settle` · PAPI `short_settle_map|name|day_left|day_line` · 再进真 enter 本图 · 路径/工坊真开菜单 · `spendChaseButtonCmd`→`/ember_p1_forge` · 无教手打 shortpath · todayRot settleCue 保持 `enter short-today` · 灰态日奖满/体力不足 · **零改** resolve/日帽/targets/Cast/Aid/variety/short_div*/sx20 · Stage2 未拧 · bv123 · gate_daily=no · **勾毕 CLOSED live**（总控收口 · 2026-10-14 07:47 Asia/Shanghai）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| V0 | Stage2 三开 · daily_kills=2400 · ×0.97 · jar `1.65.821-d13328b.local` · bv123 · short:5 · 零 sx20 · TrMenu 79 · settle live=repo · feat/入口 ancestor | **PASS** | `00-jar.json` · `00-switches-pre.json` · `00-feat-zero-raise.json` · `00-trmenu.txt` |
| V1 | 有奖 settle → `[下一环]`；菜单开 `ember_p1_short_settle`（非教手打） | **PASS** | sx12 有奖通关 · chat `[下一环]` · `40-settle.json` · 菜单 title「短征 · 下一环」`51-settle-menu.json` |
| V2 | PAPI map/name/day_left/day_line 有值；点「再进」进刚才短征图 | **PASS** | map=`sx12` name=烬枢转厅 left=2 line=`今日有奖 1/3` · `50-papi.json` · 再进→主线本开始 `52-reenter.json` |
| V3 | 灰态：日奖满 YAML 接线 · 体力不足 live iron_bars | **PASS** | `56-stam-gray.json`「再进本图 （体力不足）」· `57-day-gray.json` E_day_gray 接线 · left live=2 |
| V4 | 路径/工坊入口改开菜单；spendChase→`/ember_p1_forge`；无「可改 shortpath」裸指令；todayRot settleCue 不教手打 | **PASS** | 路径页/工坊页真开 `53`/`54` · unit NextLoop `12-unit-next-loop.json` · settle 无 shortpath/forge 裸教 `40-settle.json` |
| V5 | 零改 resolve/日帽/targets/Cast/Aid/variety/short_div*；零 sx20；零半行独档 | **PASS** | feat 文件表仅 ShortSettle/Rules/Service/Path教清/RunPapi · `00-feat-zero-raise.json` · `70-intact.json` 日差线仍在 |
| V8 | gate_daily=no | **PASS** | `60-gate-daily.json` |

## 旁注

- TrMenu **79** 已载 settle 薄页；md5 `a2b9a178…` live=repo
- jar sha256 `0305c99077da…` 与 plugins/ + server-runtime/ 一致 · HEAD `8c2dc7d95`
- 29/29 PASS · tip **勾毕 CLOSED live**（总控收口）
- 对照 TIP-CO todayRot settleCue / TIP-CV 路径追 / TIP-CU 日差：本票 = 结算后下一环菜单化

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · short target=5 · daycap=3 · ×0.97 未改 · bv123 · 零 Cast/Aid/variety/short_div*/sx20 · ≠关观察 · 未改 YAML/jar · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `8c2dc7d95` · tip `374a142fd` · STATUS `72e8e6a4e` · **CLOSED live**（总控收口）· 2026-10-14 07:47 Asia/Shanghai
