# tip · TIP-CN 短征 R2 Elite 半血轻添头（真 summon · ≠BossAid终厅复读 · ≠Cast粒子打磨 · ≠半行 · ≠抬表）· 拟 D13319

> **状态：** **勾毕 CLOSED live** · tip `021d8e351` · PASS `17f05e0f1` · live `931274777` · 证据 `/workspace/tmp/tip-cn-elite-aid/` · 2026-10-14 01:53 Asia/Shanghai · 半行 REJECT `d00ed84bc` **保持否决**
> **DESIGN** [`DESIGN-ember-short-r2-elite-aid-readable-2026-10-14.md`](../design/DESIGN-ember-short-r2-elite-aid-readable-2026-10-14.md) · RESEARCH 同日 · backlog `B-short-r2-elite-aid-readable`  
> **施工岗（批后）：** 余烬-怪物 · MM-only · **不派插件/物品/地图**（默认）  
> **硬禁：** 零半行独批 · 零 Cast 粒子主交付 · 零拧 BossAid/Warden · 零 sx20 · 零抬日表/伤表 · 零改既有 Cast/HP/掉落 · 零一次≥2 召唤 · 零庭内再叠顺手

## 一句话
sx01–19 中房 Elite 半血喊「精英添头」并召唤 1 只本本 R1 近战（一场一次）；EliteCast / BossAid 不动。

## 为何不是半行
- **主交付：** 新 Skills `EmberSxNNEliteAid` + Elite **真挂接** → 半血 **summon 1 敌**（房内须先清）  
- 选页/Open 口号 **本窗不做**（半行 REJECT 保持）

## 施工钉死（批后）
- W1a：`EmberDungeonDiffSkills.yml` 增 `EmberSxNNEliteAid` ×19：Conditions `health{h=<60}`（Elite HP120 半血）+ CD9999 + message「精英添头——先清！」+ `summon{mob=<R1>;amount=1;noise=3}` + 轻粒子辅
- R1 键：对齐 BossAid · sx01 Zombie · sx02 Guard · sx03 Patrol · sx04–19 Guard
- W1b：`EmberSxNNElite` 追加挂接（荐 `~onDamaged`；fallback `~onTimer:120`+CD9999 须记 STATUS）
- W1c：全线 sx01–19 同批
- **不改** EliteCast/Guard/Archer/BossCast/BossAid 数值；不改 SoftHit/掉落/日帽/地图/Warden

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 01:37 Asia/Shanghai
- [x] 怪物交 EliteAid 19/19 + Elite 挂接 · tip `021d8e351`
- [x] 抽测半血喊话+1 近战；同场≤1；EliteCast/BossAid 未拧；零 sx20 · 实际挂钩 `~onDamaged` · `health{h=<60}` · 未用 fallback `~onTimer:120` · PASS `17f05e0f1`

## STATUS: **勾毕 CLOSED live** · `021d8e351` · PASS `17f05e0f1` · live `931274777` · D13319 · TIP-CN · 2026-10-14 01:53 Asia/Shanghai
