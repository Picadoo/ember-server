# STATUS · B2.8 DP 进本拒门去斜杠 A · 测试验收

| 字段 | 值 |
|------|-----|
| 时间 | 2026-09-28 23:17 Asia/Shanghai |
| 岗 | 余烬-测试岗执行器 |
| 施工 tip | `e7bc1eb` · `fix(dp): B2.8 entry gate messages no slash` |
| 批准 tip | `4c99c21` · 批准 A |
| 设计 tip | `83d641a` · `docs/design-ember-dp-gate-nocmd.md` |
| Verdict | **✅ PASS** |
| JSON | `/tmp/b28-dp-gate-smoke3.json` · `/tmp/b28-dp-raid-smoke.json` · 菜单旁证 `/tmp/b28-dp-gate-smoke2.json` |

---

## 一句话

**PASS：** 周/团/深渊/盟 Boss 四文件玩家可见拒门与盟 Boss 开场已去 `/corerpg`·`/ember`；五处菜单指路口径齐；condition `text=`/min/max 相对 tip 父 **零 diff**；灾厄 OP #6 未动；可选拒门冒烟绿；**ops=[]**；本 commit **未 push**。

---

## 硬条总表

| # | 验收点 | 结果 | 证据 |
|---|--------|------|------|
| 1 | Weekly/Raid/Abyss/GuildBoss 玩家可见 `message=` / `$message{…text=…}` **无** `/corerpg` 或 `/ember`（`#` 注释可留） | ✅ **PASS** | 四文件非注释 message/text 扫 hits=**[]**；注释中 `/corerpg` 仍在（B0.1 / 门控备忘） |
| 2 | 五处口径：等级拒门含「打开枢纽 → 角色查看等级」；盟 Boss gate「枢纽 → 盟约 →「周盟 Boss」」；开场「盟约菜单「周盟 Boss」门控」 | ✅ **PASS** | #1～#3 三句含 `打开枢纽 → 角色查看等级`（Lv.20/35/25 保持）；#4 gate `需由队长在枢纽 → 盟约 →「周盟 Boss」开启（消耗盟约贡献）`；#5 开场 `进本由盟约菜单「周盟 Boss」门控（贡献消耗）` |
| 3 | `$js-condition`/`$team-condition` 的 **text=** 与 min/max 相对 tip **零 diff**（仅 message 字符串） | ✅ **PASS** | `e7bc1eb^..e7bc1eb`：四文件各 team/js 条件 `text=`/`min`/`max` 全同；仅 js `message=` 与 GuildBoss 开场 `$message text=` 变更 |
| 4 | 灾厄 OP #6 **未动**（EmberCalamity 仍可有 `/ember`，不算本窗 FAIL） | ✅ **PASS** | tip diff 无 `EmberCalamity`；现网仍：`正式灾厄请走公共窗口：/ember → 灾厄` |
| 5 | 可选冒烟：低等级拒周/团/深渊、非通行拒盟 Boss | ✅ **PASS**（可选） | 见下；不强制通关 |

禁项自检：本岗 **未改** YAML / jar；**不**宣称 B0.1 / 精英预览壳已清。

---

## 静态明细

### tip diff（施工）

| 文件 | 变更摘要 |
|------|----------|
| `EmberWeekly/option.yml` | gate message：去 `（/corerpg level 查看）` → ` · 打开枢纽 → 角色查看等级` |
| `EmberRaid/option.yml` | 同上（Lv.35） |
| `EmberAbyss/option.yml` | 同上（Lv.25） |
| `EmberGuildBoss/option.yml` | gate + 开场 `$message` 改菜单路径 |
| `docs/STATUS-ember-dp-entry-no-slash.md` | 插件岗施工 STATUS |

热更旁证（施工 STATUS）：`23:10:04` `dp reload` → `插件重载完毕`；live/runtime 同 inode。

### condition 零 diff（相对 `e7bc1eb^`）

| 副本 | team text/min/max | js text | 仅 message 变 |
|------|-------------------|---------|---------------|
| EmberWeekly | 同 | `%corerpg_gate_weekly%…` 同 | ✅ |
| EmberRaid | 同 | `%corerpg_gate_raid%…` 同 | ✅ |
| EmberAbyss | 同 | `%corerpg_gate_abyss%…` 同 | ✅ |
| EmberGuildBoss | 同 | `%corerpg_guildboss_pass%…` 同 | ✅ + 开场 text |

---

## 可选冒烟（菜单优先 + DP 条件句）

环境：proxy 25565 / login 25566 / play 25567；`JAVA_HOME=…/jdk8u504-b01`；验收号 Lv.10 新号；**未**临时 OP（`ops.json` 始终 `[]`）。

| 路径 | 账号 | 结果 | 玩家可见句（无斜杠） |
|------|------|------|----------------------|
| 菜单等价 `corerpg enter weekly/abyss` | `B28m2668` | ✅ CoreRpg 前置拒（无 `/`） | `余烬周本需要余烬 Lv.20（当前 Lv.10）` / `余烬深渊需要余烬 Lv.25（当前 Lv.10）` |
| 菜单等价 `corerpg guild boss`（无盟） | 同上 | ✅ 无斜杠 | `[盟约] 你不在任何盟约中。` |
| DP 条件：`dp start-console <p> EmberWeekly` | `B28q9240` | ✅ | `周常本需要余烬等级 Lv.20 · … · 打开枢纽 → 角色查看等级` |
| 同上 EmberAbyss | 同上 | ✅ | `深渊需要余烬等级 Lv.25 · … · 打开枢纽 → 角色查看等级` |
| 同上 EmberGuildBoss（无通行） | 同上 | ✅ | `盟 Boss 需由队长在枢纽 → 盟约 →「周盟 Boss」开启（消耗盟约贡献）` |
| 三人队 EmberRaid | `B28r1113`+2 | ✅ | `团本需要余烬等级 Lv.35 · … · 打开枢纽 → 角色查看等级` |

> 注：玩家手打 `/dp start <名>` 现网 Brigadier 对非权账号报 `Incorrect argument`（`dungeon.start`）；正式入口为 TrMenu → `corerpg enter*` / `guild boss`；DP 拒门句由 `start-console`（CoreRpg 进本底层）触发，本轮用控制台复现。

---

## ops / 禁项

| 项 | 结果 |
|----|------|
| `server-runtime/ops.json` | **`[]`** |
| `login-runtime/ops.json` | **`[]`** |
| 临时 OP | **无**（未写入 ops / 未 deop 需求） |
| 改 YAML | **无** |
| git push | **未执行**（待总控代推） |

---

## 阻塞点

无。可选冒烟曾遇 `console.in` 被残留 `cat` 抢读（已清）；不影响静态硬条。

---

## 回总控

- **总评：** ✅ **PASS**（B2.8 A）
- **各点：** 1～5 全 PASS；灾厄 #6 明示未做
- **diff：** 相对 tip 父仅四 `option.yml` message + 插件 STATUS（本测试 commit 仅本 STATUS）
- **报告路径：** `docs/STATUS-ember-dp-gate-nocmd-test.md`
- **ops：** `[]`
- **阻塞点：** 无
- **待推 tip：** 本测试 commit（总控代推；施工 tip `e7bc1eb` 已在 origin）
