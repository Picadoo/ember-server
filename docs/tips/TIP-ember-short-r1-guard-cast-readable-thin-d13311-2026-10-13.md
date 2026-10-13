# tip · TIP-CF 短征 R1 近战卫可读轻 Cast（≠sx20 · ≠抬表）· 拟 D13311

> **状态：** CLOSED live · 批 **A · 方案 M** · tip `1c90911ef` · PASS `9322cf4bc` · mm reload · 2026-10-13 23:22 Asia/Shanghai · **勾毕**  
> **DESIGN** [`DESIGN-ember-short-r1-guard-cast-readable-2026-10-13.md`](../design/DESIGN-ember-short-r1-guard-cast-readable-2026-10-13.md) · RESEARCH 同日 · backlog `B-short-r1-guard-cast-readable`  
> **施工岗（批后）：** 余烬-怪物 · MM-only · **不派插件/物品**（W1d 选页半行可选后置）  
> **硬禁：** 零 sx20 · 零抬日表/Guard·Elite·Boss 基础伤 · 零改 HP/掉落/日帽/地图 · 禁 Elite/Boss Cast 原伤复读到 Guard · 禁 R1 远程同挂

## 一句话
sx01–19 第一房近战卫挂更轻更慢主题 Cast（timer140 · dmg≈0.30–0.35 · 蓄力可读）；Elite/Boss Cast 不动；远程不挂。

## 施工钉死
- W1a：`EmberDungeonDiffSkills.yml`（或并列）增 `EmberSxNNGuardCast` ×19，主题对齐本本 Boss/Elite，粒子限 1.12.2 已用集
- W1b：sx01 `EmberSx01Zombie`、sx02–19 `EmberSxNNGuard` Skills 追加 `- skill{s=EmberSxNNGuardCast} @self ~onTimer:140`；保留 SoftHit
- W1c：全线 sx01–19 同批（非 L 试点则全线）
- 不改 Elite/Boss Cast 数值；不改 Crossbow/Skeleton；不改 Type/Equipment/掉落

## 验收（批后施工）
- [x] 总控批 A·M
- [x] 怪物交 tip `1c90911ef` + GuardCast 19/19
- [x] 抽测 PASS `9322cf4bc` · sx01/07/13/19 · 0.32<0.52<0.85 · 远程无挂

## STATUS: CLOSED live · tip `1c90911ef` · PASS `9322cf4bc` · 2026-10-13 23:22 Asia/Shanghai
