# 灾厄公共窗调度短规格（优先级 2）

**日期：** 2026-09-13（Asia/Shanghai）  
**作者岗：** 余烬-策划  
**状态：** 深渊冲层撤离已 PASS；总控开本刀。  
**现成草稿：** `plugins/CoreRpg/calamity.yml`（`enabled: false`）— **以本文为准改世界与主路径**。  
**入口文案：** `docs/ember-calamity-mv-entry.md`  
**奖励表：** `docs/ember-abyss-calamity.md` §2.3 · `docs/ember-gear-drop-t0-t3.md` §3.5  

---

## 0. 一句话

到点在 **MV `ember_event`** 刷 `EmberCalamityBoss`，全服可 `/mvtp ember_event` 围殴；**每日宝箱限 1**；DP `EmberCalamity` 只留测。

---

## 1. 改 `calamity.yml`（最小字段）

```yaml
enabled: true

boss:
  mythic_id: EmberCalamityBoss
  display: "余烬灾厄使"   # 或灰烬巨像，与 MM Display 一致即可
  world: ember_event      # ← 从 world 改到这里
  x: 0                    # ← 用 MV 世界出生/祭坛坐标（平台已铺则填实测）
  y: 65
  z: 0
  amount: 1
  # 开窗刷怪（公共）：不要默认 dp start
  spawn_command: "mm m spawn EmberCalamityBoss 1 {world},{x},{y},{z}"

schedule:
  timezone: Asia/Shanghai
  windows: ["12:00", "20:00", "22:00"]
  duration_minutes: 10

announce:
  pre_minutes: [5, 1]     # 至少 5；有余力加 1
  message_pre: "§4[余烬灾厄] §e{minutes} 分钟后降临！§7/ember → 灾厄 → 奔赴"
  message_open: "§4§l[余烬灾厄] §e灾厄使降临祭坛！§7/ember → 灾厄 → 奔赴 §8或 §f/mvtp ember_event"
  message_closed: "§c灾厄未苏醒。下一窗：§f{next_window}"
  message_end: "§4[余烬灾厄] §7本轮窗口结束，灰烬合拢。"

daily_chest:
  limit_per_day: 1
  # 击杀后发（有伤害贡献）；同日第二窗只给参战掉落 A，不发箱 B
  rewards:
    - { ni: mat_ember_core_fragment, amount: 2 }
    - { ni: crystal_ember_enchant, amount: 1 }
    - { ni: mat_calamity_ember, amount: 2 }
  protect_scroll_chance: 0.10

# 可选：窗外仍允许 mvtp 看空场，但菜单「奔赴」窗外只 tell message_closed
gate_mvtp_on_menu: true
```

`calamity-state.yml`：记 `lastFireDate` + 当日已开火窗口列表（已有雏形可沿用）。

---

## 2. 调度状态机

| 状态 | 条件 | 行为 |
|------|------|------|
| IDLE | 非窗 | 不刷 Boss；菜单奔赴 → `message_closed` |
| PRE | 开窗前 5/1 分 | 全服 announce `message_pre` |
| OPEN | 到点 | announce `message_open`；若场上无存活 Boss → `spawn_command`；标记 window_open |
| OPEN 中 | Boss 被击杀 | 参战掉落 A（MM/`ni give`）；符合条件发日箱 B；**不立刻关窗**（可防重复刷：同窗不再 spawn，或 60s 后再刷 1 次——本期 **同窗不二刷**） |
| END | 开窗 +10 分 | 清残留 Boss（可选 killall）；`message_end`；window_open=false |

**时区：** 一律 `Asia/Shanghai`。  
**同窗不二刷：** 一窗最多成功击杀结算一次 Boss 实体链；击杀后本窗不再 spawn。

---

## 3. 奖励接线（防三窗刷爆）

| 类型 | 规则 | 内容 |
|------|------|------|
| **A 参战** | Boss 死亡且本窗有伤害 | 核心 1～2、碎片 6、`mat_calamity_ember`×1、孔石 12%、外观碎片 20%（可先用现 MM 表） |
| **B 日箱** | 每人 **自然日 1 次**（上海 0:00 重置） | 核心×2 + 晶×1 + 灾厄余烬×2 + 保护券 10% |
| T3 装 | 低概率 | 见 gear-drop §3.5；无 NI 则跳过不阻塞本刀 |
| 安慰邮 | **本期可不做** | — |

存储：玩家 YAML / MySQL 字段 `calamityChestDate`（yyyy-MM-dd）；发过 B 则同日只 A。

---

## 4. 命令面（建议）

| 命令 | 作用 |
|------|------|
| `/corerpg calamity` | 状态：开窗与否、下一窗、本人日箱是否已领 |
| `/corerpg calamity forceopen` | OP 测：立即 OPEN 并刷 Boss |
| `/corerpg calamity forceend` | OP 测：结束窗口 |
| 玩家主路径 | `/mvtp ember_event`（菜单已接） |
| 测 Boss | `/dp start EmberCalamity`（菜单次按钮，保持） |

---

## 5. 菜单（已有则只改 tell）

- 窗外点「奔赴」：先 `calamity` 状态检查 → 关闭则 `tell message_closed`，**仍可**让玩家看空场（或直接拒 tp，二选一；**推荐：tell + 仍 mvtp**，到场看告示「沉睡中」）。  
- 开窗 tell：`§a灾厄进行中，前往中央祭坛！`

---

## 6. 验收（PASS）

1. `calamity.yml` `enabled: true`，`world: ember_event`，坐标正确。  
2. OP `forceopen` → 全服公告 + `ember_event` 出现 Boss。  
3. 击杀 → 参战者拿到 A；首次当日拿到 B；同日再 `forceopen` 击杀 → **只有 A 无 B**。  
4. `forceend` 或满 10 分 → 公告结束；菜单显示未苏醒 + 下一窗。  
5. 正式路径不依赖 `/dp start`；DP 测试实例仍可用。  
6. 不改 Paper；不影响深渊撤离 PASS。

---

## 7. 先不做

- 添头波完整阶段脚本（有单 Boss 可 PASS，阶段文案下期）。  
- 全服安慰邮。  
- 团本通道（优先级 3）。

---

## 路径

| 文件 | 用途 |
|------|------|
| **本文** `docs/ember-calamity-window-spec.md` | 本刀验收主规格 |
| `plugins/CoreRpg/calamity.yml` | 改 enabled/world/坐标后落地 |
| `docs/ember-calamity-mv-entry.md` | 菜单 / mvtp 文案 |
| `docs/ember-abyss-calamity.md` §2 | 奖励与播报原文 |
