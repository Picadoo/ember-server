# STATUS · B-anvil-1 余烬灰箍 A · 轻测

**日期：** 2026-09-29 01:32 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** 插件 tip `72281d9` · NI `57a6540` · 设计 `ab33c21` · 批准 `f10779b` · 施工 `docs/STATUS-ember-anvil-mat-to-part-pilot.md`  
**口径：** 仅轻测；**禁** wall-clock / DPS / 长本 / 挑刺；**禁改**配置；**勿宣称 B0.1 已清**  
**Verdict：** ✅ **PASS**  
**JSON：** `/tmp/anvil-ash-brace-light-test.json`（未入 git）

---

## 一句话

静态：NI + offhand 白名单 + `part.yml` 命中，`forge.yml` 升阶相对 tip-parent **ZERO**；菜单 `ember_forge「炼部件」`→ 不足人话不扣 / 够料入包灰箍；副手 `phys_defense=1.0` 可见；ops=[]。

---

## 总评

| 验收点 | 结果 |
|--------|------|
| 1. 静态 `part_ember_ash_brace`（NI + offhand + part.yml） | **PASS** |
| 2. `forge.yml` 升阶 4 条相对 parent ZERO | **PASS** |
| 3. 菜单：锻炉「炼部件」→ 预览灰箍 | **PASS** |
| 4. 材料不足：人话 + 不扣物 | **PASS** |
| 5. 材料够：灰箍入包、材料扣尽 | **PASS** |
| 6. 副手放上可见物防（轻一眼） | **PASS**（`phys_defense=1.0` · 减伤 0→4.8%） |
| ops=[] · 临时 OP deop | **PASS** |
| 禁改配置 / 勿宣称 B0.1 / 不叫挑刺 | **PASS**（本窗零改配置） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| CoreRpg | **1.15.26**（jar + Enabling `01:28:12` CST） |
| 账号 | 验收 `AvLtA29`（非 OP）· 辅助 `AvOpA29`（临时 OP，已 deop） |
| UX 路径 | `/ember` → 锻炉 → **炼部件**；自定义物 NI ID `part_ember_ash_brace` |

---

## 各点证据

### 1 · 静态 rg

| 目标 | 命中 |
|------|------|
| `plugins/NeigeItems/Items/ember-gear-parts.yml` | `part_ember_ash_brace:` · 物防 +1 · 名「余烬灰箍」 |
| `plugins/CoreRpg/config.yml` + src | `stats.offhand` 含 `part_ember_ash_brace` |
| `plugins/CoreRpg/part.yml` + src | `ash_brace` → output `part_ember_ash_brace` · cost shard×12 + ingot×2 |
| `part_ember_bone_charm` | **EMPTY** |
| CoreRpg jar | **1.15.26** · Enabling 命中 |

**PASS**

### 2 · forge.yml 升阶 ZERO

`git diff 72281d9^..72281d9 -- plugins/CoreRpg/forge.yml CoreRpg/src/main/resources/forge.yml` → **空**。

**PASS**

### 3 · 菜单路径（优先）

1. `/ember` → 窗「余烬 · 冒险枢纽」  
2. 点锻炉（槽 26）→「余烬锻炉 · 锻造」· 槽 22「**炼部件**」lore：灰箍碎片×12+铁锭×2 · 副手微量物防  
3. 点炼部件 →「烬砧 · 炼部件」· 预览「余烬灰箍」物防 +1 ·「确认炼制」

**PASS**（未手打 `/corerpg` 教学路径）

### 4 · 不足：人话不扣

背包仅 `mat_ember_shard`×3 → 点「确认炼制」：

> `[部件] 材料不足，未扣除：` · `余烬碎片 3/12` · `余烬铁锭 0/2` · `需要：… → 余烬灰箍`

计数：shard 3→3 · ingot 0→0 · ash 0→0。

**PASS**

### 5 · 够料：入包

给 shard×12 + ingot×2 → 点「确认炼制」：

> `[部件] 炼成 余烬灰箍 ×1（放副手槽生效）`

计数：shard 12→0 · ingot 2→0 · ash 0→1 · 包内「余烬灰箍」。

**PASS**

### 6 · 副手物防一眼

灰箍装副手后 `/corerpg stats`：

| | 装前 | 装后 |
|--|------|------|
| 减伤 | 0.0% | **4.8%** |
| raw | `{}` | `{phys_defense=1.0}` |
| lore | — | `物理防御: +1` |

**PASS**（轻一眼，非 DPS）

---

## 禁项核对

| 禁项 | 本窗 |
|------|------|
| 改配置 / YAML / jar | **未改** |
| 宣称 B0.1 已清 | **无** |
| 挑刺 / wall-clock / DPS / 长本 | **未做** |
| dirty runtime 入 commit | **否**（仅本 STATUS） |
| ops 残留 | **ops=[]** · `deop AvOpA29` |

---

## Git

- 仅本 STATUS：`docs/STATUS-ember-anvil-mat-to-part-pilot-test.md`
- tip / push：见本窗 commit 结果

## ops

`[]`（play + login）

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **各点：** 静态 PASS · forge ZERO PASS · 菜单炼部件 PASS · 不足人话不扣 PASS · 够料入包 PASS · 副手物防一眼 PASS · ops=[]
- **tip SHA：** 插件 `72281d9` · NI `57a6540` · 设计 `ab33c21` · 批准 `f10779b`（测报 tip 见本窗 commit）
- **是否已 push：** 见本窗 push 结果
- **报告路径：** `docs/STATUS-ember-anvil-mat-to-part-pilot-test.md`
- **ops：** `[]`
- **阻塞点：** 无
