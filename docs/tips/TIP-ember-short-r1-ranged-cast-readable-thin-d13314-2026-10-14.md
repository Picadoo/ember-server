# tip · TIP-CI 短征 R1 远程卫可读轻 Cast（≠sx20 · ≠抬表）· 拟 D13314

> **状态：** 打开 · 待批 **A · 方案 M** · docs-only · 2026-10-14 Asia/Shanghai  
> **DESIGN** [`DESIGN-ember-short-r1-ranged-cast-readable-2026-10-14.md`](../design/DESIGN-ember-short-r1-ranged-cast-readable-2026-10-14.md) · RESEARCH 同日 · backlog `B-short-r1-ranged-cast-readable`  
> **施工岗（批后）：** 余烬-怪物 · MM-only · **不派插件/物品**（W1d 选页半行可选后置）  
> **硬禁：** 零 sx20 · 零抬日表/伤表 · 零改 HP/掉落/日帽/地图 · 禁 Guard/Elite/Boss 原伤复读到 Archer · 禁与近战同 Timer 双压 · 禁第五件花料

## 一句话
sx01–19 第一房远程卫挂更轻更慢主题 Cast（timer180 · dmg≈0.22–0.28 · 蓄力可读）；近战 GuardCast / Elite / Boss 不动。

## 施工钉死
- W1a：`EmberDungeonDiffSkills.yml`（或并列）增 `EmberSxNNArcherCast` ×19，主题对齐本本 Boss/Elite/Guard，粒子限 1.12.2 已用集
- W1b：sx01 `EmberSx01Skeleton`、sx02 `EmberSx02Archer`、sx03–19 `EmberSxNNCrossbow` Skills 追加 `- skill{s=EmberSxNNArcherCast} @self ~onTimer:180`；保留 SoftHit
- W1c：全线 sx01–19 同批（非 L 试点则全线）
- 不改近战 GuardCast；不改 Elite/Boss Cast 数值；不改 Type/Equipment/掉落

## 验收（批后施工）
- [ ] 总控批 A·M
- [ ] 怪物交 ArcherCast 19/19 + 远程挂接
- [ ] 抽测：≥3 本见蓄力；0.22–0.28 < Guard 0.32 < Elite 0.52 < Boss 0.85；近战 Timer 仍 140

## STATUS: 打开 · 待批 A · 荐 M · 拟 D13314 · TIP-CI · 2026-10-14 Asia/Shanghai
