# 状态 · D274：旧菜单链假文案批修（商城/战令/旧挂机）

**日期：** 2026-10-07（上海时间）  
**上游：** D273（`36f64f46`）· 总控批旧链残留  
**范围：** TrMenu 旧链三文件；**未**动 quest / daily tell

## 已修

| # | 文件 | 修前 | 修后 |
|---|---|---|---|
| 1 | `ember_shop.yml` L39 | `挂机/签到/悬赏产出` | `挂机庭/签到/主线本产出` |
| 2 | `ember_pass.yml` L38/L78 | `日本` + `悬赏 15` | `日常通关` + `精英周 15`（对齐 `progress.yml` pass_xp；去掉 P1 已关悬赏） |
| 3 | `ember_afk.yml` L45 | `挂机庭僵尸 / 骷髅…` | 标明 P1 自动战斗、旧僵尸/骷髅退场 |

可达性：旧枢纽 `ember_hub_legacy`（管理员 `trmenu open`）→ 商城/战令/旧挂机子菜单仍可打开。

## 跳过与理由

| 项 | 理由 |
|---|---|
| **quest.yml 悬赏 hint** | P1 默认：`QuestService.onJoin` 直接 return；灰烛 talk 改送冒险页。普通玩家走不到旧悬赏步骤；仅 OP `quest set` 或关 P1 才触达 → **本窗不动，另记** |
| **ember_daily tell 写死 30** | `cash.yml` `costs.daily: 30` 仍成立 → 非现行假数，跳过 |

## 验收

- `trmenu reload`（未重启）
- 未开六格
