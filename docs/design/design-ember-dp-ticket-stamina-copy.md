# 设计稿 · DP 局内/超时「票」文案对齐体力（B2.10）

> **未批准前不施工。** 本稿只定 DungeonPlus **玩家可见** `$message{…text=…}`（开场 / 结算 / 超时）把假「票」口径改成 **体力**；**禁**改门控表达式、人数/等级门、体力 cost、loot、MM、TrMenu、`cash.yml`、NI 票物品定义、B0.1 扣次逻辑。  
> 债源：硬债已空；S0 进本已扣体力；开场多本已写「已消耗体力」；**超时/撤离/结算/团本开场仍念日票·周票·团本票·深渊票·票不退**——与同本开场及 TrMenu「体力不退」打架。  
> 对齐：`docs/design/design-ember-stamina-dnf-daily.md` · `docs/design/design-ember-quest-stamina-copy.md`（B2.7 · 仅扫「日票|周票」）· `docs/design/design-ember-dp-gate-nocmd.md`（B2.8）· UX「文案短清楚」。  
> 排除本轮：刚结 B2.6–B2.9；五入口奖励预览；非日常体力灰显；挂机 over_chance*/二档 UX；B1.3；墙钟 2h；霜锈前压；断塔升 B；B0.1 除非新证据；精英预览壳；isomorphic 空壳 tell→真页；新进度/货币/副本类型；Citizens；Paper。灾厄 OP #6 默认不绑（可并 B）。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | DP 局内 / 超时 · **「票」→体力**（UX · 文案一致性 · B2.10） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 23:34 Asia/Shanghai |
| 关联 | `EmberDaily` / `EmberWeekly` / `EmberAbyss` / `EmberRaid` / `EmberEliteWeekly` 的 `option.yml` + `task/timeout.yml` |
| 状态 | **PASS · 已关闭**（方案 A；施工 `9311a72` · 验收 `95c9fbc`） |
| 关联 STATUS / 稿 | `docs/status/STATUS-ember-dp-ticket-stamina-copy.md` · `docs/status/STATUS-ember-dp-ticket-stamina-copy-test.md` · backlog close |

### 硬约束（本稿）

| 可动（**仅若批 A/B 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| 上列 live DP 文件中 **玩家可见** `$message` / timeout `text=` 字符串 | `$js-condition` / `$team-condition` 的 **text=** 表达式与人数/等级 |
| 可选 B：quest live+src 「深渊票」/「票还是一天一张」两句；及/或灾厄 OP #6 | 体力 `costs.*` / `free_tickets` / loot / MM / TrMenu / NI 票显示名 |
| 热更约定的 DP reload（服侧） | 新养成/货币；Citizens；git push |

---

## 1. 问题一句话

**同本开场已喊「已消耗体力」，超时/撤离/结算仍喊「日票/周票/票不退」；团本开场仍「已扣除余烬团本票」——S0 迁后有证据的局内文案假票债，非改数。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| S0 | `cash.yml` `stamina.costs` 日 30 / 周 45 / 深 30 / 团 50 / 精 40；各线 `free_tickets: 0` |
| 开场已洁 | 日常七线 / 周本 / 深渊开场均写 **已消耗体力**（庭院等） |
| 菜单已洁 | TrMenu 深渊 tell「体力不退」；B2.6/B2.7 菜单+quest「日票\|周票」已清 |
| 庭院超时脏 | `EmberDaily/task/timeout.yml`：`日票不返还`（其它日常线无独立 timeout 文件） |
| 周本超时脏 | `EmberWeekly/task/timeout.yml`：`周票不返还`（开场已是体力） |
| 深渊脏 | 开场体力；撤离/结算 `票不退`；timeout `深渊票不返还` |
| 团本脏 | 开场 `已扣除余烬团本票 ×1`；通关 `团票每周 1`；timeout `团本票不返还` |
| 精英超时脏 | `票已扣，下周再来`（开场/拒门已人话、耗体力） |
| quest 残留（B） | live+src `quest.yml` L180 `需深渊票`；L335 `票还是一天一张`（B2.7 只 rg「日票\|周票」漏网） |
| 灾厄 OP（可选） | `EmberCalamity` L17 仍 `/ember → 灾厄`（B2.8/B2.9 批 A 刻意未做） |

### 替换表（展示层 · 不改扣费逻辑）

| # | 文件 | 旧（玩家可见） | 新 |
|---|------|----------------|----|
| 1 | `EmberDaily/task/timeout.yml` | `…本局失败（日票不返还）` | `…本局失败（体力不返还）` |
| 2 | `EmberWeekly/task/timeout.yml` | `…周常超时失败（周票不返还）` | `…周常超时失败（体力不返还）` |
| 3 | `EmberAbyss/option.yml` 撤离句 | `…按最高层结算 · 票不退` | `…按最高层结算 · 体力不退` |
| 4 | `EmberAbyss/option.yml` 结算句 | `…发箱（票不退）` | `…发箱（体力不退）` |
| 5 | `EmberAbyss/task/timeout.yml` | `…（深渊票不返还）` | `…（体力不返还）` |
| 6 | `EmberRaid/option.yml` 开场 | `已扣除余烬团本票 ×1` | `已消耗体力 ×1` |
| 7 | `EmberRaid/option.yml` 通关 | `同周再通不重复发戒（团票每周 1）· T3 不保底` | `同周再通不重复发戒（周首通一次）· T3 不保底` |
| 8 | `EmberRaid/task/timeout.yml` | `…（团本票不返还）` | `…（体力不返还）` |
| 9 | `EmberEliteWeekly/task/timeout.yml` | `票已扣，下周再来。` | `体力已扣，下周再来。` |

**口径备忘：** 「×1」= 一次进本结算（与周本/深渊开场同构），**非**宣称扣 1 点体力；数值仍以 `stamina.costs` 为准。`#` 注释里的历史「票」字 **可留**（非玩家可见）。

#### 方案 B 追加

| # | 文件 | 旧 | 新 |
|---|------|----|----|
| Q1 | `plugins/CoreRpg/quest.yml` + `CoreRpg/src/main/resources/quest.yml` L180 | `打开枢纽菜单 → 深渊（需深渊票）· …` | `打开枢纽菜单 → 深渊（耗体力）· …` |
| Q2 | 同上 L335 | `票还是一天一张。能走多深，就走多深。` | `体力日回，能走多深就走多深。` |
| C6 | `EmberCalamity/option.yml` OP gate | `…公共窗口：/ember → 灾厄` | `…公共窗口：枢纽菜单 → 灾厄` |

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. DP 九句清账（推荐）** | 只改上表 #1～#9；**不**动 quest / 灾厄 OP；零改 condition text / cost | 闭环同本「开场体力 vs 超时假票」；专岗清晰；热更 DP | quest「深渊票」两句仍挂；OP #6 仍挂 | **推荐** |
| **B. A + quest 票残留（双路径）± OP #6** | A 全做 + Q1/Q2（live+src）；可选再绑 C6 | S0 玩家可见「票」叙事更齐 | 跨 DP+quest；略厚；OP 见得少 | 备选 |

**不施工：** 改体力 cost / 门控；改 NI「余烬日票」物品显示名（易误读为重开 B0.1）；改 TrMenu（已洁）；开精英奖励子菜单；宣称通胀封死 / 断塔 B。

---

## 4. 验收（≤5）

1. **批准前：** 本稿落盘；玩法 YAML / 门控 / cost / loot **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** 上表 #1～#9 已换体力口径；`rg '日票|周票|团票|深渊票|团本票|票不退|票已扣|余烬团本票' plugins/DungeonPlus/dungeon/Ember{Daily,Weekly,Abyss,Raid,EliteWeekly}/**/*.{yml}` 在 **玩家 message 行** → **0**（`#` 注释可留）。  
3. **若批 A：** `$js-condition` / `$team-condition` 的 **text=** 与 min/max / `stamina.costs` **相对批前零 diff**。  
4. **若批 A · 禁项：** 未改 TrMenu / NI 票显示名 / over_chance*；**不**宣称 B0.1 / 精英预览壳 / quest「深渊票」已清（除非批 B）。  
5. **若批 B：** 另满足 Q1/Q2 live+src 无「深渊票」/「票还是一天一张」；若绑 C6 则灾厄 OP 拒门无 `/ember`。

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 日常/周/深/团/精 局内 chat | —（DP message） | 开场/超时/结算 | 进本后 / 失败瞬间 | 既有枢纽进本菜单 | 无 | **UI/文案可用** · 无新 NPC |
| （若批 B）灰烛深渊 hint | — | 主线 hint | 枢纽主线 | 既有 | 无 | 只改字符串 |

**站岗自检：** 无新 NPC；仅文案 → 最高标「UI 可用」，不宣称场景完成。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 枢纽 → 进本 | 菜单写体力 | CoreRpg 扣体力后 DP 开场 | 体力不足灰显/拒门（既有） |
| 2 | 本内开场 | 「已消耗体力」 | 与实扣一致 | 无 |
| 3 | 超时/撤离/结算 | 「体力不退/不返还」 | **不再**念日票/团票 | 回枢纽菜单 |

---

## 7. UX 否决条

- 禁止借机改 `stamina.costs` / 掉落 / 人数门。  
- 禁止玩家路径新增斜杠教学。  
- 禁止把 NI 票物品改名冒充「B0.1 已清」。  
- 禁止未批准改 DP/quest YAML（除非批 A/B 后另开施工）。  
- 禁止宣称其它日常线「也有 timeout 假票」（现网仅 `EmberDaily` 有 timeout 文件）。

---

## 8. 未选表（否因）

| 候选 | 本轮 | 否因 |
|------|------|------|
| B2.6–B2.9（日票菜单/quest/DP 拒门斜杠/挂机庭天梯代号） | 排除 | 刚结 PASS；本窗是 **局内假票** 新证据，非重开 |
| 五入口奖励预览 / isomorphic 空壳→真页 | 排除 | 已结或禁再开 |
| 非日常体力灰显 / 挂机 over_chance*·二档 UX | 排除 | 刚结 |
| B1.3 / 墙钟 2h / 霜锈前压 / 断塔升 B | 排除 | 关账或刻意或无证据 |
| B0.1 票扣显示名 / NI「余烬日票」改名 | 排除 | 无新进本失败证据；改显示名易误读为重开 B0.1 |
| 精英奖励预览子菜单壳 | 排除 | 一点进本 · 须另开 |
| 灾厄 OP #6 单开 | 未单开 | 见得少；可并方案 B |
| 寄售 lore「票券」 | 未选 | 迁移期旧票仍可能存在；收益低于同本开场/超时自相矛盾 |
| 其它日常线补 timeout 任务 | 未选 | 无 timeout 文件 ≠ 假票债；补任务属玩法厚窗 |
| 新进度/货币/副本/Citizens/Paper | 排除 | 任务禁 |

---

## 9. 回总控一句话

**硬债空；B2.10 方案 A PASS · 已关闭——DP 局内/超时假「票」已对齐体力；Q1/Q2 与灾厄 OP #6 保留为残余。**

**专岗：** **策划**（设计）→ **插件**（`9311a72` 落地）→ **测试**（`95c9fbc` PASS）→ **已关闭**


---

## 批准记录（总控）

| 字段 | 值 |
|------|-----|
| 批准 | **A** · 2026-09-28 23:36 Asia/Shanghai |
| 岗 | 余烬-总控 |
| 范围 | 仅 DP 玩家可见 message #1～#9；完整替换见 `docs/status/STATUS-ember-dp-ticket-stamina-copy-approve.md` |
| 下一 | **已关闭**（施工 `9311a72` · 测试 `95c9fbc`） |
| 排除 | Q1/Q2；灾厄 OP #6；cost / gate-condition text / loot / TrMenu / quest / calamity / 其它玩法逻辑 |
| 残余 | 注释中的历史「票」可留；本轮不宣称 quest 或灾厄 OP 已清 |
