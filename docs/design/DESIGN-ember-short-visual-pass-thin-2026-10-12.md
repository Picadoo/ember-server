# 余烬 · 短征轻视觉点缀薄窗（sx01–sx04 首批）

STATUS=**待批 A · 荐方案 M** · 2026-10-12  
上游 RESEARCH [`RESEARCH-ember-short-visual-pass-thin-2026-10-12.md`](RESEARCH-ember-short-visual-pass-thin-2026-10-12.md) · tip `TIP-ember-short-visual-pass-thin-d12376-d12391-2026-10-12.md`  
范式：[`DESIGN-ember-mainline-q01-q03-visual-pass-2026-10-10.md`](DESIGN-ember-mainline-q01-q03-visual-pass-2026-10-10.md)（D450）

叠饰柱/灯/饰带 · **禁**重铺地板 · **禁**改房序/奖励/Cast 表。  
短征视觉收束首批（接 D443/D444 真地形）。

## 方案 M（荐）

| 本 | 叠饰要点（对齐各本 DESIGN 主材，1.12.2 可用块） |
|----|-----------------------------------------------|
| sx01 | 石砖/橡木柱 + 荧石帽；灰·黄羊毛饰带；院落火盆位（netherrack+火）少量 |
| sx02 | 云杉/深色橡木向柱；铁栅栏杆点缀；灯笼位用荧石/红石灯链；少量海晶碎点缀潮气 |
| sx03 | 深板岩/石砖柱；碎冰·雪点缀墙根；闸侧铁栅加厚；禁整图冰板 |
| sx04 | 石砖井口柱；井底 netherrack/红地狱砖饰带 + 岩浆块点缀（**不**堵走位）；荧石帽 |

施工：`tools/p1map/d12376_sx01_sx04_visual_pass.py`（或施工号命名）· 加载既有 MCA · 采实心脚下 · 占位 skip。  
map_version：`ember_short_sx0N@d12376`（落盘时改施工号）。  
验收：safe 点脚下实心；进本三房+Boss 仍通；奖励表 / Cast git show 无抬。

## 明确不做

- sx05–sx19 本窗硬铺（另 tip）  
- 改 `ember-v1-runs` 掉落 / S4x / 日帽 / 体力  
- Cast HP/delay/dmg · Affix · glance · 挂机表  
- 假平面空板大修 / 新地图包重铺

## 方案 R / W

| 方案 | 裁决 |
|------|------|
| R · 粒子 densify / 全 19 本同窗 | **后置**（易 twin glance / 工期爆） |
| W · 重铺地形或抬短征奖 | **否决** |
