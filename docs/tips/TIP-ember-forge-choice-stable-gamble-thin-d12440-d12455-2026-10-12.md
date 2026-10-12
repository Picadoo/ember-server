# tip · TIP-S 工坊选择感 · 稳/旁路「选的是」补齐（非改价 · 非 glance）· D12440–D12455

> **状态：** CLOSED · live 1.65.772-d12455.local（2026-10-12 17:07 Asia/Shanghai）  
> **上游：** [`RESEARCH-ember-forge-choice-stable-gamble-thin-2026-10-12.md`](../design/RESEARCH-ember-forge-choice-stable-gamble-thin-2026-10-12.md) · [`DESIGN-ember-forge-choice-stable-gamble-thin-2026-10-12.md`](../design/DESIGN-ember-forge-choice-stable-gamble-thin-2026-10-12.md) · `EmberForgeChoice` · TIP-B/H · TIP-J 停 glance  
> **前置：** TIP-R CLOSED PASS live `1.65.771-d12439`；power-glance densify **仍停**  
> **施工岗：** 插件岗（ForgeChoice 扩 API + 预览 chat；**零改价/率**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · 零 skill_mult · 零抬极品权 · 零 K3

---

## 0. 一句话

成色/精工/强化与随机锻确认前，各说一句「选的是稳线还是旁路」——选择感加深，不改数值。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 枢纽/主页稳赌再一眼 | **否** | TIP-H 已齐；glance twin |
| 图装/保底再开 | **否** | 本窗方向=工坊选择感 |
| **稳/旁路「选的是」预览补齐** | **采纳** | TIP-B 只齐烙/洗/转；稳线与赌锻预览仍缺选择半行 |

---

## 2. 方案 M（定稿）

| ID | 项 | 验收 |
|----|-----|------|
| D12440 | 本 tip + RESEARCH + DESIGN | 路径可点 |
| D12441 | DESIGN power-paths **指针** TIP-S（工坊选择感深化 · 非 glance） | 进度戳 |
| D12442 | `EmberForgeChoice.stableChoice(String kind)` | 单测钉串 |
| D12443 | `EmberForgeChoice.gambleChoice(boolean floor)` | 单测钉串 |
| D12444 | 成色/精工 `!go` 预览发 `stableChoice`（直办路径也先发，禁静默） | 冒烟见 |
| D12445 | 强化 `!go` 预览发 `stableChoice("强化")` | 冒烟见 |
| D12446 | forgeroll 预览（confirm 前）发 `gambleChoice(floor)` | 冒烟见 |
| D12447 | 可选：forgeroll 且 forgeGoal 非 ROLL/NONE → 半行「本周目标… · 此为旁路」 | 可核 |
| D12448 | 热关：`forge_choice.stable_gamble` 缺省 true | 可热关 |
| D12449 | 单测：稳/旁路串；floor 真假分支 | PASS |
| D12450 | 验收：cost/rate/极品权 `git show` 无抬；无新 hub 格；lore overs 不因本 tip 恶化 | 可核 |
| D12451 | HOLD：枢纽稳赌 densify；K3；Affix；抬下限权 | 本包不开 |
| D12452–D12455 | 缓冲热修（文案压行）；禁借号抬表 | 超额新 tip |

### 玩家口径

- 点成色/精工/强化：听到「选的是稳线」。  
- 点随机锻：听到「选的是旁路」。  
- 价钱和成功率没变。

---

## 3. 明确不做

- 改 enhance/quality/refine/forgeroll 价或率或极品权  
- 枢纽 / forge 主页 glance densify · 新 PAPI  
- Affix · skill_mult · K3 自动跳页 · ConfirmTokens 种类海  

---

## 4. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] D12442–D12448 API+挂点+热关  
- [ ] 价/率 `git show` 无抬  
- [ ] 不跑 p1sim（文案级）

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 17:04 CST  
- 选题：变强途径 · 工坊选择感深化（稳/旁路选的是）· 非 glance  
- **批注：** 扩 ForgeChoice stable/gamble；挂成色精工强化+forgeroll 预览；可选目标冲突半行；零改价率极品权；硬禁 Affix/伤乘/抬表/glance twin/K3

## STATUS: CLOSED · live 1.65.772-d12455.local · 2026-10-12 17:07 CST
