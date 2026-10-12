# 余烬 · 套装手感 · 觉醒升档白话薄窗

STATUS=**待批 A · 荐方案 M** · 2026-10-12  
上游 RESEARCH [`RESEARCH-ember-set-awaken-feel-thin-2026-10-12.md`](RESEARCH-ember-set-awaken-feel-thin-2026-10-12.md) · tip `TIP-ember-set-awaken-feel-thin-d12392-d12407-2026-10-12.md`  
资产：`EmberSetRules` · `EmberAwakenBreak` · `EmberSetFeel`

觉醒 I→II→III 突破时说清「手感强在哪」· **禁**改 coef 表 · **禁**枢纽 glance twin。

## 方案 M（荐）

| 项 | 内容 |
|----|------|
| 触发 | `after.awakening > prevAwk` 且本档 latch 首次（沿用 `C_BREAK`） |
| 文案 | 族向 ≤1 行白话，读现网表相对上一档： |
| · 焚烬 scorch | 「燃跳更烫」（coef 升；不报乘区数字墙） |
| · 烬爆 burst | 「圈伤更厚」+（半径升时）「半径略扩」 |
| · 炽愈 sustain | 「回血略增」 |
| 挂点 | `EmberAwakenBreak` 突破 chat/AB 后追加半行；可选 `EmberSetFeel.awkDeltaLine(fam, from, to)` 纯函数便于单测 |
| 热关 | `ember-v1.yml`（或 set feel 段）`set_feel.awk_delta: true` 缺省 true |
| 验收 | 人为 I→II / II→III 见白话；`git show` SetRules 数组不变；无新 hub 格 |

## 明确不做

- 改 `BURN_COEF` / `BURST_*` / `SUSTAIN_PCT` / every / ICD  
- 枢纽套装 glance densify · 新 remainLine 族  
- Affix · skill_mult · 四件套 −3% 改值  
- 工坊/宝石保底 twin

## 方案 R / W

| 方案 | 裁决 |
|------|------|
| R · 每击套装系数浮动 AB | **否**（刷屏 densify） |
| W · 抬觉醒表当假变强 | **否决** |
