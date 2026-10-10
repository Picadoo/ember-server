# 状态 · D444：套装 HUD 短闪抛光批 A·M · PASS

**裁决：** **PASS · 批 A · 方案 M · D444** · 2026-10-10  
**上游：** D439 PASS jar 1.65.141 · D440 HOLD  
**编号：** tip 曾标 D442；地图 D442=sx16–19 已占用 → 本债 **D444**（旧文件 `STATUS-ember-set-hud-polish-d442-2026-10-10.md` 作废指向本页）。  
**禁：** 改套装倍率/频率 · 守招 · 抬日表 · Stage2＜17:40 · sx

## 施工

| 项 | 结果 |
|----|------|
| Almost-ready debounce | `shouldFlashAlmost` · 2.5s · Session 字段 |
| Post-proc forceHud | `forceHudUntil` 2s · `EmberSetEngine.hud(now, forceIdle)` |
| 将满无字幕 | `flashProc(..., false)` |
| EmberSetRules | **未改** |
| 版本 | CoreRpg **1.65.142-d444.local** |
| 单测 | EmberSetEngineTest **21/0/0** |

**jar：** 已构建 `/workspace/backup/CoreRpg-1.65.142-d444.local-built.jar` · **尚未装入 live**（地图窗 bot 占用；live 仍 1.65.141-d439）。

*D444 PASS（代码+单测+文档；部署待 bot 空窗）*
