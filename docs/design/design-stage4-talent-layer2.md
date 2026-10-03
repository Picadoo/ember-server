# 阶段 4.2 · 天赋第二层（J）— 节点表与加点公式

**日期：** 2026-09-27（Asia/Shanghai）  
**作者岗：** 余烬-策划  
**状态：** 仅文档（不改线上 `talent.yml` / jar）  
**批准：** 总控全量 I+J+N；落地顺序在 4.1 深渊加层之后  
**承接：** `docs/design/design-stage4-mainline-vol2.md` §3.2；现网 `plugins/CoreRpg/talent.yml`（每树总花费 20，`max_spendable_points: 20`）；`docs/design/DESIGN-ember-covenant-talent.md`  
**给谁：** **余烬-插件**（earned 公式 + YAML 节点）；测试岗验曲线

---

## 0. 目标

| 项 | 现值 | 目标 |
|----|------|------|
| `max_spendable_points` | 20 | **30** |
| Lv30 可花点 | 20（满一层树） | 仍为 **20** |
| Lv31～60 额外点 | 0 | **每 3 级 +1**，最多 +10 → Lv60 = 30 |
| 每誓约树总花费 | 20 | **30**（+10 新节点） |
| 技能倍率上限 | `talent_bonus_max` 现网 | **不提高**；二层**不加新主动技** |

---

## 1. 加点公式（推荐 A · 冻结）

```
# L = emberLevel
# 一层：保持现网在 Lv30 时 earned == 20 的行为（starting 5 + 自 Lv16 起每级 +1 等，以现 TalentService 为准）
# 二层增量：
if L <= 30:
  earned = min(20, legacy_earned(L))
else:
  earned = min(30, 20 + floor((L - 30) / 3))
```

| 等级 | earned（目标） | 相对 Lv30 |
|------|----------------|-----------|
| 30 | 20 | — |
| 31～32 | 20 | +0 |
| 33～35 | 21 | +1 |
| 36～38 | 22 | +2 |
| 39～41 | 23 | +3 |
| 42～44 | 24 | +4 |
| 45～47 | 25 | +5 |
| 48～50 | 26 | +6 |
| 51～53 | 27 | +7 |
| 54～56 | 28 | +8 |
| 57～59 | 29 | +9 |
| **60** | **30** | **+10** |

**实现注意：**

- 老存档若 `talentPointsEarned > 20`（历史 bug 已修过）：加载时 `min(earned, max_spendable)`。  
- `points_per_level` / `level_points_from` 可保留给 ≤30；≥31 走上面分支，避免双加。  
- 成就点兑换仍不自动发（成就未实装）。

YAML 建议新增键（插件可读）：

```yaml
max_spendable_points: 30
layer2:
  enabled: true
  from_level: 31
  points_per_levels: 3   # 每 3 级 +1
  base_at_30: 20
```

---

## 2. 一层节点（现状 · 勿改 ID/费用）

费用合计均为 **20**（2026-09-27 critic 已对齐）。

| 树 | 节点（cost） |
|----|----------------|
| blaze | root2 · crit1:3 · leech1:3 · crit2:3 · slash4 · cap5 |
| ash | root2 · haste1:3 · slow1:4 · fam5 · cap6 |
| warden | root2 · guard1:3 · taunt5 · charm1:3 · cap7 |

二层节点一律 `requires` 挂在对应 `*_cap` 之后（必须点满一层顶才能进二层）。

---

## 3. 二层节点表（每树 +10）

属性刻意偏小：预估满二层相对满一层战力约 **+5～8%（估）**；技能倍率节点**不加**。

### 3.1 烬刃 blaze（+10）

| ID | 显示名 | type | cost | requires | stats |
|----|--------|------|------|----------|-------|
| `blaze_ember2` | §6余烬脉 | passive | **3** | `[blaze_cap]` | `phys_damage: 1` |
| `blaze_heat2` | §c炽脉 | passive | **3** | `[blaze_ember2]` | `crit_damage_pct: 0.03` |
| `blaze_second` | §6再燃 | passive | **4** | `[blaze_ember2]` | `crit_chance_pct: 0.005` · `phys_damage: 1` |

合计 +10。`heat2` 与 `second` 都只要求 `ember2`，可先点一侧。

### 3.2 灰行 ash（+10）

| ID | 显示名 | type | cost | requires | stats |
|----|--------|------|------|----------|-------|
| `ash_veil2` | §7灰纱 | passive | **3** | `[ash_cap]` | `move_speed_pct: 0.01` |
| `ash_dust2` | §e细尘 | passive | **3** | `[ash_veil2]` | `attack_speed_pct: 0.01` |
| `ash_shroud` | §8掩息 | passive | **4** | `[ash_veil2]` | `on_hit_slow_pct: 0.03` · `attack_speed_pct: 0.01` |

合计 +10。

### 3.3 守墓 warden（+10）

| ID | 显示名 | type | cost | requires | stats |
|----|--------|------|------|----------|-------|
| `warden_bulwark2` | §9叠壁 | passive | **3** | `[warden_cap]` | `damage_taken_pct: -0.01` |
| `warden_aegis2` | §9护冢 | passive | **3** | `[warden_bulwark2]` | `max_health: 2` |
| `warden_vow2` | §1誓碑 | passive | **4** | `[warden_bulwark2]` | `phys_defense: 1` · `damage_taken_pct: -0.01` |

合计 +10。

---

## 4. `talent.yml` 草案片段（仅文档）

```yaml
max_spendable_points: 30
layer2:
  enabled: true
  from_level: 31
  points_per_levels: 3
  base_at_30: 20

trees:
  blaze:
    nodes:
      # …保留一层…
      blaze_ember2:
        display: "§6余烬脉"
        type: passive
        cost: 3
        requires: [blaze_cap]
        stats: { phys_damage: 1 }
      blaze_heat2:
        display: "§c炽脉"
        type: passive
        cost: 3
        requires: [blaze_ember2]
        stats: { crit_damage_pct: 0.03 }
      blaze_second:
        display: "§6再燃"
        type: passive
        cost: 4
        requires: [blaze_ember2]
        stats: { crit_chance_pct: 0.005, phys_damage: 1 }
  ash:
    nodes:
      ash_veil2:
        display: "§7灰纱"
        type: passive
        cost: 3
        requires: [ash_cap]
        stats: { move_speed_pct: 0.01 }
      ash_dust2:
        display: "§e细尘"
        type: passive
        cost: 3
        requires: [ash_veil2]
        stats: { attack_speed_pct: 0.01 }
      ash_shroud:
        display: "§8掩息"
        type: passive
        cost: 4
        requires: [ash_veil2]
        stats: { on_hit_slow_pct: 0.03, attack_speed_pct: 0.01 }
  warden:
    nodes:
      warden_bulwark2:
        display: "§9叠壁"
        type: passive
        cost: 3
        requires: [warden_cap]
        stats: { damage_taken_pct: -0.01 }
      warden_aegis2:
        display: "§9护冢"
        type: passive
        cost: 3
        requires: [warden_bulwark2]
        stats: { max_health: 2 }
      warden_vow2:
        display: "§1誓碑"
        type: passive
        cost: 4
        requires: [warden_bulwark2]
        stats: { phys_defense: 1, damage_taken_pct: -0.01 }
```

TrMenu `ember_talent.yml`（`/ember` → 天赋）：二层节点用同一套解锁；未满 `*_cap` 时显示「先点满一层顶」。
玩家**只点菜单**加点；`/corerpg talent` 仅管理/测试。

---

## 5. 平衡与 4.3 联动

- 二层属性总和刻意小于一层顶 `*_cap` 单节点。  
- **不上新 skill 节点**，避免烬斩/灰印倍率再涨。  
- 4.2 上线后立刻进 **4.3**：复测深渊 10/12、周本、团本；若 TTK 缩短 >15%，优先削二层 stats，其次抬怪。

---

## 6. 验收

1. [ ] Lv30：earned≤20；无法点二层（缺 `*_cap` 或点不足）。  
2. [ ] Lv33：earned=21；可点 `*_ember2` / `*_veil2` / `*_bulwark2` 之一（若已满一层）。  
3. [ ] Lv60：earned=30；一棵树可花满 30。  
4. [ ] 洗点：二层点退回；日免费/晶钻价不变。  
5. [ ] 满二层 vs 满一层：周本 Boss TTK 变化 <15%（估）；超则回调。

---

## 7. 专岗

| 岗 | 任务 |
|----|------|
| 插件 | 公式 A + YAML 节点 + 菜单/lore |
| 测试 | §1 等级表逐档 + 解锁前置 |
| 怪物 | 4.3 若需抬深渊 Deep 看守 |
| 策划 | 本稿；主线 ch9 引导「去点天赋」 |

