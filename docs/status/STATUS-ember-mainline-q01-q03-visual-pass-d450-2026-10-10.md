# 状态 · D450：Q01–Q03 轻视觉点缀（主线视觉收束）

**裁决：** **已批 A · 方案 M · D450 · PASS** · 2026-10-10  
**上游：** D449 Q04–Q07 视觉 · D447 审计 · tip「锻造成色」误开 → 关（重叠 D430）  
**bv：** **未抬** · 奖励表 / Boss HP·dmg / EmberSetRules / skill_mult **未改** · **≠** Stage2 / AFK / sx20 / 聚火 / K3

## 交付
| 项 | 内容 |
|----|------|
| q01 courtyard | 橡木原木 / 石砖柱 + 荧石帽 / 绿·黄羊毛饰带 |
| q02 ash | 地狱岩 / 红地狱砖柱 + 荧石·红石灯帽 / 红羊毛饰 |
| q03 crypt | 石砖 / 圆石柱（按 y59/y55 分层）+ 灵魂沙饰 / 铁栅 · 荧石帽 |
| map_version | `@d450`（v1 / ash_v1 / crypt_v1） |
| tip | 锻造成色 tip **关闭**（重叠 D430）；D450 号改派本债 |

重建：`/tmp/mapvenv/bin/python tools/p1map/d450_q01_q03_visual_pass.py`（叠饰，不重铺地板；落座前采样实心脚下）

## 验收
safe 点脚下实心复验 OK（q01/q02 y≈64；q03 含 y60/y56）。

## Stage2
≥17:40 CST 绿出；本号 ≠ 关观察。既有 tip `STATUS-ember-next-hard-debt-stage2-green-exit-prep-after-d448-need-design-2026-10-10.md`。

## 烟雾（FreshQ985–987）
PASS=7 FAIL=0 SOFT=14（bound 日志措辞 / 站立正则对多行 JSON `"y": N` 误判，同 D449）。
人工复核：q01/q02 均 y=64；q03 y=64/60/56（rb 落点 y=54 台阶，非虚空）。**无坠虚空灾难 FAIL。**
dp reload 导入 ember_daily_v1 / ash_v1 / crypt_v1 OK · MySQL connected · 本号无新 SEVERE。
