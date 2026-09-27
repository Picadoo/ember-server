# STATUS · 日常 Boss 掉落对齐庭院装备机会

- 时间：2026-09-28（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/STATUS-ember-daily-s1-s3-player-review.md` §必改1；总控拍板「同价体力同装备机会」
- 方案：七线日常 Boss 死亡掉落对齐庭院 `EmberDailyBrute` 三行装备概率
- 未做：HP/攻未改；通关箱 `option.yml` 未动；菜单预览文案（插件/策划）；**未 commit/push**

## 对齐内容（与庭院同三行同概率）

| 物品 | 概率 |
|------|------|
| `gear_ember_blade` | 0.12 |
| `gear_ember_charm` | 0.08 |
| `gear_ember_t1_blade` | 0.03 |

另：各线原有 `mat_ember_core_fragment` 必掉及 shard / bone_dust 风味行 **保留**。

## 改动 Boss

| MM ID | 文件 | Display 去色 |
|-------|------|--------------|
| EmberDailyBrute | EmberDaily.yml | 灰烬庭院蛮兵（原已有，未改） |
| EmberAshBrute | EmberDailyAsh.yml | 焦核蛮兵 **+三行** |
| EmberDailyCryptWarden | EmberDailyCrypt.yml | 残誓守墓 **+三行** |
| EmberDailyTideBrute | EmberDailyTide.yml | 潮闸蛮兵 **+三行** |
| EmberDailySpireWarden | EmberDailySpire.yml | 断塔守望 **+三行** |
| EmberDailyFrostBrute | EmberDailyFrost.yml | 霜核蛮兵 **+三行** |
| EmberDailyRailWarden | EmberDailyRail.yml | 锈轨矿监 **+三行** |

## 加载

- 游玩服短重启；MythicMobs 成功加载 **59** 个怪物（无新增配置报错，仅既有 ExampleItems KingsCrown）
- `ops.json` = `[]`

## 交总控

风景线与庭院同价体力下 Boss 装备机会已对齐。菜单「奖励预览」若仍只写「Boss 核心」须另派插件/文案岗改文案（本岗未动 TrMenu）。
