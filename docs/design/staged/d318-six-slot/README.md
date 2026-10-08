# D318 六槽 T1 · 预置菜单（未上线）

这里的文件 **不在** `server-runtime/plugins/TrMenu/menus/`，线上看不到；T2（测试服开关开）时由菜单岗定稿后再放进去。

| 文件 | 用途 |
|------|------|
| `trmenu/ember_p1_armor.yml` | 护甲页：穿着 4 格（只读）· 背包同部位最好的一件 4 格（点击换上）· 四件套进度（后续开放）· 全部换上（点两次确认）· 待领物品（点击领取）· 返回装备页 |
| `trmenu/ember_p1_gear.armor-slot.snippet.yml` | 装备页「护甲」格片段；`armor_on = 0`（开关关）时显示普通玻璃板 |

- 点击动作走 `corerpg p1 armor equip <head|chest|legs|boots>` / `armor all` / `armor claim`（菜单代发，玩家不需要打命令；开关关时只回「护甲功能尚未开放」）。
- 占位符全表见 `CoreRpg/src/main/java/town/sunshine/corerpg/p1/EmberSixPapi.java` 类注释；`EmberSixRankTest.stagedMenuKeysRouteAndHaveNoCommandText` 检查这里每个 `%corerpg_p1_*%` 都有插件路由、玩家文案里没有命令教学。
- 工坊 `ember_p1_forge`：手持护甲时，现有 `held_*` 占位符已自动返回「护甲随护符成长；成色和精工看掉落」，分解行显示「分解得白板胚 0.T 个」，菜单不用改字；如菜单岗要灰显按钮，可用 `held_enhance_lack` 为空 + 物品类型判断，或等 T2 再加 `armor_held` 标记。
- 物品模板（NI）由物品岗按 `docs/design/staged/d318-six-slot/ni-armor-ids.md` 建。
