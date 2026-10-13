# 状态 · TIP-CV 短征选页「路径追」（Icon 0 + 薄页）抽测

**裁决：** **PASS** · feat `c31c780a9` · tip `a206b9893` · 批 `ac2889160` · jar `1.65.819-d13327.local` · 证据 `/workspace/tmp/tip-cv-short-path/`  
**范围：** `ember_p1_short` Layout `#I0H B Z#` · Icon `0`「路径追」→ 薄页 `ember_p1_short_path` · 三偏好 fc/day/late + clear · `enter short-path` · PAPI `short_path*` · 灰态未选/无 resolve/体力不足 · Open 辅半行 · **未撤** D今日推 / F本周短征 / L日差 / 十九本 · **零改** 日帽/targets.short/Cast/Aid/variety/short_div*/sx20 · Stage2 未拧 · bv123 · gate_daily=no · ≠关观察 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| V0 | Stage2 三开 · daily_kills=2400 · ×0.97 · jar `1.65.819-d13327.local` · bv123 · short:5/daycap:3 未拧 · tip 仅 short+short_path yml · feat 零 Cast/Aid/yml · 零 sx20 · TrMenu live=repo · TrMenu 78 | **PASS** | `00-jar.json` · `00-switches-pre.json` · `00-tip-files.json` · `00-feat-zero-raise.json` · `00-feat-zero-raise-cleared.json` · `00-trmenu-reload78.txt` |
| V1 | 可切换三偏好 fc/day/late + clear；偏好切换后 lore/PAPI label·resolve 变；选页见 0；薄页 F/D/L/C/E | **PASS** | triad fc/day/late 标签变 · `30-triad-switch.json` · Icon0@11 `40-short-open.json` · 薄页 `41-thin-page.json` · resolve=sx01 `42-pick-resolve.json` |
| V2 | 有 resolve 时「进下一本」真进本；门禁同手动点该本 | **PASS** | `50-enter-short-path.json` enter short-path → sx01 烬门哨岗 · manual sx01 同进 · stamΔ≈25–30 |
| V3 | 未选 / 无 resolve / 体力不足灰态诚实；未选时 enter 拒进（禁静默改偏好） | **PASS** | clear ok=0 · 拒进「请先选短征路径偏好」且 path 仍 0 · `20-clear-none.json` `21-enter-none-reject.json` · E 灰「未选/无 resolve」`46-none-gray.json` · 体力不足 iron_bars `45-stam-gray.json` |
| V4 | 零改日帽/targets/Cast/Aid/variety/short_div*；零 sx20；零半行独档；D/F/L/十九本仍在 | **PASS** | feat 文件表 · `10-menu-static.json` · `40-short-open.json` D/F/L · `70-short-div-intact.json` 日差线仍在 · `90-endstate.json` |
| V8 | gate_daily=no | **PASS** | `60-gate-daily.json`「未知主线本 daily」· papi=no |

## 旁注

- TrMenu reload **78** 已落地；菜单 live=repo short md5 `e08cb138…` · path md5 `244b047c…`
- jar sha256 `197d6d3e7cbc…` 与 plugins/ + server-runtime/ 一致 · HEAD 测前 `a167a1523`
- 32/32 PASS · tip **未勾 CLOSED**（总控收口）
- V4 初跑 harness 误报 `short_div_touched`（feat 注释/路由表旁注 short_div* 键名）；已用 `00-feat-zero-raise-cleared.json` 澄清：无 DayDiv/yml/Cast/Aid 文件改动
- 对照 TIP-CU L日差 / TIP-CD D今日推 / TIP-CG F周冲：本票 = 路径偏好追（ShortPath resolve 复用）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · short target=5 · daycap=3 · ×0.97 未改 · bv123 · 零 Cast/Aid/variety/short_div*/sx20 · ≠关观察 · 未改 YAML/jar · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `c31c780a9` · tip `a206b9893` · STATUS `f9e4ca402`· **CLOSED live**（总控收口）（总控收口）· 2026-10-14 04:32 Asia/Shanghai

> **测岗抽测：** PASS live · feat `c31c780a9` · tip `a206b9893` · STATUS `f9e4ca402` · **CLOSED live**（总控收口）· 2026-10-14 04:33 Asia/Shanghai
