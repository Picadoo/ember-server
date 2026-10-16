# tip · TIP-FE HuntPath.offerPick「猎侧路径选打」菜单化 · D13388

> **状态：** **CLOSED live** · jar `1.65.881-d13388.local` · 2026-10-16 Asia/Shanghai · 猎侧路径选打同题勿再交 · 证据 `/workspace/tmp/tip-fe-hunt-path/`
> **DESIGN** [`DESIGN-ember-hunt-path-offer-pick-visible-2026-10-16.md`](../design/DESIGN-ember-hunt-path-offer-pick-visible-2026-10-16.md) · backlog `B-hunt-path-offer-pick-visible`
> **施工岗：** 余烬-插件（HuntPath.offerPick / applyAndReply / 无参 huntpath / 周要约改开菜单 + PAPI hunt_path* / hunt_unlock）+ 余烬-物品（薄页 `ember_p1_hunt_path`）· **不派怪物/地图**
> **硬禁：** 零抬伤害/CD表/掉落/体力价 · 零改 D559 猎侧语义 · 零 Duck/全族 *Path 同窗 · 零 Affix densify
> **ancestor：** TIP-FD CLOSED live（闪爆路径选打 D13387）

## 一句话
周选/改猎侧路径时开「猎侧路径」薄页：自动 / 提醒 / 静默 / 清空 / 技能组 / 冒险可点；清 ConfirmTokens 教 `huntpath` 主 UI。

## 验收
- [x] 总控批 A·M（自批 · 用户「你自己做」）
- [x] 插件 offerPick / applyAndReply / 无参 huntpath / 周要约改开菜单 + 单测
- [x] 薄页 ember_p1_hunt_path
- [x] jar 1.65.881-d13388.local 部署 play

## STATUS: **CLOSED live** · D13388 · TIP-FE · 2026-10-16 Asia/Shanghai
