# RESEARCH · Ember 孔石参考（2026-10-11）

> **状态：** 研究笔记 · 文档 only · **未改代码**  
> **触发：** 玩家「宝石要想办法弄了」+ 旧 `enhance.yml` 孔石在 P1 下被 `legacy_gate` / 白名单切断  
> **承接：** 设计见 [`DESIGN-ember-p1-socket-gems-2026-10-11.md`](DESIGN-ember-p1-socket-gems-2026-10-11.md)

---

## 1. Diablo（D3 普通宝石）

| 点 | 要点 |
|----|------|
| 角色 | 孔位 = 强 affix；武器孔几乎 BiS 强制 |
| 数值形态 | **flat**（武器 Ruby +伤害）与 **%**（Emerald 暴击伤、头盔 Life%）并存 |
| 风险 | % 乘区（暴击伤）可压过整件 affix；flat 在前期更显眼、后期相对变弱 |
| 可学 | 孔石应是**有意义的选择**，但单孔不应大于同槽主 affix；避免「无孔 = 废装」 |
| 避开 | 把某颗石做成永久伤害乘区（D3 Emerald 类）——与 Ember「签名改打法不改伤乘」冲突 |

**对 Ember：** 锋利石应进公式 **flat A**（小额、硬帽），不要 AttributePlus `phys_damage` 旁路；汲取/疾风走 utility，对齐 D3 头盔「非 DPS 孔」思路。

---

## 2. Path of Exile（技能石 / 辅助石）

| 点 | 要点 |
|----|------|
| 角色 | 辅助石改**行为**（范围、投射、费用、触发），不全是伤乘 |
| 链接 | 孔色/链接是构筑深度；六连易成强制进度 |
| 可学 | 「侧级 / 改手感」比「再叠一个 more」更贴 Ember 签名哲学 |
| 避开 | 全套链接进度墙；P1 不要做 PoE 式多链孔 |

**对 Ember：** 四石保持 **sidegrade**（攻 / 防 / 吸血 / 移速），玩家二选一或三选一，而不是「全镶锋利才对」。

---

## 3. 典型 1.12 私服孔石（SX / AttributePlus / LegendInlay / YeeGem 一类）

| 点 | 要点 |
|----|------|
| 常见形态 | Lore 写入 `物理攻击 +N` / `吸血倍率` / `速度 +%`，由属性插件叠乘 |
| GUI | 放装→点孔→镶/拆；孔位随强化解锁很常见 |
| 风险 | **永久伤乘 + 无帽吸血/移速** → 预算炸穿；与 MythicMobs/多插件叠伤难审计 |
| 可学 | 孔位解锁节奏、四色石心智模型、拆卸成本 |
| 避开 | 直接复开 AP lore 伤；无公式审计的百分比伤 |

**对 Ember：** 旧路正是这类（`EnhanceService` + `StatService` 读 `gem_ember_*`）。P1 必须改道 **EmberItemData + EmberFormula / 受控层**，不能把 `ember_v1_*` 塞进旧 `enhance.yml` 白名单。

---

## 4. Ember 已有资产（库存快照）

### 4.1 旧系统（P1 玩家不可用）

| 资产 | 位置 | 现状 |
|------|------|------|
| 四石 NI | `plugins/NeigeItems/Items/ember-enhance-gems.yml` | `gem_ember_sharp/steady/drain/gale` 仍在 |
| 孔表 | `plugins/CoreRpg/enhance.yml` → `socket` / `gems` / `caps` | 仅 `gear_ember_*` 旧刃/符；**无** `ember_v1_*` |
| 命令 | `/corerpg socket list\|insert\|remove` | `legacy_gate` **拒**（P1 玩家） |
| 结算 | `StatService` 读 lore 孔 → `phys_damage` / `phys_defense` / `life_steal_pct` / `move_speed_pct` | **P1 世界跳过** legacy stats（`EmberMode.isP1`） |
| 粘性路径 | D535 `EmberGemPath` | 只走 `EnhanceService` 空孔；文档已写明 **P1 无 GearLore 孔** |
| 权威结构文 | `DESIGN-ember-gear-structure-2026-10-06.md` | 标明旧镶嵌文档 **P1 下不适用** |

### 4.2 旧四石数值（enhance.yml）

| NI | 效果 | 备注 |
|----|------|------|
| `gem_ember_sharp` | `phys_damage: 2` | flat 攻；相对 T0 `weapon_a=12` 过大（≈17%），相对 T3=70 较小 |
| `gem_ember_steady` | `phys_defense: 2` | flat 防；护符 D 表为 2/6/10/14 |
| `gem_ember_drain` | `life_steal_pct: 0.01` | 帽 `caps.life_steal_pct: 0.05` |
| `gem_ember_gale` | `move_speed_pct: 0.02` | 帽 `caps.move_speed_pct: 0.08` |

刃 2 孔（强化 +0 / +5 解锁）；符 1 孔（+3 解锁）。

### 4.3 P1 公式现状（无孔字段）

```
B = A[tier] × (1 + e + q + f) + 0.2×(L−10)
H / D = charm 表；签名改打法不改伤乘
```

`EmberItemData`：**无** socket 字段。`EmberFormula`：**无** lifesteal / movespeed。移速与普攻吸血目前在 legacy `StatService` 层，P1 不吃。

### 4.4 武器基准 A（ember-v1.yml，便于预算）

| 阶 | A | h | D |
|----|---|---|---|
| T0 | 12 | 20 | 2 |
| T1 | 24 | 50 | 6 |
| T2 | 42 | 95 | 10 |
| T3 | 70 | 165 | 14 |

强化括号（进乘区）：+0…+10 → 0%…60%（加性，非连乘）。

---

## 5. 结论（喂给设计文）

1. **不要**复开旧 `/corerpg socket` 给 P1 装，也不要把 `ember_v1_*` 写入 `enhance.yml` socket 表。  
2. **要**把孔做成 P1 一等公民：数据在 `EmberItemData`，伤/防进公式 flat（或等价格式），吸血/移速进**已有帽**的受控层并进 p1sim。  
3. 四石心智模型可保留（锋/稳/汲/疾），数值必须按阶重标定，避免 T0 被 +2 攻绑架。  
4. 签名 / 套装被动仍是「改打法」；孔石做 **sidegrade + 小 flat**，禁止新永久伤乘区。
