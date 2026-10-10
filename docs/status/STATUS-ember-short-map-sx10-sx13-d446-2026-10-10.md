# 状态 · D446：sx10–sx13 真地图

**裁决：** **已批 A · 批 M · D446** · 2026-10-10  
**编号说明：** D445 已占「成长战斗手感残余」docs-only（`4456677c`）；本号用地图 D446。  
**上游：** D444（sx04–09）

## 交付
| 本 | 结构 | map_version | Cast | settle |
|----|------|-------------|------|--------|
| sx10 | ≥3 密封递闸 | `@d446` | PASS（FreshQ970 重跑） | PASS |
| sx11 | 左右对称双廊 | `@d446` | PASS（FreshQ967） | PASS |
| sx12 | 环枢+≥2侧厢 | `@d446` | PASS（FreshQ968） | PASS |
| sx13 | ≥3 配重衡梁 | `@d446` | PASS（FreshQ969） | PASS |

Cast：HP240 / delay30 / 伤0.85 · bv **88** · 奖励表 **未改**。  
首轮 FreshQ966 因 dp reload 抢跑 SOFT；重跑 PASS=10 FAIL=0。  
首轮汇总 PASS=29 FAIL=0 SOFT=11（含 sx10 抢跑）；有效 Cast+settle：sx10–13 全 PASS。

## 重建
```bash
/tmp/mapvenv/bin/python tools/p1map/d446_build_sx10_sx13_terrain.py
```
地图 `plugins/DungeonPlus/map/ember_short_sx10`–`sx13` gitignore。

## Stage2
≥**2026-10-10 17:40 CST** · 不提前关。清单见 `STATUS-ember-stage2-green-exit-checklist-2026-10-10.md`。
