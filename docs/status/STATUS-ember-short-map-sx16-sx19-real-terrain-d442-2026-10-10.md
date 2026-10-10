# 状态 · D442：sx16–sx19 真地图 + Cast 可读

**裁决：** **已批 A · 批 M · D442 · PASS** · 2026-10-10  
**上游：** D441 PASS  

## 交付
| 项 | 状态 |
|----|------|
| 地形脚本 `d442_build_sx16_sx19_terrain.py` | 已落 |
| 本地 map sx16–19 | **磁盘已装**（gitignore） |
| Cast HP240 / delay30 / 伤0.85 | MM Skills+Mobs |
| `map_version @d442` · R2 points · bv85 | runs.yml |
| 奖励表 S55–S58 | **未改** |

## 恢复
```bash
/tmp/mapvenv/bin/python tools/p1map/d442_build_sx16_sx19_terrain.py
# dp reload
```

## 烟测（2026-10-10 ~13:51 CST）
- FreshQ950–953 · **PASS=41 FAIL=0 SOFT=1**（sx16 bound 日志竞态 SOFT；仍 stay+settle）
- Cast 全本可见：影/潮/晶/弦扫斩
- Stage2 观察仍 ≥17:40 CST
