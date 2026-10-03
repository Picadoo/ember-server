# 余烬竞技 + 寄售 — 短规格（菜单壳）

**日期：** 2026-09-12（Asia/Shanghai）  
**承接：** `docs/design/DESIGN-ember-rpg-systems.md` §内容矩阵（竞技场）· §社交（竞技 / 寄售）· §插件映射（交易寄售税 5～15%）  
**栈：** Paper 1.12.2 · TrMenu ·（插件岗）CoreRpg / CoreCombat  
**硬约束：** 不改 Paper；**不重建 / 不覆盖** `CoreRpg.jar`（本文只定规格、菜单壳与命令面）。

---

## 0. 本期范围

| 交付 | 状态 |
|------|------|
| 本设计短稿 | 本文 |
| TrMenu `ember_arena.yml` / `ember_auction.yml` | 菜单壳（tell + 建议命令） |
| hub 竞技 / 寄售 | 去掉「即将点燃」，打开子菜单；**不再**走 `ember_coming` |
| CoreRpg arena / auction 逻辑 | **不做**（命令面占位，供插件岗后接） |

---

## 1. 竞技（1v1 / 2v2）

总纲：竞技场 1v1/2v2 · 日奖励箱 · 币与外观币 · 独立世界 + CoreCombat；积分赛季，奖励绑外观与币。

| 项 | 定案 |
|----|------|
| 模式 | **1v1** 单挑 · **2v2** 双人（可好友组队后排队） |
| 队列 | 菜单 tell 登记意图；匹配 / 进本待插件岗 |
| 奖励 | 日奖励箱：余烬币、外观币；**不发**核心 / 毕业伤害 |
| 赛季 | 积分赛季；段位与赛季箱后接 |
| 菜单 | `ember_arena.yml` · hub **a** → `menu: ember_arena` |

### 1.1 命令面（插件岗后接）

| 命令 | 行为（拟定） |
|------|----------------|
| `/corerpg arena` | 本人积分 / 段位 / 日箱状态 |
| `/corerpg arena queue 1v1` | 加入 1v1 匹配队列 |
| `/corerpg arena queue 2v2` | 加入 2v2 匹配队列 |
| `/corerpg arena leave` | 取消排队 |
| `/corerpg arena stats` | 可选：胜负场次 |

权限拟定：`corerpg.arena`（默认真玩家有）。  
未实装时菜单 `command:` 可能失败，**tell 回退**不崩。

---

## 2. 寄售（税 10%）

总纲：余烬寄售 · 税 + 绑定区分；插件映射税 **5～15%**，**本期定 10%**。

| 项 | 定案 |
|----|------|
| 定位 | 玩家间上架流通物；成交卖方实收 = 标价 × **90%** |
| 税率 | **10%**（卖方承担；旋钮可调，落在 5～15%） |
| 可上架 | 白名单流通物（材料、外观碎片、便利物等） |
| 禁上架 | **绑定物**、日周票、邮件附件；**核心 / 毕业刃 / 满孔装 / 战力扭蛋** |
| 菜单 | `ember_auction.yml` · hub **b** → `menu: ember_auction` |

### 2.1 命令面（插件岗后接）

| 命令 | 行为（拟定） |
|------|----------------|
| `/corerpg auction` | 我的在售 / 已售 |
| `/corerpg auction list` | 浏览在售列表（分类） |
| `/corerpg auction sell <价>` | 手持白名单物上架 |
| `/corerpg auction buy <id>` | 购买（后接） |
| `/corerpg auction cancel <id>` | 下架 |

权限拟定：`corerpg.auction`。  
税入系统池或销毁（经济文档后定）；菜单壳本期只 **tell 税率**。

---

## 3. TrMenu 接线

| 菜单 | hub 图标 | 行为 |
|------|----------|------|
| `ember_arena` | a 竞技 | `menu: ember_arena`；无「即将点燃」；**不用** `ember_coming` |
| `ember_auction` | b 寄售 | `menu: ember_auction`；无「即将点燃」；**不用** `ember_coming` |

`ember_coming.yml` 仍保留给其它未接线入口。

---

## 4. 验收（壳）

- [ ] `/ember` → 竞技 / 寄售打开子菜单，lore 无「即将点燃」
- [ ] 子菜单返回 hub；竞技有 1v1/2v2 排队 tell；寄售有 list/sell/税 10% tell
- [ ] 未改 Paper / CoreRpg.jar
