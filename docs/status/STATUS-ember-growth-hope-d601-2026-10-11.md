# 状态 · D601：希望表盘 A1（强化+洗练统一）· PASS

**日期：** 2026-10-11  
**裁决：** **已批 A · 批 M · D601**（上游 DESIGN `2252058b`）  
**jar：** `1.65.293-d601.local` · SEVERE0  
**单测：** `EmberHopeMeterTest` 3/3 · `EmberRerollPityTest` 1/1 · `EmberRunPapiTest` 路由键含新 PAPI  
**冒烟：** HopeQ601 · `enhance_blade_pity`/`held_enhance_pity` →「保底 1/3」对齐 +3 刃真实计数；T0 刃「保底 1/1 · 下次必成」；洗练 pity 委托不变  

## 交付

| 项 | 内容 |
|----|------|
| `EmberHopeMeter` | 统一 glance；强化读 `d.pity`+`MAX_TRIES`；满则「下次必成」 |
| `EmberRerollPity` | 委托 HopeMeter（文案不变） |
| PAPI | `enhance_blade_pity` / `enhance_charm_pity` / `held_enhance_pity` |
| TrMenu | hub 成长格 · forge 强化预览/确认 · gear · reroll 说明格 |

## 硬禁

**未改** RATE / MAX_TRIES / 洗练 pity 阈值·权重·费用 · ≠K3 · ≠AFK 表抬 · ≠sx20 · 不复开 legacy socket · 无新伤乘

*D601 PASS · 可见保底表盘（零乘区）。下一档 D602 A2 成色旁路 tip。*
