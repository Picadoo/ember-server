# 设计稿 · 天赋菜单裸属性键人话化（B2.15）

> **已批准 A · 待插件施工。** 本稿只定 **玩家可见** TrMenu `ember_talent.yml` 二层节点 lore 中裸属性键（`phys_damage` / `crit_*` / …）→ 短中文属性名；**禁**改 `talent.yml` 节点键 / cost / requires / stats、解锁 `command:` 实参、誓约逻辑、点数公式、洗点价、其它 TrMenu。
> 债源：B2.14 方案 B 残留 · close `3b00eff` 旁扫点名；`nodeId`/前置已清，同文件仍 **9** 行裸属性键 lore（可选附带 `passive`/`skill` 类型英词）。  
> 对齐：`design-ember-talent-nodeid-copy.md`（B2.14）· `design-ember-talent-cap-copy.md`（B2.13）· UX「文案短清楚 · 少打指令 · TrMenu」。  
> 排除本轮：刚结 B2.6–B2.14；五入口奖励预览；非日常体力灰显；挂机 over_chance*/二档 UX；B1.3；墙钟 2h；霜锈前压；断塔升 B；B0.1 除非新证据；精英预览壳；isomorphic 空壳 tell→真页；新进度/货币/副本类型；Citizens；Paper。**勿宣称 B0.1 已清。**

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 天赋 TrMenu · **裸属性键人话化**（UX · B2.14 方案 B 残留 · B2.15） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 00:25 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_talent.yml` · `plugins/CoreRpg/talent.yml` stats 键 · B2.14 close `3b00eff` |
| 状态 | **已批 A · 待插件施工** |
| 批准记录（总控） | 2026-09-29 00:27 Asia/Shanghai：余烬-总控批准 **A**；批准设计 tip `26d7895` 已推至 `origin/main`。仅允许插件另开施工改 `ember_talent.yml` 9 个裸属性 lore；本批准 commit 不改 YAML。 |
| 关联 STATUS / 稿 | `docs/STATUS-ember-talent-attr-copy-approve.md` · backlog · 测报 |

### 硬约束（本稿）

| 可动（**仅批 A 后另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ember_talent.yml` 玩家可见 lore 中裸属性键行（方案 B 类型英词不在本轮） | `talent.yml` 节点 id 与 requires/cost/**stats 键名与数值** |
| 热更 / TrMenu reload（服约定） | `command: corerpg talent unlock <nodeId>` 实参；洗点价；誓约树结构 |
| | 改点数硬顶 30、Lv 门槛、日免洗；其它菜单；git push；奖励页 `NI id:` / 套装 `gear_ember_*`（另窗） |

---

## 1. 问题一句话

**B2.14 已清 `nodeId`/前置；玩家悬停二层天赋仍见效果行 `phys_damage +1`、`crit_chance_pct +0.005 · phys_damage +1`——效果本身用内部属性键标注，属同文件可测可勾销的薄文案窗。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| B2.14 刚结 | close `3b00eff` · PASS；`rg '§8nodeId:' …/ember_talent.yml` → **0**；前置英 id → **0**；方案 B（裸属性键）明确未做 |
| 逻辑侧 stats | `talent.yml` 仍用英键（`phys_damage` 等）——**本窗不改**；仅改 TrMenu 展示句 |
| **脏点 A 域** | `ember_talent.yml` 二层 lore：**9** 行含裸属性键（余烬脉/炽脉/再燃/灰纱/细尘/掩息/叠壁/护冢/誓碑） |
| 旁扫更深（默认不绑 A） | 同文件类型行仍写英文 `passive`/`skill`（根+二层等约 **13** 处含该词） |
| 入口 | 枢纽 `ember_hub` → `menu: ember_talent`（少打指令已满足） |
| 候选对比 | 奖励页 `§8NI id:` **33** 处 / **5** 文件（主名已中文，灰字次痛）；套装 `gear_ember_*`+戒 id **4** 处 / 1 文件（刃/戒中文名已立，痛次） |

### 替换表（展示层 · 不改 talent.yml stats）

#### A · 九行裸属性键 → 短中文（数值/符号原样）

| # | 图标名 | 旧 lore | 新 |
|---|--------|---------|----|
| A1 | 烬刃 · 余烬脉 | `§7phys_damage +1` | `§7物攻 +1` |
| A2 | 烬刃 · 炽脉 | `§7crit_damage_pct +0.03` | `§7暴伤 +0.03` |
| A3 | 烬刃 · 再燃 | `§7crit_chance_pct +0.005 · phys_damage +1` | `§7暴击率 +0.005 · 物攻 +1` |
| A4 | 灰行 · 灰纱 | `§7move_speed_pct +0.01` | `§7移速 +0.01` |
| A5 | 灰行 · 细尘 | `§7attack_speed_pct +0.01` | `§7攻速 +0.01` |
| A6 | 灰行 · 掩息 | `§7on_hit_slow_pct +0.03 · attack_speed_pct +0.01` | `§7击中减速 +0.03 · 攻速 +0.01` |
| A7 | 守墓 · 叠壁 | `§7damage_taken_pct -0.01` | `§7受伤 -0.01` |
| A8 | 守墓 · 护冢 | `§7max_health +2` | `§7生命 +2` |
| A9 | 守墓 · 誓碑 | `§7phys_defense +1 · damage_taken_pct -0.01` | `§7物防 +1 · 受伤 -0.01` |

**键→中文对照（本稿口径 · 仅展示）：**

| 旧键 | 新人话 |
|------|--------|
| `phys_damage` | `物攻` |
| `crit_damage_pct` | `暴伤` |
| `crit_chance_pct` | `暴击率` |
| `move_speed_pct` | `移速` |
| `attack_speed_pct` | `攻速` |
| `on_hit_slow_pct` | `击中减速` |
| `damage_taken_pct` | `受伤` |
| `max_health` | `生命` |
| `phys_defense` | `物防` |

验收：`rg 'phys_damage|crit_damage_pct|crit_chance_pct|move_speed_pct|attack_speed_pct|on_hit_slow_pct|damage_taken_pct|max_health|phys_defense' plugins/TrMenu/menus/ember_talent.yml` → **0**（展示层）；`talent.yml` stats 键 **一字不动**。

**口径备忘：** 只改玩家可见字符串；所有 `command: corerpg talent unlock *` 与 `talent.yml` 键 / stats **一字不动**。

#### B 附加 · 类型英词人话（约 13 处含 `passive`/`skill` · 仅若批 B）

| 旧片段（摘要） | 新 |
|----------------|----|
| `· passive ·` | `· 被动 ·` |
| `· skill ·` | `· 主动 ·`（烬斩等已写「主动技 · skill」→「主动技」或「主动技 · 主动」；推荐整段统一为 `§7二层 · 被动 · cost N` / `§7主动技 · cost N`） |

根三节点 `§7根节点 · passive · cost 2` → `§7根节点 · 被动 · cost 2`；烬斩 `§7主动技 · skill · cost 4` → `§7主动技 · cost 4`（去重复英词）。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 只清九行裸属性键（推荐）** | 上表 A1～A9；零改类型英词 / talent.yml / unlock | 恰接 B2.14 方案 B 点名；单文件 9 处；效果行主痛 | `passive`/`skill` 仍在 | **推荐** |
| **B. A + 类型英词人话** | A 全做 + 类型行 `passive`→被动、`skill` 去/→主动 | 类型行也洁 | 略厚；类型词痛次于效果键 | 备选 |

**不施工：** 改 `talent.yml` 键名或 stats 数值；改 cost/requires/洗点；开新节点；清奖励页 `NI id:` / 套装 `gear_ember_*`（另窗）；宣称 B0.1 已清；未批改 YAML。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿落盘；玩法 YAML / `talent.yml` / 解锁 command **零 diff**（本 commit 仅文档 + backlog）。
2. **若批 A：** `ember_talent.yml` 九行效果 lore **无**字面英属性键（已换上表中文短名）；数值与 `+`/`-`/`·` 分隔原样；`rg 'phys_damage|crit_damage_pct|crit_chance_pct|move_speed_pct|attack_speed_pct|on_hit_slow_pct|damage_taken_pct|max_health|phys_defense' …/ember_talent.yml` → **0**。  
3. **若批 A：** 所有 `command: corerpg talent unlock …` 与 `talent.yml` 节点键 / cost / requires / **stats** **相对批前零 diff**。  
4. **若批 A · 禁项：** 未改洗点价、硬顶 30、其它菜单；**不**宣称奖励页 NI id / 套装 `gear_ember_*` / B0.1 已清；`passive`/`skill` 未清不挡 A PASS。  
5. **若批 B：** 另满足类型行无字面 `passive`/`skill`（已换被动/主动或删冗余）；仍禁改 unlock / talent.yml。

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
| 1 | 枢纽 → 天赋 | 节点名仍为「烬刃 · 余烬脉」等 | 效果行「物攻 +1」等人话 | 返回枢纽 |
| 2 | 悬停二层节点（再燃/掩息/誓碑等） | 复合效果行中文短名 + 原数值 | **不**念 eng 属性键 | — |
| 3 | 点击解锁 | 仍走既有 unlock command | 插件既有拒/成 | 未满足前置则既有提示 |

---

## 7. UX 否决条

- 禁止借机改天赋点数、cost、requires、stats、洗点价。  
- 禁止把属性 **逻辑键** 在 `talent.yml` 改名（只改 TrMenu 展示句）。  
- 禁止玩家路径新增斜杠教学。  
- 禁止未批准改 `ember_talent.yml`（除非批 A/B 后另开施工）。  
- 禁止把奖励预览 `NI id:` / 套装 `gear_ember_*` / B0.1 塞进本窗宣称勾销。

---

## 8. 未选表（否因）

| 候选 | 本轮 | 否因 |
|------|------|------|
| B2.6–B2.14 | 排除 | 刚结 PASS；本窗引 close `3b00eff` |
| 五入口奖励预览 / isomorphic 空壳→真页 | 排除 | 已结或禁再开 |
| 非日常体力灰显 / 挂机 over_chance*·二档 UX | 排除 | 刚结 |
| B1.3 / 墙钟 2h / 霜锈前压 / 断塔升 B | 排除 | 关账或刻意或无证据 |
| B0.1 票扣显示名 | 排除 | 无新进本失败证据；**勿宣称已清** |
| 精英奖励预览子菜单壳 | 排除 | 一点进本 · 须另开 |
| 奖励页 `§8NI id:`（**33** / **5** 文件） | 未选 | 主名已中文，灰字次痛；跨页厚于本窗单文件 9 行残留 |
| 套装 `gear_ember_*` / `acc_ember_raid_ring`（`ember_set.yml` **4** 处） | 未选 | 更薄但痛次；刃/戒中文名已立；可另窗 |
| `passive`·`skill` 英词 | 未选作 A | 痛次于效果属性键；可并方案 B |
| 使魔「功能筹备中」 | 未选 | 功能壳，非内部代号债 |
| 新进度/货币/副本/Citizens/Paper | 排除 | 任务禁 |
| 维护性空清点窗 | 未选 | 旁扫已有清晰薄痛，勿空交 |

---

## 9. 回总控一句话

**硬债空；荐 A——仅改 `ember_talent.yml`：9×二层效果裸属性键→短中文（物攻/暴伤/…）；零动 talent 键与 unlock/stats；引 B2.14 close `3b00eff`。当前：已批 A · 待插件施工。**

**专岗：** **策划**（本稿）→ **插件**（批后改 1× TrMenu YAML 文案）→ **测试**（静态 rg 英属性键=0 + 开菜单目视二层九节点效果行）
