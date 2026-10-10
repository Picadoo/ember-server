# RESEARCH · 非按钮真实成长环 · 2026-10-11

> 停 ConfirmTokens 同表面重排。对照 ARPG / 挂机服 / 现网 Ember。

## 候选评估

| 候选 | 现网 | 裁决 |
|------|------|------|
| 技能 set-fork 深化（无 skill_mult） | 几何分叉 D642 + 套装软推形状 D607 + SF1–3/SF-K | **NEAR-SAT**；SF4 BANNED |
| 天赋 trade-off 体感 | 一排条件型 HOLD（须 sidegrade 过线） | **HOLD** · 禁改系数 |
| 签名行为玩（无伤乘） | SigFeel 短闪 D455 + 调律路径 | **NEAR-SAT**；禁 sig dry twin |
| **主线图解锁非通关门** | 图身份 have/need 仅展示；技能解锁仍绑首通 | **OPEN → 本轴** |
| 挂机装效驱动手感（不抬表） | 真普攻+真烬斩；GearEff 仅展示 | 装效已进战斗；再改 spawn/regen = 变相抬表 → **不做** |

## 参考游戏

| 来源 | 学 |
|------|-----|
| PoE Atlas / D4 Renown | 区域身份凑齐 → 解锁路径偏好，不锁清图 |
| 挂机服 | 图鉴/收集齐 → 自动定「本周主养」减少迷茫 |
| Ember D607 | 成套软推烬斩形状（无伤乘）— 同模式可迁到套装焦点 |

## 方案钉（本轴）

**图身份图鉴齐（have≥need）且玩家未设套装焦点 → 软定 `SetFocus` 为该图 `lootFamily`。**

- 真改路径：成套追 / mapfarm 提示立刻对齐
- **不**锁通关、**不**改掉率、**不** ConfirmTokens
- 每族 latch 一次软推；玩家可随时 `/corerpg p1 setfocus` 改/清
- 已有焦点 → 不覆盖（尊重承诺）

## 禁

K3/SF4/EM1-B/pity/AFK raise/sx20/legacy socket/sticky/skill_mult/gem flat/clear-gates/drop/hope celebration/sig dry/ConfirmTokens spam
