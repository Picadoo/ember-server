# STATUS · 必改4 波次节拍短抽检

**日期：** 2026-09-28 06:06:11 Asia/Shanghai  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-daily-wave-variance.md` §6 · `docs/STATUS-ember-daily-wave-variance.md`（`76df295`）  
**测前：** play FIFO 控制台 `dp reload` → `[05:44:07] [DungeonPlus] 插件重载完毕`（**RCON 关** `enable-rcon=false`）  
**Verdict：** **❌ FAIL**（7 线 6 PASS · **霜晶 FAIL** · ops=`[]`）

---

## 一句话

七线改动房节拍抽检：庭院/焦骨/地窖/潮蚀/断塔/锈轨 **PASS**；霜晶 **交错≈4s 已证**，但房1 可见怪清尽后门1 仍铁栅（`wave1 $kill×5` 疑未计入 `wave1b` 击杀）→ **FAIL/阻塞插件**。真击杀、禁 killall、禁改配置、未 commit/push；测后 ops play+login=`[]`。

---

## 七线总表

| 线 | enter | 改动房硬条 | 结果 | 要点证据 |
|----|-------|------------|------|----------|
| 庭院 | `daily` | 房2 先骷对射再涌尸 | **PASS** | 先骷后尸；双侧柱后；「回廊·对射」→「僵尸涌上」→ door2 AIR |
| 焦骨 | `daily_ash` | 假岔×2 计入 door1 | **PASS** | 主清后门仍铁栅；岔存活；清岔后门 AIR×9 |
| 地窖 | `daily_crypt` | 中层先骷后尸 | **PASS** | 中层先誓印骷；「高台已清 · 窖卫尸压上」 |
| 潮蚀 | `daily_tide` | 桥面先浪矢再尸冲 | **PASS** | 先浪矢骷；「对岸弓已清 · 潮蚀尸冲锋」 |
| 断塔 | `daily_spire` | 环廊双侧对射再卫尸 | **PASS** | 东西双侧箭骷；「对射已清 · 卫尸压上」 |
| 霜晶 | `daily_frost` | 左刷后≈3s 右刷 | **FAIL** | 交错 Δ≈4.0s ✅；门1 全清后仍铁栅 ❌ |
| 锈轨 | `daily_rail` | 支洞矿矢计入 door2 | **PASS** | 主清后门2 铁栅；支洞骷在场仍铁栅；清支洞后 AIR |

**总评：** FAIL（霜晶门计数阻塞）

---

## 分线证据

### 1. 庭院 · 房2 先对射再涌尸 — PASS

- 清房1 → door1 AIR → 房2 首波 **skeleton**（左右柱后）先于 zombie  
- 文案：`【回廊·对射】` → `对射已清 · 僵尸涌上` → `回廊已清 · Boss 门开了`  
- 双侧箭骷：`sideCheck` left≥1 ∧ right≥1  
- door2 @z=25 AIR≥6

### 2. 焦骨 · 房1 假岔计入 door1 — PASS

- 进本刷怪：主甬 ID×4 + 假岔 ID×2（x≥5.5，坐标约 (8,65,10)/(7,65,11)）  
- **未清岔：** 按 ID 排除假岔击杀主甬 → door1 **iron≥6** 且假岔仍存活  
- **清岔后：** `前段已清 · 门开了` · door1 **air×9**  
- 文案含「主甬+假岔」「假岔也要清才开门」

### 3. 地窖 · 中层先骷后尸 — PASS

- 清上层 → 中层首波 **skeleton**（誓印骷）先于地面尸  
- 文案：`【中层·高台】誓印骷对射` → `高台已清 · 窖卫尸压上` → `中层已清 · 底层门开了`  
- door2 @z=40 y=66 AIR

### 4. 潮蚀 · 房2 桥面先浪矢再冲锋 — PASS

- 清沿岸 → 折桥首波 **浪矢骷**（firstSkel≪zombie）  
- 文案：`【水道·折桥】对岸浪矢骷` → `对岸弓已清 · 潮蚀尸冲锋` → `折桥已清 · 闸厅门开了`

### 5. 断塔 · 环廊双侧对射再卫尸 — PASS

- 清底层 → 中层环廊 **东西双侧** 裂隙箭骷（left∧right）  
- 文案：`【中层环廊·对射】` → `对射已清 · 卫尸压上` → `环廊已清 · 顶门开了`  
- door2 @z=4 y=70 AIR

### 6. 霜晶 · 房1 左右交错 — FAIL

| 检查 | 结果 | 证据 |
|------|------|------|
| 左刷后右刷交错 ≈3s | ✅ | 补测 Δ=**4008ms**（左新 ID → 右新 ID，非同怪游荡）；第三轮 Δ=**4004ms** |
| 文案交错 | ✅ | `【裂隙·左冻台】…右侧将交错刷出` → `【裂隙·右冻台】交错侧袭` |
| 门1 AIR / 冰闸 | ❌ | 可见僵尸清尽后仍 **iron_bars×9**；无「冻台已清 · 冰闸开了」 |
| 阻塞 | — | `wave1` 仅刷左×3 但 `$kill×5`；`wave1b` 右×2 另组。实锤：**全清无门** → 疑 wave1b 击杀未并入 wave1 开门计数 |

**结论：** 节拍可辨（交错 PASS）；**开门骨架 live 回归 FAIL** → 回插件核 `$kill` 合并或把 5 只放同组。

### 7. 锈轨 · 房2 支洞矿矢计入 door2 — PASS

- 清主巷 wave2a → door2 **iron×9**（未开）  
- 支洞 **skeleton** 刷出（x≥5，文案「支洞矿矢骷」）时 door2 **仍铁栅**  
- 清支洞后 `后段已清 · 机房门开了` · door2 **air×9**  
- 证明：**未清支洞则门不开**；支洞计入 door2

---

## 回归抽检（抽检级）

| 项 | 结果 |
|----|------|
| 体力 -30 | ✅ 多线进本文案实锤（非管理免扣） |
| 禁 killall / kill-any | ✅ 脚本仅 `attack`/`swingArm`；YAML 有效行无 `$kill-any` |
| RCON | ✅ 全程 `enable-rcon=false`；热更走 FIFO 控制台 |
| 门/Boss/箱 YAML 骨架 | ✅ 未改配置；Boss 组与门坐标保持 |
| 霜晶门1 live | ❌ 见上（唯一回归） |
| 测后 ops | ✅ play=`[]` · login=`[]` |

---

## 环境

| 项 | 值 |
|----|----|
| commit | `76df295` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| CoreRpg | 1.15.18 |
| 热更 | FIFO `console.in` → `dp reload`（05:44:07 CST 重载完毕） |
| JSON | `/tmp/wave-variance-test.json` · `/tmp/wave-variance-retest.json` |

---

## 阻塞点（给总控 / 插件）

1. **霜晶 `EmberDailyFrost` door1：** `wave1` `$kill{霜晶尸;amount=5}` + `wave1b` 侧刷×2 的合并计数 **live 不生效**（怪清尽门不开）。请改同组刷怪或确认 DP `$kill` 跨 group 同 Display 是否累计。  
2. 其余六线节拍硬条已 PASS，可先合入体验；霜晶需热修后复测门1。

---

## 成功标准核对

| 标准 | 状态 |
|------|------|
| 报告落盘 | ✅ 本文件 |
| 七线结论明确 | ✅ 6 PASS / 1 FAIL |
| ops 清空 | ✅ `[]` / `[]` |
| 未 commit/push | ✅ |
| 禁改配置 | ✅ |

**最终结论：** 必改4 抽检 **FAIL**（霜晶门计数）；其余六线节拍 **PASS**。
