# tip · TIP-CF 短征 R1 近战卫可读轻 Cast（≠sx20 · ≠抬表）· 拟 D13311

> **状态：** 待批 A · 荐 **方案 M** · docs-only · 2026-10-13 23:10 Asia/Shanghai  
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
- [ ] 总控批 A·M
- [ ] 怪物交 tip + GuardCast 19/19（或批 L 则 01–06）
- [ ] 抽测：R1 有蓄力；同本 Guard < Elite < Boss；远程无 Cast；零抬表

## STATUS: 待批 A · 荐方案 M · 拟 D13311 · TIP-CF · 2026-10-13 23:10 Asia/Shanghai
