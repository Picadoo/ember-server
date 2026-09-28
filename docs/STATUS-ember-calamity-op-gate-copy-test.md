# STATUS · B2.12 灾厄 OP 调试拒门去斜杠 A · 测试验收

| 字段 | 值 |
|------|-----|
| 时间 | 2026-09-29 00:03 Asia/Shanghai |
| 岗 | 余烬-测试岗执行器 |
| 施工 tip | `9be6ae4`（fix `3ce343d` · 设计 `1e109b3` · 批准 `708c62a`） |
| 范围 | `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` L17 `message=` 单句 |
| Verdict | **✅ PASS · 勾销** |
| JSON | `/tmp/b212-calamity-op-gate-smoke.json`（未入 git） |

---

## 一句话

**PASS：** 玩家可见 OP 拒门已去 `/ember`，改为「枢纽菜单 → 灾厄」；相对批准/施工仅 `option.yml` message 一字串；非 OP `dp start-console` 拒门文案绿；**ops=[]**；本 commit **未 push**。

---

## 硬条总表

| # | 验收点 | 结果 | 证据 |
|---|--------|------|------|
| 1 | 静态：`message=` 内 `/ember` = 0；`枢纽菜单 → 灾厄` 命中 L17 | ✅ **PASS** | 见下静态明细 |
| 2 | 结构禁项：相对 `708c62a`/`9be6ae4`，text=/team/人数门/loot/MM/TrMenu/calamity.yml/小写 stub **零 diff** | ✅ **PASS** | 仅 option.yml message + 施工 STATUS |
| 3 | 冒烟：非 OP 拒门含「枢纽菜单 → 灾厄」、无 `/ember` | ✅ **PASS** | `B212t4676` · start-console |

禁项自检：本岗 **未改** YAML；**不**宣称 B0.1 或其它残余债已清；**未**带 dirty runtime 进 commit。

---

## 静态明细

### `/ember` 扫描

字面：`rg -n '/ember' plugins/DungeonPlus/dungeon/EmberCalamity/option.yml`

| 行 | 内容 | 计分 |
|----|------|------|
| L1 | `# … docs/ember-abyss-calamity.md §2` | `#` 注释路径子串 · **不计玩家 message**（同施工 STATUS 口径；L2 `/dp start` 管理备忘亦同） |
| — | `rg -n 'message=.*\/ember'` | **0 命中** |

### 目标口径

`rg -n '枢纽菜单 → 灾厄'` → **L17** `message=`：

`§c灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：枢纽菜单 → 灾厄`

热更旁证（施工 STATUS）：`00:00:05` `dp reload` → `插件重载完毕`；`00:00:06` `[EmberCalamity] 地牢内容初始化完毕`。live=`plugins/`（`server-runtime/plugins` → `../plugins` 同 inode）。

---

## 结构零 diff

相对 `708c62a..9be6ae4`（含 fix `3ce343d`）：

| 路径 | 变更 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` | **仅** js-condition `message=`：`/ember → 灾厄` → `枢纽菜单 → 灾厄` |
| `docs/STATUS-ember-calamity-op-gate-copy.md` | 施工 STATUS（+后续 docs 微调） |

未变（禁项）：

- `text='%player_is_op%'=='yes'` 同
- `$team-condition{…min=1;max=5…}` 同
- action-script `$message{…text=…}` / teleport / monstergroup 同
- loot / MM（`EmberCalamity.yml`）/ TrMenu / `calamity.yml` / 小写 stub `ember_calamity`：**零 diff**

---

## 冒烟

环境：proxy 25565 / login 25566 / play 25567；`JAVA_HOME=…/jdk8u504-b01`；账号 `B212t4676`（非 OP）；**未**临时 OP。

| 路径 | 结果 | 玩家可见 |
|------|------|----------|
| 手打 `/dp start EmberCalamity` | Brigadier 无权（同 B2.8） | `Incorrect argument for command \| dp start<--[HERE]` |
| 控制台 `dp start-console B212t4676 EmberCalamity` | ✅ 拒 · 新文案 | `灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：枢纽菜单 → 灾厄` |

判定：含「枢纽菜单 → 灾厄」✅ · 无 `/ember` ✅ · 未开本（无「灾厄降临时刻」）✅。

> 正式入口非手打 `/dp start`；拒门句由 `start-console`（与 CoreRpg/菜单底层同路径）触发，与 B2.8 口径一致。

---

## ops / 禁项

| 项 | 结果 |
|----|------|
| `server-runtime/ops.json` | **`[]`** |
| `login-runtime/ops.json` | **`[]`** |
| 临时 OP | **无**（未 op / 无需 deop） |
| 改 YAML | **无**（验收只读） |
| dirty runtime 入 commit | **无** |
| git push | **未执行**（待总控代推） |
| B0.1 / 其它残余债 | **未宣称已清** |

---

## 阻塞点

无。

---

## 回总控

- **总评：** ✅ **PASS**（B2.12 A）
- **各点：** 1 静态 PASS · 2 结构零 diff PASS · 3 冒烟 PASS
- **diff：** 相对 `708c62a` 仅 option.yml message + 施工 STATUS；本测试 commit 仅本 STATUS
- **报告路径：** `docs/STATUS-ember-calamity-op-gate-copy-test.md`
- **ops：** `[]`
- **阻塞点：** 无
- **待推 tip：** 本测试 commit（总控代推；施工 tip `9be6ae4` / fix `3ce343d`）
