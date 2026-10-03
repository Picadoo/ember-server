# 深渊「冲层 + 撤离结算」落地规格（优先级 1）

**日期：** 2026-09-13（Asia/Shanghai）  
**作者岗：** 余烬-策划  
**状态：** 总控已按此开干；现网 `docs/status/STATUS-ember-abyss.md` 为 **5 层 stub**，本文定义 **做真** 验收。  
**承接：** `docs/ember-abyss-calamity.md` §1 · `docs/ember-gear-drop-t0-t3.md` §3.4 · `docs/ember-dungeon-gameplay-diff.md` §3  
**约束：** 不改 Paper；票仍进本扣；奖励 NI/MM/结算命令。

---

## 0. 相对 stub 要变什么

| 项 | 现状 stub | 目标 |
|----|-----------|------|
| 层数 | 固定 5 层通关箱 | **无尽**；清层后续层；无「通关结束」 |
| 结束方式 | 打完 5 层 `$end reward` | **撤离 / 团灭无复活 / 超时** → 按最高层发箱 |
| 结算 | 写死 5～9 档 | **按本局最高层查表**，每局只领 1 次 |
| 天梯 | `recordAbyssFloor` 未接 | 结算时写入本人最高层 |
| 菜单 | 有开始下潜 | 加一句「撤离不退票 · 按最高层结算」 |

---

## 1. 玩家规则（可写进菜单 lore）

1. 进本扣 **余烬深渊票 ×1**，不返还。  
2. 每清完一层可继续下潜；**没有强制通关**。  
3. 主动 `/dp leave`（或菜单「上浮撤离」若做）= 撤离：播报文案 + **结算箱**。  
4. 复活用尽团灭 / 1800s 超时 = 同样按最高层结算（`reward` 走结算箱，不是失败零奖）。  
5. 本局最高层 ≥1 才发箱；进本 0 层秒退可不发（防误触刷）。  
6. 日榜/赛季榜取历史最高；本局结算调用 `recordAbyssFloor(最高层)`。

**菜单补一行 lore：** `§8撤离不退票 · 按本局最高层发结算箱`

---

## 2. 层循环（与 stub MM 对齐）

沿用 STATUS 怪名（优先，勿改回地窟 Display 除非怪物岗统一）：

| MM ID | Display（kill 名） |
|-------|-------------------|
| `EmberAbyssZombie` | 余烬深渊僵尸 |
| `EmberAbyssSkeleton` | 余烬深渊骷髅 |
| `EmberAbyssBrute` | 余烬深渊蛮兵 |

**层类型（layer % 5）：**

| mod | 标签 | 刷怪意图（数量随层可 YAML） |
|-----|------|---------------------------|
| 1 | 灰烬潮 | 僵尸 ×(4 + floor/5) |
| 2 | 骨鸣 | 骷髅为主 |
| 3 | 混响 | 僵尸+骷髅 |
| 4 | 蛮压 | 小怪 + 蛮兵×1 |
| 0 | 看守 | 蛮兵×1（高血）+ 添头×2 |

**本期最小实现：** 不必真改 MM 血量系数；可用 **数量 + 看守层** 制造压力。系数 `1+0.08*(L-1)` 作下期旋钮。

**层计数：** 玩家「达成层」= 该层条件 `$kill` 完成的瞬间 +1；播报 `%layer%`。

---

## 3. 结算箱（只发一次 · 按最高层）

对齐 `ember-abyss-calamity.md` 1.4-B，并接 T2（`ember-gear-drop-t0-t3.md`）：

| 最高层 | 碎片 | 骨尘 | 核心 | 随机孔石 | 其它 |
|--------|------|------|------|----------|------|
| 1～4 | 8 | 2 | 0 | 0 | — |
| 5～9 | 12 | 4 | 1 | 1 | — |
| 10～14 | 16 | 6 | 2 | 1 | 保护券 15%；**T2 刃 4%** |
| 15～19 | 20 | 8 | 3 | 2 | 保护券 25% |
| 20+ | 24 | 10 | 4 | 2 | 稳固符 10%；T2 刃/护符 8%/5% |

孔石池：`gem_ember_sharp/steady/drain/gale`（随机一枚；stub 可先固定 sharp）。  
发放：`ni give <player.name> …`（console），全队每人按本人参与发（1～2 人）。

**战斗中掉落**维持 MM onDeath（材料/低概率孔石/看守核心）——与结算箱叠加，不取消。

---

## 4. 建议接线（给插件 · 非唯一实现）

### 4.1 数据

- 本局 meta：`abyss_floor`（int，当前达成）· `abyss_settled`（bool）  
- 清层 end 脚本：`abyss_floor++` → 播报 → 触发下一层 monstergroup（延迟 2～3s）  
- 结算入口统一函数/脚本组 `abyss_settle`：若 `!settled` 且 `floor>=1` → 查表 ni give → `recordAbyssFloor(floor)` → settled=true → `$end`（可 `reward=false` 避免双箱）

### 4.2 撤离

- 优先：监听 leave / 用 interact 或菜单命令 `dp leave` 前触发 settle  
- 若 DP 难以在 leave 前跑脚本：CoreRpg 监听 `DungeonLeaveEvent` 补结算（推荐，稳）  
- 团灭 / timeout task：调用同一 `abyss_settle`

### 4.3 播报（照抄）

见 `ember-abyss-calamity.md` §1.3；最少要有：进本、第 N 层、撤离、沉没结算。

### 4.4 时限

- stub 1800s 可保留；到点强制 settle + end。

---

## 5. 菜单微改（TrMenu）

`ember_abyss.yml`：

- 「开始下潜」lore 增加：`§8撤离不退票 · 按最高层结算`  
- 「规则」tell：同上  
- 可选第三键「上浮撤离」：`tell` 提示输入 `/dp leave`（或直 `command: dp leave`）

---

## 6. 验收（PASS）

1. 扣票进本；无票拒进。  
2. 可打过第 5 层进入第 6 层（证明非固定 5 层通关）。  
3. 第 3 层 `/dp leave` → 收到 **1～4 档**结算箱；票不回。  
4. 同一局不第二次结算。  
5. `/corerpg ladder me` 或 `%ember_abyss_best%` 更新为本局最高（若更高）。  
6. 超时/团灭同样发箱（floor≥1）。  
7. 日本/周本冒烟不被回归破坏。

---

## 7. 先不做（本刀范围外）

- 灾厄公共窗调度、团本分路、新系统壳。  
- 深渊独立竖井美术大改（可用现 map，坐标占位可保留）。  
- 层血量系数精细表（有数量压力即可）。

---

## 8. 路径

| 文件 | 用途 |
|------|------|
| **本文** `docs/ember-abyss-evacuate-spec.md` | 本刀验收主规格 |
| `docs/ember-abyss-calamity.md` | 文案与原表 |
| `docs/ember-gear-drop-t0-t3.md` §3.4 | T2 高层概率 |
| `docs/status/STATUS-ember-abyss.md` | 现状 stub；通关后请改 STATUS |
