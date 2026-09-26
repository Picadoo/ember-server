# 余烬角色 / 套装 / 仓库 — 短规格（菜单壳）

**日期：** 2026-09-12（Asia/Shanghai）  
**承接：** `DESIGN-ember-rpg-systems.md` §1（角色）· §3.3（套装）· 插件映射（仓库）  
**栈：** Paper 1.12.2 · TrMenu · CoreRpg（status / covenant / cash 已有）  
**硬约束：** 不改 Paper；**不重建 / 不覆盖** `CoreRpg.jar`（本文只定规格与菜单壳）。

---

## 0. 本期范围

| 交付 | 状态 |
|------|------|
| 本设计短稿 | 本文 |
| TrMenu `ember_character.yml` / `ember_set.yml` / `ember_storage.yml` | 菜单壳 |
| hub 角色 / 套装 / 仓库 | 去掉「即将点燃」，打开子菜单 |
| hub 御兽 | 进商城（与外观同路径），清零即将点燃 |
| 套装效果 / 多页仓库逻辑 | **不做**（命令面占位） |

---

## 1. 角色（摘要面板）

| 项 | 定案 |
|----|------|
| 定位 | 等级、战力、今日必做摘要入口 |
| 菜单 | `ember_character.yml` · hub **A** → `menu: ember_character` |
| 图标 | 状态 / 誓约 / 晶钻 / 月卡 |

### 1.1 命令面（已有或 tell 回退）

| 命令 | 行为 |
|------|------|
| `/corerpg status` | 等级 / 战力 / 今日摘要 |
| `/corerpg covenant` | 当前誓约 |
| `/corerpg cash` | 晶钻 + 日票硬顶 |
| `/corerpg coin` | 软通货（tell 提示） |
| `/corerpg monthly` | 月卡有效 / 到期 / 今日礼 |

未实装子命令时菜单 `command:` 可能失败，**tell 回退**不崩。

---

## 2. 套装（2 件壳）

总纲：刃+护符 → 小套装效果（如击杀回能）；日后 4 件再扩；**不改方块甲**。

| 项 | 定案 |
|----|------|
| 本期 | **2 件**：余烬刃 + 余烬护符 |
| 效果 | 菜单说明 only；数值与触发待插件岗 |
| 菜单 | `ember_set.yml` · hub **V** → `menu: ember_set` |
| 命令占位 | `/corerpg set`（查当前套装件数 / 效果） |

---

## 3. 仓库

| 项 | 定案 |
|----|------|
| 立即可用 | 末影箱（`/enderchest` 或世界末影箱） |
| 未来 | 多页独立仓；**绑定仓 / 流通仓**分页；勋阶加格 |
| 菜单 | `ember_storage.yml` · hub **X** → `menu: ember_storage` |
| 命令占位 | `/corerpg storage`；勋阶入口链 `ember_vip` |

---

## 4. TrMenu 接线

| 菜单 | hub 图标 | 行为 |
|------|----------|------|
| `ember_character` | A 角色 | 无「即将点燃」 |
| `ember_set` | V 套装 | 无「即将点燃」 |
| `ember_storage` | X 仓库 | 无「即将点燃」 |
| （商城） | E 御兽 | `menu: ember_shop`（与外观同） |

目标：**hub lore / 动作中「即将点燃」计数 = 0**。

---

## 5. 未改

Paper / `CoreRpg.jar` / MythicMobs / DungeonPlus / NI / 套装战斗逻辑 / 多页仓库实现
