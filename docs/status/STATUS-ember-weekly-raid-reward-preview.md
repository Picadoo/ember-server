# STATUS · 周本/团本奖励预览真分页 A

- **时间：** 2026-09-28 22:26（Asia/Shanghai）
- **岗：** 余烬-插件 executor
- **依据：** `docs/design/design-ember-weekly-raid-reward-preview.md`（批 A）
- **Verdict：** ✅ **施工完成 / TrMenu 热更加载通过**
- **Git：** 本批仅 4 个 TrMenu YAML + 本 STATUS；未 push

## 一句话

周本、团本父页 P 已从空壳 `tell` 改为有声真分页入口；两页按现网 option / loot / MM 只读展示奖励，底栏 B 可返回各自父页。

## 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/TrMenu/menus/ember_weekly_rewards.yml` | 新建 6 行周本奖励预览页：通关箱、周首通护符、再通概率、终厅摘要、中核一句、B 返回 |
| `plugins/TrMenu/menus/ember_raid_rewards.yml` | 新建 6 行团本奖励预览页：全队每人结算、通关箱、锋利石概率、周首通团戒、T2/T3、B 返回 |
| `plugins/TrMenu/menus/ember_weekly.yml` | P lore 对齐实表；actions 仅 `BLOCK_CHEST_OPEN-1-1` + `menu: ember_weekly_rewards`，删除空壳 tell |
| `plugins/TrMenu/menus/ember_raid.yml` | P lore 对齐实表；actions 仅 `BLOCK_CHEST_OPEN-1-1` + `menu: ember_raid_rewards`，删除空壳 tell |
| `docs/status/STATUS-ember-weekly-raid-reward-preview.md` | 本施工状态 |

## 口径对照

- **周本通关箱：** 核心×3、附魔晶×2、碎片×8、骨尘×4、原版经验 +6 级。
- **周首通 / 再通：** `gear_ember_t1_talisman` 首通保底×1；其后精炼刃约 10% / 护符约 8%。
- **周本终厅：** 核心×1 必掉；刃约 20% / 精炼护符约 12% / 稳定符约 4%；中核摘要为较低 T1/T2 刃机会。
- **团本通关箱：** 核心×4、附魔晶×2、碎片×10、骨尘×6；锋利石约 50%，明确非必给。
- **团首通 / 终厅：** `acc_ember_raid_ring` 周首通不重复；戒+刃=同袍；T2/T3 刃/符约 15/12、8/6，均不保底。

## 热更证据

- **22:26:30 CST**：`printf 'trmenu reload\n' > /workspace/minecraft/server-runtime/console.in`
- `server-runtime/logs/latest.log`：`[TrMenu] 良好 | 34 个菜单已加载 (41 ms)`；热更前记录为 32 个菜单，新增两页已纳入加载数。
- 自动 watcher 亦记录：22:26:13 自动载入 `ember_weekly.yml`、`ember_raid.yml`。

## 禁项自检

- `git diff HEAD -- plugins/DungeonPlus plugins/MythicMobs plugins/CoreRpg/loot.yml`：空。
- 未改 DP、MM、`loot.yml`、体力、进本 command、票、`over_chance*`；未发物、未进本；未创建 NPC；未 git push。
- 新页 Open 仅播放 `BLOCK_CHEST_OPEN-1-1`；无经济、掉落或逻辑动作。

## 验收命令

```bash
python3 -c 'import yaml; [yaml.safe_load(open(f)) for f in ["plugins/TrMenu/menus/ember_weekly.yml","plugins/TrMenu/menus/ember_raid.yml","plugins/TrMenu/menus/ember_weekly_rewards.yml","plugins/TrMenu/menus/ember_raid_rewards.yml"]]'
git diff --check
git diff HEAD -- plugins/DungeonPlus plugins/MythicMobs plugins/CoreRpg/loot.yml
rg -n "menu: ember_(weekly|raid)_rewards|34 个菜单已加载" plugins/TrMenu/menus server-runtime/logs/latest.log
```

## Blocker

无。
