# 余烬 · 短征轻视觉收尾 · sx17–sx19 叠饰

STATUS=**待批 A · 方案 M · 荐交施工** · 2026-10-12 17:23 CST  
上游 RESEARCH [`RESEARCH-ember-short-visual-sx17-sx19-thin-2026-10-12.md`](RESEARCH-ember-short-visual-sx17-sx19-thin-2026-10-12.md) · tip `TIP-ember-short-visual-sx17-sx19-thin-d12512-d12527-2026-10-12.md`  
范式：TIP-W / `d12496_sx13_sx16_visual_pass.py` · TIP-V / d12480 · TIP-U / d12464 · TIP-O / d12376 · D450 采实心+占位 skip

三本主题柱/灯/饰带 · **禁**重铺地板 · **禁**改房序/奖励/Cast · **闭合** sx01–19 视觉叠饰线。

## 方案 M（荐）

| 本 | 叠饰要点 |
|----|----------|
| sx17 烬潮回廊 | 棱柱岩柱 + 海晶灯帽；青羊毛饰带；闸侧铁栅点缀；落潮通道与水饰面不堵不填 |
| sx18 烬晶折棱 | 石英/安山柱 + 折角侧铁栅；紫/青染色玻璃饰带；折角走道与晶棱视线缝不封 |
| sx19 烬弦共鸣 | 石砖/安山柱 + 节点侧铁栅饰带；紫羊毛点缀；共鸣侧穿口不封、音符方块不盖 |

脚本：`tools/p1map/d12512_sx17_sx19_visual_pass.py`（复用 d12496/d12480/d12464/d12376 工具函数风格：solid footing · occupied skip · **禁** floor rewrite）。  
`ember-v1-runs.yml` 三本 `map_version` → `@d12512`（现：sx17–sx19 均 `@d442`）。

## 明确不做

- sx20 · 粒子 densify · 挂机 R  
- Affix / 伤乘 / 抬表 / glance twin / ForgeChoice·wear·pity·awkDelta  
- 改 S56–S58 / Cast / afk  
- 改 rooms·boss 坐标 / 房序

## 方案 R / W

| 方案 | 裁决 |
|------|------|
| R · 顺带开 sx20 或粒子 densify | **否**（D428 链暂停；HOLD） |
| W · 重铺地形或抬短征奖 | **否决** |

## 验收（批 A 后施工）

- 脚本可跑；三本叠饰进本可感主题差  
- 脚下实心；Director 房序不变  
- S56–S58 / Cast / afk `git show` **无抬**  
- 不跑 p1sim（叠饰级）

## 进度戳
- **TIP-X 待批 A·M**（2026-10-12）· tip D12512–D12527 · 荐交施工 sx17–sx19 收尾
