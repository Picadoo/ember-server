# D318 六槽 T1 · 预置菜单（未上线）

这里的文件 **不在** `server-runtime/plugins/TrMenu/menus/`，线上看不到；T2（测试服开关开）时由菜单岗定稿后再放进去。

| 文件 | 用途 |
|------|------|
| `trmenu/ember_p1_armor.yml` | 护甲页：穿着 4 格（只读，点击回穿着什么）· 背包同部位最好的一件 4 格（点击换上）· 四件套真实三态（D325；点击回进度）· 装备入口镜像（D329）· 全部换上（点两次确认）· 待领物品（点击领取）· 返回装备页。开关关时每格是灰玻璃板「护甲功能尚未开放」，只有有待领时显示领取入口 |
| `trmenu/ember_p1_gear.armor-slot.snippet.yml` | 装备页「护甲」格片段；`armor_on = 0`（开关关）时显示普通玻璃板；开关关但有待领时显示「待领物品」，点击直接领取 |

- 点击动作走 `corerpg p1 armor equip <head|chest|legs|boots>` / `armor worn <部位>` / `armor all` / `armor set` / `armor claim`（菜单代发，玩家不需要打命令）。**每一格点击都有一句回复，从不静默**（D320 ⑤，路由见 `EmberSixPapi.route`）：
  - 开关关：除「待领」外一律回「护甲功能尚未开放。」；待领与开关脱钩（D320 ④），有待领就能领，没有待领时也回「尚未开放」。
  - 开关开：穿着格 → 穿着什么 / 下面一格能换；候选格 → 换上，或「背包里没有可换的…」；全部换上 → 预览 / 确认，或「四个部位都已是最好的一件」；四件套 → 真实三态进度（已激活 / 进行中 / 未激活；激活时 −3%）；待领 → 领取结果或「没有待领物品」。
  - 「全部换上」：方案变了回 `§7背包有变化，已刷新方案`，纯超时回 `§7确认已超时，请再点一次`（D320 ⑥），两者都重新显示清单、不执行。
- 子图标写法：默认图标 = 开关关的样子；`priority 2` = `armor_on == 1`；`priority 3` = 有候选 / 能全部换上 / 有待领。`armor_<slot>_has`、`armor_all_has` 开关关时为空串，所以 priority 3 不会在开关关时误显示；`armor_stash_has` / `armor_stash` / `armor_stash_line` 开关关也有值。`EmberSixRankTest.stagedMenuEveryButtonReplies_D320` 检查每个默认图标和子图标都有动作、动作都路由到会回话的分支。
- 占位符全表见 `CoreRpg/src/main/java/town/sunshine/corerpg/p1/EmberSixPapi.java` 类注释；`EmberSixRankTest.stagedMenuKeysRouteAndHaveNoCommandText` 检查这里每个 `%corerpg_p1_*%` 都有插件路由、玩家文案里没有命令教学。
- 工坊 `ember_p1_forge`：手持护甲时，现有 `held_*` 占位符已自动返回「护甲随护符成长；成色和精工看掉落」，分解行显示「分解得白板胚 0.T 个」，菜单不用改字；如菜单岗要灰显按钮，可用 `held_enhance_lack` 为空 + 物品类型判断，或等 T2 再加 `armor_held` 标记。
- 物品模板（NI）由物品岗按 `docs/design/staged/d318-six-slot/ni-armor-ids.md` 建。
