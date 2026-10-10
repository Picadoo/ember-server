# 状态 · D602：成色希望旁路 A2 · PASS

**日期：** 2026-10-11  
**裁决：** **已批 A · 批 M · D602** · DESIGN `DESIGN-ember-quality-hope-bypass-2026-10-11.md`  
**jar：** `1.65.294-d602.local` · SEVERE0 · Loading/Enabling OK  
**单测：** `EmberHopeMeterTest` 4/4（含 quality 0/2→1/2→工坊顶→极品）  
**冒烟：** 工坊成色本为确定升档；表盘文案钉死；menus hub/forge/gear 挂 `quality_*_hope`；**未改** qualityCost / 掉落权重 / sock  

## 交付

| 项 | 内容 |
|----|------|
| `EmberHopeMeter.quality` | 成色进度 cur/2；下次确定升精良/卓越 + 费用短句；q2 工坊顶·极品仅掉落 |
| PAPI | `quality_blade_hope` / `quality_charm_hope` / `held_quality_hope` |
| TrMenu | hub 希望表盘 · forge 成色预览/确认 · gear |

## 硬禁

≠改价 ≠改掉落成色权重 ≠随机锻成色保底 ≠开放极品养成 ≠K3 ≠AFK 表抬 ≠sx20 ≠legacy socket ≠动 gem sock

*D602 PASS · 下一档 D603 精工希望旁路（同模式）。*
