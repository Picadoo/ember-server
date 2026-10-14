# tip · TIP-DA 挑战本结算「下一环」菜单化（settle 聊天钮去指令化 · ≠团本settle下一环同题 · ≠短征settle下一环·回看同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠深渊同窗大扫 · ≠抬掉落/体力价）· 拟 D13332

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13332 · TIP-DA · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改挑战掉落/体力价/成色 · ChallengePath.afterSettle→开 ember_p1_challenge_settle · 清 ConfirmTokens 教裸指令主 UI · 深渊后置 · 2026-10-14 09:02 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-challenge-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-challenge-settle-next-loop-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-challenge-settle-next-loop-visible`  
> **施工岗（批后）：** 余烬-插件（ChallengePath.afterSettle 下一环改开菜单）+ 余烬-物品（薄页 `ember_p1_challenge_settle`）· **不派怪物/地图**  
> **硬禁：** 零半行独批 · 零团本settle下一环同题 · 零短征settle下一环同题 · 零短征settle回看同题 · 零 variety再拧/完整经济 · 零 soft-reward同题 · 零路径追/日差/今日推/周目标/三冲复读主交 · 零 Cast 粒子主交付 · 零拧 EliteAid/BossAid · 零 todayRot/W1e 独批 · 零 sx20 · 零抬日帽/伤表/S表/挑战掉落/体力价/成色 · 零庭内旁路再拧 · 零 densify · 零教裸指令主 UI · 零深渊同窗大扫（§6 后置）  
> **ancestor：** TIP-CZ CLOSED live `dceae5440` · feat `166586585` · tip `f7a3b824c` · PASS `64bfa4265` · jar `1.65.824-d13331.local` · 批 sha `2e12d099e`

## 一句话
挑战结算后一点开「下一环」：再挑战同图、回挑战选图、换硬本路径、回冒险、去工坊、图装追全可点；清裸指令文案；掉落与体力价不动。

## 为何不是半行
- **主交付：** 插件 **改 ChallengePath.afterSettle 下一环入口** + 薄页 `ember_p1_challenge_settle` 真开菜单去向 + 清 `/corerpg p1 enter … challenge|forge|mapfarm` 教手打主路径  
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 团本settle下一环同题（TIP-CZ 已齐）· ≠ 短征settle下一环同题（TIP-CW 已齐）· ≠ 短征settle回看同题（TIP-CY 已齐）· ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ 今日推/周目标/三冲复读 · ≠ Cast 粒子打磨 · ≠ EliteAid/BossAid 再拧 · ≠ todayRot/W1e 独批 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬掉落/体力价 · ≠ 深渊同窗大扫 · ≠ 教裸指令

## 施工钉死（批后）
- W1a：`EmberChallengePath.afterSettle` 下一环→开 `/ember_p1_challenge_settle`（或等价 open）；记住 settle map；清以 ConfirmTokens 教 `/corerpg p1 enter … challenge`/`forge`/`mapfarm` 为主 UI；同批对齐 D671 工坊钮→菜单；单测
- W1b：薄页 `ember_p1_challenge_settle`：再挑战同图（菜单 actions 调既有 enter challenge）/ 挑战选图 `ember_p1_challenge` / 换硬本路径（菜单点 challengepath 别名或薄格）/ 冒险 `ember_p1_adventure` / 工坊 `ember_p1_forge` / 图装追（菜单 actions mapfarm）；图名+路径 lore 诚实；灰态诚实
- W1c：同批辅指针若有 → **不可独批**
- **不改** 挑战掉落 / 体力价 / 成色表 / 日帽 / Cast/Aid/variety / 短征·团本薄页 / 庭内 / soft-reward / ShortPath resolve

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧  
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 09:02 Asia/Shanghai
- [ ] 插件交 ChallengePath 入口改菜单 + 清裸指令主路径 + 单测
- [ ] 物品交薄 challenge settle 页 + TrMenu reload
- [ ] 抽测 PASS live（再挑战/选图/路径/工坊真开·PAPI·零抬掉落/体力价）· **勿勾 CLOSED**

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · L不批 · D13332 · TIP-DA · 2026-10-14 09:02 Asia/Shanghai
