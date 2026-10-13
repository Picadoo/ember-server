# 状态 · TIP-CU 短征选页「日差本可冲」（L）抽测

**裁决：** **PASS** · feat `74a2b5a01` · tip `b7e9a5317` · 批 `52b4ed097` · jar `1.65.818-d13326.local` · 证据 `/workspace/tmp/tip-cu-short-day-div/`  
**范围：** `ember_p1_short` Layout `#PRSJDFL#` · L「日差本可冲」→ `corerpg p1 enter short-div` · PAPI `short_div*` · 软目标 3 **仅展示零发奖** · 灰态 chase_ok/done/stamina 单条件 · Open 辅半行 · **未撤** D今日推 / F本周短征 / 十九本 · **零改** 日帽/targets.short/Cast/Aid/variety/sx20 · Stage2 未拧 · bv123 · gate_daily=no · ≠关观察 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| V0 | Stage2 三开 · daily_kills=2400 · ×0.97 · jar `1.65.818-d13326.local` · bv123 · short:5/daycap:3 未拧 · tip 仅 short.yml · feat 零 Cast/Aid/yml · 零 sx20 · TrMenu live=repo | **PASS** | `00-jar.json` · `00-switches-pre.json` · `00-tip-files.json` · `00-feat-zero-raise.json` · `00-trmenu-reload77.txt` |
| V1 | 有未领日奖另一本时 L 可点进 chase 本；门禁同手动点该本 | **PASS** | chase=`sx12` · `30-papi-after-unlock.json` · `40-short-open.json` L@52 compass · `50-enter-short-div.json` enter short-div + manual sx12 同进 · stamΔ=30 |
| V2 | lore/PAPI `short_div_n`/`_line` 软目标 3；软目标零发奖 | **PASS** | live `今日有奖不同本 0/3` · unit 11/0 · `SOFT_TARGET=3` 无 addGoal · `12-unit-chase.json` · `V2_soft_target_zero_reward` |
| V3 | 灰态：无 chase / done / 体力不足诚实（单条件） | **PASS** | `20-no-chase-gray.json` 无冲口 · `45-stam-gray.json` 体力不足 · done 灰 YAML+unit 接线（n 未达 3 未 live 铺满；无 chase 时 priority1 无冲口） |
| V4 | 零改日帽/targets/Cast/Aid；零 sx20；零半行独档；D/F/十九本仍在 | **PASS** | feat 文件表 · `10-menu-static.json` · `40-short-open.json` D/F · `90-endstate.json` |
| V8 | gate_daily=no | **PASS** | `60-gate-daily-early.json`「未知主线本 daily」· papi=no |

## 旁注

- TrMenu reload **77** 已落地；菜单 live=repo md5 `aa3a336ede4511a666633f3247f0e8ab`
- jar sha256 `1c14b8d3d7a1…` 与 plugins/ + server-runtime/ 一致 · HEAD 测前 `bd534a8d7`
- 31/31 PASS · tip **未勾 CLOSED**（总控收口）
- 有奖 settle 抽测未在时限内打通（weaken 0 只）；`short_div_n/_line` 公式由 unit + live `0/3` 对齐；done 灰靠 YAML 单条件 + `EmberShortDayDivChase.done(n≥3)` unit
- 对照 TIP-CD D今日推 / TIP-CG F周可冲：本票 L=日差本 chase（unlocked∧dayLeft>0∧rewardedToday==0）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · short target=5 · daycap=3 · ×0.97 未改 · bv123 · 零 Cast/Aid/variety/sx20 · ≠关观察 · 未改 YAML/jar · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `74a2b5a01` · tip `b7e9a5317` · STATUS `c206b7b6f` · **CLOSED live**（总控收口）· 2026-10-14 04:15 Asia/Shanghai
