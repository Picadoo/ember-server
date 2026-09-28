# 设计稿 · 灾厄 OP 调试拒门去斜杠（B2.12）

> **未批准前不施工。** 本稿只定 **玩家/OP 可见** `EmberCalamity/option.yml` 拒门 `message=` 一句（去 `/ember` 教学 → 枢纽菜单指路）；**禁**改门控 `text=` 表达式、人数门、体力 cost、票、掉落、MM、TrMenu、`calamity.yml` 数值、公共窗逻辑。  
> 债源：B2.8～B2.11 批 A 均刻意保留「灾厄 OP #6」；总控 B2.11 close `5378b23` 后点名剩余薄候选。  
> 对齐：`design-ember-dp-gate-nocmd.md` #6 · `STATUS-ember-dp-gate-nocmd-test.md` · `calamity.yml` 已洁「打开枢纽菜单 → 灾厄 → 奔赴」· UX「少打指令 · 文案短清楚」。  
> 排除本轮：刚结 B2.6–B2.11；五入口奖励预览；非日常体力灰显；挂机 over_chance*/二档 UX；B1.3；墙钟 2h；霜锈前压；断塔升 B；B0.1 除非新证据；精英预览壳；isomorphic 空壳 tell→真页；新进度/货币/副本类型；Citizens；Paper。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 灾厄 DP · **OP 调试拒门去斜杠**（UX · B0.3/B2.8 域外残留 · B2.12） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 23:56 Asia/Shanghai |
| 关联 | live `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` L17 · 债源 B2.8 #6 / B2.11 close `5378b23` |
| 状态 | **已批 A · 待插件施工** |
| 关联 STATUS / 稿 | `STATUS-ember-calamity-op-gate-copy-approve.md` · 测报 · backlog |

### 硬约束（本稿）

| 可动（**仅若批 A/B 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `EmberCalamity/option.yml` OP gate **message=** 字符串一句 | `$js-condition` 的 `text=`（`%player_is_op%` 判定） |
| 热更 / DP reload（或服约定的 dungeon 重载） | 人数门 / 体力 / loot / MM / TrMenu / `calamity.yml` 广播 |
| 方案 B：天赋菜单 `*_cap` 玩家可见内部代号薄扫 | 把 OP 门改成对普通玩家开放；新养成/货币；Citizens；git push |
| | 小写 stub `ember_calamity/option.yml`（地图编辑空壳 · 无本债句） |

---

## 1. 问题一句话

**B2.8 已清周/团/深渊/盟 Boss 玩家拒门斜杠；灾厄公共广播与 quest 已写「打开枢纽菜单 → 灾厄」；唯 DP `EmberCalamity` OP 调试拒门仍教 `/ember → 灾厄`——非 OP 误开测本时唯一仍带斜杠教学的进本拒门，属 B0.3 域外最后一句薄修。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| B2.8 A | 周/团/深渊 level + 盟 Boss 拒门/开场已去 `/corerpg`；#6 刻意未做（`e7bc1eb` / `12846c7`） |
| B2.9～B2.11 | 批 A 均不纳入 OP #6；close 链末 `5378b23` |
| 公共窗已洁 | `plugins/CoreRpg/calamity.yml` L23–24：`打开枢纽菜单 → 灾厄 → 奔赴`（无 `/ember`） |
| quest 已洁 | `quest.yml` calamity hint：`打开枢纽菜单 → 灾厄 → 奔赴` |
| **OP #6 脏** | `EmberCalamity/option.yml` L17：`§c灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：/ember → 灾厄` |
| 门控语义 | `%player_is_op%=='yes'`；非 OP `/dp start EmberCalamity` 被拒（smoke-gating-drops 已验） |
| 小写 stub | `ember_calamity/option.yml` 仅「该地牢实例仅供 OP 编辑地图使用」——**无** `/ember`，本窗不动 |
| 旁扫 | TrMenu/`DungeonPlus` 玩家路径 message 已无其它 `/ember`/`/corerpg` 拒门句；天赋 `*_cap` 为内部代号残留（见未选） |

### 替换表（展示层 · 不改门控逻辑）

| # | 文件 | 约位 | 旧（玩家/OP 可见） | 新 |
|---|------|------|-------------------|----|
| C6 | `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` L17 | OP gate `message=` | `…公共窗口：/ember → 灾厄` | `…公共窗口：枢纽菜单 → 灾厄` |

**口径备忘：** 完整新句建议：`§c灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：枢纽菜单 → 灾厄`（与 `calamity.yml` / quest「枢纽菜单 → 灾厄」同构；**不**改 `text=`）。只动 message 子串，零改 condition 结构。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 单句 OP 拒门清账（推荐）** | 只改上表 C6 · `EmberCalamity/option.yml` message；零改 `text=` / 人数 / loot | 闭环 B2.8–B2.11 唯一斜杠残留；与公共窗广播同构；专岗极薄 | 见众为误开测本的非 OP / 测岗 | **推荐** |
| **B. 本轮继续挂 / 不施工** | backlog 保留「灾厄 OP #6」；或明文本轮不批 | 零 diff | B0.3 域外斜杠债继续；与已洁广播口径裂 | 备选 |

**不施工：** 改 OP 判定表达式；开放非 OP 进 DP 灾厄；改 `calamity.yml`/TrMenu/loot；改小写 stub；扩扫天赋 `*_cap`（另窗）；宣称其它票债已清。

---

## 4. 验收（≤5）

1. **批准前：** 本稿落盘；玩法 YAML / 门控 `text=` / cost / loot **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `EmberCalamity/option.yml` 拒门 message **无** `/ember`；已为「枢纽菜单 → 灾厄」；`rg '/ember' plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` → **0**（注释若仍写 `/dp start` 作管理备忘可不计玩家 message）。  
3. **若批 A：** `$js-condition` 的 `text='%player_is_op%'=='yes'` **相对批前零 diff**；人数门 / reward-script **零 diff**。  
4. **若批 A · 禁项：** 非 OP 仍被拒；正式灾厄仍走公共窗/菜单；**不**宣称 B0.1 / 五入口预览 / 天赋 `*_cap` 已清。  
5. **若批 B：** backlog 明文「灾厄 OP #6 本轮不批」；不得把 B2.8～B2.11 PASS 写成「全进本拒门已无斜杠」。

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 灾厄 OP 调试拒门 | —（DP message） | OP 调试拒门 chat | 误 `/dp start EmberCalamity` | 枢纽 → 灾厄（既有） | 无 | **UI/文案可用** · 无新 NPC；正式玩家走菜单奔赴 |

**站岗自检：** 无新 NPC；仅拒门文案 → 最高标「UI 可用」，不宣称场景/公共窗改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 非 OP 误开灾厄 DP | 「仅供 OP 调试 · 正式请走公共窗口：枢纽菜单 → 灾厄」 | **不**教 `/ember` | 回枢纽菜单 → 灾厄 → 奔赴 |
| 2 | OP 调试开本 | （过门）既有开场文案 | 门控仍认 OP | — |
| 3 | 正式灾厄 | 公共窗 / 菜单（本窗零改） | 既有 CalamityService | — |

---

## 7. UX 否决条

- 禁止借机改 `%player_is_op%` 门或开放非 OP。  
- 禁止玩家路径新增斜杠教学（含改成 `/menu` 等）。  
- 禁止改 `calamity.yml` 广播 / TrMenu / loot / MM（本窗非其债）。  
- 禁止未批准改 DP YAML（除非批 A 后另开施工）。  
- 禁止把小写地图编辑 stub 与正式 `EmberCalamity` 混改。

---

## 8. 未选表（否因）

| 候选 | 本轮 | 否因 |
|------|------|------|
| B2.6–B2.11 | 排除 | 刚结 PASS；本窗是 B2.8 #6 **残余单句**，非重开整窗 |
| 五入口奖励预览 / isomorphic 空壳→真页 | 排除 | 已结或禁再开 |
| 非日常体力灰显 / 挂机 over_chance*·二档 UX | 排除 | 刚结 |
| B1.3 / 墙钟 2h / 霜锈前压 / 断塔升 B | 排除 | 关账或刻意或无证据 |
| B0.1 票扣显示名 | 排除 | 无新进本失败证据 |
| 精英奖励预览子菜单壳 | 排除 | 一点进本 · 须另开 |
| 天赋菜单 `*_cap` 内部代号 | 未选作 A | 玩家可见且多行，痛感可能高于 OP 门，但 **非总控点名**、跨多 lore/tell，厚于单句；可另开薄窗 |
| 套装 lore `gear_ember_t*_blade` / 使魔「功能筹备中」 | 未选 | 内部 id / 功能壳，非本轮斜杠拒门债 |
| 小写 `ember_calamity` stub 拒门 | 未选 | 已无人话斜杠；地图编辑空壳 |
| 新进度/货币/副本/Citizens/Paper | 排除 | 任务禁 |

---

## 9. 回总控一句话

**硬债空；已批 A——仅改 `EmberCalamity` OP 调试拒门一句去 `/ember`，对齐公共窗「枢纽菜单 → 灾厄」；闭环 B2.8～B2.11 残留 #6。待插件施工。**

**专岗：** **策划**（本稿）→ **插件**（已批后改 1× option.yml message）→ **测试**（静态 rg + 非 OP `/dp start EmberCalamity` 拒门冒烟）


---

## 批准记录（总控）

| 字段 | 值 |
|------|-----|
| 批准 | **A** · 2026-09-28 23:57 Asia/Shanghai |
| 岗 | 余烬-总控 |
| 依据 | parent design tip `1e109b3`，已在 `main` / `origin/main` 核验 |
| 范围 | 仅 `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` L17 `message=` 的 C6 玩家可见字符串替换；完整替换见 `docs/STATUS-ember-calamity-op-gate-copy-approve.md` |
| 禁 | `text=` / team condition；人数门；loot / MM / TrMenu；`calamity.yml`；小写 stub `ember_calamity`；B0.1 与其它残余债不得宣称已清；本批准 commit 不改 YAML |
| 下一 | **待插件施工**（仅一条 `message=` 字符串） |
| 状态 | **已批 A · 待插件施工** |
