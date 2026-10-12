# tip · TIP-M 枢纽一眼格挤度 · 停窗观察纪要（只合并/折叠候选 · 本窗无码）· D12352–D12359

> **状态：** CLOSED · 批 **A · 方案 M（停窗观察）**（总控 2026-10-12 16:32 Asia/Shanghai）· 无码  
> **上游：** [`TIP-ember-power-paths-stop-window-d12312-d12319-2026-10-12.md`](TIP-ember-power-paths-stop-window-d12312-d12319-2026-10-12.md) §3 P0 · [`DESIGN-ember-power-paths-variety-2026-10-12.md`](../design/DESIGN-ember-power-paths-variety-2026-10-12.md) HD1  
> **前置：** TIP-L CLOSED live `1.65.767-d12351`；TIP-A～I glance **CLOSED**；TIP-J 停窗仍有效  
> **硬禁：** **零 Affix · 零伤乘 · 零抬表 · 零 glance densify twin**（不新造进度串 / 不新占 Layout 字母）· lore 非空 ≤3 · 不改 Economy/pom

---

## 0. 一句话

盘清枢纽一眼格现状与折叠候选；**本窗不改菜单**。有真人挤度反馈或总控改判「确认挤」后再开专 tip 做合并。

---

## 1. 现网盘点（`ember_hub.yml` · 2026-10-12）

Layout：

```
LnHowpvFS
TBmXgek i
ZR c Y d 
Q A G U j
```

| 格 | 名 | 来源 tip | lore 骨架 | 动作 |
|----|----|----------|-----------|------|
| **H** | 今天该打哪 | 日路由（非 power-glance） | 路由 / 体力 / 点击 | 冒险·短征等 |
| **w** | 希望 | TIP-A / TIP-I | hope_now / mapid_focus / 点击 | hope · cycle |
| **g** | 宝石 | TIP-E | gem_pity_hope / 说明 / 点击 | 宝石页 |
| **e** | 签名 | TIP-G | sig_feel / 左右说明 / 点击 | sig · chase |
| **v** | 套装 | TIP-D | setchase_line / 说明 / 点击 | setchase · setfocus |
| **p** | 工坊 | TIP-H（稳/赌文案） | 稳 / 赌 / 点击 | forge |
| **S** | 技能组 | TIP-F | kit_set_hope / 形状·身法 / 点击 | skill_kit |

一眼簇位置：row0 `w p v … S`，row1 `g e` 落在希望/工坊正下方 → **六格进度一眼 + 工坊文案** 挤在左上 2×3 区（另 H 路由）。

每格 lore 已压到 ≤3；挤的是 **格数**，不是单格行数。

**对照 HD1 原意（DESIGN power-paths）：**「希望一眼」本可单面板承载 hope + 焦点图 + **宝石**；TIP-E 另占 `g` 后，一眼轨变宽。

---

## 2. 观察结论（本窗）

| 问 | 答 |
|----|----|
| 是否已有真人试玩「过挤」反馈？ | **无**（仅文档/布局推断） |
| 是否值得本窗直接合并？ | **否** — TIP-J P0 = 先观察；误合并会砍可达入口 |
| 本窗交付 | **停窗观察纪要** + 折叠候选（未批施工） |

---

## 3. 折叠候选（冻结 · 确认挤后再开专 tip）

只允许 **合并/折叠**，禁止新造进度 / 新 PAPI / 新 Layout 字母。

| ID | 候选 | 做法 | 风险 |
|----|------|------|------|
| F1 | **宝石并回希望** | 删 `g`；`w` L2 或 L3 半行挂 `gem_pity_hope`（或右键进宝石）；腾出格留空或还 Layout | 希望格信息变满；须保 ≤3 行 |
| F2 | **签名⊕套装 →「追逐」一格** | 一格 L1=sig 短 / L2=set 短；左右分键保留 | 两套动作抢键；易糊 |
| F3 | **工坊稳赌只留 p，希望区不重复指针** | 文案审计：他格勿再写稳/赌三行 | 收益薄；已基本如此 |
| F4 | **技能 S 不进一眼折叠** | 手感入口独立；不与 w/g/e/v 合并 | — |

**默认推荐（若日后确认挤）：F1 优先** — 对齐 HD1 单面板；改动面最小。F2 慎开。

---

## 4. 本窗切片（文档 only）

| ID | 项 |
|----|-----|
| D12352 | 本观察纪要 tip |
| D12353 | DESIGN power-paths 进度戳：TIP-M 停窗观察；默认不派折叠施工 |
| D12354 | 冻结：不新开 power-glance twin；不借观察窗改 pity / stamp / skill_mult |
| D12355 | 触发条件（日后开工）：业主/试玩明示「枢纽一眼挤」**或**总控改判确认挤 → 另开 TIP（续 D 号）只做 F1（或批注指定的 F#） |
| D12356 | 插件岗：本 tip **无码** |
| D12357–D12359 | 缓冲：仅纪要勘误；禁借号改 hub.yml |

---

## 5. 明确不做

- 本窗改 `ember_hub.yml` / 删格 / 并 lore  
- 新进度串、新 hope PAPI、新 Layout 字母  
- Affix / 伤乘 / 抬表  
- 把 TIP-F/G/D/E 功能删掉只留空壳（折叠须保留可达）

---

## 6. 验收（文档）

- [x] 总控批 **A · 方案 M（停窗观察）**  
- [x] 矩阵 §1 与现网 Layout 一致  
- [x] 明确 **不派插件**  
- [x] 候选 F1–F4 冻结，触发条件写入 §4  

---

## 7. 批注

- [x] 总控批 **A · 方案 M（停窗观察）** · 2026-10-12 16:32 CST  
- glance densify：**仍停**；本 tip 只观察  
- 下步：确认挤 → F1 专 tip；未确认 → 保持停窗
- **批注：** 矩阵与 Layout 一致；F1 宝石并回希望为默认候选；无真人反馈前不派码

## STATUS: CLOSED · 停窗观察 · 2026-10-12 16:32 CST
