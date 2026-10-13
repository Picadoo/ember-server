# tip · TIP-CS 挂机庭「顺手转化」（真 convert 入口 · ≠第五件花料 · ≠推荐花复读 · ≠洗练/烙纹再拧 · ≠半行 · ≠抬表 · ≠宝石无同构）· 拟 D13324

> **状态：** **待批 A** · 荐 **方案 M** · 拟 D13324 · TIP-CS · docs-only · 2026-10-14 03:17 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-afk-courtyard-quick-convert-2026-10-14.md`](../design/DESIGN-ember-afk-courtyard-quick-convert-2026-10-14.md) · RESEARCH 同日 · backlog `B-afk-courtyard-quick-convert`
> **施工岗（批后）：** 余烬-插件（W1a 返回源）+ 余烬-物品（W1b/c/d TrMenu）· **不派怪物/地图**（默认）
> **硬禁：** 零半行独批 · 零第五件花料 · 零推荐花复读 · 零洗练/烙纹再拧 · 零 P 兼确认 · 零抬日表/改转化价/周帽 · 零 Cast/Aid/todayRot/variety 再拧 · 零 W1e 独批 · 零 sx20/L13/K3 · 零庭内宝石/随机锻本窗主交
> **ancestor：** TIP-CR CLOSED live `fed2cf6aa` · feat `cf35d9948` · tip `ce2f8c95f` · PASS `d9f70ed9a` · jar `1.65.815-d13323.local` · 批 sha `d1004caaf`

## 一句话
挂机庭一点「顺手转化」进既有每周转化页；返回回庭；产胚/币喂改族。

## 为何不是半行
- **主交付：** 插件补 `CONVERT_FROM[afk]` + 新 Icon **C** + `command: corerpg p1 convert from afk`（可点→转化页→可改族）
- Open「本页也可顺手转化」**仅同批辅**，不可独批（否决方案 A）

## ≠ 清单
- ≠ 第五件花料 · ≠ 推荐花复读 · ≠ 洗练再拧 · ≠ 烙纹再拧 · ≠ 四花顺手镜像 · ≠ 半行空转 · ≠ Cast/Aid打磨 · ≠ todayRot/W1e再拧 · ≠ variety 再拧 · ≠ 抬 event_core · ≠ 庭内拆/换复读 · ≠ 导航三冲复读 · ≠ 庭内顺手宝石本窗（同构未过）· ≠ 顺手随机锻本窗主交

## 施工钉死（批后）
- W1a：`CONVERT_FROM.put("afk","ember_p1_afk")` + `convert from/back` + 返回「挂机庭」
- W1b：布局建议用 **C** 替 digit `3`；**C**=顺手转化；lore 挂 gap_convert；`p1 convert from afk`
- W1c：`ember_p1_convert` 返回→`p1 convert back`；工坊 V→`p1 convert from forge`
- W1d：Open/去哪花辅半行
- **不改** convert 周帽/价/族表 · afk.tiers · daily_kills · Cast/Aid/variety · 四花+P+D+W/w+L+G · 洗练/烙纹

## 方案
- **M（荐）** = W1a+W1b+W1c+W1d 全弧（庭内闭环）
- **L** = 仅 TrMenu `menu: ember_p1_convert`（返回落工坊）· **默认不批 L**

## 验收（批后施工）
- [ ] 总控批 A·M
- [ ] 插件交 afk 返回源
- [ ] 物品交 C 格 + convert/forge 返回动态 + Open 辅半行
- [ ] 抽测：庭内进转化→返回庭；价/周帽 零 diff；零 sx20；零半行独档

## 改题摘要（给总控）
- GEM 同构：**庭面未亮 gem gap**（afk I/Open 无 `recipe_gap_gem`）→ 不交宝石
- 短征 Cast/Aid/todayRot/variety 齐 → 不复读
- 采纳：**面亮 `forge_gap_convert` + forge V live + afk 无格 + 无 CONVERT_FROM**

## STATUS: **待批 A** · 荐 **方案 M** · 拟 D13324 · TIP-CS · docs-only · 2026-10-14 03:17 Asia/Shanghai
