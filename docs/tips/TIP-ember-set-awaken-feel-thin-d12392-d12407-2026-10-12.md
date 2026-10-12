# tip · TIP-P 套装手感 · 觉醒升档白话（非改倍率）· D12392–D12407

> **状态：** CLOSED · live 1.65.769-d12407（2026-10-12 16:51 Asia/Shanghai）  
> **上游：** [`RESEARCH-ember-set-awaken-feel-thin-2026-10-12.md`](../design/RESEARCH-ember-set-awaken-feel-thin-2026-10-12.md) · [`DESIGN-ember-set-awaken-feel-thin-2026-10-12.md`](../design/DESIGN-ember-set-awaken-feel-thin-2026-10-12.md) · `EmberSetRules` / `EmberAwakenBreak` · TIP-J 停 glance  
> **前置：** TIP-O CLOSED PASS live `b566bfe4`；power-glance densify **仍停**  
> **施工岗：** 插件岗（AwakenBreak / SetFeel 文案；**零改 SetRules 数值数组**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · 零 skill_mult · 禁改四件套伤减

---

## 0. 一句话

套装觉醒升档时说清「燃更烫 / 圈更厚 / 血略增」——玩家感到套装途径变强，不抬任何系数。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 工坊选择感再一眼 | **否** | TIP-B/H/forgegoal/HeldNext 已齐；再动易 glance densify |
| 图装身份再一眼 | **否** | TIP-C/I/L 已齐 |
| 可见保底再铺 | **否** | 强化/宝石/签名 pity 表面化已齐；SUR densify 冻结 |
| **套装手感 · 觉醒升档白话** | **采纳** | 突破只喊罗马数字；表已有档差未读给人听——变强途径深度、非 glance |

---

## 2. 方案 M（定稿）

| ID | 项 | 验收 |
|----|-----|------|
| D12392 | 本 tip + RESEARCH + DESIGN | 路径可点 |
| D12393 | DESIGN power-paths / growth-variety **指针** TIP-P（套装手感深度 · 非 glance） | 进度戳 |
| D12394 | 纯函数 `EmberSetFeel.awkDeltaLine(family, fromAwk, toAwk)`（或同级）：读 `EmberSetRules` 相对差 → ≤1 行白话；空族/无升 → `""` | 单测钉串 |
| D12395 | 口径（展示 only）：焚烬「燃跳更烫」；烬爆「圈伤更厚」（半径升则「· 半径略扩」）；炽愈「回血略增」——**禁止**在聊天贴 raw 0.65→0.95 墙 | 可核 |
| D12396 | `EmberAwakenBreak.maybeAfterAwkUp`：latch 首次突破消息后追加 delta 半行 + AB 可并半行 | 冒烟见 |
| D12397 | 热关：`set_feel.awk_delta` 缺省 true；false=退回仅罗马数字 | 可热关 |
| D12398 | 单测：I→II / II→III 三族各非空；同档/降档空；Rules 数组断言未改 | PASS |
| D12399 | 冒烟：成套件换高觉醒件 → 见突破+白话；`git show` SetRules **无抬** | 记待恢复服 |
| D12400 | HOLD：枢纽套装格 densify；改 coef；四件套 −3%；Affix；skill_mult | 本包不开 |
| D12401–D12407 | 缓冲热修（文案过长压行）；禁借号抬表 | 超额新 tip |

### 玩家口径

- 觉醒升一档，会听到这套「具体好在哪」一句。  
- 数字表没动；只是说人话。

---

## 3. 明确不做

- 改 `BURN_COEF` / `BURST_COEF` / `BURST_RADIUS` / `SUSTAIN_PCT` / every / ICD  
- 枢纽 glance twin / 新 hope·set PAPI 丛林  
- Affix densify · skill_mult · 伤乘  
- 工坊/宝石保底再 densify · 图装 soft gift

---

## 4. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] D12394–D12397 函数+挂点+热关  
- [x] SetRules `git show` 无抬  
- [x] 不跑 p1sim（文案级）

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 16:48 CST  
- 选题：变强途径 · 套装手感深度（觉醒白话）· 非 glance  
- **批注：** 读表不改表；挂 AwakenBreak；热关 awk_delta；≤1 行禁 raw 系数墙；硬禁 Affix/伤乘/抬表/glance twin

## STATUS: CLOSED · live 1.65.769-d12407 · 2026-10-12 16:51 CST
- feat/chore 另附；零 SetRules 抬；Affix/glance 仍禁
