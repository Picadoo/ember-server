# STATUS · B-flex-4 踏步热键 · 批 A

- **时间：** 2026-09-29 04:35 Asia/Shanghai
- **决定：** **批 A** · 候选 3（潜行+Q）
- **设计 tip：** `docs/design-ember-flex-step-hotkey.md` · 交稿 `a9311ac`
- **施工硬条（插件主 · TrMenu lore 可选）：**
  1. `FlexSkillService` implements `Listener`；`PlayerDropItemEvent`：潜行 + 已装配 → cancel + `cast`；未潜行/未装配不拦
  2. `CoreRpgPlugin`：`registerEvents(flexSkillService, this)`
  3. **勿**改 `skills.yml` `flex_ember_step` CD14 / distance 5.0
  4. 可选：TrMenu `ember_flex_skill` / hub 轻技 lore 半行「潜行+Q」；可选 config `flex.hotkey`
  5. **勿**裸 F / 裸 Q / 快捷栏符；菜单装配与释放保留
- **验收：** 静态 rg + 按键/菜单轻测；副手 F 仍只换手；禁 wall-clock/DPS/挑刺；勿宣称 B0.1 已清
- **交岗：** 余烬-插件
