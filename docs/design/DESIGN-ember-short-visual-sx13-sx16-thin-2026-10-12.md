# 余烬 · 短征轻视觉续 · sx13–sx16 叠饰

STATUS=**待批 A · 方案 M · 荐交施工** · 2026-10-12 17:16 CST  
上游 RESEARCH [`RESEARCH-ember-short-visual-sx13-sx16-thin-2026-10-12.md`](RESEARCH-ember-short-visual-sx13-sx16-thin-2026-10-12.md) · tip `TIP-ember-short-visual-sx13-sx16-thin-d12496-d12511-2026-10-12.md`  
范式：TIP-V / `d12480_sx09_sx12_visual_pass.py` · TIP-U / d12464 · TIP-O / d12376 · D450 采实心+占位 skip

四本主题柱/灯/饰带 · **禁**重铺地板 · **禁**改房序/奖励/Cast。

## 方案 M（荐）

| 本 | 叠饰要点 |
|----|----------|
| sx13 烬衡悬梁 | 石砖/安山/圆石柱 + 梁侧铁栅栏杆；衡梁走面与梁下虚空不堵 |
| sx14 烬裂折廊 | 石砖/安山柱 + 折角侧铁栅；灰·红羊毛饰带（裂隙可读，不封折角走道） |
| sx15 烬旋坡廊 | 石砖柱沿坡段 + 坡缘铁栅；天井侧荧石/灯点缀；螺旋走面不堵 |
| sx16 烬影叠廊 | 石砖/安山柱 + 影墙侧铁栅饰带；侧穿口不封、影墙不填实 |

脚本：`tools/p1map/d12496_sx13_sx16_visual_pass.py`（复用 d12480/d12464/d12376 工具函数风格：solid footing · occupied skip · **禁** floor rewrite）。  
`ember-v1-runs.yml` 四本 `map_version` → `@d12496`（现：sx13 `@d446` · sx14–15 `@d441` · sx16 `@d442`）。

## 明确不做

- sx17–sx19 本窗硬铺 · 粒子 densify · 挂机 R  
- Affix / 伤乘 / 抬表 / glance twin / ForgeChoice·wear·pity·awkDelta  
- 改 S52–S55 / Cast / afk  
- 改 rooms·boss 坐标 / 房序

## 方案 R / W

| 方案 | 裁决 |
|------|------|
| R · 一次铺满 sx13–sx19 | **否**（TIP-O/U/V 已禁；拆批；本窗仅取 HOLD 前四） |
| W · 重铺地形或抬短征奖 | **否决** |

## 验收（批 A 后施工）

- 脚本可跑；四本叠饰进本可感主题差  
- 脚下实心；Director 房序不变  
- S52–S55 / Cast / afk `git show` **无抬**  
- 不跑 p1sim（叠饰级）

## 进度戳
- **TIP-W 批 A·M** · tip D12496–D12511 · 交施工 sx13–sx16

- **TIP-W 待批 A·M**（2026-10-12）· tip D12496–D12511 · 荐交施工 sx13–sx16
