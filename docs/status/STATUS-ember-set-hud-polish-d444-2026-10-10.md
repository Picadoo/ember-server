# 状态 · D444：套装 HUD 短闪抛光批 A·M · PASS · LIVE

**裁决：** **PASS · 批 A · 方案 M · D444** · 2026-10-10  
**上游：** D439 PASS jar 1.65.141 · D440 HOLD  
**编号：** tip 曾标 D442；地图 D442=sx16–19 已占用 → 本债 **D444**（旧文件 `STATUS-ember-set-hud-polish-d442-2026-10-10.md` 作废指向本页）。  
**版本：** jar **1.65.142-d444.local** · bv 未抬  
**代码：** `129a14f0` · almost-ready 防抖 2.5s · 闪后 `forceHudUntil` 回挂层数 · 将满仅 ActionBar  
**禁改：** EmberSetRules coef/every/ICD  

## 部署

| 项 | 结果 |
|----|------|
| 线上 | **2026-10-10 14:52 CST** · play PID 见 runtime · Enabling `v1.65.142-d444.local` |
| MySQL | CoreRpg + CoreGacha connected · SEVERE **0** |
| 冒烟 | FreshQ966 进 sx01 实例 OK · FreshQ967 上线 OK · PASS=9 FAIL=0 · botd [] |
| 备份 | `/workspace/backup/CoreRpg-1.65.141-d439-pre-d444-deploy-*.jar` |
| 地图烟 | sx04–09 map-smoke PASS=52 FAIL=0 SOFT=6（部署前结束） |

*D444 PASS · LIVE*
