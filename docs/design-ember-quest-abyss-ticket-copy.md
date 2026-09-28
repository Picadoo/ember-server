# 设计稿 · 主线 quest「深渊票」台词薄扫（B2.11）

> **未批准前不施工。** 本稿只定 **玩家可见** `quest.yml` 灰烛 done / step hint 两句（假「深渊票/票」→体力口径）；**禁**改步骤 `type` / `event` / `count` / `mobs` / `complete_on` / `items` 键与数量、体力 cost、进本、TrMenu、`cash.yml` 数值、DP/MM/loot。  
> 债源：硬债已空；B2.7 只 rg「日票|周票」漏网；B2.10 方案 A 已清 DP 局内假票，**方案 B 残余 Q1/Q2** 已由本窗 B2.11 方案 A 清账（close `ddaeb46`）。
> 对齐：`design-ember-dp-ticket-stamina-copy.md`（B2.10-B Q1/Q2）· `design-ember-quest-stamina-copy.md`（B2.7）· `design-ember-stamina-dnf-daily.md` · UX「文案短清楚」。  
> 排除本轮：刚结 B2.6–B2.10；五入口奖励预览；非日常体力灰显；挂机 over_chance*/二档 UX；B1.3；墙钟 2h；霜锈前压；断塔升 B；B0.1 除非新证据；精英预览壳；isomorphic 空壳 tell→真页；新进度/货币/副本类型；Citizens；Paper。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 主线 `quest.yml` · **「深渊票」两句薄扫**（UX · 文案一致性 · B2.11） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 23:46 Asia/Shanghai |
| 关联 | live `plugins/CoreRpg/quest.yml` · 镜像 `CoreRpg/src/main/resources/quest.yml` · 债源 B2.10 close `ddaeb46` |
| 状态 | **PASS · 已关闭**（施工 `8605d10` · 验收 `a538d26`） |
| 关联 STATUS / 稿 | `docs/STATUS-ember-quest-abyss-ticket-copy.md` · `docs/STATUS-ember-quest-abyss-ticket-copy-test.md` · backlog close |

### 硬约束（本稿）

| 可动（**仅若批 A/B 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| live + src `quest.yml` 上表 Q1/Q2 **玩家可见字符串** | 任一步骤的 `type` / `event` / `count` / `mobs` / `complete_on` / `floor` / `desc` 语义结构 |
| 方案 B：灾厄 OP 拒门去 `/ember`（`EmberCalamity/option.yml` message） | `items:` 奖励键与数量；体力 `costs.*` / `free_tickets` |
| 热更 / `corerpg reload`（或服约定的 quest 重载） | TrMenu / DP 其它本 / MM / loot / NI 票显示名 |
| | 新养成/货币；Citizens；git push |

---

## 1. 问题一句话

**B2.7/B2.10 已清「日票|周票」与 DP 局内假票；主线卷4 hint 仍写「需深渊票」、卷8 灰烛仍说「票还是一天一张」——与 S0 进本扣体力、深渊菜单「体力不退」、同本开场「已消耗体力」打架，属有证据的台词薄修。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| S0 | `cash.yml` `stamina.costs.abyss=30`；`free_tickets: 0`；进本扣体力 |
| B2.7 | live+src 已无「日票\|周票」；rg 串当时未覆盖「深渊票」「票还是一天一张」 |
| B2.10 A | DP 九句假票→体力 **PASS**（`9311a72`/`95c9fbc`）；Q1/Q2 刻意未做 |
| 菜单 | TrMenu 深渊 tell「体力不退」；开场「已消耗体力」 |
| Q1 脏 | live+src L180 hint：`打开枢纽菜单 → 深渊（需深渊票）· 本次结算也算完成` |
| Q2 脏 | live+src L335 done：`§6灰烛：§f票还是一天一张。能走多深，就走多深。` |
| 灾厄 OP（B） | `EmberCalamity/option.yml` L17：`…公共窗口：/ember → 灾厄`（仅 OP 调试门；B2.8/B2.10 批 A 刻意未做） |

### 替换表（展示层 · 不改步骤/扣费）

| # | 文件 | 约位 | 旧（玩家可见） | 新 |
|---|------|------|----------------|----|
| Q1 | `plugins/CoreRpg/quest.yml` + `CoreRpg/src/main/resources/quest.yml` L180 | 卷4 kill hint | `打开枢纽菜单 → 深渊（需深渊票）· 本次结算也算完成` | `打开枢纽菜单 → 深渊（耗体力）· 本次结算也算完成` |
| Q2 | 同上 L335 | 卷8 talk done | `§6灰烛：§f票还是一天一张。能走多深，就走多深。` | `§6灰烛：§f体力日回，能走多深就走多深。` |
| C6（B） | `EmberCalamity/option.yml` OP gate | message | `…公共窗口：/ember → 灾厄` | `…公共窗口：枢纽菜单 → 灾厄` |

**口径备忘：** Q1「耗体力」对齐 B2.7 日常 hint「耗体力」与深渊开场；Q2「体力日回」对齐日回体力叙事，**不**承诺「一天一张票」或改 `costs.abyss`。live 与 src **同 diff**（防打 jar 盖回；B2.7 实批双路径先例）。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. quest 双路径两句清账（推荐）** | 只改上表 Q1/Q2 · live+src；**不**动灾厄 OP；零改步骤结构 / items / cost | 闭环 B2.10-B 主残余；玩家面主线全覆盖；与体力口径直接对齐；专岗清晰 | OP #6 仍挂 | **推荐** |
| **B. A + 灾厄 OP #6** | A 全做 + C6 | B0.3/B2.8 域外斜杠更齐 | OP 调试门见得少；略厚（跨 quest+DP） | 备选 |

**不施工：** 改步骤 ID/条件/items；改体力 cost；改 TrMenu；改 DP 其它本；教斜杠；重开 B0.1；扩扫非本两句。

---

## 4. 验收（≤5）

1. **批准前：** 本稿落盘；玩法 YAML / 步骤结构 / items / cost / DP **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** live+src `quest.yml` **无**「深渊票」「票还是一天一张」；上表 Q1/Q2 已换；`rg '深渊票|票还是一天一张' plugins/CoreRpg/quest.yml CoreRpg/src/main/resources/quest.yml` → **0**。  
3. **若批 A：** 步骤 `type`/`event`/`count`/`items` 键与数量 **相对批前零 diff**（仅字符串行变更）。  
4. **若批 A · 禁项：** 玩家路径 **无** 新增斜杠教学；**不**宣称 B0.1 / 灾厄 OP #6 / 其它票债已清。  
5. **若批 B：** 另满足：`EmberCalamity` OP 拒门无 `/ember`；改为枢纽菜单指路；门控 `text=` 表达式零 diff。

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 主线深渊 hint / 灰烛台词 | 灰烛（既有） | 灰烛 | 枢纽出生点北 · 工坊旁 | 既有 talk / 任务推进 | 无 | **UI/文案可用** · 无新 NPC |
| （若批 B）灾厄 OP 拒门 | —（DP message） | OP 调试拒门 | 误开灾厄 DP 实例 | 枢纽 → 灾厄（既有） | 无 | 仅 OP 见；正式玩家走菜单 |

**站岗自检：** 无新 NPC；仅文案 → 最高标「UI 可用」，不宣称场景完成。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 主线卷4 | hint「深渊（耗体力）」 | 指路枢纽菜单 → 深渊 | 体力不足既有灰显/拒门 |
| 2 | 主线卷8 · 灰烛 | 「体力日回，能走多深就走多深」 | 与日回体力叙事一致 | 回枢纽点深渊 |
| 3 | （若批 B）OP 误开灾厄 DP | 「枢纽菜单 → 灾厄」 | **不**教 `/ember` | 回枢纽菜单 |

---

## 7. UX 否决条

- 禁止借机改 `stamina.costs` / 掉落 / 步骤条件。  
- 禁止玩家路径新增斜杠教学。  
- 禁止只改 live 不改 src（本窗 A 即双路径；防 jar 盖回）。  
- 禁止未批准改 quest/DP YAML（除非批 A/B 后另开施工）。  
- 禁止把 OP 调试门改成对普通玩家开放。

---

## 8. 未选表（否因）

| 候选 | 本轮 | 否因 |
|------|------|------|
| B2.6–B2.10 | 排除 | 刚结 PASS；本窗是 B2.10-B **残余**，非重开整窗 |
| 五入口奖励预览 / isomorphic 空壳→真页 | 排除 | 已结或禁再开 |
| 非日常体力灰显 / 挂机 over_chance*·二档 UX | 排除 | 刚结 |
| B1.3 / 墙钟 2h / 霜锈前压 / 断塔升 B | 排除 | 关账或刻意或无证据 |
| B0.1 票扣显示名 | 排除 | 无新进本失败证据 |
| 精英奖励预览子菜单壳 | 排除 | 一点进本 · 须另开 |
| **灾厄 OP #6 单开为本窗 A** | 未作 A | OP 调试门见得少；玩家面窄于主线两句；作方案 B |
| 寄售 lore「票券」/ NI「余烬日票」改名 | 未选 | 迁移期旧票可能仍在；易误读为重开 B0.1；收益低于主线假票 |
| 新进度/货币/副本/Citizens/Paper | 排除 | 任务禁 |

---

## 9. 回总控一句话

**硬债空；B2.11 方案 A PASS · 已关闭——主线 quest「深渊票/票还是一天一张」已在 live+src 双路径改为体力口径；灾厄 OP #6 保留为剩余薄候选。**

**专岗：** **策划**（设计）→ **插件**（`8605d10` 落地）→ **测试**（`a538d26` PASS）→ **已关闭**


---

## 批准记录（总控）

| 字段 | 值 |
|------|-----|
| 批准 | **A** · 2026-09-28 23:48 Asia/Shanghai |
| 岗 | 余烬-总控 |
| 依据 | parent design tip `4a2a671`，已在 `main` / `origin/main` 核验 |
| 范围 | 仅 live + src `quest.yml` 的 Q1/Q2 两个玩家可见字符串；完整替换见 `docs/STATUS-ember-quest-abyss-ticket-copy-approve.md` |
| 禁 | 灾厄 OP #6；步骤 `type` / `event` / `count` / `mobs` / `complete_on` / `floor`；`items` 键与数量；体力 `costs.*` / `free_tickets`；进本、TrMenu、DP/MM/loot；任何非文档改动 |
| 下一 | **已关闭**（施工 `8605d10` · 测试 `a538d26`） |
| 状态 | **PASS · 已关闭**（施工 `8605d10` · 验收 `a538d26`） |
