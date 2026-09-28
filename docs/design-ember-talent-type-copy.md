# 设计稿 · 天赋菜单类型英词 `passive`/`skill` 人话化（B2.16）

> **已批准 A · 待插件施工。** 本稿只定 **玩家可见** TrMenu `ember_talent.yml` lore 类型行中英文 `passive`/`skill` → 短中文（被动 / 去冗余 skill）；**禁**改 `talent.yml` 节点键 / cost / requires / stats、解锁 `command:` 实参、誓约逻辑、点数公式、洗点价、其它 TrMenu。
> 债源：B2.15 方案 B 残留 · close `a43010d` 旁扫点名；裸属性键已清，同文件仍 **13** 处类型英词。  
> 对齐：`design-ember-talent-attr-copy.md`（B2.15）· `design-ember-talent-nodeid-copy.md`（B2.14）· UX「文案短清楚 · 少打指令 · TrMenu」。  
> 排除本轮：刚结 B2.6–B2.15；五入口奖励预览；非日常体力灰显；挂机 over_chance*/二档 UX；B1.3；墙钟 2h；霜锈前压；断塔升 B；B0.1 除非新证据；精英预览壳；isomorphic 空壳 tell→真页；新进度/货币/副本类型；Citizens；Paper。**勿宣称 B0.1 已清。**

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 天赋 TrMenu · **类型英词 `passive`/`skill` 人话化**（UX · B2.15 方案 B 残留 · B2.16） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 00:35 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_talent.yml` · B2.15 close `a43010d` |
| 状态 | **已批 A · 待插件施工** |
| 批准记录（总控） | 2026-09-29 00:37 Asia/Shanghai：余烬-总控批准 **A**；批准设计 tip `34e61dd` 已推至 `origin/main`。仅允许插件另开施工改 `ember_talent.yml` 13 个类型 lore 行；本批准 commit 不改 YAML。 |
| 关联 STATUS / 稿 | `docs/STATUS-ember-talent-type-copy-approve.md` · backlog · 测报 |

### 硬约束（本稿）

| 可动（**仅批 A 后另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ember_talent.yml` 玩家可见 lore 类型行中 `passive`/`skill`（方案 B 的 `cost`→消耗不在本轮） | `talent.yml` 节点 id 与 requires/cost/**stats 键名与数值** |
| 热更 / TrMenu reload（服约定） | `command: corerpg talent unlock <nodeId>` 实参；洗点价；誓约树结构 |
| | 改点数硬顶 30、Lv 门槛、日免洗；其它菜单；git push；奖励页 `NI id:` / 套装 `gear_ember_*`（另窗） |

---

## 1. 问题一句话

**B2.15 已清二层效果裸属性键；玩家悬停天赋仍见「根节点 · passive · cost 2」「主动技 · skill · cost 4」——类型标签仍是英词，属同文件可测可勾销的薄文案窗。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| B2.15 刚结 | close `a43010d` · PASS；`rg 'phys_damage\|crit_\|…' …/ember_talent.yml` → **0**；方案 B（类型英词）明确未做 |
| **脏点 A 域** | `ember_talent.yml`：**13** 处含字面 `passive`/`skill`（根×3 + 烬斩 skill×1 + 二层×9） |
| 入口 | 枢纽 `ember_hub` → `menu: ember_talent`（少打指令已满足） |
| 候选对比 | 奖励页 `§8NI id:`/`§8NI:` **34** 处 / **5** 文件（主名已中文，灰字次痛）；套装 `ember_set.yml` `gear_ember_*`+`acc_ember_raid_ring` **4** 处 lore/tell（刃/戒中文名已立，痛次） |

### 替换表（展示层 · 不改 talent.yml / unlock）

#### A · 十三处类型英词 → 短中文 / 去冗余（数值与 `cost` 原样）

| # | 形态（摘要） | 旧片段 | 新 |
|---|--------------|--------|----|
| A1–A3 | 根节点×3（燃锋/灰踪/守碑） | `§7根节点 · passive · cost 2` | `§7根节点 · 被动 · cost 2` |
| A4 | 烬刃 · 烬斩 | `§7主动技 · skill · cost 4` | `§7主动技 · cost 4`（「主动技」已含类型，去英词冗余） |
| A5–A13 | 二层×9 | `§7二层 · passive · cost N` | `§7二层 · 被动 · cost N`（N=3 或 4 原样） |

验收：`rg 'passive|skill' plugins/TrMenu/menus/ember_talent.yml` → **0**（展示层）；`talent.yml` / unlock command **一字不动**。

**口径备忘：** 只改玩家可见字符串；所有 `command: corerpg talent unlock *` 与 `talent.yml` 键 / stats **一字不动**；效果行中文属性名（物攻/暴伤/…）**不动**。

#### B 附加 · 同句 `cost`→`消耗`（仅若批 B）

| 旧片段（A 后） | 新 |
|----------------|----|
| `· cost 2` / `· cost 3` / `· cost 4` | `· 消耗 2` / `· 消耗 3` / `· 消耗 4` |

同文件类型行约 **13** 处一并替换；其它菜单 `cost` 字样不在本窗。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 只清 13×类型英词（推荐）** | 上表 A1～A13；`cost` 数字原样；零改 talent.yml / unlock | 恰接 B2.15 方案 B 点名；单文件 13 处；类型行主痛；可测 rg=0 | 同句仍留英词 `cost` | **推荐** |
| **B. A + 同句 `cost`→消耗** | A 全做 + 类型行 `cost`→消耗 | 类型行全中文 | 略厚；`cost` 痛次于类型词；需与其它菜单「cost」口径另对齐 | 备选 |

**不施工：** 改 `talent.yml` 键名或 stats 数值；改 cost 数值/requires/洗点；开新节点；清奖励页 `NI id:` / 套装 `gear_ember_*`（另窗）；宣称 B0.1 已清；未批改 YAML。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿落盘；玩法 YAML / `talent.yml` / 解锁 command **零 diff**（本 commit 仅文档 + backlog）。
2. **若批 A：** `ember_talent.yml` 类型行 **无**字面 `passive`/`skill`；`cost` 与数值原样（除非批 B）；`rg 'passive|skill' …/ember_talent.yml` → **0**。  
3. **若批 A：** 所有 `command: corerpg talent unlock …` 与 `talent.yml` 节点键 / cost / requires / **stats** **相对批前零 diff**。  
4. **若批 A · 禁项：** 未改洗点价、硬顶 30、其它菜单；**不**宣称奖励页 NI id / 套装 `gear_ember_*` / B0.1 已清；同句 `cost` 未清不挡 A PASS。  
5. **若批 B：** 另满足类型行无字面 `cost`（已换「消耗」）；仍禁改 unlock / talent.yml / 其它菜单。

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
| 1 | 枢纽 → 天赋 | 节点名仍为「烬刃 · 燃锋」等 | 类型行「被动 / 主动技」人话 | 返回枢纽 |
| 2 | 悬停根/二层/烬斩 | 无字面 `passive`/`skill` | **不**念英类型词 | — |
| 3 | 点击解锁 | 仍走既有 unlock command | 插件既有拒/成 | 未满足前置则既有提示 |

---

## 7. UX 否决条

- 禁止借机改天赋点数、cost **数值**、requires、stats、洗点价。  
- 禁止把属性 / 节点 **逻辑键** 在 `talent.yml` 改名（只改 TrMenu 展示句）。  
- 禁止玩家路径新增斜杠教学。  
- 禁止未批准改 `ember_talent.yml`（本轮仅限批 A 后另开施工）。
- 禁止把奖励预览 `NI id:` / 套装 `gear_ember_*` / B0.1 塞进本窗宣称勾销。

---

## 8. 未选表（否因）

| 候选 | 本轮 | 否因 |
|------|------|------|
| B2.6–B2.15 | 排除 | 刚结 PASS；本窗引 close `a43010d` |
| 五入口奖励预览 / isomorphic 空壳→真页 | 排除 | 已结或禁再开 |
| 非日常体力灰显 / 挂机 over_chance*·二档 UX | 排除 | 刚结 |
| B1.3 / 墙钟 2h / 霜锈前压 / 断塔升 B | 排除 | 关账或刻意或无证据 |
| B0.1 票扣显示名 | 排除 | 无新进本失败证据；**勿宣称已清** |
| 精英奖励预览子菜单壳 | 排除 | 一点进本 · 须另开 |
| 奖励页 `§8NI id:`（**34** / **5** 文件） | 未选 | 主名已中文，灰字次痛；跨页厚于本窗单文件 13 行残留 |
| 套装 `gear_ember_*` / `acc_ember_raid_ring`（`ember_set.yml` **4** 处） | 未选 | 更薄但痛次；刃/戒中文名已立；可另窗 |
| 同句 `cost` 英词 | 未选作 A | 痛次于类型词；可并方案 B |
| 枢纽 `ember_event` 世界 id 灰字 | 未选 | 单点旁证；非本轮旁扫主候选 |
| 新进度/货币/副本/Citizens/Paper | 排除 | 任务禁 |
| 维护性空清点窗 | 未选 | 旁扫已有清晰薄痛，勿空交 |

---

## 9. 回总控一句话

**硬债空；已批 A——仅改 `ember_talent.yml`：13×类型行 `passive`→被动、`skill` 去冗余；`cost` 原样；零动 talent 键与 unlock/stats；引 B2.15 close `a43010d`。当前：已批 A · 待插件施工。**

**专岗：** **策划**（本稿）→ **插件**（批后改 1× TrMenu YAML 文案）→ **测试**（静态 rg `passive|skill`=0 + 开菜单目视根/二层/烬斩类型行）

## 总控批准记录

**已批方案 A**（2026-09-29 00:37 Asia/Shanghai · 余烬-总控）

- 批准依据：设计 tip `34e61dd`（已推至 `origin/main`）；只处理 `plugins/TrMenu/menus/ember_talent.yml` 的玩家可见类型英词残留。
- 批准范围：根节点 `passive`→`被动` 3 处；主动技去掉 `skill` 1 处；二层 `passive`→`被动` 9 处；`cost` 与 2/3/4 数值原样。
- 硬边界：不做方案 B；不改 `cost`→`消耗`，不改 `talent.yml`、节点键、requires/cost/stats、unlock command 实参、其它菜单或 runtime/player/world。
- 本批准 commit 只改文档；插件 tip 落地后再施工、reload 并由测试按 §4 静态验收。不得宣称 B0.1 已清。

**专岗：** **策划**（本稿）→ **插件**（已批后改 1× TrMenu YAML 文案）→ **测试**（静态 rg `passive|skill`=0 + 开菜单目视根/二层/烬斩类型行）
