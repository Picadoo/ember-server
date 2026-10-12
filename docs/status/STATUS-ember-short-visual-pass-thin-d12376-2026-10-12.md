# 状态 · TIP-O / D12376–D12391：短征 sx01–sx04 轻视觉叠饰

**裁决：** **已批 A · 方案 M · PASS · live** · 2026-10-12 16:45 CST  
**commit：** `b566bfe4` · tip `TIP-ember-short-visual-pass-thin-d12376-d12391`  
**bv：** **未抬** · 奖励 / Cast / AFK / STAMP / skill_mult / Affix densify / glance twin **未改**

## 交付
| 项 | 内容 |
|----|------|
| 脚本 | `tools/p1map/d12376_sx01_sx04_visual_pass.py` |
| sx01 | 柱20 + trim12 + 火盆2 |
| sx02 | 柱22 + 栏8 + 海晶6（deck fy68；地面灯链占位 skip lamps=0） |
| sx03 | 柱20 + 冰点12 + 闸栅8 |
| sx04 | 柱26 + 赤灰4 + 岩浆5（井口 rim，不堵 aisle） |
| map_version | `ember_short_sx01..04@d12376` |
| MCA | `plugins/DungeonPlus/map/ember_short_sx0{1..4}/` 磁盘已改（gitignore，未进仓） |

## 验收
脚下 spawn/r0–r3/boss 全实心 · 静态 git show 无 reward/Cast/afk/STAMP/balance 抬 · PASS  
偏差：sx02 灯链 skip；sx04 井口玻璃篦子既有石砖盖，仅叠 rim。

## 热更
`dp reload` 导入四本模板 · `corerpg reload` 刷 map_version · 清 sx01–04 dungeon-caches。
