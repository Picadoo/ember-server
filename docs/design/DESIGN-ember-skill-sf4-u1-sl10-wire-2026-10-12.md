# DESIGN · 烬斩 SF4 U1_sl10 显式开工接线（T1 · CoreRpg）· 2026-10-12

> **状态：** APPROVED · TIP-BG · **显式开工令** · 已派 **余烬-插件** · 总控 2026-10-12 23:18 Asia/Shanghai  
> **文首硬句：** 本文件 = **显式开工** 施工规格。门禁已清：TIP-BE stage0 APPROVED + TIP-BF `STAGE0_SF4_U1_T0=PASS`。本 tip **是**开工令（BF PASS 本身不是）。**锁参 U1_sl10**；**零改 skill_mult / CD / 几何盒 / 伤乘 / Affix**。  
> **对接 tip：** [`TIP-ember-skill-sf4-u1-sl10-wire-thin-d13016-d13031-2026-10-12.md`](../tips/TIP-ember-skill-sf4-u1-sl10-wire-thin-d13016-d13031-2026-10-12.md)  
> **上游 RESEARCH：** [`RESEARCH-ember-skill-sf4-u1-sl10-wire-2026-10-12.md`](RESEARCH-ember-skill-sf4-u1-sl10-wire-2026-10-12.md)  
> **门禁：** stage0 [`DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md`](DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md) · T0 [`DESIGN-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md`](DESIGN-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md) · RESULT [`RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md`](RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md) · push BF `9ab79c257`  
> **旁注 HOLD：** **L13 施工 HOLD**（TIP-AI FAIL）；Anvil soft densify / topUpTo **仍冻**；灰印/聚火旁债；soft/开孔文案勿再叠；U2/U3 **本窗不打包**  
> **硬禁：** 零 skill_mult · 零伤乘 · 零 Affix densify · 零抬表 · 零 glance twin · 零 sx20 · 零粒子 densify · 零 ForgeChoice densify · 零改 PARRY_CD/flat · 零 Anvil densify · 零 topUpTo · 零开 L13 施工 · 零再扫甲闸 · 零扩 stampable · 零 SetChase 六槽 · 零 soft 再叠 · 零 GemHole/enhance+5 再叠 · 零灰印/聚火夹带 · 零改 CD / FAN·LINE·RING 命中盒

---

## 0. 一句话

总控显式开工后：在烬斩命中且形状门通过时，给目标 **Slow I · 1.0s**（U1_sl10）；不抬伤、不改 CD/几何。

---

## 1. 分阶（本窗 = T1）

| 阶 | 内容 | 状态 |
|----|------|------|
| 0 · 规格 | TIP-BE DESIGN stage0 | ✅ CLOSED |
| T0 · 离线门禁 | TIP-BF G-within PASS | ✅ CLOSED · PASS≠开工 |
| **T1 · 施工** | **本窗 · 显式开工** · CoreRpg 接线 U1_sl10 | **OPEN** |
| T2 · 测/冒烟 | 单测 + 轻冒烟 | 插件窗 |

---

## 2. 参数锁（终表 · 禁拧）

| 键 | 值 | 来源 |
|----|-----|------|
| cand | **U1_sl10** | BF RESULT 主荐 |
| `MARK_TICKS` | **20**（1.0 s） | BF mark_secs=1.0 · 对齐 `EmberSkillKit.SLASH_BURST_SLOW_TICKS` 量级 |
| `SLOW_AMP` | **0**（Slow I） | stage0 弱档 · 离线 slow=0.12 代理 |
| `HIT_LIM` | **3** | 与 D438 burst 非 sync 上限同构；禁抬 |
| ICD | 每次 `landEmberSlash` 成功命中且形状门过 → 至多一次（自然=烬斩充能拍） | stage0；**不**新 CD 字段 |
| 形状门 | 见 §3.1 | stage0「LINE / 套装推荐」 |
| 空挥 | `hit.isEmpty()` → **无**效用 | stage0 |

**禁拧：** `skill_mult` · 1.5B · 烬斩/身法/招架 CD · FAN/LINE/RING 命中盒 · Affix · 伤乘 · U1_sl08 替换主荐 · U2/U3 同窗。

---

## 3. 实现口径（插件可施工）

### 3.1 形状门（纯）

```
boolean shapeOk(String fam, int shapeId):
  if fam == null || fam empty || "none".equals(fam):
    return shapeId == EmberSkillKit.SHAPE_LINE
  return EmberSkillKit.setShapeSynced(fam, shapeId)
```

### 3.2 效用（纯）

对 `hit` 前 `min(hit.size(), HIT_LIM)` 个 `LivingEntity`：

```
addPotionEffect(SLOW, MARK_TICKS, SLOW_AMP, ambient=false, particles=true), true)
```

- **不**写 `AshMark` pct · **不**改 `dealP1` 伤害 · **不**改 `applySlashSetIdentity`  
- 与 D438 burst Slow **同 amp**：Bukkit 刷新时长即可；**禁**为「叠层」抬 amp  
- 可选：成功且至少 1 目标上缓 → ActionBar 一行 `§e烬斩·形缓`（禁 prepend 角色墙 / 禁 TIP-K twin）

### 3.3 接线落点

| 步骤 | 位置 | 动作 |
|------|------|------|
| 1 | 新文件 `CoreRpg/src/main/java/town/sunshine/corerpg/p1/EmberSkillSf4.java` | 常量 + `maybeApply(Player, List<LivingEntity> hit, int shapeId, String fam)` |
| 2 | `SkillService.landEmberSlash` | 在 `applySlashSetIdentity(player, hit);` **之后**、`maybeFlashSlashHit` **之前**调用；取 `activeSet` 作 fam |
| 3 | 单测 `EmberSkillSf4Test` | 形状门真假 · 空 hit · ticks/amp · lim |
| 4 | **不**改 | `EmberSkillFeelSf` 展示公式 · `EmberSkillKit` 几何/`slashLandSlowTicks` · charged 蓄力 Slow · 烬突 · `EmberParry` |

### 3.4 目标分类（可选增强 · 非门禁）

若现网已有稳定 boss/elite 分类 helper 且可单测：优先只对首领/精英施放。  
**缺 helper 时**：对 `hit` 前 lim 施放即可（与 BF 离线 always-on 主行一致）；**禁**为此发明新 Mythic 反射债拖死本窗。

---

## 4. 单测最小包

| 用例 | 期望 |
|------|------|
| fam=burst · shapeId=LINE | shapeOk true |
| fam=burst · shapeId=FAN | shapeOk false |
| fam=scorch · shapeId=FAN | shapeOk true |
| fam=none · shapeId=LINE | shapeOk true |
| fam=none · shapeId=RING | shapeOk false |
| hit empty | maybeApply 不抛 · 无副作用契约（可测返回 false） |
| 常量 | MARK_TICKS==20 · SLOW_AMP==0 · HIT_LIM==3 |

---

## 5. NON 核（`git show` 插件窗必过）

- 无 `skill_mult` / 1.5B / CD 秒数字段改动  
- 无 FAN/LINE/RING `geometryFor` / arc / range / maxTargets 改动  
- 无 Affix / 伤乘表 / STAMP / L11/L13 mods  
- 无 `PARRY_CD` / parry flat · 无灰印 `AshMark.pct` · 无聚火  
- 无 Anvil / soft / GemHole / enhance+5 文案轨  
- 无 TrMenu 必改（本窗 **仅插件**；物品岗不派）

---

## 6. 玩家口径（固定）

- 穿着套装并用**推荐形状**打中敌人时，烬斩会附带 **短缓**（约 1 秒 · 缓速 I）。  
- 没穿套装时，用**直线穿刺**才有这段短缓。  
- 空挥没有；不加快充能、不加重伤害、不改招架。  
- 招架 / 开孔文案 / 强化文案本窗不动。

---

## 7. 对接文件（真 ID）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-skill-sf4-u1-sl10-wire-2026-10-12.md` |
| stage0 | `DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md` |
| T0 / RESULT | `DESIGN-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md` · `RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md` |
| 接线 | `CoreRpg/.../SkillService.java` `landEmberSlash` |
| 形状 SoT | `CoreRpg/.../p1/EmberSkillKit.java` |
| 新 util | `CoreRpg/.../p1/EmberSkillSf4.java`（待建） |
| 展示只读 | `EmberSkillFeelSf` |
| 套装落点只读 | `applySlashSetIdentity` / D438 |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · soft/开孔再叠 |

## STATUS: APPROVED · TIP-BG 显式开工 · U1_sl10 · 已派余烬-插件 · HOLD L13 · 总控 2026-10-12 23:18 Asia/Shanghai
