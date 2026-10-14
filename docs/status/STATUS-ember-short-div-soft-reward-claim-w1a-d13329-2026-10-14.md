# STATUS · TIP-CX W1a 短征日差软目标极薄有奖 · D13329

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **CLOSED live** · PASS `ed440e0e1` · tip `0749d92c7` · 总控收口 2026-10-14 08:13 Asia/Shanghai

## 交付
- `EmberShortDayDivSoftClaim`：`claimedToday` / `claimable(n,claimed)` / `tryClaim` → period `p1_short_div_soft_claim`（day）+ Vault/NI `mat_ember_bone_dust`×2
- 复用 `EmberShortDayDivChase.SOFT_TARGET`（=3）——**未改该数**；n 仍走 `distinctRewarded`
- PAPI：`%corerpg_p1_short_div_claim_ok%` / `_claimed` / `_claim_line`（可领/已领/未满 · 进度 n/3）
- 命令：`/corerpg p1 claim short-div-soft`（别名 `short_div_soft` / `short-div-soft-claim`）· 菜单调；拒领诚实提示
- 单测：`EmberShortDayDivSoftClaimTest`（n&lt;3 拒 / n≥3 可领 / 已领拒 / period latch / claim_ok·claimed·line / SOFT_TARGET 钉死）；`EmberRunPapiTest` 路由三键
- `EmberCounters`：登记 `p1_short_div_soft_claim`
- jar：`1.65.822-d13329.local` · sha256 `1880338c6e4c…` · ECONOMY_BV **123** 未动
- play：live `1.65.822-d13329.local` · storage=mysql · bv123 · MySQL connected
- feat：`1b93bc6b4` → origin/main
- **零改** SOFT_TARGET / chase·enter 谓词 / 日帽 / S40–S58 / targets.short / weekly_goals / Cast / Aid / variety / 路径追 / settle / ShortPath resolve / 菜单 yml
- **未做** 菜单 W1b（物品岗 L 可领态）

## 键/命令
| 项 | 口径 |
|----|------|
| `short_div_claim_ok` | claimable→1 否则 0 |
| `short_div_claimed` | 今日已领→1 |
| `short_div_claim_line` | 可领 · 骨尘×2 · n/3 \| 已领 · n/3 \| 未满 · n/3 |
| `claim short-div-soft` | tryClaim；一日一次 |

## 落点
- `EmberShortDayDivSoftClaim`（新）
- `EmberRunPapi.shortDayDiv`：三 claim 键
- `EmberRunService.cmd`：claim 别名 → `cmdShortDivSoftClaim`
- `EmberCounters`：period 登记
- `pom.xml` / `plugin.yml`：1.65.822-d13329.local

## 零改边界
- SOFT_TARGET=3 · chase/enter · 日帽 · S40–S58 · weekly_goals.targets.short
- Cast / Aid / variety / 路径追 / settle / ShortPath resolve · TrMenu yml
- balance_version 123 · sx20 · event_core

## 下步
- 物品 W1b / 测岗均已齐 · 总控收口 CLOSED

## 测岗抽测（TIP-CX spot）
- **PASS** · 2026-10-14 08:13 Asia/Shanghai · 证据 `/workspace/tmp/tip-cx-short-div-soft/` · 详 `STATUS-ember-short-div-soft-reward-claim-tip-cx-spot-d13329-2026-10-14.md`
- live jar `1.65.822-d13329.local` · feat `1b93bc6b4` · tip `0749d92c7` · 批 `e469ba35e` · 满 soft3→骨尘×2 · 日一次已领灰

## 续 · 测岗抽测 PASS（2026-10-14 Asia/Shanghai · 余烬-测试）
- PASS live · feat `1b93bc6b4` · 证据 `/workspace/tmp/tip-cx-short-div-soft/` · STATUS `ed440e0e1`
- **CLOSED live**（总控收口）· tip `0749d92c7` · jar `1.65.822-d13329.local`
