# B1.4 · 主线第一卷 hint / join 文案扫尾标句

- 日期：2026-09-28（Asia/Shanghai）
- 设计：余烬-策划 · **仅文档，未改任何现网 YAML**
- 依据：`docs/design-ember-content-backlog.md` §B1.4；体例对齐 `docs/design-ember-b03-menu-no-cmd.md`
- 关联：旧设计片段 `STATUS-ember-b1-quest-copy.md`；插件落地记 `STATUS-ember-b14-quest-copy.md`（2026-09-27 已替硬指令句）
- 范围：`plugins/CoreRpg/quest.yml`（ch1～10 玩家字段）+ `plugins/CoreRpg/config.yml` → `join_message`（及第一卷相关 hint）
- **不改：** 步骤 type/count、数值、掉落、票、底层 event/command；不新开系统；不改 TrMenu（B0.3 已结）

**替换原则（若仍有硬句）：** 玩家句只留「打开枢纽菜单 → …」/「右键灰烛（或工坊 NPC）」；不写 `/corerpg` `/dp` `/hub` `/ember` 教学；底层 `command:` / 管理注释可保留。

---

## 1. 问题一句话

主线第一卷 / 进服提示曾教聊天斜杠指令；本轮复扫确认 **硬指令教学已清净**，仅余若干「斜杠作并列符」的软口吻可选用菜单路径改写。

---

## 2. 扫描范围与方法

| 项 | 内容 |
|----|------|
| 文件 | `plugins/CoreRpg/quest.yml`、`plugins/CoreRpg/config.yml` |
| 玩家字段 | `join_message`；quest 的 `intro` / `hint` / `done` / `desc`（desc 作 HUD 短目标亦视玩家可见） |
| 关键词 | `/corerpg`、`/dp`、`/hub`、`/ember`、请执行、请输入、打指令、命令、tell、建议执行、请打、指令： |
| 区分 | **玩家可见** hint/lore/message vs **内部** `command_template` / `spawn_command` / `ni give` / YAML `#` 注释 |
| 对照 | B0.3 菜单去指令已 PASS → 本债 **不**重复扫 TrMenu；聚焦 quest + join |
| 灰烛 | quest 内找灰烛 / 右键交谈句；总控曾提的「/ember」教法本轮再验 |

**硬命中判定（验收同 backlog）：** 玩家可见句含「请打 /corerpg|/dp|/hub」类，或等价斜杠命令教学（含 `/ember →`、`或 /corerpg …`、`指令：…`）。

---

## 3. 扫查结果摘要

| 类别 | 条数 | 说明 |
|------|------|------|
| **硬命中（须改）** | **0** | `join_message` 4 行 + quest ch1～10 的 hint/done/intro：**无** `/corerpg` `/dp` `/hub` `/ember` 教学 |
| 软残留（可选抛光） | **8** | 等级 hint 用 `A / B / C` 作并列；卷一 4 条缺「打开枢纽菜单」前缀；卷二 4 条已有前缀 |
| 已清（历史，勿重复派工） | 约 20 | 见 `STATUS-ember-b1-quest-copy.md` §3；插件记 `STATUS-ember-b14-quest-copy.md` |
| 内部/管理保留 | 3+ | 见 §5 |

`quest.yml` 文件头 `# … docs/ember-mainline-spec.md` 含路径片段 `/ember-…`，**非**玩家字段。

---

## 4. 对照表

### 4.1 硬指令句（现网）

| 路径/键 | 现网原文 | 问题 | 建议替换 | 玩家可见？ | 专岗 |
|---------|----------|------|----------|------------|------|
| — | （无） | 硬命中 0 | **无需替换** | — | — |

**现网合规样例（保持）：**

| 路径/键 | 现网原文（摘要） | 判定 |
|---------|------------------|------|
| `config.yml` `join_message[0]` | `…灰烛就在出生点北边——右键他看当前目标。` | PASS |
| `config.yml` `join_message[2]` | `打开枢纽菜单：主页 · 强化 · 镶嵌 · 签到 · 生活补给。工坊 NPC 也可右键进入。` | PASS |
| `config.yml` `join_message[3]` | `进本门槛：…——打开枢纽菜单进入。` | PASS |
| `quest.yml` ch1 talk hint | `就在出生点北边几步、工坊旁，右键他` | PASS（灰烛右键，无 /ember） |
| `quest.yml` ch2 covenant done | `…打开枢纽菜单 → 技能 看看。` | PASS（原「试试 /corerpg skill」已替） |
| `quest.yml` ch7～10 多处 | `打开枢纽菜单 → …` / `枢纽工坊旁找灰烛（右键交谈）` | PASS（原「打开 /ember」已替） |

### 4.2 软残留（可选 · 非硬验收 FAIL）

斜杠此处为**中文并列符**，不是命令字；但与「玩家路径零斜杠」观感不完全一致，且卷一等级 hint 未点明菜单入口。总控若批「顺手抛光」再派插件。

| # | 路径/键 | 现网原文 | 问题 | 建议替换 | 玩家可见？ | 专岗 |
|---|---------|----------|------|----------|------------|------|
| S1 | `quest.yml` ch2 Lv20 `hint`（约 L125） | `签到 / 日常 / 悬赏 / 挂机（灰坡）都能补经验` | 裸并列斜杠；未写菜单 | `打开枢纽菜单 → 签到 · 日常本 · 悬赏 · 挂机庭①灰坡，都能补经验` | 是 | 插件（可选） |
| S2 | `quest.yml` ch3 Lv25 `hint`（约 L166） | `日常 / 悬赏 / 签到 / 挂机 ② 荒原` | 同上 | `打开枢纽菜单 → 日常本 · 悬赏 · 签到 · 挂机庭②荒原` | 是 | 插件（可选） |
| S3 | `quest.yml` ch4 Lv30 `hint`（约 L207） | `深渊 / 日常 / 悬赏 / 挂机 ② 荒原` | 同上 | `打开枢纽菜单 → 深渊 · 日常本 · 悬赏 · 挂机庭②荒原` | 是 | 插件（可选） |
| S4 | `quest.yml` ch5 Lv35 `hint`（约 L249） | `深渊 / 日常 / 悬赏 / 灾厄 / 挂机 ③ 焦土` | 同上 | `打开枢纽菜单 → 深渊 · 日常本 · 悬赏 · 灾厄 · 挂机庭③焦土` | 是 | 插件（可选） |
| S5 | `quest.yml` ch7 Lv40 `hint`（约 L318） | `打开枢纽菜单 → 日常本 / 深渊 / 挂机庭 / 悬赏` | 菜单前缀已有；斜杠并列 | `打开枢纽菜单 → 日常本 · 深渊 · 挂机庭 · 悬赏` | 是 | 插件（可选） |
| S6 | `quest.yml` ch8 Lv45 `hint`（约 L361） | `打开枢纽菜单 → 深渊 / 日常本 / 灾厄 / 挂机庭` | 同上 | `打开枢纽菜单 → 深渊 · 日常本 · 灾厄 · 挂机庭` | 是 | 插件（可选） |
| S7 | `quest.yml` ch9 Lv50 `hint`（约 L395） | `打开枢纽菜单 → 精英试炼 / 深渊 / 日常本` | 同上 | `打开枢纽菜单 → 精英试炼 · 深渊 · 日常本` | 是 | 插件（可选） |
| S8 | `quest.yml` ch10 Lv60 `hint`（约 L436） | `打开枢纽菜单 → 日常本 / 深渊 / 精英试炼 / 团本 / 灾厄` | 同上 | `打开枢纽菜单 → 日常本 · 深渊 · 精英试炼 · 团本 · 灾厄` | 是 | 插件（可选） |

**最刺的 2～3 例（相对）：** S1～S3（卷一等级 hint 无菜单前缀 + 裸 `/` 并列）。相对硬验收仍属软债。

### 4.3 非问题（desc 内「烬刃 / 灰印」等）

`desc: 选择一个誓约（烬刃 / 灰印 / 守墓）` 等为选项并列，**不**标改。

---

## 5. 管理 / 测试 / 内部句清单（标清保留）

| 路径/键 | 原文摘要 | 处理 |
|---------|----------|------|
| `config.yml` `spawn.command_template` | `mm m spawn {mob} …` | **保留**（内部刷怪，非玩家可见） |
| `config.yml` `calamity.spawn_command` | `mm m spawn EmberCalamityBoss …` | **保留**（灾厄刷怪） |
| `config.yml` `calamity.kill_rewards` | `ni give {player} …` | **保留**（奖励接线） |
| `quest.yml` 文件头 `#` 注释 | step type / event 名 / 文档路径 | **保留**（运维备忘） |
| 管理指令（若运维文档另列） | `/corerpg quest …` 等 | 仅文档/OP；**不得**写回 join / hint / done |

`config.yml` 灾厄 `warning_message` / `spawn_message`：剧情广播，无斜杠命令 → **不动**。

---

## 6. 验收硬条

1. `config.yml` `join_message` 玩家可见文本 **无**「请打 /corerpg|/dp|/hub」类，亦无 `/ember` 指令清单教学。
2. `quest.yml` 主线（含第一卷及已菜单化第二卷）`hint` / `done` / `intro` **无** 同上斜杠命令教学。
3. 指引口径：菜单路径或右键灰烛 / 工坊 NPC；与 B0.3 / UX 一致。
4. 管理/测试命令仅出现在内部键或运维文档，不进玩家字段。
5. **不**改数值、掉落、步骤结构、票规则。

**本轮自评：** 硬条 1～4 对现网 YAML → **已 PASS**（与 `STATUS-ember-b14-quest-copy.md` 一致）。软表 §4.2 非硬条。

---

## 7. 不动清单

| 项 | 说明 |
|----|------|
| 步骤 `type` / `event` / `count` / `mobs` / `xp` / `items` / `levels` | 禁止借文案改进度逻辑 |
| 体力 / 票 / 掉落 / 刷怪数值 | 禁止 |
| TrMenu `ember_*.yml` | B0.3 已结；本单不扫 |
| 底层 `command:` / `ni give` / MM spawn | 可保留 |
| 新系统 / 新 NPC / Citizens | 禁止 |
| 现网 YAML（未批前） | **本岗不改**；批后由插件按表施工 |

---

## 8. 交卷

| 项 | 值 |
|----|----|
| 硬命中条数 | **0** |
| 软残留条数 | **8**（可选抛光） |
| 历史已清（参照旧 STATUS） | 约 join 3～4 行 + 卷一 hint/done ~16 + 卷二 /ember ~13 |
| 最刺 2～3 例 | S1 `签到 / 日常 / …`；S2 `日常 / 悬赏 / …`；S3 `深渊 / 日常 / …`（皆软） |
| 是否建议批准后插件改 YAML | **硬验收：否（已 PASS，不必再派硬改）**；若总控要「零斜杠并列」观感 → **可批可选软抛光 8 条**，专岗插件，不动数值 |
| 产出路径 | `docs/design-ember-b14-quest-join-copy.md` |
| 下步 | 硬关单；插件只做 §4.2；测岗抽测等级 hint |

---

## 9. 一页摘要（总控）

B1.4 扫尾标句完成：`quest.yml` + `join_message` 硬指令教学 **0 命中**（灰烛无 `/ember` 教法；先前落地有效）。可选 8 条等级 hint 斜杠并列软抛光。未改 YAML。总控批示：**硬债关单 PASS**；**软抛光 8 条本额度批准施工**。

---

## 10. 总控批示（2026-09-28 Asia/Shanghai）

| 项 | 决定 |
|----|------|
| 硬验收 B1.4 | **关单 · PASS**（硬命中 0；不必再派硬改） |
| 软抛光 §4.2 | **批准本额度施工**（8 条等级 hint：斜杠并列 → 间隔号 + 卷一补菜单前缀） |
| 施工范围 | 仅 `plugins/CoreRpg/quest.yml` 上表 S1～S8 的 `hint` 字符串 |
| 禁触 | join_message（已 PASS）；步骤 type/count/数值/掉落；TrMenu；config 内部 command |
| 下一岗 | **余烬-插件** 按 §4.2 替换 → 热重载（若需）→ 交测短抽 2～3 条 hint；总控推 |

