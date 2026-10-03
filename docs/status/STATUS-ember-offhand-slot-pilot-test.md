# STATUS · B-flex-1 副手试点 A · 轻测（测试岗）

- **时间：** 2026-09-29 00:58（Asia/Shanghai）
- **执行：** 余烬-测试岗执行器
- **依据：** 施工 `docs/status/STATUS-ember-offhand-slot-pilot.md` · 设计 tip `7dda194` · 批准 tip `eb3c843`
- **插件 tip：** `08b4cd4` · **NI tip：** `52f817a` · CoreRpg **1.15.24**
- **Verdict：** **✅ PASS**（轻测三点全绿；**未**宣称 B0.1 已清；**未**做 wall-clock / DPS / 长本）
- **未改：** jar / YAML / 数值 / 配置（测号清包 + 临时 OP 仅）
- **脚本：** `mineflayer-tests/offhand-pilot-light-test.js` · 菜单补目视 `offhand-menu-gaze.js`
- **JSON：** `/tmp/offhand-pilot-light-test.json` · `/tmp/offhand-menu-gaze.json`

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 验收号 | `OfLtA29`（属性）· `OfLtB29`（菜单） |
| 临时 OP | `OfOpA29` / `OfOpB29`（FIFO `op` + LP admin；测后 `/deop` + `ops=[]`） |
| UX | `/ember` → 套装；自定义物 `/ni give <id>` |

---

## 分项

| # | 硬条 | 结果 | 证据要点 |
|---|------|------|----------|
| 1 | 静态：`rg offhand` 白名单含 ward/vita；hub/set 文案有副手说明 | ✅ **PASS** | 双路径 `stats.offhand`：`acc_ember_offhand_ward` / `_vita`；`ember_set` Open tell + 图标 O；`ember_hub` 套装 V lore「副手饰品 · 放副手槽生效」；NI ward 物防+2 / vita 生命+8；jar+日志 **1.15.24** |
| 2 | 进服：副手装 ward / vita 各一眼，属性刷新合理；换下即掉 | ✅ **PASS** | 基线生命 20 · raw `{}`。**ward** 副手 → raw `{phys_defense=2.0}` · 减伤 9.1%；卸下 → raw `{}` · 减伤 0%。**vita** 副手 → 生命 **28.0** · raw `{max_health=8.0}`；卸下 → **20.0** |
| 3 | 菜单：hub/set 目视 | ✅ **PASS** | `/ember` 开「余烬 · 冒险枢纽」；V lore 含副手句；点进「余烬 · 套装」Open tell「副手饰品 · 放副手槽生效」；O 图标名「副手饰品」lore 同句 |

---

## 证据摘要

### 静态

```text
plugins/CoreRpg/config.yml + src/.../config.yml → offhand: ward / vita
ember_set.yml → tell/图标「副手饰品 · 放副手槽生效」
ember_hub.yml → 套装 lore 半句同文
ember-gear-offhand.yml → ward 物理防御+2 · vita 生命力+8
Enabling CoreRpg v1.15.24 @ 00:54:14 CST
```

### 属性一眼（`OfLtA29` · `/corerpg stats`）

```text
基线：生命 20.0 · 减伤 0.0% · raw {}
ward 装：生命 20.0 · 减伤 9.1% · raw {phys_defense=2.0}
ward 卸：生命 20.0 · 减伤 0.0% · raw {}
vita 装：生命 28.0 · 减伤 0.0% · raw {max_health=8.0}
vita 卸：生命 20.0 · raw {}
发放：/ni give … acc_ember_offhand_ward|vita（NI ID，非显示名）
```

### 菜单目视（`OfLtB29` · `/ember` → 套装）

```text
hub 标题：余烬 · 冒险枢纽
hub V lore：…「副手饰品 · 放副手槽生效」…
set 标题：余烬 · 套装
set Open tell：副手饰品 · 放副手槽生效
set O：副手饰品 · lore 同句 + 试点守腕/生坠
```

### ops

- 测后：`server-runtime/ops.json=[]` · `login-runtime/ops.json=[]`
- 临时 OP 已 deop；LP admin 已卸

---

## 债 / 未覆盖

- **未**做 wall-clock / DPS / 长本 / 双装并行数值细账。
- **未**宣称 B0.1 已清；**未**改配置。
- 首轮 `offhand-pilot-light-test` 菜单项曾因误走 `/trmenu open`（无权限）记 FAIL；以 `offhand-menu-gaze` 菜单路径重目视 **PASS** 为准（属性项首轮已全绿）。

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **各点：** 静态 PASS · 副手装卸属性 PASS · hub/set 目视 PASS
- **tip SHA：** 插件 `08b4cd4` · NI `52f817a` · 设计 `7dda194` · 批准 `eb3c843` · 本测报 tip 见 commit
- **是否已 push：** 见本 commit push 结果
- **报告路径：** `docs/status/STATUS-ember-offhand-slot-pilot-test.md`
- **ops：** `[]`
- **阻塞点：** 无
