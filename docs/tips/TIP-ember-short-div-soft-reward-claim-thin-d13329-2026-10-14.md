# tip · TIP-CX 短征日差软目标极薄有奖（真可点领 NI · ≠日差本同题 · ≠路径追同题 · ≠settle下一环同题 · ≠今日推/周目标复读 · ≠半行 · ≠庭内旁路 · ≠抬表 · ≠写 weekly_goals）· 拟 D13329

> **状态：** **CLOSED live** · feat `1b93bc6b4` · tip `0749d92c7` · jar `1.65.822-d13329.local` · PASS `ed440e0e1` · 批 sha `e469ba35e` · 证据 `/workspace/tmp/tip-cx-short-div-soft/` · 2026-10-14 08:13 Asia/Shanghai · 日差软目标极薄有奖同题勿再交
> **DESIGN** [`DESIGN-ember-short-div-soft-reward-claim-2026-10-14.md`](../design/DESIGN-ember-short-div-soft-reward-claim-2026-10-14.md) · RESEARCH 同日 · backlog `B-short-div-soft-reward-claim`  
> **施工岗（批后）：** 余烬-插件（软目标 claim NI + PAPI）+ 余烬-物品（选页 L 可领态）· **不派怪物/地图**  
> **硬禁：** 零半行独批 · 零日差本同题 · 零路径追同题 · 零 settle下一环同题 · 零今日推/周目标/三冲复读主交 · 零 Cast 粒子主交付 · 零拧 EliteAid/BossAid · 零 todayRot/W1e 独批 · 零 variety 再拧/完整经济 · 零 sx20 · 零抬日表/伤表/S表/targets.short · 零写入 weekly_goals · 零庭内旁路再拧 · 零 densify · 零教裸指令主 UI · 零改 ShortPath resolve / enter short-div 谓词  
> **ancestor：** TIP-CW CLOSED live `71db92dfb` · feat `8c2dc7d95` · tip `374a142fd` · PASS `72e8e6a4e` · jar `1.65.821-d13328b.local` · 批 sha `e73f5847e`

## 一句话
今日有奖不同本满软目标 3 → 一日一次骨尘×2，选页 L 一点领；日帽与周目标不动。

## 为何不是半行
- **主交付：** 插件 **一日一次 NI 真发** + `claim short-div-soft` + 选页 L **可领态真点领**  
- Open/半行 **不可独批**（REJECT 保持）

## ≠ 清单
- ≠ 日差本同题 · ≠ 路径追同题 · ≠ settle下一环同题 · ≠ 今日推复读 · ≠ 本周短征可冲复读 · ≠ 三冲复读 · ≠ Cast 粒子打磨 · ≠ EliteAid/BossAid 再拧 · ≠ todayRot/W1e 独批 · ≠ variety 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬 event_core · ≠ 完整 variety 经济 · ≠ 教裸指令 · ≠ 写 weekly_goals · ≠ 抬 targets.short

## 施工钉死（批后）
- W1a：`n≥3` 且今日未领 → claimable；`claim short-div-soft` 发 `mat_ember_bone_dust`×2；period `p1_short_div_soft_claim`；PAPI `short_div_claim_ok`/`_claimed`/`_claim_line`；单测
- W1b：选页 L 可领态优先于 enter；点领→claim；已领/未满诚实；未满保留 `enter short-div`
- W1c：同批辅指针若有 → **不可独批**
- **经济：** 骨尘×2/日 · 零徽 · 零 event_core · 零写 weekly_goals · 零抬日帽/S/targets
- **不改**日帽/S40–S58/`weekly_goals.targets.short`/Cast/Aid/variety/庭内菜单/D 今日推/F 本周短征/enter short-div 谓词/路径追/settle 薄页/ShortPath resolve

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧  
- **L** = 仅 lore「满 3 有奖」无真发 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 07:53 Asia/Shanghai
- [x] 插件交 claim NI + PAPI + 单测
- [x] 物品交 L 可领态 + TrMenu reload · tip `0749d92c7` · TrMenu 79
- [x] 抽测 PASS live · STATUS-ember-short-div-soft-reward-claim-tip-cx-spot-d13329-2026-10-14.md · PASS `ed440e0e1` · 证据 `/workspace/tmp/tip-cx-short-div-soft/`

## STATUS: **CLOSED live** · `1b93bc6b4` / `0749d92c7` · PASS `ed440e0e1` · jar `1.65.822-d13329.local` · D13329 · TIP-CX · 2026-10-14 08:13 Asia/Shanghai
