# DESIGN · 技能手感 Stage2 微效用 Stage0 · 2026-10-11

> **状态：** Stage0 + **SF1–SF3 SHIP** · 已批 A · 方案 M · tip `TIP-ember-skill-feel-stage2-micro-d1148-2026-10-11.md`  
> **上游：** shape-fork S0 Stage1 live · set-fork D606 · `DESIGN-ember-skill-shape-fork-s0-2026-10-11.md` Stage2 未批条

## 1. 缺口

Stage1 只做推荐形状软设；玩家仍可能觉「形状只是皮」。Stage2 = 形状×套装**可感知微差**，但 **禁 skill_mult**。

## 2. 候选（须另 p1sim 才开码）

| ID | 微效用 | 风险 |
|----|--------|------|
| SF1 | 命中短反馈按形状略异 | **D1153– SHIP** |
| SF2 | 命中数 ×N 展示 | **SHIP** |
| SF3 | 套装落点与形状同屏半行 | **SHIP** |
| SF4 | 微缓/微疗按形状触发窗（战斗） | **高** · 须 p1sim within · **BANNED** |
| SF-miss | 落空形状 ActionBar | **低** · 展示 only · **D1207 SHIP** |
| SF-dashblock | 烬突受阻 ActionBar | **低** · **D1239 SHIP** |
| SF-cdshape | 充能CD带形状短名 | **低** · **D1271 SHIP** |

## 3. 方案 M

1. 下一施工优先 **SF1–SF3（零伤乘）**；SF4 单独 tip+p1sim。  
2. 本窗不改 `EmberSetRules` / skill_mult / CD。  
3. 与工坊 K2 并行文档；不抢 K2 预算窗。

## 4. 批注

- [x] 总控批 A · 方案 M · 2026-10-11
