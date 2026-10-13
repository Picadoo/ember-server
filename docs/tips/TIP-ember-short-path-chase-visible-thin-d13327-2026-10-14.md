# tip · TIP-CV 短征选页「路径追」可点（ShortPath 菜单化 + enter short-path · ≠日差同题 · ≠今日推/周目标复读 · ≠D406 首通同屏复读 · ≠半行 · ≠庭内旁路 · ≠抬表）· 拟 D13327

> **状态：** **已批 A · 方案 M** · W1b/c 齐 · tip `a206b9893` · TrMenu 78 · 等插件 W1a · 勿勾 CLOSED · 批 sha `ac2889160` · 拟 D13327 · TIP-CV · 2026-10-14 04:24 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-short-path-chase-visible-2026-10-14.md`](../design/DESIGN-ember-short-path-chase-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-short-path-chase-visible`  
> **施工岗（批后）：** 余烬-插件（薄 short_path PAPI + enter `short-path`）+ 余烬-物品（TrMenu 选页 `0` + 薄页 `ember_p1_short_path`）· **不派怪物/地图**  
> **硬禁：** 零半行独批 · 零日差同题 · 零今日推/周目标/三冲/D406 复读主交 · 零 Cast 粒子主交付 · 零拧 EliteAid/BossAid · 零 todayRot/W1e 独批 · 零 variety 再拧/完整经济 · 零 sx20 · 零抬日表/伤表/S表/targets.short · 零庭内旁路再拧 · 零 densify · 零教裸指令主 UI  
> **ancestor：** TIP-CU CLOSED live `3e3b14fd1` · feat `74a2b5a01` · tip `b7e9a5317` · PASS `c206b7b6f` · jar `1.65.818-d13326.local` · 批 sha `52b4ed097`

## 一句话
短征选页一点开「路径追」：点击切换首通追/有奖追/后段追，并一键进当前偏好 resolve 的下一本；日帽与奖励不动。

## 为何不是半行
- **主交付：** 插件 **short_path 冲钉** + `enter short-path` + 选页入口 **`0`** + 薄路径页真切偏好/真进本  
- Open lore 半行 **不可独批**（REJECT 保持）

## ≠ 清单
- ≠ 日差同题 · ≠ 今日推复读 · ≠ 本周短征可冲复读 · ≠ 三冲复读 · ≠ D406 首通同屏复读 · ≠ Cast 粒子打磨 · ≠ EliteAid/BossAid 再拧 · ≠ todayRot/W1e 独批 · ≠ variety 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬 event_core · ≠ 完整 variety 经济 · ≠ 教裸指令

## 施工钉死（批后）
- W1a：`short_path` / `_label` / `_tip` / `_resolve` / `_resolve_name` / `_ok` / `_line`；`enter short-path`（别名 `short-chase-path`）；复用 `EmberShortPath.resolve`；未选→`_ok=0` 拒进
- W1b：`ember_p1_short` Layout 行2空位落 **`0`**→薄页 `ember_p1_short_path`；薄页：fc/day/late/clear +「进下一本」真 enter；灰态诚实
- W1c：Open 辅半行「路径追见本页 0」
- **不改**日帽/S40–S58/`weekly_goals.targets.short`/Cast/Aid/variety/庭内菜单/D 今日推/F 本周短征/L 日差/十九本格/ShortPath resolve 算法

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧  
- **L** = 仅入口刷聊天无薄页切偏好/无 enter · **默认不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 04:22 Asia/Shanghai
- [ ] 插件交 short_path* + short-path + 单测
- [x] 物品交选页 `0` + 薄路径页 + TrMenu reload · `a206b9893` · TrMenu 78
- [ ] 抽测：可切换三偏好；有 resolve 时可进；未选/无 resolve 灰诚实；零抬日帽/targets；零改 Cast/Aid；零日差同题；零 sx20；零半行独档

## STATUS: **已批 A · 方案 M** · W1b/c 齐 · 等插件 · 勿勾 CLOSED · D13327 · TIP-CV · 2026-10-14 04:24 Asia/Shanghai
