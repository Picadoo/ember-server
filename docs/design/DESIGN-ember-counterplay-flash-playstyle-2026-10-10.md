# 余烬 · 成长花样 · 破招/破绽成功短闪（D440 → D448）

STATUS=**已批 A · 方案 M · D448 · 施工 PASS · jar 1.65.144-d448** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-counterplay-flash-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-counterplay-flash-need-design-2026-10-10.md) · backlog `B-counterplay-flash` · **≠守招 ≠抬日表 ≠关 Stage2（＜17:40）≠sx ≠改 boss 招 wall_stun/whiff_stun/break_hp/dmg ≠改 EmberSetRules/skill_mult/CD/AFK/聚火/K3**

**上游：** D439 套装触发可读 PASS · D440 **HOLD**（地图未优化）· D441–D447 真地图/审计 **PASS** → **解 HOLD 为 D448**。

## 0. 问题

破招 / 落空破绽 / 撞墙破绽成功时反馈偏弱，玩家难确认「打对了」。

## 1. 方案

| 方案 | 内容 | 裁定 |
|------|------|------|
| **M（荐）** | 成功瞬间 ActionBar/字幕短闪（沿 D439 `flash` 模式）；**零改招表**；每次成功 ActionBar（防抖 ≥0.8s）；首次仍用 D283 tip（ActionBar+字幕） | **主推 · 已批 · 已施工** |
| A | 只改教程文案 | 否 |
| L | 改 break_hp / stun 时长 | 否 |
| W | 守招/sx/抬日表 | 否 |

### 批 A

- [x] **方案 M** · 总控 **已批 A · 方案 M · D440** · 2026-10-10 · **可读优先 · 禁改招表**
- [x] **D448 解 HOLD** · 地图 D441–D447 PASS · jar **1.65.144-d448.local**

## 2. 起点 / 施工

`EmberCounterplay.successFlash` / `shouldFlashSuccess`（800ms）+ `EmberRunDirector.flashCounterplaySuccess` → wall/whiff/break 三分支。

## 3. 验收

- 单测 `successFlash_D448_shortVerbs` + `shouldFlashSuccess_D448_debounce`
- 冒烟 FreshQ973 进 Q01 · MySQL×2 · SEVERE0
- 招表 / EmberSetRules / skill_mult / AFK / bv **零改**

*D448 · 施工 PASS · jar 1.65.144-d448 · D440 HOLD 已解。*
