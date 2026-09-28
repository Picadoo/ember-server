# STATUS · B-flex-4 踏步少开菜单热键 · 潜行+Q · 薄验收（测试岗）

- **时间：** 2026-09-29 04:44（Asia/Shanghai）
- **执行：** 余烬-测试岗执行器
- **依据：** 施工 tip **`005a03b`** · 批 A `docs/STATUS-ember-flex-step-hotkey-approve.md` · 设计 `docs/design-ember-flex-step-hotkey.md` · 施工 PASS `docs/STATUS-ember-flex-step-hotkey.md`
- **插件：** CoreRpg **1.15.28**（Enabling @ 04:37:59 CST）
- **Verdict：** **✅ PASS**（静态 + 按键/副手/菜单全绿；**未**宣称 B0.1 已清；**未**做 wall-clock / DPS / 挑刺）
- **未改：** jar / YAML / 数值 / 配置（仅测号清包 + 临时 OP；开阔地临时 fill 未入库）
- **脚本：** `mineflayer-tests/flex-hotkey-sneak-drop-light-test.js`
- **JSON：** `/tmp/flex-hotkey-sneak-drop-light-test.json`

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567（均在听） |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 验收号 | `FxHkU29` |
| 临时 OP | `FxHkOp29`（FIFO `op` + LP admin；测后 `/deop` + `ops=[]`） |
| UX | 装配踏步后开阔地 **潜行+Q**；菜单装配/释放仍可用；玩家不必依赖手打命令 |
| 热更 | 施工 tip 已 Enabling **v1.15.28**（本岗未热更） |

---

## 分项

| # | 硬条 | 结果 | 证据要点 |
|---|------|------|----------|
| 1 | 静态：`FlexSkillService` 含 `PlayerDropItemEvent` / sneak / `cast(`；`CoreRpgPlugin` `registerEvents` 含 flex；`skills.yml` CD14 / distance 5.0 **未变**；可选 `flex.hotkey` | ✅ **PASS** | 见下「静态」；双路径 skills + jar/日志 1.15.28 |
| 2 | 按键：装配踏步后开阔地 **潜行+Q** → 短位移/CD 提示；**未潜行 Q** → 仍可丢物 | ✅ **PASS** | sneak+Q：`moved=5.00`「释放 余烬踏步」· dirt 未丢；裸 Q：圆石 4→3 · 无释放文案 |
| 3 | 副手回归：副手持守腕/生坠/灰箍时 **裸 F** 只换手、不触发踏步 | ✅ **PASS** | ward/vita/ash_brace：`casted=false` · `moved=0` · 副手↔主手互换 |
| 4 | 菜单：轻技页装配/释放仍可用；lore 可见「潜行+Q」 | ✅ **PASS** | lore「战斗中 潜行+Q 可释放」；装配「已装配 余烬踏步 · CD 14s」；菜单释放 `moved=5.00` |

---

## 证据摘要

### 静态

```text
FlexSkillService.java → PlayerDropItemEvent / isSneaking() / cast(player) / implements Listener / onDropHotkey
CoreRpgPlugin.java → registerEvents(flexSkillService, this)
plugins/CoreRpg/skills.yml ≡ src skills.yml
  flex.hotkey: sneak_drop
  flex_ember_step cooldown_seconds: 14 · distance: 5.0（未变）
TrMenu ember_flex_skill / ember_hub → lore「战斗中 潜行+Q 可释放」
Enabling CoreRpg v1.15.28 @ 04:37:59 CST
```

### 按键

```text
潜行+Q：(200.5,80,200) → movedXZ=5.00 · 「[轻技] 释放 余烬踏步」· 泥土未丢（cancel drop）
裸 Q：cobble before=4 after=3 · 无「释放」文案（仍可丢物）
```

### 副手裸 F

```text
余烬守腕 → F：casted=false swapped oh0=余烬守腕→oh1=stick moved=0
余烬生坠 → F：同上 casted=false
余烬灰箍 → F：同上 casted=false
```

### 菜单

```text
轻技页 lore：释放轻技 / 轻技说明 均含「战斗中 潜行+Q 可释放」
装配：已装配 余烬踏步 · CD 14s · 零体力
释放（页内 C）：moved=5.00 「[轻技] 释放 余烬踏步」
```

### ops

- 测后：`server-runtime/ops.json=[]` · `login-runtime/ops.json=[]`
- 临时 OP `FxHkOp29` 已 deop；LP admin 已卸

---

## 债 / 未覆盖

- **未**做 wall-clock / DPS / 长本 / 挑刺。
- **未**宣称 B0.1 已清；**未**改玩法数值或配置文件。
- 裸 Q 测中 bot 曾「fell from a high place」（地形/落地旁证，不影响丢物结论）。
- CD 等待仅用于串测，**非** wall-clock 验收。

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **各点：** ① 静态 PASS · ② 潜行+Q/裸Q PASS · ③ 副手裸F×3 PASS · ④ 菜单装配/释放/lore PASS
- **施工 tip SHA：** `005a03b`
- **测报 tip short SHA：** （本 commit）
- **是否已 push：** 见本 commit push 结果
- **报告路径：** `docs/STATUS-ember-flex-hotkey-sneak-drop-test.md`
- **ops：** `[]`
- **阻塞点：** 无
- **静态证据：** Drop/sneak/cast/Listener + registerEvents(flex) + CD14/dist5.0 + flex.hotkey + lore 潜行+Q + v1.15.28
- **按键/副手/菜单摘要：** sneak+Q moved=5 释放；裸Q丢物；F×守腕/生坠/灰箍不踏步只换手；菜单装配+释放+lore 潜行+Q
