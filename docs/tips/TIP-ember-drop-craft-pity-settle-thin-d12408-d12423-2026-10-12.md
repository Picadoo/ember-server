# tip · TIP-Q 可见保底 · 精工软保底结算进度（对齐成色 · 非改表）· D12408–D12423

> **状态：** CLOSED · live 1.65.770-d12423.local（2026-10-12 16:57 Asia/Shanghai）  
> **上游：** [`RESEARCH-ember-drop-craft-pity-settle-thin-2026-10-12.md`](../design/RESEARCH-ember-drop-craft-pity-settle-thin-2026-10-12.md) · [`DESIGN-ember-drop-craft-pity-settle-thin-2026-10-12.md`](../design/DESIGN-ember-drop-craft-pity-settle-thin-2026-10-12.md) · `EmberDropCraftPity` / `EmberSettleService` · TIP-J 停 glance  
> **前置：** TIP-P CLOSED PASS live `1.65.769-d12407`；power-glance densify **仍停**  
> **施工岗：** 插件岗（结算文案对齐；**零改 NEED/FLOOR/权重**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · 零 skill_mult · 禁改掉率

---

## 0. 一句话

主线结算时，精工软保底像成色一样报「还差几次」——已有保底摸得到，不抬任何表。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 工坊选择感再一眼 | **否** | TIP-B/H/forgegoal/HeldNext 已齐；再动易 glance densify |
| 图装身份再一眼 / 拾取 densify | **否** | TIP-C/I/L + LootMapFeel/共鸣已齐；MG3 soft 仍冻 |
| **可见保底 · 精工结算进度对齐成色** | **采纳** | 成色每局 remain；精工仅触发喊——同轴不对称；非新 pity 族、非 hub |

---

## 2. 方案 M（定稿）

| ID | 项 | 验收 |
|----|-----|------|
| D12408 | 本 tip + RESEARCH + DESIGN | 路径可点 |
| D12409 | DESIGN power-paths **指针** TIP-Q（可见保底深化 · 非 glance） | 进度戳 |
| D12410 | `EmberSettleService`：`DropCraftPity.applicable` 且 **非** `dropCForced` → chat `§7掉落精工 · §f` + `remainLine(pd)` | 冒烟见 |
| D12411 | 同枝可选 AB 一行（与成色 else 对称；过挤可只 chat） | 可核 |
| D12412 | `dropCForced==true`：保持现触发句；**禁止**再叠 remain | 单测/日志 |
| D12413 | 热关：`drop_craft_pity.settle_remain` 缺省 true；false=退回仅触发喊 | 可热关 |
| D12414 | 单测：cur 0..NEED-1 remain 非空；forced 路径无双重 remain | PASS |
| D12415 | 验收：`git show` `NEED`/`FLOOR`/`CRAFT_WEIGHTS` **无抬**；无新 hub/PAPI | 可核 |
| D12416 | HOLD：枢纽 hope 挂精工保底；改 NEED；成色 remain densify；Affix；图装 soft | 本包不开 |
| D12417–D12423 | 缓冲热修（文案过长压行）；禁借号抬表 | 超额新 tip |

### 玩家口径

- 通关主线后，成色保底在走，精工保底也会说「还差几次」。  
- 表没动；只是以前精工进度不说话。

---

## 3. 明确不做

- 改 `DropCraftPity.NEED` / `FLOOR` / `CRAFT_WEIGHTS` / 掉率  
- HopeMeter / 枢纽 glance twin / 新 remainLine 族（本窗只对齐既有 settle 口）  
- Affix densify · skill_mult · 伤乘  
- 工坊/图装一眼再 densify · MG3 soft gift

---

## 4. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] D12410–D12413 结算挂点+热关  
- [ ] NEED/FLOOR `git show` 无抬  
- [ ] 不跑 p1sim（文案级）

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 16:54 CST  
- 选题：变强途径 · 可见保底深化（精工结算进度对齐成色）· 非 glance  
- **批注：** 镜像成色 else；零改 NEED/FLOOR；热关 settle_remain；forced 不叠 remain；硬禁 Affix/伤乘/抬表/glance twin

## STATUS: CLOSED · live 1.65.770-d12423.local · 2026-10-12 16:57 CST
