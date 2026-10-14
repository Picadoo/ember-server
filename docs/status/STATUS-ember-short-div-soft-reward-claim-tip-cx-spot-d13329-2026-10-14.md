# 状态 · TIP-CX 短征日差软目标极薄有奖抽测

**裁决：** **PASS** · feat `1b93bc6b4` · tip `0749d92c7` · 批 `e469ba35e` · jar `1.65.822-d13329.local` · 证据 `/workspace/tmp/tip-cx-short-div-soft/`  
**范围：** 满 soft 3 → L「日差软奖可领」→ `claim short-div-soft` 骨尘×2 · 日一次已领灰 · 未满仍 `enter short-div` · PAPI `short_div_claim_ok/_claimed/_claim_line` 诚实 · **零改** SOFT_TARGET=3 / 日帽 / targets.short / weekly_goals / Cast / Aid / variety / 路径追 / settle · 零 sx20 · 零半行独档 · Stage2 未拧 · bv123 · gate_daily=no · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| V0 | Stage2 三开 · daily_kills=2400 · ×0.97 · jar `1.65.822-d13329.local` · bv123 · soft=3 · tip 仅 short.yml · feat 零 Cast/Aid/settle/path · 零 sx20 · TrMenu 79 | **PASS** | `00-jar.json` · `00-switches-pre.json` · `00-tip-files.json` · `00-feat-zero-raise.json` · `00-trmenu.txt` |
| V1 | 满 soft 3 且未领：L 可领 · 骨尘×2 → claim 入仓/包 | **PASS** | n=3 · claim_ok=1 · L「日差软奖可领」gold_nugget · chat「日差软奖 · 骨尘 +2（今日一次 · 3/3）」· `40-menu-claimable.json` · `41-claim-grant.json` · settles sx12+sx01+sx02 |
| V2 | 日一次：再点已领灰 / `short_div_claimed` | **PASS** | claimed=1 · claim_ok=0 · line「已领 · 3/3」· L「日差本可冲 （今日已领）」gray · 再 claim 拒「今日日差软奖已领」· `50-claimed-once.json` |
| V3 | 未满：仍可 enter short-div chase；无误发 | **PASS** | 开局 n=0 claim 拒「未满软目标」· enter short-div 进 chase · `21-claim-reject-notfull.json` · `22-enter-short-div-notfull.json` |
| V4 | PAPI `short_div_claim_ok`/`_claimed`/`_claim_line` 诚实 | **PASS** | 未满→可领→已领 三态 · `20-papi-before.json` · `31-papi-after-settles.json` · `51-papi-honesty.json` · SoftClaim unit 7/0 |
| V5 | 零改 SOFT_TARGET/日帽/targets/Cast/Aid/variety/路径追/settle；零 sx20；零半行独档 | **PASS** | feat 仅 SoftClaim+Papi+Counters+RunService+plugin.yml · `00-feat-zero-raise.json` · `70-intact.json` · `90-endstate.json` |
| V8 | gate_daily=no | **PASS** | `60-gate-daily.json`「未知主线本 daily」· papi=no |

## 旁注

- TrMenu **79** 已载；short.yml live=repo md5 `d6575c0d…`
- jar sha256 `1880338c6e4c…` 与 plugins/ + server-runtime/ 一致 · HEAD 测前 `8010d5e34`
- 27/27 PASS · tip **未勾 CLOSED**（总控收口）
- 对照 TIP-CU：本票 = 满 soft 后极薄有奖 claim（TIP-CU 软目标仅展示零发奖）
- 对照 TIP-CW：settle 下一环未拧（feat 零 ShortSettle / short_settle.yml）
- claim 发奖路径 live chat 确认骨尘+2；`vault_bone` PAPI 测时未即时跳数（仓/NI 双路径 · SoftClaim Vault 优先 + NI 回落）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · short target=5 · daycap=3 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零 Cast/Aid/variety/路径追/settle/sx20 · ≠关观察 · 未改 YAML/jar · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `1b93bc6b4` · tip `0749d92c7` · STATUS `ed440e0e1` · **勿勾 CLOSED**（总控收口）· 2026-10-14 08:10 Asia/Shanghai
