# 设计稿 · 主线 quest「日票/周票」台词薄扫

> **未批准前不施工。** 本稿只定 **玩家可见** `quest.yml` 灰烛 done/intro / step hint 文案（票→体力/体力药口径）；**禁**改步骤 `type` / `event` / `count` / `mobs` / `complete_on` / `items` 键与数量、体力 cost、进本、TrMenu、`cash.yml` 数值、DP/MM/loot。  
> 债源：S0 进本已扣体力；战令菜单「日票」假文案 **B2.6 PASS**（`71473da`/`8340ac3`）；**主线灰烛/hint 仍写「日票/周票」** → 与实发 `consumable_ember_stamina_30` 及日回体力打架。  
> 对齐：`design-ember-stamina-copy-align.md` **方案 B 残余** · `design-ember-stamina-dnf-daily.md` · live `cash.yml` `stamina.base_max=90` / `costs.daily=30`（约 3 次日常）· `free_tickets: 0`。  
> 排除本轮：五入口奖励预览；B2.6 菜单日票（刚结）；非日常灰显 / 挂机 over_chance* / 二档菜单 UX；B2.3/B2.4；B1.3 砍血；墙钟 2h；霜晶/锈轨前压；断塔 B；新养成/货币/副本类型；Citizens；改 Paper；**禁改任务步骤 ID/条件**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 主线 `quest.yml` · **「日票/周票」台词薄扫**（UX · 可维护性 · 文案软债） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 22:56 Asia/Shanghai |
| 关联 | live `plugins/CoreRpg/quest.yml` · 镜像 `CoreRpg/src/main/resources/quest.yml` · 对照 `design-ember-stamina-copy-align.md` |
| 状态 | **PASS · 勾销**（批准 `a6c48bc` · 施工 `4eb9af8` · 测 `f2edc25`） |
| 关联 STATUS / 稿 | backlog · B2.6 PASS 残余 · `STATUS-ember-stamina-copy-align*.md` |

### 硬约束（本稿）

| 可动（**仅若批 A/B 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `plugins/CoreRpg/quest.yml` 玩家可见 **字符串**（done / intro / hint） | 任一步骤的 `type` / `event` / `count` / `mobs` / `complete_on` / `level` / `desc` 语义结构 |
| 方案 B：同步 `CoreRpg/src/main/resources/quest.yml` 同 diff | `items:` 奖励键与数量（已发 `consumable_ember_stamina_30` 等） |
| 热更 / `corerpg reload`（或服约定的 quest 重载） | 体力 cost / 进本 / DP / MM / loot / TrMenu / `cash.yml` 数值 |
| | 新养成/货币；Citizens；git push |

---

## 1. 问题一句话

**菜单已对齐「体力/体力药」；主线灰烛仍承诺「日票拿去 / 每日 3 张免费日票 / 需日票 / 带上周票」——与 `free_tickets: 0`、实发体力药、进本扣体力不一致，属有证据的台词一致性薄修。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 进本 | 日常 cost **30**；`base_max` **90** → 日回满约可刷 **3** 次日常（叙事「每天开三次」仍可保留，**勿**再称票） |
| 发票 | `cash.yml` `daily.free_tickets: **0**`（停发日票） |
| 主线实发 | 卷1 签到复命 `items: consumable_ember_stamina_30: 1`（台词却说「日票拿去」） |
| 菜单 | B2.6 已清 `ember_pass` / `ember_character` / `ember_auction`；商城已写体力药 |
| 命中路径 | live `plugins/CoreRpg/quest.yml` 与 src 镜像 **各 6 行**（全库串 `日票\|周票`） |

### 命中清单（live · 2026-09-28）

| # | 行 | 键 | 旧（玩家可见） | 建议新（体力/体力药口径） |
|---|----|----|----------------|---------------------------|
| 1 | 68 | `1` 签到复命 `done` | `手还稳。日票拿去，地窟今天的门还开着。` | `手还稳。体力药拿去，地窟今天的门还开着。` |
| 2 | 73 | `2` intro | `地窟的门每天开三次（每日 3 张免费日票，主线再送 1 张）。先下去看看。` | `地窟的门每天大约开三次（日回体力约可刷 3 次日常，主线再送 1 瓶体力药）。先下去看看。` |
| 3 | 81 | `2` kill hint | `打开枢纽菜单 → 日常本（需日票）· 本次通关也算完成` | `打开枢纽菜单 → 日常本（耗体力）· 本次通关也算完成` |
| 4 | 140 | `3` talk `done` | `带上周票，最好叫上同伴。` | `留够体力，最好叫上同伴。` |
| 5 | 243 | `5` daily_clear hint | `每日 3 张免费日票` | `日回体力约可刷 3 次日常` |
| 6 | 355 | `8` daily_clear hint | `打开枢纽菜单 → 日常本（每日免费日票）` | `打开枢纽菜单 → 日常本（日回体力）` |

**口径备忘（展示层 · 不改数）：**

| 旧说法 | 对齐 |
|--------|------|
| 日票拿去 / 再送 1 张 | **体力药**（对齐 `consumable_ember_stamina_30`） |
| 每日 3 张免费日票 | **日回体力约可刷 3 次日常**（90÷30；**不**承诺「免费票」） |
| 需日票 / 每日免费日票 | **耗体力** / **日回体力** |
| 带上周票 | **留够体力**（周本 cost 45；**不**改周本门） |

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. live quest 六句清账（推荐）** | 只改 `plugins/CoreRpg/quest.yml` 上表 6 处玩家字符串；**零**动步骤结构 / items 键 | 闭环 B2.6 残余；零玩法数；专岗清晰 | src 镜像暂不同步 → 日后打 jar 可能盖回（记债或批后跟 B） | **推荐** |
| **B. A + src 镜像同 diff** | A 全做 + `CoreRpg/src/main/resources/quest.yml` 同 6 处 | 防重打包覆盖；双路径一致 | 触源码资源树；略厚（仍零玩法） | 备选 |

**不施工：** 改 `items` 数量；改 `cash.yml` / 体力 cost；改 TrMenu；教斜杠；扩扫非「日票/周票」句；B0.1 票扣显示名债。

---

## 4. 验收（≤5）

1. **批准前：** 本稿落盘；玩法 YAML / 步骤结构 / items 键 / 体力 cost **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** live `quest.yml` 玩家可见句 **无**「日票」「周票」字样；上表 6 处已换体力/体力药口径；`rg '日票\|周票' plugins/CoreRpg/quest.yml` → **0**。  
3. **若批 A：** 步骤 `type`/`event`/`count`/`items` 键与数量 **相对批前零 diff**（仅字符串行变更）。  
4. **若批 A · 禁项：** 玩家路径 **无** 新增斜杠教学；**不**宣称菜单外其它票债（B0.1）已清。  
5. **若批 B：** 另满足：`CoreRpg/src/main/resources/quest.yml` 与 live 同 6 处一致；`rg` 两路径均 0。

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 主线台词对齐 | 灰烛（既有） | 灰烛 | 枢纽出生点北 · 工坊旁 | 既有 talk / 任务推进 | 无 | **UI/文案可用** · 无新 NPC |
| 日常 hint | — | 任务栏 hint | 主线卷内 | 枢纽 → 日常本（既有） | 无 | 只改 hint 字 |

**站岗自检：** 无新 NPC；仅文案 → 最高标「UI 可用」，不宣称场景完成。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 枢纽 · 灰烛 | 签到复命 done | 「体力药拿去」与实发一致 | 不改步骤 |
| 2 | 残窟之门 intro / hint | 「日回体力 / 耗体力」 | 不承诺免费日票 | 进本仍走菜单 |
| 3 | 周烬 talk done | 「留够体力」 | 不提周票 | 周本菜单既有 |
| 4 | 卷中 daily_clear hint | 「日回体力…」 | 与 cost 叙事一致 | 不改 count=2 |

---

## 7. UX 否决条

- 禁止借机改步骤条件、奖励键、体力数值「顺便调表」。  
- 禁止把「每天开三次」改成承诺无限次或改 cost。  
- 禁止本窗动 TrMenu / 商城 / 战令（B2.6 已结）。  
- 禁止未批准改 `quest.yml`（除非批 A/B 后另开施工）。  
- 禁止玩家路径新增斜杠教学。

---

## 8. 专岗

| 岗 | 职责 |
|----|------|
| **策划** | 本稿替换表；批 A/B |
| **插件** | 按表改 quest 字符串（± src 若批 B）；reload |
| **测试** | `rg` 清零；抽卷1 签到复命 / 残窟 intro+hint / 周烬 talk / 两处 daily_clear hint；确认步骤仍可推进 |

---

## 9. 未选说明（给总控）

| 候选 | 未选原因 |
|------|----------|
| B2.6 菜单日票 | **刚结 PASS** |
| 五入口奖励预览 / 精英 P 壳 | 刚结或本轮禁止默认 |
| 非日常灰显 / 挂机 UX / B2.3/B2.4 / B1.3 / 霜锈前压 / 断塔 B | 任务排除 |
| B0.1 票扣显示名 | 玩法逻辑债，非文案薄窗；另挂 |
| 扩扫 quest 其它旧口吻 | 无「日票」证据不吞；本窗只清 6 命中 |

---

## 10. 回总控一句话

**荐 A：** 主线 `quest.yml` 六处「日票/周票」→ 体力药/体力口径；不改步骤与奖励键；专岗插件+测试。未批不施工。


---

## 批准记录（总控）

| 字段 | 值 |
|------|-----|
| 批准 | **B** · 2026-09-28 22:59 Asia/Shanghai |
| 岗 | 余烬-总控 |
| 理由 | 同薄窗 + 防 jar 重打包盖回 live |
| 范围 | live `plugins/CoreRpg/quest.yml` **与** `CoreRpg/src/main/resources/quest.yml` 同 6 玩家可见字符串 |
| 禁 | 步骤 type/event/count/items；体力 cost；进本；TrMenu；cash 数值；DP/MM/loot；git push（插件） |
| 下一 | 派 **插件** 双路径施工 + reload；测岗待插件 tip 后再派；STATUS `STATUS-ember-quest-stamina-copy-approve.md` |
