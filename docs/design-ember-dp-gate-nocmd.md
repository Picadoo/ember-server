# 设计稿 · DP 进本拒门消息去斜杠（B0.3 域外残留）

> **未批准前不施工。** 本稿只定 DungeonPlus `option.yml` 玩家可见 **拒门 / 开场 message 字符串**（去斜杠教学）；**禁**改门控条件表达式、人数门、体力 cost、票、掉落、MM、TrMenu、CoreRpg 数值。  
> 债源：B0.3 TrMenu 玩家可见去指令 **PASS**（`STATUS-ember-b03-menu-no-cmd-test.md`）；本轮扫入口发现 **周本/团本/深渊拒门** 仍写 `（/corerpg level 查看）`，**盟 Boss** 拒门+开场仍教 `/corerpg guild boss`——日常七线 / 精英拒门已无人话斜杠，属菜单/入口一致性薄修。  
> 对齐：`design-ember-b03-menu-no-cmd.md` · UX 否决「零手打指令」· backlog 软债扫。  
> 排除本轮：B2.6/B2.7 日票台词（刚结）；五入口奖励预览；非日常体力灰显；挂机 over_chance*/二档 UX；B2.3/B2.4；B1.3；墙钟 2h；霜晶/锈轨前压；断塔 B；精英奖励子菜单壳；B0.1 除非新证据；新养成/货币/副本类型；Citizens；改 Paper。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | DP 进本拒门 · **去斜杠教学**（UX · 可维护性 · B0.3 域外） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 23:06 Asia/Shanghai |
| 关联 | `EmberWeekly` / `EmberRaid` / `EmberAbyss` / `EmberGuildBoss` `option.yml` · 对照日常/精英已洁句 |
| 状态 | **待批 · 仅文档** |
| 关联 STATUS / 稿 | backlog · `design-ember-b03-menu-no-cmd.md` · `STATUS-ember-b03-menu-no-cmd-test.md` |

### 硬约束（本稿）

| 可动（**仅若批 A/B 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| 上列 4 个 live `option.yml` 中 **message=** / `$message{…text=…}` 玩家字符串 | `$js-condition` / `$team-condition` 的 **text=** 表达式与人数/等级阈值 |
| 可选：灾厄 OP 调试拒门句去 `/ember`（方案 B） | 体力 cost / 票 / 进本 command / loot / MM / TrMenu |
| 热更约定的 DP reload（服侧） | 新养成/货币；Citizens；git push |

---

## 1. 问题一句话

**TrMenu 已清斜杠教学；周本/团本/深渊等级不足拒门仍教 `/corerpg level`，盟 Boss 拒门与开场仍教 `/corerpg guild boss`——与日常/精英已洁口径及盟约菜单「点击前往」不一致，属有证据的入口文案薄修。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| B0.3 | TrMenu 16 文件玩家 lore/tell 斜杠教学 **0** · 测 PASS |
| 日常七线拒门 | 已洁：`§c日常本需要余烬等级 §eLv.10§c · 队伍中有人等级不足`（**无**斜杠） |
| 精英拒门 | 已洁：`§c精英试炼需要余烬 Lv.40，且本周尚未通关` |
| 周本脏 | `EmberWeekly/option.yml` L17：`…等级不足（/corerpg level 查看）` |
| 团本脏 | `EmberRaid/option.yml` L18：同上 `/corerpg level 查看` |
| 深渊脏 | `EmberAbyss/option.yml` L19：同上 |
| 盟 Boss 脏 | `EmberGuildBoss/option.yml` L18 拒门教 `/corerpg guild boss`；L20 开场 `$message` 再念一遍斜杠门控 |
| 盟约菜单（对照） | `ember_guild` 周盟 Boss：tell「点击前往盟 Boss」+ 底层 `command: corerpg guild boss`（玩家不可见） |
| 灾厄 OP（低优先） | `EmberCalamity` OP 拒门：`正式灾厄请走公共窗口：/ember → 灾厄`（仅 OP 调试门；正式玩家走菜单） |
| bak 堆 | `ember_weekly.yml_*.bak` ×76 · **未入库**（gitignore `*.bak`）→ **不**挂本窗 |

### 替换表（展示层 · 不改门控逻辑）

| # | 文件 | 约位 | 旧（玩家可见） | 新 |
|---|------|------|----------------|----|
| 1 | `EmberWeekly/option.yml` | gate message | `…等级不足（/corerpg level 查看）` | `…等级不足 · 打开枢纽 → 角色查看等级` |
| 2 | `EmberRaid/option.yml` | 同上 | 同上 | 同上（Lv 数字保持 35） |
| 3 | `EmberAbyss/option.yml` | 同上 | 同上 | 同上（Lv 数字保持 25） |
| 4 | `EmberGuildBoss/option.yml` | gate message | `需由队长执行 /corerpg guild boss（消耗盟约贡献）开启` | `需由队长在枢纽 → 盟约 →「周盟 Boss」开启（消耗盟约贡献）` |
| 5 | `EmberGuildBoss/option.yml` | 开场 `$message` | `进本由 §e/corerpg guild boss §7门控（贡献消耗）` | `进本由盟约菜单「周盟 Boss」门控（贡献消耗）` |
| 6（B） | `EmberCalamity/option.yml` | OP gate | `…公共窗口：/ember → 灾厄` | `…公共窗口：枢纽菜单 → 灾厄` |

**口径备忘：** 等级数字与贡献语义 **一字不改**；只删「请打斜杠」教学，改指既有菜单路径。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 四本玩家路径清账（推荐）** | 只改上表 #1～#5（周/团/深渊 level 三句 + 盟 Boss 拒门+开场）；**不**动灾厄 OP 句；零改 condition text | 覆盖全玩家可见脏点；与日常/精英/盟约菜单对齐；专岗清晰 | OP 灾厄句仍挂（见得少） | **推荐** |
| **B. A + 灾厄 OP 拒门** | A 全做 + 上表 #6 | OP 调试句也无斜杠；B0.3 域外更齐 | 触测试实例文案；略厚 | 备选 |

**不施工：** 改人数/等级门表达式；改体力/掉落；改 TrMenu；开精英奖励子菜单；清未入库 bak；宣称 B0.1 已清。

---

## 4. 验收（≤5）

1. **批准前：** 本稿落盘；玩法 YAML / 门控表达式 / 体力 cost / loot **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `EmberWeekly` / `EmberRaid` / `EmberAbyss` / `EmberGuildBoss` 的玩家可见 message **无** `/corerpg`；上表 #1～#5 已换菜单指路口径；`rg '/corerpg' …/option.yml` 在四文件玩家 message 行 → **0**（`#` 注释可留）。  
3. **若批 A：** `$js-condition` / `$team-condition` 的 **text=** 与 min/max **相对批前零 diff**（仅 message 字符串变更）。  
4. **若批 A · 禁项：** 未改体力/票/掉落/TrMenu；**不**宣称 B0.1 / 精英预览壳 / 断塔 B 已动。  
5. **若批 B：** 另满足：`EmberCalamity` OP 拒门无 `/ember`；改为枢纽菜单指路。

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 周/团/深渊拒门文案 | —（DP message） | 拒门 chat | 进本失败瞬间 | 既有枢纽菜单 | 无 | **UI/文案可用** · 无新 NPC |
| 盟 Boss 拒门/开场 | — | 盟约周 Boss | 枢纽 → 盟约 → 周盟 Boss | `ember_guild` 既有 | 无 | 只改 DP 人话，入口仍菜单 |

**站岗自检：** 无新 NPC；仅文案 → 最高标「UI 可用」，不宣称场景完成。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 枢纽菜单进周/团/深渊 | 等级不足拒门 | 人话指路「枢纽 → 角色」；**不**教斜杠 | 回枢纽点角色 |
| 2 | 盟约 → 周盟 Boss | 非队长/无通行拒门 | 人话指路盟约菜单按钮 | 回盟约页 |
| 3 | 盟 Boss 开场 | 已点燃摘要 | 说「盟约菜单门控」；不念斜杠 | 正常开打 |

---

## 7. UX 否决条

- 禁止借机改等级门、人数门、贡献消耗数值。  
- 禁止玩家路径新增其它斜杠教学。  
- 禁止本窗动 TrMenu / quest / 体力 / loot。  
- 禁止未批准改 `option.yml`（除非批 A/B 后另开施工）。  
- 禁止把 OP 调试门改成对普通玩家开放。

---

## 8. 未选表（否因）

| 候选 | 本轮 | 否因 |
|------|------|------|
| B0.1 票扣显示名 | 排除 | 进本主路径已体力+NI；票 lore 已无裸 id；任务禁「无新证据不挑」 |
| 精英奖励预览子菜单壳 | 未选 | backlog 明示一点进本、无 P；要做须另开壳，厚于本窗文案 |
| 断塔掉底厅升 B | 排除 | 软观察 · 无日刷频繁证据 |
| 霜晶/锈轨 Boss 前压 | 排除 | 设计刻意 |
| 挂机 over_chance* / 墙钟 2h | 排除 | 刚结 / 用户否决 |
| TrMenu `ember_weekly.yml_*.bak` 清堆 | 未选 | 未入库；gitignore 已挡；非玩家体验 |
| B2.3/B2.4 / B2.6/B2.7 | 排除 | 已 PASS 勾销 |

---

## 9. 回总控一句话

**无高杠杆硬债；扫入口新脏点：荐批 A——周/团/深渊拒门 + 盟 Boss 拒门/开场去斜杠，对齐日常/精英与盟约菜单，零改门控与数值。**

**专岗：** **策划**（本稿）→ **插件**（批后改 4× option.yml message）→ **测试**（静态 rg + 拒门冒烟）
