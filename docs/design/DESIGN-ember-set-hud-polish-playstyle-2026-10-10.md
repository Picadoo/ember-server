# 余烬 · 成长花样 · 套装 HUD 短闪抛光（D444）

STATUS=**PASS · 方案 M · D444** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-set-hud-polish-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-set-hud-polish-need-design-2026-10-10.md) · backlog `B-set-hud-polish` · **≠守招 ≠抬日表 ≠关 Stage2 ≠sx ≠改套装倍率/频率表**

**上游：** D439 套装触发可读 PASS（jar 1.65.141）· D440 破招短闪 HOLD · **编号更正：** tip 曾写 D442，但地图 D442（sx16–19）已占用 → 本债为 **D444**。

## 0. 问题

D439 已有触发/将满短闪；残余：短闪与常态套装 HUD 交接、防刷屏闸、粒子/字幕与 ActionBar 一致性，战斗中仍偶发「闪完不知道层数」。

## 1. 方案

| 方案 | 内容 | 裁定 |
|------|------|------|
| **M（荐）** | 抛光 `procFlashUntil` 交还 / 层数 HUD 回挂；可选轻量防抖；**零数值改动** | **主推 · 已施工** |
| A | 只改菜单文案 | 否 |
| L | 改 every / coef / ICD | 否 |
| W | 守招/sx/抬日表/复活聚火 | 否 |

### 批 A

- [x] **方案 M** · 总控 **已批 A · 方案 M · D444**（原 tip 误标 D442）· 2026-10-10 · **可读抛光 · 禁改表**

## 2. 施工（薄）

1. **Almost-ready debounce**：`Session.lastAlmostCounter` + `lastAlmostAt`；同层数 ~2.5s 内不重复闪；离开 almost-ready 后清 debounce。
2. **Post-proc HUD handback**：闪完设 `forceHudUntil` ~2s；`showHud` 用 `engine.hud(now, force)`，idle `0/every` 仍显示族名+层数（及 CD/燃烧后缀）。
3. **字幕减噪**：将满只 ActionBar；真触发仍 ActionBar+字幕。
4. **零** `EmberSetRules` coef/every/ICD 改动。

## 3. 验收

- 单测：`almostReadyDebounce_oncePerApproach_D444` · `forcedIdleHudLine_afterProc_D444` · 保留 `almostReadyCounterIsEveryMinusOne_reasonCount_D439`
- jar **1.65.142-d444.local** · FreshQ959+ 冒烟

*D444 · 方案 M · PASS（施工）。*
