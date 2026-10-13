# tip · TIP-CM 短征 R3 Boss 半血轻添头（真 summon · ≠Cast粒子打磨 · ≠半行 · ≠抬表）· 拟 D13318

> **状态：** **待批 A** · 荐 **方案 M** · 2026-10-14 01:16 Asia/Shanghai  
> **DESIGN** [`DESIGN-ember-short-r3-boss-aid-readable-2026-10-14.md`](../design/DESIGN-ember-short-r3-boss-aid-readable-2026-10-14.md) · RESEARCH 同日 · backlog `B-short-r3-boss-aid-readable`  
> **施工岗（批后）：** 余烬-怪物 · MM-only · **不派插件/物品/地图**（默认）  
> **硬禁：** 零半行独批 · 零 Cast 粒子主交付 · 零 sx20 · 零抬日表/伤表 · 零改既有 Cast/HP/掉落 · 零一次≥2 召唤 · 零庭内再叠顺手

## 一句话
sx01–19 终厅 Boss 半血喊「添头」并召唤 1 只本本 R1 近战（一场一次）；Cast 波不动。

## 为何不是半行
- **主交付：** 新 Skills `EmberSxNNBossAid` + Warden **真挂接** → 半血 **summon 1 敌**（房内须先清）  
- 选页/Open 口号 **本窗不做**（半行 REJECT 保持）

## 施工钉死（批后）
- W1a：`EmberDungeonDiffSkills.yml`（或并列）增 `EmberSxNNBossAid` ×19：Conditions `healthpercent{percent=<50}` + CD9999 + message + `summon{mob=<R1>;amount=1;noise=3}` + 轻粒子辅
- R1 键：sx01 Zombie · sx02 Guard · sx03 Patrol · sx04–19 Guard
- W1b：`EmberSxNNWarden` 追加挂接（荐 `~onDamaged`；fallback `~onTimer:160`+CD9999 须记 STATUS）
- W1c：全线 sx01–19 同批
- **不改** BossCast/Guard/Archer/Elite Cast 数值；不改 SoftHit/掉落/日帽/地图

## 验收（批后施工）
- [ ] 总控批 A·M
- [ ] 怪物交 BossAid 19/19 + Warden 挂接
- [ ] 抽测半血（或 fallback）喊话+1 近战；同场≤1；Cast 未拧；零 sx20

## STATUS: **待批 A** · 荐 M · 拟 D13318 · TIP-CM · 2026-10-14 01:16 Asia/Shanghai
