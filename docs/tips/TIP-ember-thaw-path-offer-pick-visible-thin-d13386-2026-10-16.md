# tip · TIP-FC ThawPath.offerPick「解冻路径选打」菜单化 · 拟 D13386

> **状态：** **CLOSED live** · jar `1.65.879-d13386.local` · 2026-10-16 Asia/Shanghai · 解冻路径选打同题勿再交 · 证据 `/workspace/tmp/tip-fc-thaw-path/`
> **DESIGN** [`DESIGN-ember-thaw-path-offer-pick-visible-2026-10-16.md`](../design/DESIGN-ember-thaw-path-offer-pick-visible-2026-10-16.md) · backlog `B-thaw-path-offer-pick-visible`
> **施工岗：** 余烬-插件（ThawPath.offerPick / applyAndReply / 无参 thawpath / 周要约改开菜单 + PAPI thaw_path*）+ 余烬-物品（薄页 `ember_p1_thaw_path`）· **不派怪物/地图**
> **硬禁：** 零抬伤害/CD表/掉落/体力价 · 零改 D561 解冻语义 · 零 Duck/全族 *Path 同窗 · 零 Affix densify
> **ancestor：** TIP-FB CLOSED live（清房突进路径选打 D13385）

## 一句话
周选/改解冻路径时开「解冻路径」薄页：自动 / 提醒 / 静默 / 清空 / 技能组 / 冒险可点；清 ConfirmTokens 教 `thawpath` 主 UI。

## 验收
- [x] 总控批 A·M（自批 · 用户「你自己做」）
- [x] 插件 offerPick / applyAndReply / 无参 thawpath / 周要约改开菜单 + 单测
- [x] 薄页 ember_p1_thaw_path
- [x] jar 1.65.879-d13386.local 部署 play

## STATUS: **CLOSED live** · D13386 · TIP-FC · 2026-10-16 Asia/Shanghai
