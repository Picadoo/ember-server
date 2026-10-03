# STATUS · B2.10 DP 局内/超时假票→体力 A · 验收

**日期：** 2026-09-28 23:41 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗  
**依据：** 施工 tip `9311a72` · 批准 `bf174fc` · 设计 `f2eb738` · 方案 A  
**口径：** 静态 rg + 可选深渊开场撤离冒烟；**禁改**玩法 YAML；**不宣称** quest「深渊票」/B0.1 已清  
**Verdict：** ✅ **PASS** · 报告仅本地 commit · **未 push**（交总控代推）

---

## 一句话

日/周/深/团/精 玩家 `$message` 行已无假票禁词，九处体力口径齐；门控 text=/cost/loot/TrMenu/quest/灾厄 OP 相对 tip 零 diff；深渊开场冒烟见「体力不退」；ops=[]。

---

## 总评

| 验收点 | 结果 |
|--------|------|
| 1. 日/周/深/团/精 玩家 message 行无：日票\|周票\|团票\|深渊票\|团本票\|票不退\|票已扣\|余烬团本票（`#` 注释可留） | **PASS** |
| 2. 九处体力口径：体力不返还/不退、已消耗体力 ×1、周首通一次、体力已扣 | **PASS** |
| 3. 门控 text=/cost/loot/TrMenu/quest/灾厄 OP 相对 tip 零 diff | **PASS** |
| 4. 可选冒烟：深渊开场撤离句 | **PASS** |
| ops=[] · 临时 OP deop | **PASS** |
| quest「深渊票」/B0.1 | **不计本轮**（仍残留，未宣称已清） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `9311a72` fix(dp): B2.10 in-run timeout copy ticket→stamina |
| 热更 | 施工 STATUS：`dp reload` @ **23:37:56 CST**（`[DungeonPlus] 插件重载完毕`；EmberAbyss/Raid/Elite/Daily/Weekly 初始化完成） |
| 账号 | 验收 `B210t2503`（临时 OP 仅进本，已 deop）· 辅助 `B210o2503`（临时 OP，已 deop） |
| JSON | `/tmp/b210-dp-ticket-stamina-smoke.json`（未入 git） |

---

## 各点证据

### 1 · 玩家 message 行无假票禁词

`rg '日票|周票|团票|深渊票|团本票|票不退|票已扣|余烬团本票'` 于  
`Ember{Daily,Weekly,Abyss,Raid,EliteWeekly}/**/*.yml`：

| 命中 | 性质 |
|------|------|
| `EmberAbyss/option.yml:3` `# …「余烬深渊票」…` | `#` 注释 · 可留 |
| `EmberWeekly/option.yml:4` `# …「余烬周票」…` | `#` 注释 · 可留 |
| `EmberRaid/option.yml:3` `# …「余烬团本票」…` | `#` 注释 · 可留 |
| `EmberRaid/option.yml:30` `# 团票每周 1 张 …` | `#` 注释 · 可留 |

非注释行 / `$message{…票…}`：**EMPTY**。

### 2 · 九处体力口径

| # | 文件 | 现网 message 体 | 判定 |
|---:|---|---|---|
| 1 | `EmberDaily/task/timeout.yml` | `§c挑战超时，本局失败（体力不返还）` | OK |
| 2 | `EmberWeekly/task/timeout.yml` | `§c周常超时失败（体力不返还）` | OK |
| 3 | `EmberAbyss/option.yml` 撤离 | `…按最高层结算 · 体力不退` | OK |
| 4 | `EmberAbyss/option.yml` 结算 | `…发箱（体力不退）` | OK |
| 5 | `EmberAbyss/task/timeout.yml` | `…（体力不返还）` | OK |
| 6 | `EmberRaid/option.yml` 开场 | `§8已消耗体力 ×1` | OK |
| 7 | `EmberRaid/option.yml` 通关 | `…（周首通一次）· T3 不保底` | OK |
| 8 | `EmberRaid/task/timeout.yml` | `…（体力不返还）` | OK |
| 9 | `EmberEliteWeekly/task/timeout.yml` | `§c试炼失败。§7体力已扣，下周再来。` | OK |

与批准表 `bf174fc` 全文一致。

### 3 · 禁项相对 tip 零 diff

- tip `9311a72` 相对 parent `bf174fc` 仅 8 文件：7 个 DP YAML（各仅 `$message` text 替换）+ 施工 STATUS。  
- tip hunk 全部为 `+/- $message{…}` 文案，**无** `js-condition` / `team-condition` / `stamina.costs` / `free_tickets` / min-max / level 变更。  
- `plugins/TrMenu` · `plugins/CoreRpg/quest.yml` · `CoreRpg/src/.../quest.yml` · `EmberCalamity` · `cash.yml`：**相对 tip parent 零 diff**。  
- 工作树 `plugins/DungeonPlus/` **相对 tip `9311a72` 零 diff**（验收只读，未改 YAML）。

### 4 · 可选冒烟（深渊开场撤离）

`dp start-console EmberAbyss`（临时 OP 过门控）玩家 chat：

> 深渊已开启。已消耗体力 ×1 —— 能走多深，就走多深。本期可下潜至第 12 层。  
> 需要撤离：打开菜单 → 深渊 → 撤离 · 按最高层结算 · **体力不退**

假票禁词：**EMPTY** · `/dp leave` 后 deop。

### ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`。

### 残余（本轮不宣称已清）

| 项 | 证据 |
|----|------|
| quest「深渊票」Q1/Q2 | live+src `quest.yml` L180 `需深渊票`；L335 `票还是一天一张`（方案 B，未批本轮） |
| 灾厄 OP #6 | `EmberCalamity/option.yml` 仍 `/ember → 灾厄` |
| B0.1 / NI 票显示名 | 未动、未验清 |

---

## Diff 摘要（相对 tip parent `bf174fc`）

| 文件 | 变更性质 |
|------|----------|
| `EmberDaily/Weekly/Abyss/Raid/EliteWeekly` 的 timeout / option | **仅** 9 处 `$message` 假票→体力 |
| `docs/status/STATUS-ember-dp-ticket-stamina-copy.md` | 施工 STATUS（既有 tip） |

本验收 **未** 改任何玩法 YAML。

---

## 版本控制

- 本验收 STATUS：`docs/status/STATUS-ember-dp-ticket-stamina-copy-test.md`
- 测试 commit：**本地** · **未 push** · tip 交总控代推  
- 施工 tip 待推：`9311a72`（若尚未在 origin）

## 阻塞点

无。
