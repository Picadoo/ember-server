# STATUS · B2.9 挂机庭 / 天梯去内部代号 A · 验收

**日期：** 2026-09-28 23:30 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗  
**依据：** 施工 tip `d0c9ef4` · 批准 `ed4f54c` · 设计 `a8b8ef2` · 方案 A  
**口径：** 静态 rg + 可选短冒烟；**禁改**玩法 YAML；**禁教玩家斜杠**  
**Verdict：** ✅ **PASS · 勾销** · STATUS 已 push

---

## 一句话

HD 四块说明行与 `ember_ladder` lore/tell 已无人话禁词；location / 排行榜数据行相对 tip 零结构性改动；禁项（over_chance* / MM / EmberCalamity / ember_weekly / 其它菜单）零 diff；挂机庭全息与枢纽→天梯说明/三榜冒烟 OK；ops=[]。

---

## 总评

| 验收点 | 结果 |
|--------|------|
| 1. HD 四块说明行无禁词；location / 排行榜数据行相对 tip 零结构性改动 | **PASS** |
| 2. `ember_ladder.yml` lore/tell 无字面 board id / EmberAbyss / EmberWeekly / 字面 power_score；`%ember_*%` 仍在 | **PASS** |
| 3. 禁项零 diff：over_chance* / MM id / EmberCalamity / ember_weekly / 其它菜单 | **PASS** |
| 4. 可选冒烟：进挂机庭看标题全息；枢纽→天梯点开说明/三榜 | **PASS** |
| ops=[] · 临时 OP deop | **PASS** |
| YAML board 键名保留 | **不计 FAIL**（`ember_ladder_power|abyss|speed` 键名仍在） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `d0c9ef4` fix(ux): B2.9 afk/ladder copy no internal ids |
| 热更 | 施工 STATUS：`hd reload` + `trmenu reload` @ 23:25:18 CST；测前 live 与 tip 说明行一致 |
| 账号 | 验收 `UxB29ejvws`（非 OP）· 辅助 `UxOp29ejvws`（临时 OP，已 deop） |
| JSON | `/tmp/b29-afk-ladder-id-copy-smoke.json`（未入 git） |

---

## 各点证据

### 1 · HD 四块说明行

相对 `d0c9ef4^..d0c9ef4`，仅改说明/底注；**location 四块全同**：

| Board | location | 说明行变更摘要 |
|-------|----------|----------------|
| `ember_afk_hub` | `ember_afk, -46.700, 70.000, 250.362` | →「挂机庭」/「掉落：碎片 · 骨尘」/「打僵尸拿碎片 · 打骷髅拿骨尘」 |
| `ember_ladder_power` | `world, -43.500, 68.200, 265.500` | 去 `power_score` / `only` |
| `ember_ladder_abyss` | `world, -39.500, 68.200, 265.500` | 「对齐 EmberAbyss」→「对齐深渊层数」 |
| `ember_ladder_speed` | `world, -35.500, 68.200, 265.500` | 「EmberWeekly」→「周本」；去 `only` |

玩家可见 `- ` 行禁词扫描（`Ember AFK` / `MM→NI` / `EmberAfk*` / `EmberAbyss` / `EmberWeekly` / 字面 `power_score` / 英文 `only`）：**EMPTY**。  
排行榜人名/分值行相对 tip **零结构性改动**（行数与非说明行字节同 tip；工作树 live 排行刷新未纳入本 tip，符合施工 STATUS）。

### 2 · `ember_ladder.yml`

stripping `%ember_*%` 后禁词（字面 `ember_ladder_power|abyss|speed` / `EmberAbyss` / `EmberWeekly` / 字面 `power_score` / `only`）：**EMPTY**。  
PAPI **保留 7 个**：`%ember_power_score%` + 三榜 `*_1_name%` / `*_1_value%`。

### 3 · 禁项零 diff

`d0c9ef4` 仅三文件：

```
A  docs/STATUS-ember-afk-ladder-id-copy.md
M  plugins/HolographicDisplays/database.yml
M  plugins/TrMenu/menus/ember_ladder.yml
```

相对 parent：`over_chance`/`over_chance_2` 仍 **0.25 / 0.08**；MythicMobs / EmberCalamity / `ember_weekly.yml` / 其它 TrMenu **无 diff**。  
（批 A 刻意不动灾厄 OP #6 / weekly EmberWeekly tells — 本验收不宣称其已清。）

### 4 · 可选冒烟

**菜单路径（UX，禁教斜杠）：** 枢纽菜单 →「挂机庭」进层看标题全息；枢纽菜单 →「天梯」→「全息榜说明」/「战力榜」/「深渊层数榜」/「周本竞速榜」。

| 步骤 | 实测 | 判定 |
|------|------|------|
| 进 `ember_afk` 近全息 | armor_stand 文案：`挂机庭` · `掉落：碎片 · 骨尘` · `打僵尸拿碎片 · 打骷髅拿骨尘`；禁词 EMPTY | **PASS** |
| Open tell | 「全息板在挂机庭附近 · 战力 / 深渊 / 竞速三块」；无 board id | **PASS** |
| 全息榜说明 lore/tell | 「挂机庭西侧三块全息（战力·深渊·竞速）」 | **PASS** |
| 战力榜 lore/tell | 「按展示分排名」·「挂机庭全息 · 战力」；无 `power_score`/`only` | **PASS** |
| 深渊层数榜 | YAML lore/tell 人话「对齐深渊层数」·「挂机庭全息 · 深渊」；无 `EmberAbyss`（图标显示名=「深渊层数榜」） | **PASS**（静态+同菜单其它 live） |
| 周本竞速榜 lore/tell | 「周本通关秒数」·「挂机庭全息 · 竞速」；无 `EmberWeekly` | **PASS** |
| 玩家可见 tell 无斜杠教学 | 无 `/corerpg`/`/hd`/`/mvtp` 教学句（底层 `command:` 保留，与 B0.3 同） | **PASS** |

### ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP 已 `deop`。

---

## Diff 摘要（相对 tip parent）

| 文件 | 变更性质 |
|------|----------|
| `plugins/HolographicDisplays/database.yml` | 仅四块 **lines** 说明/底注人话化 |
| `plugins/TrMenu/menus/ember_ladder.yml` | Open / lore / tell 人话化；PAPI 不动 |
| `docs/STATUS-ember-afk-ladder-id-copy.md` | 施工 STATUS（既有 tip） |

---

## 版本控制

- 本验收 STATUS：`docs/STATUS-ember-afk-ladder-id-copy-test.md`
- 测试 commit：`796dc1b` · 已 push。
- backlog close 由总控文档提交。

## 阻塞点

无。
