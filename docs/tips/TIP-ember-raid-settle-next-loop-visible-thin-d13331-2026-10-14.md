# tip · TIP-CZ 团本结算「下一环」菜单化（settle 聊天钮去指令化 · ≠短征settle下一环同题 · ≠短征settle回看同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬周帽）· 拟 D13331

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13331 · TIP-CZ · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改 weeklyCap/loot · RaidLoop afterWeeklyClear→开 ember_p1_raid_settle · 清 ConfirmTokens 教裸指令主 UI · 2026-10-14 08:41 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-raid-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-raid-settle-next-loop-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-raid-settle-next-loop-visible`  
> **施工岗（批后）：** 余烬-插件（RaidLoop 下一环改开菜单）+ 余烬-物品（薄页 `ember_p1_raid_settle`）· **不派怪物/地图**  
> **硬禁：** 零半行独批 · 零短征settle下一环同题 · 零短征settle回看同题 · 零 variety再拧/完整经济 · 零 soft-reward同题 · 零路径追/日差/今日推/周目标/三冲复读主交 · 零 Cast 粒子主交付 · 零拧 EliteAid/BossAid · 零 todayRot/W1e 独批 · 零 sx20 · 零抬周帽/伤表/S表/weeklyCap/loot · 零庭内旁路再拧 · 零 densify · 零教裸指令主 UI · 零挑战/深渊同窗大扫（§6 后置）  
> **ancestor：** TIP-CY CLOSED live `bdf88e272` · feat `e5cb2ddd6` · tip `a1f5a2537` · PASS `176e2fe03` · jar `1.65.823-d13330.local` · 批 sha `afeb9a210`

## 一句话
团本周清后一点开「下一环」：再进路径团本、选/换路径、回冒险、去工坊、招募全可点；清裸指令文案；周帽与掉落不动。

## 为何不是半行
- **主交付：** 插件 **改 RaidLoop 下一环入口** + 薄页 `ember_p1_raid_settle` 真开菜单去向 + 清 `/corerpg p1 raidpath|recruit|enter` 教手打主路径  
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 短征settle下一环同题（TIP-CW 已齐）· ≠ 短征settle回看同题（TIP-CY 已齐）· ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ 今日推/周目标/三冲复读 · ≠ Cast 粒子打磨 · ≠ EliteAid/BossAid 再拧 · ≠ todayRot/W1e 独批 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬 weeklyCap/loot · ≠ 挑战/深渊同窗大扫 · ≠ 教裸指令

## 施工钉死（批后）
- W1a：`EmberRaidLoop.afterWeeklyClear` 下一环→开 `/ember_p1_raid_settle`（或等价 open）；额度满/未满两态皆进薄页；清以 ConfirmTokens 教 `/corerpg p1 raidpath|recruit` 为主 UI；单测
- W1b：薄页 `ember_p1_raid_settle`：再进路径团本（菜单 actions 调既有 enter）/ 选·换路径（菜单点 `raidpath` 别名或薄路径格）/ 冒险页 `ember_p1_adventure` / 工坊 `ember_p1_forge` / 招募（菜单 actions）；周次 lore 诚实；灰态诚实
- W1c：同批辅指针若有 → **不可独批**
- **不改** weeklyCap / loot / 体力价 / 团本数值 / Cast/Aid/variety / 短征薄页 / 庭内 / soft-reward / ShortPath resolve

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧  
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 08:41 Asia/Shanghai
- [ ] 插件交 RaidLoop 入口改菜单 + 清裸指令主路径 + 单测
- [ ] 物品交薄 raid settle 页 + TrMenu reload
- [ ] 抽测：周清后可开下一环；再进/路径/冒险/工坊真开；无教手打 raidpath 主 UI；零抬周帽/loot；零短征同题复读；零 sx20；零半行独档

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · L不批 · D13331 · TIP-CZ · 2026-10-14 08:41 Asia/Shanghai
