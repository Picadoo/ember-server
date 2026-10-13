# tip · TIP-CW 短征结算「下一环」菜单化（settle 聊天钮去指令化 · ≠路径追同题 · ≠日差同题 · ≠今日推/周目标复读 · ≠半行 · ≠庭内旁路 · ≠抬表）· 拟 D13328

> **状态：** **已批 A · 方案 M** · W1a/b 齐 · **short_settle_* PAPI 已交** jar `1.65.821-d13328b.local` · **待测岗抽测** · 勿勾 CLOSED · 批 sha `e73f5847e` · 拟 D13328 · TIP-CW · 2026-10-14 07:32 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-short-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-short-settle-next-loop-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-short-settle-next-loop-visible`  
> **施工岗（批后）：** 余烬-插件（settle 下一环改开菜单）+ 余烬-物品（薄页 `ember_p1_short_settle`）· **不派怪物/地图**  
> **硬禁：** 零半行独批 · 零路径追同题 · 零日差同题 · 零今日推/周目标/三冲/D406 复读主交 · 零 Cast 粒子主交付 · 零拧 EliteAid/BossAid · 零 todayRot/W1e 独批 · 零 variety 再拧/完整经济 · 零 sx20 · 零抬日表/伤表/S表/targets.short · 零庭内旁路再拧 · 零 densify · 零教裸指令主 UI · 零改 ShortPath resolve 算法  
> **ancestor：** TIP-CV CLOSED live `f3defd0bb` · feat `c31c780a9` · tip `a206b9893` · PASS `f9e4ca402` · jar `1.65.819-d13327.local` · 批 sha `ac2889160`

## 一句话
短征有奖通关后一点开「下一环」：再进本图、回选页、路径追、去工坊全可点；清裸指令文案；日帽与奖励不动。

## 为何不是半行
- **主交付：** 插件 **改 settle 下一环入口** + 薄页 `ember_p1_short_settle` 真开菜单去向 + 清 `/corerpg p1 shortpath|forge` 教手打  
- Open/半行 **不可独批**（REJECT 保持）

## ≠ 清单
- ≠ 路径追同题 · ≠ 日差同题 · ≠ 今日推复读 · ≠ 本周短征可冲复读 · ≠ 三冲复读 · ≠ D406 首通同屏复读 · ≠ Cast 粒子打磨 · ≠ EliteAid/BossAid 再拧 · ≠ todayRot/W1e 独批 · ≠ variety 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬 event_core · ≠ 完整 variety 经济 · ≠ 教裸指令 · ≠ 改 resolve 算法

## 施工钉死（批后）
- W1a：有奖 settle 下一环→开 `/ember_p1_short_settle`（或等价 open）；清「可改 /corerpg p1 shortpath …」；`spendChaseButtonCmd`→菜单开；todayRot settleCue 对齐不教手打
- W1b：薄页 `ember_p1_short_settle`：再进本图 / 回选页 `ember_p1_short` / 路径追 `ember_p1_short_path` / 工坊 `ember_p1_forge`；灰态诚实
- W1c：同批辅指针若有 → **不可独批**
- **不改**日帽/S40–S58/`weekly_goals.targets.short`/Cast/Aid/variety/庭内菜单/D 今日推/F 本周短征/L 日差/路径追薄页逻辑/ShortPath resolve 算法

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧  
- **L** = 仅改一条聊天钮目标命令无薄页 · **默认不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 04:38 Asia/Shanghai
- [x] 插件交 settle 入口改菜单 + 清裸指令 + 单测 · W1a jar `1.65.820-d13328.local` · feat `d13eabcba`
- [x] 插件补 short_settle_map/name/day_left/day_line + settle remember · jar `1.65.821-d13328b.local` · 单测 EmberShortSettleTest
- [x] 物品交薄 settle 页 + TrMenu reload · `374a142fd` · TrMenu 79
- [ ] 抽测：有奖通关可开下一环；再进/路径/工坊真开；无教手打 shortpath；零抬日帽/targets；零改 Cast/Aid；零路径追同题；零 sx20；零半行独档

## STATUS: **已批 A · 方案 M** · W1a/b + short_settle_* PAPI 齐 · **待测岗抽测后总控收口** · 勿勾 CLOSED · D13328 · TIP-CW · 2026-10-14 07:32 Asia/Shanghai
