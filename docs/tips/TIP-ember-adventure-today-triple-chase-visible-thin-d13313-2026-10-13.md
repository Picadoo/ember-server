# tip · TIP-CH 冒险「今日三冲」聚合（今日推+冲一下+本周短征 · ≠抬表 · ≠sx20）· 拟 D13313

> **状态：** CLOSED live · 批 **A · 方案 M** · jar `1.65.810-d13313.local` · 插件 `ef967a409` · 物品 `76f0a6c5a` · TrMenu reload 77 · PASS STATUS `ce9cd56bb` · 2026-10-14 00:10 Asia/Shanghai · **勾毕**
> **DESIGN** [`DESIGN-ember-adventure-today-triple-chase-visible-2026-10-13.md`](../design/DESIGN-ember-adventure-today-triple-chase-visible-2026-10-13.md) · RESEARCH 同日 · backlog `B-adventure-today-triple-chase-visible`  
> **施工岗（批后）：** 余烬-插件（薄 `today_triple*` + enter `today-triple`）+ 余烬-物品（TrMenu P 格）· **不派怪物/地图**  
> **硬禁：** 零抬日帽/`short:5`/徽/`event_rate`/NEED · 零 sx20/L13/K3 · 零第五件花料 · 零只半行无进本 · 零撤 H/E/F

## 一句话
冒险首页一眼三冲（今日推 / 冲一下 / 本周短征），一点冲当前推荐；日帽与目标数不动。

## 布局钉死
- 冒险第三行空位落 **P**（`'#WsEbPu n'` 或等价；保留 W/s/E/b/u/n）
- Icon **P**：名 `§e今日三冲`；lore 三行复用现键 + 推荐名；`enter today-triple`
- `_ok=0` / 体力不足灰态诚实；**不撤** H / E / 选页 D·F
- W1c：Open「今日三冲见本页 P」
- update:20

## jar 钉死（W1a）
- 优先序：`short-today`（今日推有奖）→ `short-goal`（周目标未满且 chase_ok）→ `event-chase`（chase_ok）→ 空
- `%corerpg_p1_today_triple%` / `_name` / `_kind`（today|week|event|empty）/ `_ok`
- enter `today-triple`（内部转到现有三别名）
- **零改** addGoal / targets / SoftPity / event_rate / 日帽 / 徽

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-13 23:56 Asia/Shanghai
- [x] 插件交 today_triple* + today-triple + 单测 · `1.65.810-d13313.local`
- [x] 物品交 P 格 + TrMenu reload · `76f0a6c5a` · reload 77
- [x] 测岗 PASS STATUS `ce9cd56bb` · kind=today→sx12 · H·E·D·F · 零抬

## STATUS: CLOSED live · jar `1.65.810-d13313.local` · menu `76f0a6c5a` · PASS `ce9cd56bb` · 2026-10-14 00:10 Asia/Shanghai
