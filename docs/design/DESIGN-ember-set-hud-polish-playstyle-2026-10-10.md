# 余烬 · 成长花样 · 套装 HUD 短闪抛光（D442）

STATUS=**已批 A · 方案 M · D442 · 待施工** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-set-hud-polish-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-set-hud-polish-need-design-2026-10-10.md) · backlog `B-set-hud-polish` · **≠守招 ≠抬日表 ≠关 Stage2（＜17:40）≠sx ≠改套装倍率/频率表**

**上游：** D439 套装触发可读 PASS（jar 1.65.141）· D440 破招短闪 HOLD · D441 sx 真地图并行（本债不抢）。

## 0. 问题

D439 已有触发/将满短闪；残余：短闪与常态套装 HUD 交接、防刷屏闸、粒子/字幕与 ActionBar 一致性，战斗中仍偶发「闪完不知道层数」。

## 1. 方案

| 方案 | 内容 | 裁定 |
|------|------|------|
| **M（荐）** | 抛光 `procFlashUntil` 交还 / 层数 HUD 回挂；可选轻量防抖；**零数值改动** | **主推** |
| A | 只改菜单文案 | 否 |
| L | 改 every / coef / ICD | 否 |
| W | 守招/sx/抬日表/复活聚火 | 否 |

### 批 A

- [x] **方案 M** · 总控 **已批 A · 方案 M · D442** · 2026-10-10 · **可读抛光 · 禁改表**

## 2. 起点

审 `EmberSetService.showHud` / `flashProc` 交接 → 薄改 → jar → FreshQ 冒烟进图。

*D442 · 已批 A·M · 待施工。*
