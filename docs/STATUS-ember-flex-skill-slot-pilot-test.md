# STATUS · B-flex-2 余烬踏步 A · 轻测（测试岗）

- **时间：** 2026-09-29 01:21（Asia/Shanghai）
- **执行：** 余烬-测试岗执行器
- **依据：** 施工 `docs/STATUS-ember-flex-skill-slot-pilot.md` · 设计 tip `d1e88e4` · 批准 tip `7d7bf5a` · 补记 `3841f4f`
- **插件 tip：** `fc89225` · CoreRpg **1.15.25**
- **Verdict：** **✅ PASS**（轻测三点全绿；**未**宣称 B0.1 已清；**未**做 wall-clock / DPS / 长本 / 挑刺）
- **未改：** jar / YAML / 数值 / 配置（测号清包 + 临时 OP 仅；开阔地临时 fill 未入库）
- **脚本：** `mineflayer-tests/flex-step-pilot-light-test.js` · 位移补测 `flex-cast-move-once.js`
- **JSON：** `/tmp/flex-step-pilot-light-test.json` · `/tmp/flex-cast-move-once.json`

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 验收号 | `FxLtD29`（主流程）· `FxMvU1`（开阔地短移） |
| 临时 OP | `FxOpD29` / `FxMvOp1`（FIFO `op` + LP admin；测后 `/deop` + `ops=[]`） |
| UX | `/ember` → hub「轻技」；装配/释放走 `ember_flex_skill`（页内点击）；自定义物走 NI ID（本窗无 NI） |

---

## 分项

| # | 硬条 | 结果 | 证据要点 |
|---|------|------|----------|
| 1 | 静态：`rg flex_ember_step` / `flex_skill`；skills.yml 有 flex；誓约三技 id 未改 | ✅ **PASS** | 双路径 `skills.yml` 含 `flex_ember_step` `type: step` distance 5；`PlayerData`/`Store` `flex_skill_id`；`ember_blaze_slash`/`ember_ash_familiar`/`ember_warden_taunt` 块 vs tip-parent **ZERO**；jar+日志 **1.15.25** |
| 2 | 菜单：hub「轻技」→ 装配踏步 → 释放短移一眼；卸下后释放应拒；hub「技能」仍可放誓约技 | ✅ **PASS** | hub 槽7「轻技」lore 含踏步/零体力；点入触发轻技页 Open tell。页内装配「已装配 余烬踏步 · CD 14s · 零体力」。枢纽广场四面「前方受阻」；开阔地 `world` 菜单释放 **movedXZ=5**「释放 余烬踏步」。卸下后「尚未装配轻技」。hub「技能」→「附近没有目标」/烬斩路径（誓约已选 blaze） |
| 3 | 零体力扣、无伤害宣称口径 | ✅ **PASS** | flex yml 无 `damage_mult`/`cost`；Java 未接 `StaminaService`，文案「零体力 · 无伤害」。直播力 90→90（释放前后）；无「体力不足」 |

---

## 证据摘要

### 静态

```text
plugins/CoreRpg/skills.yml + src/.../skills.yml → flex_ember_step type:step distance:5.0 CD14
PlayerData/Store → flex_skill_id
誓约三技块 ZERO vs fc89225^
ember_hub.yml → m「轻技」menu: ember_flex_skill；E「技能」corerpg skill 未改
ember_flex_skill.yml → 装配/卸下/释放
Enabling CoreRpg v1.15.25 @ 01:09:40 CST
```

### 菜单 / 释放

```text
hub「轻技」：名=轻技 · lore 含「试点余烬踏步」「零体力」
装配：已装配 余烬踏步 · CD 14s · 零体力
短移（开阔地菜单 C）：(200.5,69,200.5) → (200.5,69,205.5) movedXZ=5 · 「释放 余烬踏步」
卸下：已卸下轻技 → 释放「尚未装配轻技」
hub「技能」：烬斩路径触发（附近无目标提示 / 冷却信息）
```

### 体力

```text
释放前 [体力] 90/90 · 释放后 90/90 · 无体力不足
```

### ops

- 测后：`server-runtime/ops.json=[]` · `login-runtime/ops.json=[]`
- 临时 OP 已 deop；LP admin 已卸

---

## 债 / 未覆盖

- **未**做 wall-clock / DPS / 长本 / 挑刺数值细账。
- **未**宣称 B0.1 已清；**未**改配置。
- 枢纽广场多朝向踏步呈「前方受阻」（地形）；短移一眼以开阔地菜单路径补证。
- hub「轻技」在本环境 mineflayer `window_click mode=0` 会连带触发 `shift_all`（释放+关窗）；装配/卸下/释放以轻技页内点击为准，入口以 hub 图标+Open tell 目视。

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **各点：** 静态 PASS · 菜单装配/短移/卸下拒/誓约技 PASS · 零体力/无伤害口径 PASS
- **tip SHA：** 插件 `fc89225` · 补记 `3841f4f` · 设计 `d1e88e4` · 批准 `7d7bf5a` · 本测报 tip 见 commit
- **是否已 push：** 见本 commit push 结果
- **报告路径：** `docs/STATUS-ember-flex-skill-slot-pilot-test.md`
- **ops：** `[]`
- **阻塞点：** 无
