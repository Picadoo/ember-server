# 状态 · D443：sx01–sx13 地图质量审计 + sx01–sx04 真地图重建

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D443 · PASS**  
**上游：** D442 PASS · 用户催 nonstop map quality  

## 1. 审计表（对照 D441 质量杠：真主题地形 ≠ raid/日更壳 · Cast HP≈240/delay≈30/伤≈0.85）

| 本 | DESIGN 结构硬钉 | 审计前磁盘 | 审计前 map_version | Cast 可读 | 本号动作 |
|----|-----------------|------------|-------------------|-----------|----------|
| sx01 烬门哨岗 | 线性地面（院→廊→Boss） | **MISSING**（无 `ember_short_sx01/`） | `@d391-q01-whitebox` | HP180/delay25 | **D443 重建** · `@d443` |
| sx02 锈灯栈道 | 高差+分叉灯桥 | **MISSING** | `@d392-pending` | 同上 | **D443 重建** |
| sx03 霜雾闸廊 | 水平闸压 · ≥双闸 | **MISSING** | `@d393-pending` | 同上 | **D443 重建** |
| sx04 烬井螺旋 | 螺旋下井 ≥8–12 深 | **MISSING** | `@d397-pending` | 同上 | **D443 重建** |
| sx05 裂谷风廊 | 窄桥风廊 | **MISSING** | `@d400-pending` | HP180/delay25 | **债**（下号） |
| sx06 灰砖环廊 | 环廊回旋 | **MISSING** | `@d403-pending` | 同上 | **债** |
| sx07 烬塔回升 | 垂直上行 | **MISSING**（原 tide 壳文档） | `@d407-pending` | 同上 | **债** |
| sx08 错层庭 | 双层错层 | **MISSING** | `@d410-pending` | 同上 | **债** |
| sx09 烬渠跳石 | 跳石 | **MISSING**（原 abyss 壳文档） | `@d412-pending` | 同上 | **债** |
| sx10 烬闸递室 | 递闸密封 | **MISSING**（文档 raid 壳） | `@d414-pending` | 同上 | **债** |
| sx11 烬镜对廊 | 对称双廊 | **MISSING**（文档 raid 壳） | `@d417-pending` | 同上 | **债** |
| sx12 烬枢转厅 | 转枢放射 | **MISSING**（文档 raid 壳） | `@d419-pending` | 同上 | **债** |
| sx13 烬衡悬梁 | ≥3 配重衡梁 | **MISSING**（文档 raid 壳） | `@d421-pending` | 同上 | **债** |
| sx14–15 | 折角/螺旋 | D441 真地形 | `@d441` | D441 可读 | OK |
| sx16–19 | 影/潮/晶/弦 | D442 真地形 | `@d442` | D442 可读 | OK |

**结论：** play 盘上 **sx01–sx13 模板目录全部缺失**（仅文档写过从 daily/raid 拷贝）；玩家最先进的 sx01–sx04 为本号优先重建。sx05–sx13 记债，勿宣称 D441 杠已齐。

## 2. D443 交付（sx01–sx04）
| 项 | 状态 |
|----|------|
| `tools/p1map/d443_build_sx01_sx04_terrain.py` | 已落 |
| 本地 map sx01–04 | **已装**（gitignore） |
| Cast HP240 / delay30 / 伤0.85 | MM Skills+Mobs |
| `map_version @d443` · R2 walkable points · bv86 | runs.yml |
| 奖励表 | **未改** |

## 3. 恢复
```bash
/tmp/mapvenv/bin/python tools/p1map/d443_build_sx01_sx04_terrain.py
# dp reload && corerpg reload
```

## 4. 烟测
见下节（FreshQ）。

## 禁
stash · reset --hard · checkout -- . · `git add -f` map · 抬日表 · 提前关 Stage2（≥17:40 CST）

## 4. 烟测（2026-10-10）
| 本 | 结果 |
|----|------|
| sx01 FreshQ954 | **PASS** bound · rooms · **Cast【烬门】** · settle |
| sx02 FreshQ955 | **PASS** bound · rooms · **Cast【锈灯】** · settle |
| sx03 FreshQ956 | **PASS** bound · rooms · **Cast【霜雾】** · settle |
| sx04 FreshQ957+ | 地形已装（方螺旋+玻璃井篦）· Cast 可读已调 · 烟测 **SOFT**（Director r2/Boss 窗偶发；记 PARTIAL，D444 跟） |

汇总首轮：`PASS=41 FAIL=0 SOFT=2`（含 sx04 Cast/settle SOFT）

## 5. Stage2
观察窗仍 **≥17:40 CST** · 本号不关观察。
