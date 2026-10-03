# STATUS · 深渊奖励预览真分页 A

- **一句话：** TrMenu 深渊奖励预览已由 P 空壳 tell 切换为三档同屏真分页，纯展示且不改变结算逻辑。
- **依据 tip：** `18b96f4`
- **状态：** 完成；未 push。

## 文件

- `plugins/TrMenu/menus/ember_abyss_rewards.yml`：新建 6 行奖励预览页。
- `plugins/TrMenu/menus/ember_abyss.yml`：仅改 P lore 与 actions，去除全部空壳 tell，链到 `ember_abyss_rewards`。
- `docs/status/STATUS-ember-abyss-reward-preview.md`：本状态记录。

## 三档对照

| 结算档 | 展示内容 |
|---|---|
| 1～4 | 余烬碎片×8、余烬骨尘×2、核心×0、孔石×0 |
| 5～9 | 余烬碎片×12、余烬骨尘×4、余烬核心×1、锋利石约 10%（非必给） |
| 10+（本期顶 12） | 余烬碎片×16、余烬骨尘×6、余烬核心×2、锋利石约 10%（非必给） |

另列 COMPLETE T2 周首通（同周再通刃约 4% / 符约 3%）、L12 稳定符周首通及战斗一行摘要；底栏返回 `ember_abyss`。

## 热更与验收

- **热更时间（Asia/Shanghai）：** 2026-09-28 22:34:40 CST。
- **日志证据：** `[22:34:32] [TrMenu] 良好 | 自动重新载入菜单 ember_abyss.yml (5ms)`；`[22:34:40] [TrMenu] 良好 | 35 个菜单已加载 (33 ms)`。
- **验收命令：** `printf 'trmenu reload\n' > /workspace/minecraft/server-runtime/console.in`；`git diff HEAD -- plugins/CoreRpg/abyss.yml plugins/MythicMobs plugins/CoreRpg/loot.yml plugins/DungeonPlus`。

## 禁项自检

- [x] 仅 TrMenu 菜单 + 本 STATUS；未改 `abyss.yml`、MM、`loot.yml`、`DungeonPlus`。
- [x] 未改体力、进本、撤离或 `over_chance*`。
- [x] 未做一层一页；三档同屏。
- [x] 孔石写为约 10%（非必给），未写成必给。
- [x] 预览页无 command / tell / 发物动作；无进本、撤离动作。
- [x] 未执行 git push。
