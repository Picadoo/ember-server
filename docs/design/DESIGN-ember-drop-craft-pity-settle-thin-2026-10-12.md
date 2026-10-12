# 余烬 · 可见保底 · 精工软保底结算进度薄窗

STATUS=**批 A · 方案 M · 交施工** · 2026-10-12 16:54 CST  
上游 RESEARCH [`RESEARCH-ember-drop-craft-pity-settle-thin-2026-10-12.md`](RESEARCH-ember-drop-craft-pity-settle-thin-2026-10-12.md) · tip `TIP-ember-drop-craft-pity-settle-thin-d12408-d12423-2026-10-12.md`  
资产：`EmberDropCraftPity` · `EmberSettleService`（对齐 `DropQualityPity` 结算 else）

精工软保底进度在通关结算可读 · **禁**改 NEED/FLOOR · **禁**枢纽 glance twin。

## 方案 M（荐）

| 项 | 内容 |
|----|------|
| 触发口 | `EmberSettleService` 精工 pity 块（`dropCForced` 旁） |
| 未触发 | `sendMessage` + 可选 AB：`§7掉落精工 · §f` + `EmberDropCraftPity.remainLine(pd)`（镜像成色文案骨架） |
| 已触发 | 保持现「精工软保底触发」一句；**不**双重刷屏 |
| 热关 | `ember-v1.yml`（或 settle soft 段）`drop_craft_pity.settle_remain: true` 缺省 true |
| 验收 | 连空 1..4 见进度；满触发仍只一句；`git show` NEED/FLOOR 不变；无新 hub 格 |

## 明确不做

- 改 `NEED` / `FLOOR` / `CRAFT_WEIGHTS` / 掉率  
- HopeMeter / 枢纽 glance densify · 新 PAPI  
- Affix · skill_mult · 图装 soft gift  
- 成色 remain densify / 双轴合并成一条墙  

## 方案 R / W

| 方案 | 裁决 |
|------|------|
| R · 枢纽再挂精工保底格 | **否**（glance twin / hub 挤） |
| W · 抬 NEED 或强制更高精工当假变强 | **否决** |

## 进度戳
- **TIP-Q 批 A·M**（2026-10-12）· tip D12408–D12423 · 交插件
