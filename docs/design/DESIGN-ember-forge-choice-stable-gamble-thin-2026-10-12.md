# 余烬 · 工坊选择感 · 稳/旁路「选的是」薄窗

STATUS=**待批 A · 荐方案 M** · 2026-10-12  
上游 RESEARCH [`RESEARCH-ember-forge-choice-stable-gamble-thin-2026-10-12.md`](RESEARCH-ember-forge-choice-stable-gamble-thin-2026-10-12.md) · tip `TIP-ember-forge-choice-stable-gamble-thin-d12440-d12455-2026-10-12.md`  
资产：`EmberForgeChoice` · `EmberForgeService` · `EmberRunService.cmdForgeRoll`

确认前能说出稳线 vs 旁路 · **禁**改价/率 · **禁** glance twin。

## 方案 M（荐）

| 项 | 内容 |
|----|------|
| API | `stableChoice("成色"|"精工"|"强化")` → `§7选的是：§f稳线·{k}确定+1 §8· 非旁路赌锻` |
| | `gambleChoice(floor)` → 普通：`旁路·随机锻（极品仍0）`；下限：`旁路·下限锻（成色≥精良·极品仍0）` |
| 挂点 | 成色/精工 `!go` 预览（直办前仍发一句）；强化 `!go` 预览一句；forgeroll 预览在产物行后一句 |
| 冲突（可选） | forgeroll 预览且 `ForgeGoal`∈{ENHANCE,QUALITY,REFINE,UPGRADE,BRAND,GEM,LOCK} → 追加 `§8本周目标是稳线/确定路 · 此为旁路`（ROLL 目标则不喊） |
| 热关 | `forge_choice.stable_gamble: true` 缺省 true |
| 验收 | 同件：成色预览见稳线句；forgeroll 见旁路句；表/价 `git show` 不变；无新 hub 格 |

## 明确不做

- 抬 RATE / skill_mult / 极品权 / C06  
- 枢纽稳赌 glance densify · 新 PAPI  
- Affix · K3 · ConfirmTokens 种类海  

## 方案 R / W

| 方案 | 裁决 |
|------|------|
| R · 菜单再并排稳赌格 | **否**（TIP-H 已齐 · glance twin） |
| W · 抬下限权当假选择 | **否决** |
