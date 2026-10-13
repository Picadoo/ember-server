# tip · TIP-CT 挂机庭「顺手随机锻」（真 forgeroll 入口 · ≠第五件花料 · ≠推荐花复读 · ≠洗练/烙纹/转化再拧 · ≠半行 · ≠抬表 · ≠宝石无同构 · ≠densify）· 拟 D13325

> **状态：** **抽测 PASS** · W1a–d 齐 · jar `1.65.817-d13325.local` · D13325 · TIP-CT · 全弧非 L · **勿勾 CLOSED**（总控收口）· 2026-10-14 03:44 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-afk-courtyard-quick-forgeroll-2026-10-14.md`](../design/DESIGN-ember-afk-courtyard-quick-forgeroll-2026-10-14.md) · RESEARCH 同日 · backlog `B-afk-courtyard-quick-forgeroll`
> **施工岗（批后）：** 余烬-插件（W1a 返回源）+ 余烬-物品（W1b/c/d TrMenu）· **不派怪物/地图**（默认）
> **硬禁：** 零半行独批 · 零第五件花料 · 零推荐花复读 · 零洗练/烙纹/转化再拧 · 零 P 兼确认 · 零抬日表/改 forgeroll 价权重 · 零 Cast/Aid/todayRot/variety 再拧 · 零 W1e 独批 · 零 sx20/L13/K3 · 零庭内宝石本窗主交 · 零 densify
> **ancestor：** TIP-CS CLOSED live `00fae3f34` · feat `0eb85b785` · tip `b5aea9bc5` · PASS `372360684` · jar `1.65.816-d13324.local` · 批 sha `3ab6a4792`

## 一句话
挂机庭一点「顺手随机锻」进既有旁路随机锻页；返回回庭；产印记/胚/币喂随机锻（含下限）。

## 为何不是半行
- **主交付：** 插件补 `FORGEROLL_FROM[afk]` + 新 Icon **O** + `command: corerpg p1 forgeroll from afk`（可点→随机锻页→可点族/槽/阶/下限）
- Open「本页也可顺手随机锻」**仅同批辅**，不可独批（否决方案 A）

## ≠ 清单
- ≠ 第五件花料 · ≠ 推荐花复读 · ≠ 洗练再拧 · ≠ 烙纹再拧 · ≠ 转化再拧 · ≠ 四花顺手镜像 · ≠ 半行空转 · ≠ Cast/Aid打磨 · ≠ todayRot/W1e再拧 · ≠ variety 再拧 · ≠ 抬 event_core · ≠ 庭内拆/换复读 · ≠ 导航三冲复读 · ≠ 庭内顺手宝石本窗（同构未过）· ≠ forgeroll densify

## 施工钉死（批后）
- W1a：`FORGEROLL_FROM.put("afk","ember_p1_afk")` + `forgeroll from/back`（openMenu；保留既有 `<族>…`）+ 返回「挂机庭」
- W1b：布局建议用 **O** 替 digit `4`；**O**=顺手随机锻；lore 挂 gap_roll/gap_roll_floor；`p1 forgeroll from afk`
- W1c：`ember_p1_forgeroll` 返回→`p1 forgeroll back`；工坊 F→`p1 forgeroll from forge`
- W1d：Open/去哪花辅半行
- **不改** forgeroll 印记/胚/骨/币/floor 价 · QUALITY_W · afk.tiers · daily_kills · Cast/Aid/variety · 四花+P+D+W/w+L+G+C · 洗练/烙纹/转化 · convert 周帽/价

## 方案
- **M（荐）** = W1a+W1b+W1c+W1d 全弧（庭内闭环）
- **L** = 仅 TrMenu `menu: ember_p1_forgeroll`（返回落工坊）· **默认不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 03:35 Asia/Shanghai
- [x] 插件交 afk 返回源 · jar 1.65.817-d13325.local
- [x] 物品交 O 格 + forgeroll/forge 返回动态 + Open 辅半行 · `07738f8bb` · forgeroll_back PAPI · TrMenu 77
- [x] 抽测 PASS · feat `2705550cd` · tip `07738f8bb` · STATUS spot · jar `1.65.817-d13325.local` · 庭内 O→forgeroll from afk·返回挂机庭；工坊 F from forge·返回工坊 · **勿勾 CLOSED** · 2026-10-14 03:44 Asia/Shanghai

## 改题摘要（给总控）
- GEM 同构：**庭面仍未亮 gem gap** → 不交宝石
- 短征 Cast/Aid/todayRot/variety 齐 → 不复读
- 采纳：**D13324§6③ 同构四项齐**（面亮 `forge_gap_roll` + afk 无格 + forge F live + 无 FORGEROLL_FROM）

## STATUS: **抽测 PASS** · feat `2705550cd` · tip `07738f8bb` · jar `1.65.817-d13325.local` · D13325 · TIP-CT · 全弧非 L · **勿勾 CLOSED**（总控收口）· 2026-10-14 03:44 Asia/Shanghai
