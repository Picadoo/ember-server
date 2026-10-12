# tip · TIP-A 希望一眼（单格三行 · 只报当前优先轨 · 点跳工坊）· D12240–D12247

> **状态：** 待批 A（策划 tip · 2026-10-12 11:29 Asia/Shanghai）· 方案 **M**  
> **上游：** [`DESIGN-ember-growth-variety-thin-2026-10-12.md`](../design/DESIGN-ember-growth-variety-thin-2026-10-12.md) §2A · [`DESIGN-ember-power-paths-variety-2026-10-12.md`](../design/DESIGN-ember-power-paths-variety-2026-10-12.md) Batch1 HD1  
> **定调：** 服主 2026-10-12 变强途径花样；**停词缀 Pack**；lore 非空 ≤3  
> **施工岗：** 插件岗（CoreRpg 展示 + TrMenu）· 策划已定文案骨架  
> **硬禁：** 零改 pity/RATE/cost/掉率/伤乘 · **不新造 hope PAPI twin** · 无 celebration · 无 K3/AFK 抬/sx20 · 不改 Economy 脏树

---

## 0. 一句话

枢纽多一格 **「希望」**：永远只展示 **当前最该看的一条轨**；左键聊天报齐三行并进工坊；右键切下一条轨。不把全表盘塞回成长格。

---

## 1. 缺口

- `EmberHopeMeter` 各轨方法已在；枢纽成长格压薄后 **进服第一眼看不到 n/N**。  
- 多页 remainLine / held_* 散落，玩家不知「先看哪条」。  
- Stage0 HD1「三行并行三轨」与薄窗定案冲突 → **本 tip 采薄窗：单轨优先 + 可切换**。

---

## 2. 方案 M（定稿）

### 2.1 优先轨顺序（固定）

手持可养时：

1. 持件强化保底（`enhance` / 已满则跳过）  
2. 持件成色希望（未到工坊顶）  
3. 持件精工希望（未满）  
4. 洗练 pity（刃或护符有进度者）  
5. 掉落成色希望（`dropQuality`）  
6. 宝石通关保底（`gemPity`）  
7. 签名追烙（`sigChase` 当前 chase）

空手 / 全满：回落文案 `§8今天可稳养：分解差装 → 工坊强化`（仍占「当前轨」）。

玩家右键 **循环** 下标；存在 `PlayerData` 软计数 `p1_hope_i`（0..n-1），**不**进 pity 数学。

### 2.2 Java（展示 only）

| ID | 项 |
|----|----|
| D12240 | 本 tip |
| D12241 | `EmberHopeMeter.priorityTracks(player)` → 有序列表；`current(player)` → `{title, progressLine, matHalf, forgeHint}`；复用已有 `enhance/quality/refine/...`，**禁止**新计数器 |
| D12242 | `corerpg p1 hope`：聊天依次发 3 行（轨名+progress · 缺料/够 · `➥ 工坊：…`），再 `open` 工坊菜单或 `forge from hub` 等价入口（与现网 Shift 右进工坊一致） |
| D12243 | `corerpg p1 hope cycle`：`p1_hope_i++`；短 tell 新轨名一行 |
| D12244 | Hub **Open** 追加 **一行** tell：当前 `progressLine`（与 route tell 并列；禁止再堆希望墙） |

### 2.3 TrMenu（`ember_hub.yml`）

| ID | 项 |
|----|----|
| D12245 | Layout 第一行空位插入格 `w`：现网 `'LnHo p FS'` → `'LnHowp FS'`（原 index4 空格改 `w`，不挤掉 H/o/p） |
| D12246 | Icon `w`：name `§e希望`；lore **恰好 3 非空行**：`§7当前优先轨（点左看进度）` / `§8右键切换 · 不改数值` / `§e➥ §f左：汇报并开工坊 · 右：下一条`；`update: 20`；**lore 不挂新 PAPI**（进度以聊天+Open tell 为准，遵守禁 hope PAPI twin） |
| D12247 | actions：left → sound + `command: corerpg p1 hope`；right → `command: corerpg p1 hope cycle`；**不动** T/成长格、Economy、pity 阈值 |

装备页 / 工坊页本批 **只**加半行指针（可选，同 commit）：`§8详细希望 → 枢纽「希望」` —— 若超 lore≤3 则本批 **不做** 装备/工坊改动。

### 2.4 文案骨架（聊天三行，插件按轨填）

```
§e希望 · {轨名}
§7{progressLine 来自 HopeMeter} §8· {matHalf 或「材料够」}
§e➥ §f打开工坊 · {forgeHint 如「点强化」}
```

---

## 3. 明确不做

- 新 `%corerpg_p1_hope_*%` PAPI 族 / celebration / sig-dry twin  
- 改 MAX_TRIES、cost、掉率、伤乘；commit Economy/pom 脏文件  
- 枢纽 T 成长格回潮希望表盘长墙  
- 词缀 Pack；Batch2 图装 / Batch3 工坊选择（下 tip）

---

## 4. 验收

- [ ] `git show --stat` 无 `EmberEconomy` / `pom.xml` 无关脏文件  
- [ ] hub lore：`w` 非空行 = 3；全文件 overs>3 = 0  
- [ ] 左键：聊天 3 行 + 进入 `ember_p1_forge`  
- [ ] 右键：轨名切换；刷新后再左键内容变  
- [ ] Open 仅 **+1** 行希望 tell  
- [ ] 单测：priority 跳过已满强化；空手回落句；**pity 数学单测不动**  
- [ ] reload：`trmenu reload` + 进服点希望 · 记「待恢复服后实测」若无服

---

## 5. 批注

- [ ] 总控批 **A · 方案 M** · ____（tip 开工）  
- 本地 tip commit · **勿 push** · 总控推
