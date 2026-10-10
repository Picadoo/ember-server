# 状态 · D441：sx14/sx15 真地图 + Cast 可读

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D441 · PASS**  
**上游：** D440 HOLD（薄反馈暂停）；D423/D424 规格在、地图 pending  

## 交付
| 项 | 状态 |
|----|------|
| 地形脚本 `tools/p1map/d441_build_sx14_sx15_terrain.py` | 已落 · 可重跑装盘 |
| 本地 `ember_short_sx14` ≥3 折角裂隙廊 | **磁盘已装**（gitignore） |
| 本地 `ember_short_sx15` 绕心螺旋≥2圈 | **磁盘已装**（gitignore） |
| Cast 可读（HP240 / delay30 / 伤0.85） | MM Skills + Mobs |
| `map_version @d441` · R2 points · bv84 | runs.yml（资源+live） |
| 奖励表 S53/S54 | **未改** |

## 恢复（gitignore map）
```bash
/tmp/mapvenv/bin/python tools/p1map/d441_build_sx14_sx15_terrain.py
# 或：python3 -m venv /tmp/mapvenv && /tmp/mapvenv/bin/pip install nbt anvil-parser
# 然后 dp reload / 重启 play 使模板重载
```
见 `docs/maps/ember_short_sx14-README.md` / `sx15-README.md`。

## 禁
stash · reset --hard · checkout -- . · `git add -f` map · 抬日表 · 改 AFK

## 烟测（2026-10-10 ~13:26 CST）
- FreshQ948 sx14 · FreshQ949 sx15
- **PASS=19 FAIL=0 SOFT=0**
- Cast 可见：`【烬裂】裂扫斩` / `【烬旋】旋扫斩`
- 无 suffocate；bound+settle 双本通过
- Stage2 观察仍 ≥17:40 CST
