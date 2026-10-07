# 状态 · D315：六槽护甲追赶 总控批 F → 另号 T0″（docs-only）

**日期：** 2026-10-08（上海时间）  
**上游：** D314 回 HOLD（[`STATUS-ember-six-slot-stage1-hold-d314.md`](STATUS-ember-six-slot-stage1-hold-d314.md)）→ 策划交稿 [`DESIGN-ember-six-slot-armor-catchup-2026-10-08.md`](../design/DESIGN-ember-six-slot-armor-catchup-2026-10-08.md)（`ea7671f2`）  
**版本：** CoreRpg **1.65.97** 未动 · bv **60** 未动 · **零 Java / yml / NI / TrMenu / 部署** · p1sim `SIX` 默认仍 None

## 人话

团本那段时间护甲跟不上护符，策划给的办法是「护甲共鸣」：护甲的阶级和强化直接跟着护符走，护甲自己只管成色、精工和族。总控批了这个方向，但只放行下一步离线复算（T0″，D316），同时跑「只跟强化」的 E 方案作对照。线上什么都不变。

## 总控批注

> 批 F → 另号 T0″（总控 2026-10-08）。E 作对照臂。D169 Stage1「每件独立阶级 / 强化」的修订只暂认，以 T0″ 结果为准。F 不过而 E 过 → 总控另选；两者都不过 → 六槽维持 HOLD。线上零变化；CoreRpg 1.65.97、bv 60 不动；不开任何 R；T1–T3 不授权。

## T0″（D316）范围

| 项 | 内容 |
|---|---|
| 臂 | 2 槽基线 · F（`follow:"all"`，护符 ×1.0）· E（`follow:"enh"`，护符强化 ×1.0、升阶 ×0.85）· T0' A 复现（应 3/9） |
| 门禁 | G0–G9（G8 团本 9/9 必过：点估 ±2pp 或 CI 伸进 ±2）+ 新 G10 团本池逐人落后分布 |
| 工具 | 设计稿 §2.2 最小改动，全部可选开启：`SIX` 默认 None、G0 逐位不变 |

## 本号改动（只 docs）

| 文件 | 改动 |
|---|---|
| `docs/design/DESIGN-ember-six-slot-armor-catchup-2026-10-08.md` | STATUS → 已批 F；§4 勾「批 F → 另号 T0″」+ 总控批注；§5 变更记录；文末 |
| `docs/design/design-ember-content-backlog.md` | `B-six-slot-armor-catchup` 指针 → 已批 F · 下一步 T0″（D316） |
| 本文件 | 新增 |

## 不动

任一 R 窗 · D308 表（当前全表不得开）· Pack6 · 天赋 row1 / 灰印 HOLD · D309 副手方案 A · 刃 / 护符掉率 · 价表 · event_rate · 体力 · 怪物数值 · 团本产出 · T1–T3（不授权）
