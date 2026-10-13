# tip · TIP-CU 短征选页「日差本可冲」（日更换本真进本 · ≠今日推复读 · ≠本周短征可冲复读 · ≠半行 · ≠庭内旁路 · ≠抬表）· 拟 D13326

> **状态：** **已批 A · 方案 M** · W1a–c 齐 · feat `74a2b5a01` · tip `b7e9a5317` · jar `1.65.818-d13326.local` · 待测岗 · 拟 D13326 · TIP-CU · 全弧非 L · 勿勾 CLOSED · 2026-10-14 03:57 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-short-day-div-chase-visible-2026-10-14.md`](../design/DESIGN-ember-short-day-div-chase-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-short-day-div-chase-visible`  
> **施工岗（批后）：** 余烬-插件（薄 short_div PAPI + enter `short-div`）+ 余烬-物品（TrMenu L 格）· **不派怪物/地图**  
> **硬禁：** 零半行独批 · 零今日推/周目标/三冲/FC 复读主交 · 零 Cast 粒子主交付 · 零拧 EliteAid/BossAid · 零 todayRot/W1e 独批 · 零 variety 再拧/完整经济 · 零 sx20 · 零抬日表/伤表/S表/targets.short · 零庭内旁路再拧 · 零洗练/烙纹/转化/随机锻再拧 · 零 densify  
> **ancestor：** TIP-CT CLOSED live `ea265a3ed` · feat `2705550cd` · tip `07738f8bb` · PASS `2b24a1647` · jar `1.65.817-d13325.local` · 批 sha `08e77d55d`

## 一句话
短征选页一眼今日有奖不同本进度，一点进「今天还没领过日奖」的另一本；日帽与周目标数不动。

## 为何不是半行
- **主交付：** 插件 **short_div 冲钉** + `enter short-div` + 选页 Icon **L** 真进本  
- Open lore 半行 **不可独批**（REJECT 保持）

## ≠ 清单
- ≠ 今日推复读 · ≠ 本周短征可冲复读 · ≠ 三冲复读 · ≠ FC 路径复读 · ≠ Cast 粒子打磨 · ≠ EliteAid/BossAid 再拧 · ≠ todayRot/W1e 独批 · ≠ variety 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬 event_core · ≠ 完整 variety 经济

## 施工钉死（批后）
- W1a：`short_div` / `_n` / `_line` / `_chase` / `_chase_name` / `_chase_day_line` / `_chase_ok` / `_done`；`enter short-div`（别名 `short-day-div`）；chase=已解锁∧dayLeft>0∧rewardedToday==0；解锁谓词 `EmberShortPath.unlocked`；软目标 3 **仅展示零发奖**
- W1b：`ember_p1_short` Layout `'#PRSJDFL#'`；**L**=日差本可冲；lore 挂进度+chase；真 enter；灰态诚实
- W1c：Open 辅半行「日差本可冲见本页 L」
- **不改**日帽/S40–S58/`weekly_goals.targets.short`/Cast/Aid/variety/庭内菜单/D 今日推/F 本周短征/十九本格/forgeroll·convert 价

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧  
- **L** = 仅 Open 半行无 L/enter · **默认不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 03:52 Asia/Shanghai
- [x] 插件交 short_div* + short-div + 单测 · W1a jar `1.65.818-d13326.local`
- [x] 物品交 L 格 + TrMenu reload · `b7e9a5317` · TrMenu 77
- [ ] 抽测：有未领日奖另一本时可进；进度随有奖不同本变；零抬日帽/targets；零改 Cast/Aid；零 sx20；零半行独档

## STATUS: **已批 A · 方案 M** · W1a–c 齐 · 待测岗抽测 · D13326 · TIP-CU · 全弧非 L · **勿勾 CLOSED** · 2026-10-14 03:57 Asia/Shanghai
