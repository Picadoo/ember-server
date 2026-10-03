# STATUS · B-flex-3 灰粮 stub 币购副手 · 轻测（测试岗）

- **时间：** 2026-09-29 04:22（Asia/Shanghai）
- **执行：** 余烬-测试岗执行器
- **依据：** 批 A `docs/status/STATUS-ember-flex-offhand-thin-acquire-approve.md` · 施工 tip **`58baffe`**
- **Verdict：** **✅ PASS**（静态三点 + 菜单购入/周限/旧 offers 全绿；**未**宣称 B0.1 已清；**未**做长测 / 挑刺 / DPS）
- **未改：** jar / YAML / 数值 / 配置（仅测号清包 + 临时 OP；热更读入施工 tip）
- **脚本：** `mineflayer-tests/flex-offhand-thin-acquire-light-test.js`
- **JSON：** `/tmp/flex-offhand-thin-acquire-light-test.json`

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567（均在听） |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 验收号 | `FxOfLt29` |
| 临时 OP | `FxOfOp29`（FIFO `op` + LP admin；测后 `/deop` + `ops=[]`） |
| UX | `/ember` → hub「补给 · 生活」(槽17) → `ember_life` O/W 购入；自定义物 NI `acc_ember_offhand_ward` / `acc_ember_offhand_vita` |
| 热更 | `corerpg reload`（Life: **11** offers）+ `trmenu reload`（38 菜单） |

---

## 分项

| # | 硬条 | 结果 | 证据要点 |
|---|------|------|----------|
| 1 | 静态 `life.yml`：`offhand_ward`/`offhand_vita` coin 80 · weekly 1 · 无 life_level · give NI×1；双路径对齐 | ✅ **PASS** | `plugins/CoreRpg/life.yml` ≡ `CoreRpg/src/main/resources/life.yml`；两 offer 块无 `life_level`；`give: { acc_ember_offhand_ward/vita: 1 }` |
| 2 | 静态 `ember_life.yml`：O/W 图标 + `corerpg life buy offhand_ward\|vita`；lore 无手打 `/corerpg` 教学 | ✅ **PASS** | layout `#OV#I#WZ#`；O=铁粒「余烬守腕」· W=绿宝石「余烬生坠」；lore=`80 余烬币 / 每周 1 次 / 副手饰品… / ➥ 购买`（无 `/corerpg`） |
| 3 | 旁证：hub/set「工坊灰粮可购」；NI 属性未改；灰箍/体力门/T0–T3 刃护符未动 | ✅ **PASS** | tip 仅改 life.yml×2 + ember_life/hub/set；`ember-gear-offhand.yml` 仍 `物防+2` / `生命+8`（属 tip `52f817a`，本 tip 未触） |
| 4 | 菜单轻测：灰粮→补给·生活→守腕/生坠购入（够币）；周限；旧 offers 不回归 | ✅ **PASS** | 见下「菜单直播」 |

---

## 证据摘要

### 静态

```text
offhand_ward: coin 80 · weekly 1 · give acc_ember_offhand_ward:1 · 无 life_level
offhand_vita: coin 80 · weekly 1 · give acc_ember_offhand_vita:1 · 无 life_level
双路径 life.yml IDENTICAL
ember_life Layout … #OV#I#WZ# · O/W actions: corerpg life buy offhand_ward|vita
ember_hub.yml / ember_set.yml → 「工坊灰粮可购」半行
58baffe files: life.yml×2 · ember_life · ember_hub · ember_set（无 NI / 灰箍 / 体力 / 刃护符）
热更后日志：Life: 11 offers（原 9 + ward/vita）
```

### 菜单路径

```text
/ember →「余烬 · 冒险枢纽」→ 槽17「补给 · 生活」→「补给 · 生活」
  O(37) 余烬守腕 · W(42) 余烬生坠 · A(11) 余烬面包 ×8 仍在
购入守腕：[生活] 余烬守腕 → 余烬守腕×1（-80 币） · inv 余烬守腕
购入生坠：[生活] 余烬生坠 → 余烬生坠×1（-80 币）
再点守腕：[生活] 本周次数已用完（1）。 · count 仍 1
购入面包：[生活] 余烬面包 ×8 → 余烬面包×8（-40 币） · 旧 offers 不回归
```

### ops

- 测后：`server-runtime/ops.json=[]` · `login-runtime/ops.json=[]`
- 临时 OP `FxOfOp29` 已 deop；LP admin 已卸

---

## 债 / 未覆盖

- **未**做长测 / 挑刺 / DPS / wall-clock。
- **未**宣称 B0.1 已清；**未**改玩法数值或配置文件。
- 副手装卸属性一眼属 B-flex-1 已测范围，本窗仅验「灰粮币购可达」。

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **各点：** ① life.yml 双路径 PASS · ② ember_life O/W PASS · ③ 旁证 PASS · ④ 菜单购入/周限/旧 offers PASS
- **施工 tip SHA：** `58baffe`
- **测报 tip short SHA：** （本 commit）
- **是否已 push：** 见本 commit push 结果
- **报告路径：** `docs/status/STATUS-ember-flex-offhand-thin-acquire-test.md`
- **ops：** `[]`
- **阻塞点：** 无
- **菜单路径：** `/ember` → hub「补给 · 生活」→ O 守腕 / W 生坠
- **旁证结果：** hub/set「工坊灰粮可购」有；NI offhand 属性未改；灰箍/体力门/T0–T3 刃护符未入 tip
