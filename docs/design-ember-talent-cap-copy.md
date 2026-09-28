# 设计稿 · 天赋菜单「*_cap」玩家可见文案人话化（B2.13）

> **已批 A · 待插件施工。** 本稿只定 **玩家可见** TrMenu `ember_talent.yml` lore / Open·tell 中字面 `*_cap` → 一层顶中文名；**禁**改 `talent.yml` 节点键 / cost / requires / stats、解锁 `command:` 实参、誓约逻辑、点数公式、洗点价、其它 TrMenu。
> 债源：硬债空；B2.12 close `483215f` 后旁扫总控点名；`ember_talent.yml` **14** 处玩家句仍写字面 `*_cap`，与一层顶显示名「燎原 / 烟幕 / 永护」脱节。  
> 对齐：`design-ember-afk-ladder-id-copy.md`（B2.9 去内部代号）· UX「文案短清楚 · 少打指令 · TrMenu」。  
> 排除本轮：刚结 B2.6–B2.12；五入口奖励预览；非日常体力灰显；挂机 over_chance*/二档 UX；B1.3；墙钟 2h；霜锈前压；断塔升 B；B0.1 除非新证据；精英预览壳；isomorphic 空壳 tell→真页；新进度/货币/副本类型；Citizens；Paper。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 天赋 TrMenu · **`*_cap` 玩家可见文案人话化**（UX · 内部代号 · B2.13） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 00:06 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_talent.yml` · `plugins/CoreRpg/talent.yml` 一层顶 display · B2.12 close `483215f` |
| 状态 | **已批 A · 待插件施工** |
| 批准记录（总控） | 2026-09-29 00:07 Asia/Shanghai：余烬-总控批准 **A**；批准 tip `c86ebd6` 已推至 `origin/main`。仅允许插件另开施工改 `ember_talent.yml` 14 个玩家可见 `*_cap` 字面；本批准 commit 不改 YAML。 |
| 关联 STATUS / 稿 | backlog · 批后 STATUS-approve / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A/B 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ember_talent.yml` 玩家可见 lore / Open tell / 说明 tell 中含 `*_cap` 的字符串 | `talent.yml` 节点 id（`blaze_cap`/`ash_cap`/`warden_cap`）与 requires/cost/stats |
| 热更 / TrMenu reload（服约定） | `command: corerpg talent unlock <nodeId>` 实参；洗点价；誓约树结构 |
| 方案 B：同文件 §8 `nodeId:` / `前置：*` 内部 id / 裸属性键人话化 | 改点数硬顶 30、Lv 门槛、日免洗；其它菜单；git push |

---

## 1. 问题一句话

**枢纽/进本/挂机入口内部代号与斜杠债已清；天赋主路径仍在 Open tell、解锁说明、规则速览与九个二层节点 lore 写字面 `*_cap`——玩家看不到哪颗是一层顶，与 `talent.yml` 已有中文显示名「燎原 / 烟幕 / 永护」脱节，属可测可勾销的薄文案窗。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| B2.12 刚结 | close `483215f` · PASS；下一薄窗待挑；旁扫点名天赋 `*_cap` |
| 一层顶 display（逻辑侧已洁） | `talent.yml`：`blaze_cap`→「§6燎原」· `ash_cap`→「§7烟幕」· `warden_cap`→「§9永护」 |
| **脏点** | `ember_talent.yml` **14** 处字面 `*_cap`：Open tell×1 · U lore/tell×2 · 规则速览 lore/tell×2 · 二层 a～i lore×9 |
| 入口 | 枢纽 `ember_hub` → `menu: ember_talent`（少打指令已满足） |
| 旁扫洁域 | TrMenu/DP 玩家 message 无新 `/corerpg`/`/ember` 拒门句；quest 无日/周/深渊票；HD 无 MM→NI |
| 同文件更深灰字（本窗默认不绑） | §8 `nodeId: *` ×13；部分 `前置：blaze_ember2` 等内部 id；裸 `phys_damage` 等属性键 |

### 替换表（展示层 · 不改节点键）

#### A1 · 全局说明（Open / U / 规则速览）

| # | 约位 | 旧（摘要） | 新 |
|---|------|-----------|----|
| T1 | Open tell | `二层需先点满一层顶（*_cap）` | `二层需先点满一层顶（燎原 / 烟幕 / 永护）` |
| T2 | U lore | 同上 | 同上 |
| T3 | U tell | `先点满 §f*_cap` | `先点满一层顶 §f燎原 / 烟幕 / 永护` |
| T4 | 规则速览 lore #4 | `二层需先点满一层顶（*_cap）` | `二层需先点满一层顶（燎原 / 烟幕 / 永护）` |
| T5 | 规则速览 tell | `二层需 *_cap` | `二层需一层顶（燎原 / 烟幕 / 永护）` |

#### A2 · 二层节点 lore（按誓约写一层顶中文名；**不**改 unlock command）

| 图标 | 誓约 | 旧 | 新 |
|------|------|----|----|
| a / b / c | 烬刃 | `§e先点满一层顶（*_cap）` | `§e先点满一层顶「燎原」` |
| d / e / f | 灰行 | 同上 | `§e先点满一层顶「烟幕」` |
| g / h / i | 守墓 | 同上 | `§e先点满一层顶「永护」` |

**口径备忘：** 只改玩家可见字符串；`command: corerpg talent unlock *` 与 `talent.yml` 键 **一字不动**。验收以 `rg '\*_cap' plugins/TrMenu/menus/ember_talent.yml` → **0** 为准。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 只清 `*_cap` 十四处（推荐）** | A1+A2；零改 nodeId/前置/属性键/talent.yml | 总控旁扫点名；玩家主痛；单文件可测可勾销 | §8 nodeId 等灰字仍在 | **推荐** |
| **B. A + §8 内部 id/属性键薄扫** | A 全做 + 删或人话化 `nodeId:` / `前置：*` / 裸属性键 | 天赋页更洁 | 行数多、需对照 display 表，略厚；易误伤管理排查 | 备选 |

**不施工：** 改 `talent.yml` 键名或 requires；改 cost/stats/洗点；开新节点；清奖励页 `NI id:` / 套装 `gear_ember_*`（另窗）；宣称 B0.1 已清。

---

## 4. 验收（≤5）

1. **批准前：** 本稿落盘；玩法 YAML / `talent.yml` / 解锁 command **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `ember_talent.yml` 玩家 lore/tell **无** 字面 `*_cap`；已出现「燎原 / 烟幕 / 永护」或按誓约「一层顶「…」」口径；`rg '\*_cap' …/ember_talent.yml` → **0**。  
3. **若批 A：** 所有 `command: corerpg talent unlock …` 与 `talent.yml` 节点键 / cost / requires **相对批前零 diff**。  
4. **若批 A · 禁项：** 未改洗点价、硬顶 30、其它菜单；**不**宣称 nodeId/NI id/套装内部代号已清。  
5. **若批 B：** 另满足同文件玩家可见 §8 **无** 字面 `nodeId:` / 裸 `前置：<eng_id>`（可改为人话节点名）；属性键可改短中文或删，**仍禁**改解锁 command。

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
| 1 | 枢纽 → 天赋 | Open tell 写「一层顶（燎原/烟幕/永护）」 | **不**念 `*_cap` | 返回枢纽 |
| 2 | 点解锁说明 / 规则速览 | 同口径 | 知二层前置 | — |
| 3 | 点二层节点 | lore「先点满一层顶「燎原」」等 | 点击仍走既有 unlock command | 未满一层顶则插件既有拒 |

---

## 7. UX 否决条

- 禁止借机改天赋点数、cost、requires、stats、洗点价。  
- 禁止把节点 **逻辑 id** 改名（只改展示句）。  
- 禁止玩家路径新增斜杠教学（含改成「请打 /corerpg talent」）。  
- 禁止未批准改 `ember_talent.yml`（除非批 A/B 后另开施工）。  
- 禁止把奖励预览 `NI id:` / 套装 `gear_ember_*` 塞进本窗宣称勾销。

---

## 8. 未选表（否因）

| 候选 | 本轮 | 否因 |
|------|------|------|
| B2.6–B2.12 | 排除 | 刚结 PASS；本窗引 close `483215f` 旁扫 |
| 五入口奖励预览 / isomorphic 空壳→真页 | 排除 | 已结或禁再开 |
| 非日常体力灰显 / 挂机 over_chance*·二档 UX | 排除 | 刚结 |
| B1.3 / 墙钟 2h / 霜锈前压 / 断塔升 B | 排除 | 关账或刻意或无证据 |
| B0.1 票扣显示名 | 排除 | 无新进本失败证据 |
| 精英奖励预览子菜单壳 | 排除 | 一点进本 · 须另开 |
| §8 `nodeId:` / `前置：eng` / 裸属性键 | 未选作 A | 痛次于字面 `*_cap`；可并方案 B 或另窗 |
| 奖励页 `NI id:` / 套装 `gear_ember_*` | 未选 | §8 管理灰字跨多页，非总控旁扫本点 |
| 使魔「功能筹备中」 | 未选 | 功能壳，非内部代号债 |
| 新进度/货币/副本/Citizens/Paper | 排除 | 任务禁 |
| 维护性空清点窗 | 未选 | 本旁扫已有清晰薄痛，勿空交 |

---

## 9. 回总控一句话

**硬债空；已批 A——仅改 `ember_talent.yml` 十四处字面 `*_cap` 为人话一层顶（燎原/烟幕/永护），零动 talent 键与 unlock command；引 B2.12 close `483215f`。当前：已批 A · 待插件施工。**

**专岗：** **策划**（本稿）→ **插件**（已批后改 1× TrMenu YAML 文案）→ **测试**（静态 rg `*_cap`=0 + 开菜单目视 Open/二层 lore）
