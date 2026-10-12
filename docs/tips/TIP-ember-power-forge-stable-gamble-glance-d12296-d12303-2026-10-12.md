# tip · TIP-H 工坊稳/赌一眼（WS3 表面化 · 并排半行 · 点进工坊）· D12296–D12303

> **状态：** CLOSED · live trmenu `82512b4d`（2026-10-12 16:08 Asia/Shanghai）  
> **上游：** [`DESIGN-ember-power-paths-variety-2026-10-12.md`](../design/DESIGN-ember-power-paths-variety-2026-10-12.md) §3.2 **WS3** · [`DESIGN-ember-growth-variety-thin-2026-10-12.md`](../design/DESIGN-ember-growth-variety-thin-2026-10-12.md) §2B · forge-choice K1/K2 已 SHIP · TIP-B CLOSED（d12263，F 格已写下限）  
> **前置：** TIP-A～G CLOSED live（至 1.65.764-d12295）  
> **施工岗：** 插件岗（TrMenu 为主；**零改价 / 零抬表**）  
> **硬禁：** **零 Affix Pack · 零伤乘 · 零抬 RATE / skill_mult / gem flat / forgeroll 极品权** · 零 K3 自动跳页 · lore 非空 ≤3 · 不发明新主循环 · 不改 Economy/pom 脏树

---

## 0. 一句话

进枢纽「工坊」或工坊主页，**一眼分清稳锻 vs 赌锻**（材料确定 +1 vs 旁路随机·有下限）；点进对应口。不抬数值，不重开 forge twin。

---

## 1. 缺口（对照现网 · WS3）

| 已有 | 问题 |
|------|------|
| K1 Open tell「稳线 / 旁路」；K2/K2b forgeroll 上下排 + floor | 玩家要先开工坊、再读 Open 墙 |
| TIP-B D12260：主页 **F** lore 已写「旁路赌线 + 有下限」 | **缺并排的「稳」半行**——上排 E/U/R 无「这是稳线」总览格；枢纽 `p` 只写「强化/升阶/精工/成色」，**不提赌线对比** |
| TIP-B WS1/WS2 commit/预览 | 已 CLOSED；本 tip **不**重开 commit twin |

**WS3 原文：** 工坊主页一格「稳锻 / 赌锻」并排半行；禁抬极品权、禁 K3。

---

## 2. 方案 M（定稿）

### 2.1 并排短串（展示 only · 可静态）

| ID | 项 |
|----|----|
| D12296 | 本 tip |
| D12297 | 文案骨架（**优先纯 TrMenu 静态**；无需新 PAPI）。若要用动态手持下一稳线，可半行挂已有 `%corerpg_p1_forge_goal_line%` / held pity——**禁止**新 hope/forge PAPI 丛林 |
| D12298 | 固定口径两行：`§a稳：§f强化·成色·精工（材料→确定+1）` / `§6赌：§f随机锻·有下限（极品仍0）` |

### 2.2 枢纽 `p`（工坊入口一眼）

| ID | 项 |
|----|----|
| D12299 | `ember_hub.yml` Icon `p` lore **恰好 3 非空行**：`§a稳：§f强化·成色·精工 → 确定+1` / `§6赌：§f随机锻 · 页内有下限` / `§e➥ §f打开工坊`；actions 保持 `menu: ember_p1_forge` |

### 2.3 工坊主页「一格并排」（WS3 本体）

| ID | 项 |
|----|----|
| D12300 | Layout：在底栏 F 旁挤一格说明 **或** 把某一装饰 `#`（建议第 2 行右空 / 底栏可读者）改为 Icon `N`（字母以不冲突为准）：name `§a稳§8/§6赌`；lore 三行：`§a稳：§f上排 强化/成色/精工` / `§6赌：§f点 F · 下页第二排买下限` / `§e➥ §f认路 · 不改数值`。若 Layout 实在挤：允许 **不占新格**，改为 F 左侧名 `§6赌§8｜§a稳在上排`，且 Open **只留 1 行**并排 tell（见 D12301）——优先占格 |
| D12301 | Open：删减重复「稳线/旁路」长墙，保留 **一行**：`§a稳§7上排确定+1 §8· §6赌§7点F（有下限·极品0）`；合计 Open 教学类 tell ≤2（可另留缺料/配方指针一行） |
| D12302 | F 格：保持 TIP-B 三行骨架（旁路 / 下限 / 打开）；**不**再 densify。forgeroll 页顶说明保留「主养回工坊稳线」半行即可 |

### 2.4 验收

| ID | 项 |
|----|----|
| D12303 | 验收：枢纽悬停工坊能读出稳 vs 赌；工坊主页一格（或 Open+ F）并排可读；点 F→forgeroll、上排仍为稳线命令；lore overs>3=0；forgeroll 极品权 / enhance 表 / Affix / 伤乘单测 **不动** |

---

## 3. 明确不做

- Affix Pack、伤乘、抬 RATE / skill_mult / gem flat / forgeroll 极品权  
- K3 自动跳页、ConfirmTokens 种类海、同构 forge twin / pity densify twin  
- 新主循环、新消耗税、改 C22/C06 单价  
- 把配方全表 / 洗练池贴回主页 lore  

---

## 4. 验收清单

- [x] 枢纽 `p`：三行内分清稳/赌  
- [x] 工坊主页：WS3 并排半行可见（专格或等价 Open+F）  
- [x] F → forgeroll；E/e 等稳线命令不变  
- [x] hub + forge lore overs>3 = 0  
- [x] `git show` 无 Affix / Economy / pom 无关脏文件；密码检查 0  
- [ ] reload 记「待恢复服后实测」

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 16:07 CST
- tip 已推 · 交插件岗
- 变强途径续：**TIP-H · WS3**；TIP-B 已收 commit，本批只表面化稳/赌对照
- **批注补强：** 优先占格 `N`（Layout 底栏 `#FGPmWbB#` 旁 `#`→`N`，或第2行右空）；Open 教学 tell 压到 ≤2（现墙过长）；F 三行不动；枢纽 `p` 现仅2行→补满3行稳/赌

## STATUS: CLOSED · live trmenu 82512b4d · 2026-10-12 16:08 CST
- hub `p` 稳/赌三行 · forge `N` 认路格 · Open≤2 · F 未动
- 纯 YAML；无 JAR bump；零 Affix/Economy/数值
