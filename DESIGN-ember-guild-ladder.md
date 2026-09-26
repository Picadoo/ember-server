# 余烬盟约 + 天梯 — 短规格（菜单壳 / HD 榜）

**日期：** 2026-09-12（Asia/Shanghai）  
**承接：** `DESIGN-ember-rpg-systems.md` §6  
**栈：** Paper 1.12.2 · TrMenu · HolographicDisplays · PlaceholderAPI ·（插件岗）CoreRpg  
**硬约束：** 不改 Paper；**不重建 / 不覆盖** `CoreRpg.jar`（本文件只定规格、菜单壳与 ops 全息命令）。

---

## 0. 本期范围

| 交付 | 状态 |
|------|------|
| 本设计短稿 | 本文 |
| TrMenu `ember_guild.yml` / `ember_ladder.yml` | 菜单壳（tell + 建议命令） |
| hub 盟约 / 天梯入口 | 去掉「即将点燃」，打开子菜单 |
| HD 全息 ops 笔记 | `docs/ember-holograms.md`（`/hd create` + PAPI 行） |
| CoreRpg guild / ladder 逻辑 | **不做**（命令面占位） |

---

## 1. 盟约（公会轻量）

展示名：**盟约**；命令/配置 ID 前缀 `guild`。

### 1.1 创建

| 项 | 默认 |
|----|------|
| 命令面 | `/corerpg guild create <名>`（占位） |
| 创建费 | 余烬币 `5000` 或晶钻 `40`（二选一；YAML 旋钮） |
| 命名 | 2～8 字；禁敏感词；服内唯一 |
| 人数 | 起步 cap `20`；盟等级升 cap |
| 权限 | 盟主 / 干部 / 成员三级 stub |

未实装前：菜单 tell「建议命令」；点击可尝试执行，失败由聊天提示。

### 1.2 捐献（盟课）

| 项 | 默认 |
|----|------|
| 日课 | 捐材料换 **盟贡献**（活跃清单「盟课 1 次」+10） |
| 可捐 | `mat_ember_shard` / `mat_ember_bone_dust`（绑料可捐；**不**收核心毕业件） |
| 换算示意 | 碎片 ×10 → 贡献 1；骨尘 ×5 → 贡献 1 |
| 日 cap | 个人贡献日上限 `50`（防挂机刷） |
| 命令面 | `/corerpg guild donate <item_id> <amt>` |

盟仓库：只存流通材料与外观碎片；**禁止**直存毕业刃/孔石满配。

### 1.3 盟 Boss（周）

| 项 | 默认 |
|----|------|
| 周期 | 每周 1 次（Asia/Shanghai 周一 0:00 重置票） |
| 进本 | 扣盟 Boss 票 ×1（盟贡献兑换或周活跃箱） |
| 结算 | 贡献排名奖：币 / 外观碎片 / 低概率票；**不**发毕业伤害 |
| 命令面 | `/corerpg guild boss` → 日后 `dp start EmberGuildBoss` |

本期菜单只说明规则；不接 DP 本。

---

## 2. 天梯（三榜）

`power_score` **仅展示 / 天梯**，不乘进伤害公式（总纲红线）。

| 榜 ID | 显示名 | 指标 | 结算 |
|-------|--------|------|------|
| `power` | 战力榜 | `power_score`（强化+附魔+孔石+天赋+使魔展示分） | 周结算外观称号 |
| `abyss` | 深渊层数榜 | 本周最高层 / 历史最高层 | 周结算外观 |
| `speed` | 周本竞速榜 | EmberWeekly 通关用时（秒，越低越好） | 周结算外观 |

### 2.1 命令 / PAPI（插件岗预留）

| 用途 | 占位 |
|------|------|
| 查本人 | `/corerpg ladder` / `ladder me` |
| 查榜 | `/corerpg ladder power\|abyss\|speed [page]` |
| PAPI TOP | `%ember_ladder_power_1_name%` / `%ember_ladder_power_1_value%` … `_10_` |
| PAPI 本人 | `%ember_power_score%` · `%ember_abyss_best%` · `%ember_weekly_best_sec%` |

未接线前全息与菜单 lore 用静态示意字；ops 按 `docs/ember-holograms.md` 建板。

### 2.2 奖励红线

周结算 **只** 外观 / 称号 / 币；**禁止** 核心、满孔毕业刃、碾压属性。

---

## 3. 全息（HolographicDisplays）

HD 本服为 **文件库**（`plugins/HolographicDisplays/database.yml`，插件提示勿手改）。  
运维用 **`/hd create` / `/hd addline`**；行内可嵌 PAPI（需 HD + PAPI 正常联动）。

三板建议名：`ember_ladder_power` / `ember_ladder_abyss` / `ember_ladder_speed`。  
精确命令与行文案 → **`docs/ember-holograms.md`**。

大厅坐标占位：与挂机点同区可改，默认示意 `world -40 67 265` 一带错开。

---

## 4. TrMenu 接线

| 菜单 | hub 图标 | 行为 |
|------|----------|------|
| `ember_guild` | Y 盟约 | `menu: ember_guild`；无「即将点燃」 |
| `ember_ladder` | Z 天梯 | `menu: ember_ladder`；无「即将点燃」 |
| （可选）竞技 / 寄售 | a / b | 仍 stub tell 或 `ember_coming` |

---

## 5. 验收（壳）

- [ ] `/ember` → 盟约 / 天梯 打开子菜单，lore 无「即将点燃」
- [ ] 子菜单返回 hub；创建/捐献/Boss、三榜图标有 tell + 建议命令
- [ ] ops 可按 `docs/ember-holograms.md` 建战力 TOP 全息（PAPI 未接时显示字面或空）
- [ ] 未改 Paper / CoreRpg.jar

