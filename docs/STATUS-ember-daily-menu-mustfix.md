# STATUS · 日常菜单必改 2+3

**日期：** 2026-09-28 05:08（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/STATUS-ember-daily-s1-s3-player-review.md` §必改 2、3；总控【派工 · 挑刺必改2+3 · TrMenu】  
**范围：** 仅 TrMenu 菜单 YAML；未改 S4 frost/rail、地图或 Java builder；未 commit/push。

## 结论

**priority: true · ✅ 结案**

### 必改 2 · 枢纽入口文案

已改 `plugins/TrMenu/menus/ember_hub.yml` 的图标 `H`「日常 · 余烬窟」：

- 删除「日限次数」语义。
- 改为「消耗 `%corerpg_stamina_cost_daily%` 体力闯余烬窟 · 多线可选」。
- 明列五线：庭院、焦骨、地窖、潮蚀、断塔。
- 保留并补充当前体力 `%corerpg_stamina%/%corerpg_stamina_max%`。
- 玩家文案未加入 `/dp`、`/corerpg` 教学。

### 必改 3 · 体力不足灰显

已改 `plugins/TrMenu/menus/ember_daily.yml` 的现有五个进本键位：`S / A / C / D / E`。

- 正常态保留原有「点击进本」及底层 `corerpg enter ...` 校验。
- 每个键新增 `update: 20`，并在 `icons` 下加高优先级灰态子图标：
  `condition: 'check papi *%corerpg_stamina% < *%corerpg_stamina_cost_daily%'`。
- 灰态使用 `gray stained glass pane`，显示当前/所需体力，并统一提示：`体力不足 · 每日 0:00 回满`；点击仅短提示，不执行进本命令。
- 文件当前没有 F/G 键位；若 S4 合并 F/G，复用上述灰显条件、材质和 lore 模板。

### 顺手 · 奖励预览

- `P`「奖励预览」保留为 lore 真预览，不再执行只有一句摘要的空 `tell`。
- 预览改为事实口径：通关箱材料列出核心碎片、附魔晶、碎片；Boss 显示核心与各线对应的碎片/骨尘，并明确各线均有概率获得刃、符、T1 装备。

## 校验

- TrMenu 3.12.5 自动重载成功：初载 `ember_hub.yml` 41ms、`ember_daily.yml` 13ms（05:07 CST）；奖励文案最终改动后 `ember_daily.yml` 19ms（05:08 CST）。
- `git diff --check` 通过。
- 工作区原有其他执行器改动未触碰；本岗未 commit/push。
