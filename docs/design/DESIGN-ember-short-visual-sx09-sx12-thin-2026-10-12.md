# 余烬 · 短征轻视觉续 · sx09–sx12 叠饰

STATUS=**待批 A · 荐方案 M** · 2026-10-12 17:12 CST  
上游 RESEARCH [`RESEARCH-ember-short-visual-sx09-sx12-thin-2026-10-12.md`](RESEARCH-ember-short-visual-sx09-sx12-thin-2026-10-12.md) · tip `TIP-ember-short-visual-sx09-sx12-thin-d12480-d12495-2026-10-12.md`  
范式：TIP-U / `d12464_sx05_sx08_visual_pass.py` · TIP-O / d12376 · D450 采实心+占位 skip

四本主题柱/灯/饰带 · **禁**重铺地板 · **禁**改房序/奖励/Cast。

## 方案 M（荐）

| 本 | 叠饰要点 |
|----|----------|
| sx09 烬渠跳石 | 石砖/安山/圆石柱 + 岸缘铁栅栏杆；跳石顶与渠面不堵走位 |
| sx10 烬闸递室 | 石砖/安山柱 + 闸口铁栅；灰·红羊毛饰带（闸室可读，不封走道） |
| sx11 烬镜对廊 | 石砖柱对称双廊 + 荧石/灯点缀合镜门侧；中轴饰带不封汇心 |
| sx12 烬枢转厅 | 石砖/安山柱环枢 + 侧厢铁栅饰带；转心少量火盆/灯点 |

脚本：`tools/p1map/d12480_sx09_sx12_visual_pass.py`（复用 d12464/d12376 工具函数风格：solid footing · occupied skip · **禁** floor rewrite）。  
`ember-v1-runs.yml` 四本 `map_version` → `@d12480`（现：sx09 `@d444` · sx10–12 `@d446`）。

## 明确不做

- sx13–sx19 本窗硬铺 · 粒子 densify · 挂机 R  
- Affix / 伤乘 / 抬表 / glance twin / ForgeChoice·wear·pity·awkDelta  
- 改 S48–S51 / Cast / afk  
- 改 rooms·boss 坐标 / 房序

## 方案 R / W

| 方案 | 裁决 |
|------|------|
| R · 一次铺满 sx09–sx19 | **否**（TIP-O/U 已禁；拆批） |
| W · 重铺地形或抬短征奖 | **否决** |

## 验收（批 A 后施工）

- 脚本可跑；四本叠饰进本可感主题差  
- 脚下实心；Director 房序不变  
- S48–S51 / Cast / afk `git show` **无抬**  
- 不跑 p1sim（叠饰级）

## 进度戳
- **TIP-V 待批 A·M**（2026-10-12）· tip D12480–D12495 · 荐交施工
