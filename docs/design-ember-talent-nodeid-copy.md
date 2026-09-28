# 设计稿 · 天赋菜单 §8 `nodeId` / 前置内部 id 人话化（B2.14）

> **未批准前不施工。** 本稿只定 **玩家可见** TrMenu `ember_talent.yml` lore 中 `§8nodeId:` 与 `§8前置：<eng_id>` → 删或人话中文节点名；**禁**改 `talent.yml` 节点键 / cost / requires / stats、解锁 `command:` 实参、誓约逻辑、点数公式、洗点价、其它 TrMenu。
> 债源：B2.13 方案 B 残留 · close `33218c8` 旁扫点名；`*_cap` 十四处已清，同文件仍 **13**×`nodeId` + **7**×裸英前置。  
> 对齐：`design-ember-talent-cap-copy.md`（B2.13）· `design-ember-afk-ladder-id-copy.md`（B2.9）· UX「文案短清楚 · 少打指令 · TrMenu」。  
> 排除本轮：刚结 B2.6–B2.13；五入口奖励预览；非日常体力灰显；挂机 over_chance*/二档 UX；B1.3；墙钟 2h；霜锈前压；断塔升 B；B0.1 除非新证据；精英预览壳；isomorphic 空壳 tell→真页；新进度/货币/副本类型；Citizens；Paper。**勿宣称 B0.1 已清。**

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 天赋 TrMenu · **§8 `nodeId` / 前置内部 id 人话化**（UX · B2.13 方案 B 残留 · B2.14） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 00:15 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_talent.yml` · `plugins/CoreRpg/talent.yml` display · B2.13 close `33218c8` |
| 状态 | **已批 A · 待插件施工** |
| 关联 STATUS / 稿 | backlog · 批后 STATUS-approve / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A/B 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ember_talent.yml` 玩家可见 lore 中 `§8nodeId:` 行、`§8前置：*` 行（及方案 B 的裸属性键人话） | `talent.yml` 节点 id 与 requires/cost/stats |
| 热更 / TrMenu reload（服约定） | `command: corerpg talent unlock <nodeId>` 实参；洗点价；誓约树结构 |
| | 改点数硬顶 30、Lv 门槛、日免洗；其它菜单；git push；奖励页 `NI id:` / 套装 `gear_ember_*`（另窗） |

---

## 1. 问题一句话

**B2.13 已清字面 `*_cap`；玩家点开天赋节点仍见灰字 `nodeId: blaze_ember2`、`前置：blaze_crit1 + blaze_leech1`——与图标名「烬刃 · 余烬脉 / 烬斩」脱节，属同文件可测可勾销的薄文案窗。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| B2.13 刚结 | close `33218c8` · PASS；`rg '\*_cap' …/ember_talent.yml` → **0**；方案 B（§8 nodeId/前置）未做 |
| 逻辑侧已洁 | `talent.yml` 各节点已有中文 `display`（燃锋/灰踪/守碑/烬斩/余烬脉/…） |
| **脏点 A 域** | `ember_talent.yml`：**13**×`§8nodeId:` + **7**×`§8前置：<eng>` = **20** 处 |
| 旁扫更深（默认不绑 A） | 同文件 **9** 行裸属性键 lore（`phys_damage`/`crit_*`/`max_health`/…）；类型行仍写英文 `passive`/`skill` |
| 入口 | 枢纽 `ember_hub` → `menu: ember_talent`（少打指令已满足） |
| 候选对比 | 奖励页 `§8NI id:` 约 **34** 处 / **5** 文件；套装 `gear_ember_*`+戒 id 约 **4** 处 / 1 文件——跨页或痛次 |

### 替换表（展示层 · 不改节点键 / unlock command）

#### A1 · 删除 `§8nodeId:`（13 行 · 与图标中文名重复）

| # | 约位（图标） | 旧 lore | 新 |
|---|-------------|---------|----|
| N1～N13 | 根 1/2/3 · 烬斩 4 · 二层 a～i | `§8nodeId: <eng_id>` | **整行删除**（名称行已写中文节点名） |

验收：`rg '§8nodeId:' plugins/TrMenu/menus/ember_talent.yml` → **0**。

#### A2 · `§8前置：` 人话化（7 行 · 对照 `talent.yml` display）

| # | 图标约位 | 旧 | 新 |
|---|----------|----|----|
| P1 | 烬斩 | `§8前置：blaze_crit1 + blaze_leech1` | `§8前置：燃眼 + 饮烬` |
| P2 | 炽脉 | `§8前置：blaze_ember2` | `§8前置：余烬脉` |
| P3 | 再燃 | `§8前置：blaze_ember2` | `§8前置：余烬脉` |
| P4 | 细尘 | `§8前置：ash_veil2` | `§8前置：灰纱` |
| P5 | 掩息 | `§8前置：ash_veil2` | `§8前置：灰纱` |
| P6 | 护冢 | `§8前置：warden_bulwark2` | `§8前置：叠壁` |
| P7 | 誓碑 | `§8前置：warden_bulwark2` | `§8前置：叠壁` |

验收：`rg '§8前置：.*(blaze_|ash_|warden_)' …/ember_talent.yml` → **0**；前置行仅余中文节点名。

**口径备忘：** 只改/删玩家可见字符串；所有 `command: corerpg talent unlock *` 与 `talent.yml` 键 **一字不动**。

#### B 附加 · 裸属性键人话（9 行 · 仅若批 B）

| 旧键（摘要） | 新人话（建议） |
|--------------|----------------|
| `phys_damage` | `物攻` |
| `crit_damage_pct` | `暴伤` |
| `crit_chance_pct` | `暴击率` |
| `move_speed_pct` | `移速` |
| `attack_speed_pct` | `攻速` |
| `on_hit_slow_pct` | `击中减速` |
| `damage_taken_pct` | `受伤` |
| `max_health` | `生命` |
| `phys_defense` | `物防` |

数值与符号（`+1` / `+0.03` / `-0.01`）保持原样；**不**改 `talent.yml` stats。类型行 `passive`/`skill` 本窗默认不动（可另窗）。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 只清 nodeId + 前置（推荐）** | A1 删 13 行 + A2 七句人话；零改属性键 / talent.yml / unlock | 恰接 B2.13 方案 B 点名；单文件 20 处；玩家主痛（灰字内部 id） | 裸属性键 / passive·skill 仍在 | **推荐** |
| **B. A + 裸属性键人话** | A 全做 + 上表 9 行属性键中文 | 二层节点 lore 更洁 | 略厚；需统一属性中文表，易与 scrap/enhance 口径漂移 | 备选 |

**不施工：** 改 `talent.yml` 键名或 requires；改 cost/stats/洗点；开新节点；清奖励页 `NI id:` / 套装 `gear_ember_*`（另窗）；宣称 B0.1 已清；未批改 YAML。

---

## 4. 验收（≤5）

1. **本批准 commit：** 本稿落盘；玩法 YAML / `talent.yml` / 解锁 command **零 diff**（本 commit 仅文档 + backlog）。
2. **若批 A：** `ember_talent.yml` **无** `§8nodeId:`；**无** `§8前置：` 后接 `blaze_`/`ash_`/`warden_` 英 id；前置已为中文 display 名；`rg '§8nodeId:'` 与 `rg '§8前置：.*(blaze_|ash_|warden_)'` → **0**。  
3. **若批 A：** 所有 `command: corerpg talent unlock …` 与 `talent.yml` 节点键 / cost / requires / stats **相对批前零 diff**。  
4. **若批 A · 禁项：** 未改洗点价、硬顶 30、其它菜单；**不**宣称奖励页 NI id / 套装 `gear_ember_*` / B0.1 已清；裸属性键未清不挡 A PASS。  
5. **若批 B：** 另满足上表 9 行属性 lore **无** 字面英键（已换中文短名）；数值不变；仍禁改 unlock / talent.yml。

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 天赋菜单 | —（TrMenu） | 余烬 · 天赋 | 枢纽 → 天赋（既有） | `ember_talent` | 查看/洗点既有 | **UI/文案可用** · 无新 NPC |

**站岗自检：** 无新 NPC；仅文案 → 最高标「UI 可用」，不宣称养成数值改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 枢纽 → 天赋 | 节点名仍为「烬刃 · 余烬脉」等 | 无灰字 `nodeId:` | 返回枢纽 |
| 2 | 悬停需前置节点（烬斩等） | `§8前置：燃眼 + 饮烬`（中文） | **不**念 eng id | — |
| 3 | 点击解锁 | 仍走既有 unlock command | 插件既有拒/成 | 未满足前置则既有提示 |

---

## 7. UX 否决条

- 禁止借机改天赋点数、cost、requires、stats、洗点价。  
- 禁止把节点 **逻辑 id** 改名（只改/删展示句）。  
- 禁止玩家路径新增斜杠教学。  
- 禁止未批准改 `ember_talent.yml`（除非批 A/B 后另开施工）。  
- 禁止把奖励预览 `NI id:` / 套装 `gear_ember_*` / B0.1 塞进本窗宣称勾销。

---

## 8. 未选表（否因）

| 候选 | 本轮 | 否因 |
|------|------|------|
| B2.6–B2.13 | 排除 | 刚结 PASS；本窗引 close `33218c8` |
| 五入口奖励预览 / isomorphic 空壳→真页 | 排除 | 已结或禁再开 |
| 非日常体力灰显 / 挂机 over_chance*·二档 UX | 排除 | 刚结 |
| B1.3 / 墙钟 2h / 霜锈前压 / 断塔升 B | 排除 | 关账或刻意或无证据 |
| B0.1 票扣显示名 | 排除 | 无新进本失败证据；**勿宣称已清** |
| 精英奖励预览子菜单壳 | 排除 | 一点进本 · 须另开 |
| 奖励页 `§8NI id:`（约 34 / 5 文件） | 未选 | 主名已中文，灰字次痛；跨页厚于本窗单文件残留 |
| 套装 `gear_ember_*` / `acc_ember_raid_ring`（`ember_set.yml`） | 未选 | 更薄但痛次；刃/戒中文名已立；可另窗 |
| 裸属性键 / `passive`·`skill` 英词 | 未选作 A | 痛次于 nodeId/前置；可并方案 B 或另窗 |
| 使魔「功能筹备中」 | 未选 | 功能壳，非内部代号债 |
| 新进度/货币/副本/Citizens/Paper | 排除 | 任务禁 |
| 维护性空清点窗 | 未选 | 旁扫已有清晰薄痛，勿空交 |

---

## 9. 回总控一句话

**硬债空；已批 A——仅改 `ember_talent.yml`：删 13×`§8nodeId:` + 7×前置英 id→中文 display；零动 talent 键与 unlock command；引 B2.13 close `33218c8`。当前：已批 A · 待插件施工。**

## 总控批准记录

**已批方案 A**（2026-09-29 00:16 Asia/Shanghai · 余烬-总控）

- 批准依据：设计 tip `9e129cc`（已推至 `origin/main`）；只处理 `plugins/TrMenu/menus/ember_talent.yml` 的玩家可见 §8 内部 id 残留。
- 批准范围：A1 整行删除 13×`§8nodeId: …`；A2 将 7×英前置替换为 `燃眼 + 饮烬`、`余烬脉`、`灰纱`、`叠壁`（按替换表）。
- 硬边界：不做方案 B；不改裸属性键（`phys_damage` 等）、`talent.yml`、节点键、requires/cost/stats、unlock command 实参、其它菜单或 runtime/player/world。
- 本批准 commit 只改文档；插件 tip 落地后再施工、reload 并由测试按 §4 静态验收。不得宣称 B0.1 已清。

**专岗：** **策划**（本稿）→ **插件**（已批后改 1× TrMenu YAML 文案）→ **测试**（静态 rg nodeId/英前置=0 + 开菜单目视根/二层/烬斩前置）
