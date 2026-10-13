# tip · TIP-CR 挂机庭「顺手烙纹」（真 brand 入口 · ≠第五件花料 · ≠推荐花复读 · ≠洗练再拧 · ≠半行 · ≠抬表）· 拟 D13323

> **状态：** **勾毕 · CLOSED live** · live `fed2cf6aa` · feat `cf35d9948` · tip `ce2f8c95f` · jar `1.65.815-d13323.local` · PASS `d9f70ed9a` · 批 sha `d1004caaf` · 证据 `/workspace/tmp/tip-cr-afk-brand/` · 宝石仍后置 · 2026-10-14 03:17 Asia/Shanghai · 半行 REJECT `d00ed84bc` **保持否决**
> **DESIGN** [`DESIGN-ember-afk-courtyard-quick-brand-2026-10-14.md`](../design/DESIGN-ember-afk-courtyard-quick-brand-2026-10-14.md) · RESEARCH 同日 · backlog `B-afk-courtyard-quick-brand`  
> **施工岗（批后）：** 余烬-插件（W1a 返回源）+ 余烬-物品（W1b/c/d TrMenu）· **不派怪物/地图**（默认）  
> **硬禁：** 零半行独批 · 零第五件花料 · 零推荐花复读 · 零洗练再拧 · 零 P 兼确认 · 零抬日表/改烙纹价 · 零 Cast/Aid/todayRot/variety 再拧 · 零 W1e 独批 · 零 sx20/L13/K3 · 零庭内宝石本窗主交  
> **ancestor：** TIP-CQ CLOSED live `e099c02b7` · feat `a342f34cf` · tip `6804ed53a` · PASS `16998530c` · jar `1.65.814-d13322.local` · 批 sha `33d94eb40`

## 一句话
挂机庭一点「顺手烙纹」进既有烙纹页；返回回庭；产碎喂烙纹合成/定向。

## 为何不是半行
- **主交付：** 插件补 `BRAND_FROM[afk]` + 新 Icon **G** + `command: corerpg p1 brand from afk`（可点→烙纹页→可合成/定向）  
- Open「本页也可顺手烙纹」**仅同批辅**，不可独批（否决方案 A）

## ≠ 清单
- ≠ 第五件花料 · ≠ 推荐花复读 · ≠ 洗练再拧 · ≠ 四花顺手镜像 · ≠ 半行空转 · ≠ Cast/Aid打磨 · ≠ todayRot/W1e再拧 · ≠ variety 再拧 · ≠ 抬 event_core · ≠ 庭内拆/换复读 · ≠ 导航三冲复读 · ≠ 庭内顺手宝石本窗主交

## 施工钉死（批后）
- W1a：`BRAND_FROM.put("afk","ember_p1_afk")` + `brand from/back` + 返回「挂机庭」
- W1b：布局建议用 **G** 替 digit `2`（或空槽）；**G**=顺手烙纹；lore 挂 gap_brand/gap_pin；`p1 brand from afk`
- W1c：`ember_p1_brand` 返回→`p1 brand back`；工坊 G→`p1 brand from forge`
- W1d：Open/去哪花辅半行
- **不改** brand craftShards/定向价/次数 · afk.tiers · daily_kills · Cast/Aid/variety · 四花+P+D+W/w+L · 洗练

## 方案
- **M（荐）** = W1a+W1b+W1c+W1d 全弧（庭内闭环）  
- **L** = 仅 TrMenu `menu: ember_p1_brand`（返回落工坊）· **默认不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 03:00 Asia/Shanghai
- [x] 插件交 afk 返回源 · `1.65.815-d13323.local` · feat `cf35d9948` · 2026-10-14 03:03
- [x] 物品交 G 格 + brand/forge 返回动态 + Open 辅半行 · `ce2f8c95f` · TrMenu 77
- [x] 抽测：庭内进烙纹→返回庭；价/次数 零 diff；零 sx20；零半行独档 · PASS `d9f70ed9a` · 证据 `/workspace/tmp/tip-cr-afk-brand/`

## STATUS: **勾毕 · CLOSED live** · live `fed2cf6aa` · feat `cf35d9948` · tip `ce2f8c95f` · PASS `d9f70ed9a` · jar `1.65.815-d13323.local` · 批 `d1004caaf` · D13323 · TIP-CR · 宝石仍后置 · 2026-10-14 03:17 Asia/Shanghai
