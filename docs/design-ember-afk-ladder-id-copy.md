# 设计稿 · 挂机庭/天梯全息与菜单去内部代号（B2.9）

> **未批准前不施工。** 本稿只定 **玩家可见** HolographicDisplays 四块全息 **lines** + TrMenu `ember_ladder.yml` lore/tell 人话化；**禁**改全息 location、HD board id、挂机 `over_chance*` / 日顶、MM 怪 id、CoreRpg 数值、天梯结算、DP/loot、其它 TrMenu。  
> 债源：硬债已空；扫入口一致性 → 挂机庭标题全息仍写 `Ember AFK` / `MM→NI` / `EmberAfkZombie`；天梯全息/菜单仍写 `EmberAbyss` / `EmberWeekly` / `ember_ladder_*` / `power_score`——玩家路径露内部代号，与 B0.3/B2.4「人话指路」口径打架。  
> 对齐：`design-ember-b03-menu-no-cmd.md` · `STATUS-ember-b24-signs.md` · UX「文案短清楚少套娃」。  
> 排除本轮：刚结 B2.6/B2.7/B2.8；五入口奖励预览；非日常体力灰显；挂机 over_chance*/二档 UX；B1.3；墙钟 2h；霜锈前压；断塔升 B；B0.1 除非新证据；灾厄 OP #6（可并 B，默认不绑）；精英预览壳；新进度/货币/副本类型；Citizens；Paper；isomorphic 空壳 tell→真页。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 挂机庭 / 天梯 · **去内部代号**（UX · 入口文案一致性 · B2.9） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 23:20 Asia/Shanghai |
| 关联 | `plugins/HolographicDisplays/database.yml` · `plugins/TrMenu/menus/ember_ladder.yml` |
| 状态 | **待批**（未施工） |
| 关联 STATUS / 稿 | backlog · B2.8 PASS `944c017` · B2.4 告示 PASS（HD 当时无命令教学，未清内部代号） |

### 硬约束（本稿）

| 可动（**仅若批 A/B 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| HD `ember_afk_hub` / `ember_ladder_*` 的 **lines** 玩家字符串 | 四块全息 **location** / board 键名 / 排行榜数据行格式 |
| `ember_ladder.yml` 玩家可见 lore / tell / Open 提示 | PAPI 占位符本身（`%ember_power_score%` / `%ember_ladder_*_%` 须保留） |
| 可选 B：灾厄 OP 拒门 `/ember`→菜单指路（B2.8 #6） | `afk_caps` / `over_chance*`；MM 怪 id；CoreRpg jar/数值；天梯结算规则 |
| 服侧 HD reload / TrMenu reload（约定） | 新养成/货币；Citizens；git push |

---

## 1. 问题一句话

**枢纽告示与进本拒门已人话化；挂机庭落地全息仍念插件管道与怪内部 id，天梯全息/菜单仍念 DP 本 id 与全息 board id——属有证据的入口文案一致性薄修，非改数值。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 挂机庭 HD | `ember_afk_hub` L42–44：`Ember AFK` · `MM→NI` · `EmberAfkZombie / EmberAfkSkeleton` |
| 天梯 HD · 深渊 | L27：`对齐 EmberAbyss · 周结算外观` |
| 天梯 HD · 竞速 | L32：`EmberWeekly 通关用时` |
| 天梯 HD · 战力 | L5：`展示分 power_score · 不乘伤害`；L16：`外观称号 only` |
| 天梯菜单 | `ember_ladder.yml`：lore/tell 露 `ember_ladder_power/abyss/speed`、`EmberAbyss`、`EmberWeekly`、`power_score`；Open/说明仍堆裸坐标 |
| 对照 | B2.4 已清告示 `/ember`；B2.8 已清 DP 玩家拒门斜杠；本脏点 **不在** 刚结窗内 |
| 灾厄 OP（可选） | `EmberCalamity/option.yml` L17 仍：`/ember → 灾厄`（B2.8 批 A 刻意未做 · #6） |

### 替换表（展示层）

#### A1 · HD `ember_afk_hub` lines

| # | 旧 | 新 |
|---|----|----|
| 1 | `&6挂机庭 &7\| Ember AFK` | `&6挂机庭` |
| 2 | `&a碎片·骨尘 &8MM→NI` | `&a掉落：碎片 · 骨尘` |
| 3 | `&7EmberAfkZombie / EmberAfkSkeleton` | `&7打僵尸拿碎片 · 打骷髅拿骨尘` |

#### A2 · HD 天梯 boards（只改说明行；排行榜人名/分值行不动）

| Board | 约位 | 旧 | 新 |
|-------|------|----|----|
| `ember_ladder_power` | 副题 | `&7展示分 power_score · 不乘伤害` | `&7展示分 · 不乘伤害` |
| `ember_ladder_power` | 底注 | `&8周结算：外观称号 only` | `&8周结算：外观称号` |
| `ember_ladder_abyss` | 底注 | `&8对齐 EmberAbyss · 周结算外观` | `&8对齐深渊层数 · 周结算外观` |
| `ember_ladder_speed` | 副题 | `&7EmberWeekly 通关用时` | `&7周本通关用时` |

#### A3 · TrMenu `ember_ladder.yml`（玩家 lore/tell；**保留** PAPI `%…%`）

| 区域 | 旧口径（摘要） | 新口径 |
|------|----------------|--------|
| Open tell | `全息板在挂机点附近 -40 70 265 一带（power/abyss/speed）` | `全息板在挂机庭附近 · 战力 / 深渊 / 竞速三块` |
| 说明钮 lore/tell | 裸坐标为主 + 无板名 | `挂机庭西侧三块全息（战力·深渊·竞速）· 每分钟刷新`（坐标可留 **一行** 次要，勿当主教学） |
| 战力/深渊/竞速 lore | `全息：ember_ladder_*` · `对齐 EmberAbyss` · `EmberWeekly 通关秒数` · `按 power_score 排名` | 删 board id；改「对齐深渊层数」/「周本通关秒数」/「按展示分排名」 |
| 战力/深渊/竞速 tell | `全息板 ember_ladder_* @ x y z` | `挂机庭全息 · 战力/深渊/竞速`（可选附简短坐标，**禁止**念 board id） |

**口径备忘：** `%ember_power_score%` / `%ember_ladder_*_N_*%` **一字不删**（显示值）；只删玩家可见的 **字面** 内部代号。

| #（B） | 文件 | 旧 | 新 |
|--------|------|----|----|
| 6 | `EmberCalamity/option.yml` OP gate | `…公共窗口：/ember → 灾厄` | `…公共窗口：枢纽菜单 → 灾厄` |

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. HD + 天梯菜单清内部代号（推荐）** | A1+A2+A3；**不**动灾厄 OP；零改 location / 数值 / PAPI 键 | 覆盖挂机落地与天梯主路径脏点；专岗清晰；与 B2.4/B0.3 人话口径对齐 | OP 灾厄句仍挂（见得少） | **推荐** |
| **B. A + 灾厄 OP #6** | A 全做 + 上表 #6（B2.8 批 A 残留） | 斜杠教学域外更齐 | 跨 DP+HD+TrMenu，略厚；OP 专测 | 备选 |

**不施工：** 改 `over_chance*` / 日顶；改 MM 怪 id；改天梯结算/奖励；开精英奖励子菜单；宣称 B0.1 已清；清空壳图录/coming。

---

## 4. 验收（≤5）

1. **批准前：** 本稿落盘；玩法 YAML / HD location / afk 数值 / TrMenu 其它页 **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `database.yml` 四块 board 的玩家说明行 **无** `MM→NI` / `EmberAfk*` / `Ember AFK` / `EmberAbyss` / `EmberWeekly` / 字面 `power_score` / 英文 `only`；排行榜数据行与 location **相对批前零 diff**（除说明行）。  
3. **若批 A：** `ember_ladder.yml` 玩家 lore/tell **无** 字面 `ember_ladder_power|abyss|speed` / `EmberAbyss` / `EmberWeekly` / 字面 `power_score`；`%ember_*%` 占位符仍在。  
4. **若批 A · 禁项：** 未改 `over_chance*` / 体力 / loot / 其它菜单；**不**宣称断塔 B / 霜锈 prep / 精英预览壳已动。  
5. **若批 B：** 另满足 `EmberCalamity` OP 拒门无 `/ember`，改为枢纽菜单指路。

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 挂机庭标题全息 | —（HD） | 挂机庭 | `ember_afk` 入口附近既有 | 无（只读） | 无 | **UI/文案可用** · 无新 NPC |
| 天梯三块全息 | —（HD） | 战力/深渊/竞速 | 主世界挂机区西侧既有 | `ember_ladder` 既有说明 | 无 | 只改说明行 |
| 天梯菜单 | — | 天梯 | 枢纽 → 天梯 | `ember_ladder` | 无 | 只改 lore/tell |

**站岗自检：** 无新 NPC；仅文案 → 最高标「UI 可用」，不宣称场景完成。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 进挂机庭 | 标题全息人话（掉落说明） | **不**念 MM/怪 id | 回枢纽菜单换层 |
| 2 | 枢纽 → 天梯 | 菜单用中文榜名 | 点说明知「挂机庭三块全息」 | 返回枢纽 |
| 3 | 挂机区看榜 | 全息副题人话 | 读展示分/层数/用时；不念 DP 本 id | 无 |

---

## 7. UX 否决条

- 禁止借机改挂机掉率、日顶、二档数值。  
- 禁止删除或改名 HD board id / 怪 MM id（逻辑身份不动）。  
- 禁止玩家路径新增斜杠教学或其它内部管道词（如 `MM→NI`）。  
- 禁止未批准改 `database.yml` / `ember_ladder.yml`（除非批 A/B 后另开施工）。  
- 禁止把排行榜机器人假名当「正式内容完成」宣称。

---

## 8. 未选表（否因）

| 候选 | 本轮 | 否因 |
|------|------|------|
| B2.6/B2.7/B2.8 | 排除 | 刚结 PASS |
| 五入口奖励预览 / isomorphic 空壳→真页 | 排除 | 已结或禁再开一遍 |
| 非日常体力灰显 / 挂机 over_chance*·二档 UX | 排除 | 刚结 |
| B1.3 / 墙钟 2h / 霜锈前压 / 断塔升 B | 排除 | 关账或刻意或无证据 |
| B0.1 票扣显示名 / NI「日票」显示名 | 排除 | 无进本体力+NI 新证据；显示名改易被误读为重开 B0.1 |
| 精英奖励预览子菜单壳 | 排除 | 一点进本 · 另开才厚 |
| 灾厄 OP #6 单开 | 未单开 | 见得少；并入方案 B 可选 |
| 寄售「票券」二字 | 未选 | 旧票迁移物仍存在，改词收益低于本窗 HD 脏点 |
| 新进度/货币/副本/Citizens/Paper | 排除 | 任务禁 |

---

## 9. 回总控一句话

**无高杠杆硬债；扫入口新脏点：荐批 A——挂机庭/天梯全息说明行 + 天梯菜单 lore/tell 去内部代号，零改 location/数值/PAPI 键。**

**专岗：** **策划**（本稿）→ **落地岗/插件**（批后改 HD `database.yml` lines + `ember_ladder.yml` 文案；热更 HD/TrMenu）→ **测试**（静态 rg + 挂机落地/天梯点开冒烟）
