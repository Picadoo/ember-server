# tip · TIP-BZ 短征 R2 精英可读 Cast（≠sx20 · ≠抬表）· D13305

> **状态：** CLOSED live MM · 批 **A · 方案 M** · 怪物 `d39c32bb6` · mm reload 21:34 · 2026-10-13 21:35 Asia/Shanghai  
> **DESIGN** [`DESIGN-ember-short-r2-elite-cast-readable-2026-10-13.md`](../design/DESIGN-ember-short-r2-elite-cast-readable-2026-10-13.md) · RESEARCH 同日 · backlog `B-short-r2-elite-cast-readable`  
> **施工岗：** 余烬-怪物 · MM-only · **不派插件/物品**（W1d 选页半行可选后置）  
> **硬禁：** 零 sx20 · 零抬日表/Elite·Boss 基础伤 · 零改 HP/掉落/日帽/地图 · 禁 Boss Cast 原伤复读到 Elite

## 一句话
sx01–19 第二房精英挂轻压主题 Cast（timer100 · dmg≈0.50–0.55 · 蓄力可读），Boss Cast 不动。

## 施工钉死
- W1a：`EmberDungeonDiffSkills.yml`（或并列）增 `EmberSxNNEliteCast` ×19，主题对齐本本 Boss Cast，粒子限 1.12.2 已用集
- W1b：`EmberSxNNElite` Skills 追加 `- skill{s=EmberSxNNEliteCast} @self ~onTimer:100`；保留 SoftHit
- W1c：全线 sx01–19 同批（非 L 试点）
- 不改 Boss Cast 数值；不改 Type/Equipment/掉落

## 验收
- [x] 总控批 A·M
- [x] 怪物交 tip + push `d39c32bb6` · 19/19 EliteCast
- [x] MM reload 189 mob / 67 skills（+19）；测岗 gameplay 联验中

## STATUS: CLOSED live MM `d39c32bb6` · 测岗 PASS spot `4c902219f` · 2026-10-13 21:35 Asia/Shanghai
