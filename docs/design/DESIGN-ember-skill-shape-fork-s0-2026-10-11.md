# DESIGN · 烬斩形状分叉 S0（技能希望 · F 手感）

> **状态：** 已批 A · 方案 M · 2026-10-11 · Stage1=套装推荐形状（零伤乘）· Stage2 微效用另批
> **来源 tip：** `TIP-ember-skill-feel-shape-fork-s0-2026-10-11.md`
> **硬禁：** skill_mult / 新键 / 伤脉冲 / sticky 作变强

## 1. 缺口

形状（扇/直/环）是几何差；套装落点 D438 多为落地 FX。玩家仍觉 F 光 → 要让 **每套装的 F 默认形状可感知不同**。

## 2. 方案 M（已批）

| Stage | 内容 | 战斗？ |
|-------|------|--------|
| **1（本船）** | 两件套 → 推荐形状：焚扇 / 爆直 / 承环；首成套或 Q04 解锁时软设一次（可随时换）；技能页/解锁行写明 | 否（几何已在 live） |
| 2（未批） | 形状×套装微效用（tip SF1–SF3）须单独 p1sim | 是 |

否决本船：抬 1.5B、焚烬点燃回潮、新 DPS 键。

## 3. 落点

- `EmberSkillKit.preferredShape(family)` → 0/1/2
- `EmberSkillUnlock` / kit 页 / TrMenu：推荐行
- 软设：`maybeSoftPreferShape(player)` 仅当尚未手选过（新 counter `p1_shape_manual`=0）且已解锁形状

## 4. 验收

- 单测 preferredShape 映射
- 无 skill_mult / CD 表 diff
- SEVERE0
